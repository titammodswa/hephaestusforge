package com.titammods.common.blockentities;

import com.titammods.common.blockentities.multiblock.IDisplayFluidListener;
import com.titammods.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public class SearedChuteBlockEntity extends BlockEntity implements IDisplayFluidListener, WorldlyContainer {

    private @Nullable BlockPos controllerPos;
    public FluidStack displayFluid = FluidStack.EMPTY;

    private final NonNullList<ItemStack> buffer = NonNullList.withSize(1, ItemStack.EMPTY);

    public SearedChuteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEARED_CHUTE.get(), pos, state);
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

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return buffer.get(0).isEmpty(); }
    @Override public ItemStack getItem(int slot) { return buffer.get(slot); }
    @Override public void setItem(int slot, ItemStack stack) { buffer.set(slot, stack); setChanged(); }
    @Override public ItemStack removeItem(int slot, int count) { return ContainerHelper.removeItem(buffer, slot, count); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(buffer, slot); }
    @Override public boolean stillValid(Player player) { return false; }
    @Override public void clearContent() { buffer.clear(); }

    @Override
    public int[] getSlotsForFace(Direction side) { return new int[]{0}; }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction dir) {
        return controllerPos != null;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir) {
        return false;
    }

    @SuppressWarnings("removal")
    public void serverTick() {
        if (level == null || level.isClientSide() || controllerPos == null) return;
        ItemStack buffered = buffer.get(0);
        if (buffered.isEmpty()) return;

        if (level.getBlockEntity(controllerPos) instanceof SmelteryControllerBlockEntity ctrl) {
            for (int i = 0; i < ctrl.itemHandler.getSlots(); i++) {
                ItemStack remainder = ctrl.itemHandler.insertItem(i, buffered.copy(), false);
                if (remainder.getCount() < buffered.getCount()) {
                    buffer.set(0, remainder.isEmpty() ? ItemStack.EMPTY : remainder);
                    setChanged();
                    ctrl.inventoryVersion++;
                    ctrl.setChanged();
                    return;
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (controllerPos != null) {
            output.putInt("cx", controllerPos.getX());
            output.putInt("cy", controllerPos.getY());
            output.putInt("cz", controllerPos.getZ());
        }
        if (!displayFluid.isEmpty())
            output.putString("display_fluid", BuiltInRegistries.FLUID.getKey(displayFluid.getFluid()).toString());
        output.store("buffer", ItemStack.OPTIONAL_CODEC, buffer.get(0));
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
        buffer.set(0, input.read("buffer", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider r) { return saveWithoutMetadata(r); }
    @Override public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}