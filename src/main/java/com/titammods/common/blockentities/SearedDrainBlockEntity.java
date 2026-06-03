package com.titammods.common.blockentities;

import com.titammods.common.blockentities.multiblock.IDisplayFluidListener;
import com.titammods.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jspecify.annotations.Nullable;

public class SearedDrainBlockEntity extends BlockEntity implements IDisplayFluidListener {

    private @Nullable BlockPos controllerPos;
    public FluidStack displayFluid = FluidStack.EMPTY;

    public SearedDrainBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEARED_DRAIN.get(), pos, state);
    }

    public void setControllerPos(@Nullable BlockPos pos) { this.controllerPos = pos; setChanged(); }
    public @Nullable BlockPos getControllerPos()         { return controllerPos; }

    @Override public FluidStack getDisplayFluid() { return displayFluid; }

    @Override
    public void notifyDisplayFluidUpdated(FluidStack fluid) {
        if (!FluidStack.isSameFluidSameComponents(displayFluid, fluid)
                || displayFluid.isEmpty() != fluid.isEmpty()) {
            displayFluid = fluid.isEmpty() ? FluidStack.EMPTY : fluid.copy();
            setChanged();
            if (level != null)
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
    @SuppressWarnings("removal")
    public @Nullable IFluidHandler getFluidHandler() {
        if (controllerPos == null || level == null) return null;
        if (level.getBlockEntity(controllerPos) instanceof SmelteryControllerBlockEntity ctrl) {
            return new IFluidHandler() {
                @Override public int getTanks() { return ctrl.fluidTank.getTanks(); }
                @Override public FluidStack getFluidInTank(int t) { return ctrl.fluidTank.getFluidInTank(t); }
                @Override public int getTankCapacity(int t) { return ctrl.fluidTank.getTankCapacity(t); }
                @Override public boolean isFluidValid(int t, FluidStack s) { return false; }

                @Override
                public int fill(FluidStack resource, FluidAction action) {
                    return 0;
                }

                @Override
                public FluidStack drain(FluidStack resource, FluidAction action) {
                    FluidStack drained = ctrl.fluidTank.drain(resource, action);
                    if (action.execute() && !drained.isEmpty()) ctrl.setChanged();
                    return drained;
                }

                @Override
                public FluidStack drain(int maxDrain, FluidAction action) {
                    FluidStack drained = ctrl.fluidTank.drain(maxDrain, action);
                    if (action.execute() && !drained.isEmpty()) ctrl.setChanged();
                    return drained;
                }
            };
        }
        return null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (controllerPos != null) {
            output.putInt("cx", controllerPos.getX()); output.putInt("cy", controllerPos.getY()); output.putInt("cz", controllerPos.getZ());
        }
        if (!displayFluid.isEmpty())
            output.putString("display_fluid", BuiltInRegistries.FLUID.getKey(displayFluid.getFluid()).toString());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (input.getIntOr("cy", Integer.MIN_VALUE) != Integer.MIN_VALUE)
            controllerPos = new BlockPos(input.getIntOr("cx", 0), input.getIntOr("cy", 0), input.getIntOr("cz", 0));
        String id = input.getStringOr("display_fluid", "");
        if (!id.isEmpty()) {
            Fluid f = BuiltInRegistries.FLUID.getValue(Identifier.parse(id));
            displayFluid = (f != null && !f.isSame(Fluids.EMPTY)) ? new FluidStack(f, 1000) : FluidStack.EMPTY;
        }
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider r) { return saveWithoutMetadata(r); }
    @Override public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}