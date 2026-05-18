package com.titammods.block;

import com.titammods.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FaucetBlockEntity extends BlockEntity {

    private boolean isPouring = false;
    private boolean lastRedstone = false;
    private FluidStack renderFluid = FluidStack.EMPTY;

    public FaucetBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FAUCET.get(), pos, state);
    }

    public FluidStack getRenderFluid() { return renderFluid; }
    public boolean isPouring() { return isPouring; }
    public boolean hasRedstone() { return lastRedstone; }

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
        isPouring = false;
        renderFluid = FluidStack.EMPTY;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public void tick() {
        if (level == null) return;

        if (!isPouring && !lastRedstone) return;

        Direction facing = getBlockState().getValue(SearedFaucetBlock.FACING);
        BlockPos posBehind = worldPosition.relative(facing.getOpposite());
        BlockPos posBelow = worldPosition.below();

        IFluidHandler source = level.getCapability(Capabilities.FluidHandler.BLOCK, posBehind, facing);
        IFluidHandler destination = level.getCapability(Capabilities.FluidHandler.BLOCK, posBelow, Direction.UP);

        if (source != null && destination != null) {
            FluidStack simulatedDrain = source.drain(10, IFluidHandler.FluidAction.SIMULATE);

            if (!simulatedDrain.isEmpty()) {
                int filled = destination.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);

                if (filled > 0) {
                    if (!isPouring) {
                        isPouring = true;
                    }

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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("pouring", isPouring);
        tag.putBoolean("lastRedstone", lastRedstone);
        if (!renderFluid.isEmpty()) {
            tag.put("fluid", renderFluid.saveOptional(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        isPouring = tag.getBoolean("pouring");
        lastRedstone = tag.getBoolean("lastRedstone");
        if (tag.contains("fluid")) {
            renderFluid = FluidStack.parseOptional(registries, tag.getCompound("fluid"));
        } else {
            renderFluid = FluidStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}