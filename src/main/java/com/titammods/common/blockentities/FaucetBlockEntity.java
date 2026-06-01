package com.titammods.common.blockentities;

import com.titammods.common.blocks.SearedFaucetBlock;
import com.titammods.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jspecify.annotations.Nullable;

public class FaucetBlockEntity extends BlockEntity {

    private boolean isPouring    = false;
    private boolean lastRedstone = false;
    private FluidStack renderFluid = FluidStack.EMPTY;

    public FaucetBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FAUCET.get(), pos, state);
    }

    public FluidStack getRenderFluid() { return renderFluid; }
    public boolean isPouring()         { return isPouring; }
    public boolean hasRedstone()       { return lastRedstone; }

    public void activate() {
        if (!isPouring) {
            isPouring = true;
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        } else {
            stopPouring();
        }
    }

    public void handleRedstone(boolean hasSignal) {
        if (hasSignal != lastRedstone) {
            lastRedstone = hasSignal;
            if (hasSignal && !isPouring) {
                activate();
            }
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void stopPouring() {
        isPouring   = false;
        renderFluid = FluidStack.EMPTY;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public void tick() {
        if (level == null) return;
        if (!isPouring && !lastRedstone) return;

        Direction facing   = getBlockState().getValue(SearedFaucetBlock.FACING);
        BlockPos posBehind = worldPosition.relative(facing.getOpposite());
        BlockPos posBelow  = worldPosition.below();

        IFluidHandler source      = getFluidHandler(posBehind, facing);
        IFluidHandler destination = getFluidHandler(posBelow, Direction.UP);

        if (source != null && destination != null) {
            FluidStack simulatedDrain = source.drain(10, IFluidHandler.FluidAction.SIMULATE);

            if (!simulatedDrain.isEmpty()) {
                int filled = destination.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);

                if (filled > 0) {
                    if (!isPouring) isPouring = true;

                    FluidStack actuallyDrained = source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    destination.fill(actuallyDrained, IFluidHandler.FluidAction.EXECUTE);

                    if (renderFluid.isEmpty() || renderFluid.getFluid() != actuallyDrained.getFluid()) {
                        renderFluid = actuallyDrained.copy();
                        renderFluid.setAmount(1000);
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                    }
                    return;
                }
            }
        }

        if (isPouring) {
            stopPouring();
        }
    }

    private @Nullable IFluidHandler getFluidHandler(BlockPos pos, Direction side) {
        if (level == null) return null;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.titammods.common.blockentities.SearedTankBlockEntity tank) {
            return tank.getFluidTank();
        }
        if (be instanceof com.titammods.common.blockentities.MelterBlockEntity melter) {
            return melter.tank;
        }
        return null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("pouring",      isPouring);
        output.putBoolean("lastRedstone", lastRedstone);
        output.store("fluid", FluidStack.OPTIONAL_CODEC, renderFluid);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        isPouring    = input.getBooleanOr("pouring",      false);
        lastRedstone = input.getBooleanOr("lastRedstone", false);
        renderFluid  = input.read("fluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}