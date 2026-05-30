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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.NonNull;

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

        ResourceHandler<FluidResource> source = level.getCapability(Capabilities.Fluid.BLOCK, posBehind, facing);
        ResourceHandler<FluidResource> destination = level.getCapability(Capabilities.Fluid.BLOCK, posBelow, Direction.UP);

        if (source != null && destination != null) {
            ResourceStack<FluidResource> simulatedDrain = ResourceHandlerUtil.extractFirst(source, _ -> true, 10, Transaction.openRoot());

            if (simulatedDrain != null && !simulatedDrain.resource().toStack(simulatedDrain.amount()).isEmpty()) {
                int filled = destination.insert(simulatedDrain.resource(), simulatedDrain.amount(), Transaction.openRoot());

                if (filled > 0) {
                    if (!isPouring) {
                        isPouring = true;
                    }

                    try (var tx = Transaction.openRoot()) {
                        ResourceStack<FluidResource> actuallyDrainedExtract = ResourceHandlerUtil.extractFirst(source, _ -> true, 10, tx);
                        if (actuallyDrainedExtract != null) {
                            FluidResource actuallyDrained = actuallyDrainedExtract.resource();
                            destination.insert(actuallyDrained, actuallyDrainedExtract.amount(), tx);

                            if (renderFluid.isEmpty() || renderFluid.getFluid() != actuallyDrained.getFluid()) {
                                renderFluid = actuallyDrained.toStack(actuallyDrainedExtract.amount()).copy();
                                renderFluid.setAmount(1000);
                                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                            }
                        }
                        tx.commit();
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
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("pouring", isPouring);
        output.putBoolean("lastRedstone", lastRedstone);
        if (!renderFluid.isEmpty()) {
            output.store("fluid", FluidStack.OPTIONAL_CODEC, renderFluid);
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        isPouring = input.getBooleanOr("pouring", false);
        lastRedstone = input.getBooleanOr("lastRedstone", false);
        if (input.keySet().contains("fluid")) {
            renderFluid = input.read("fluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
        } else {
            renderFluid = FluidStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
