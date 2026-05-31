package com.titammods.menu;

import com.titammods.common.blockentities.MelterBlockEntity;
import com.titammods.setup.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MelterMenu extends AbstractContainerMenu {

    private final MelterBlockEntity blockEntity;
    private final ContainerData data;

    public MelterMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory,
                (MelterBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(12));
    }

    public MelterMenu(int id, Inventory playerInventory, MelterBlockEntity entity, ContainerData data) {
        super(ModMenus.MELTER_MENU.get(), id);
        checkContainerDataCount(data, 12);
        this.blockEntity = entity;
        this.data        = data;

        this.addSlot(new SlotItemHandler(entity.inventory, 0, 22, 16));
        this.addSlot(new SlotItemHandler(entity.inventory, 1, 22, 34));
        this.addSlot(new SlotItemHandler(entity.inventory, 2, 22, 52));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        this.addDataSlots(data);
    }

    public MelterBlockEntity getBlockEntity() { return blockEntity; }

    public FluidStack getTankFluid() {
        if (blockEntity == null) return FluidStack.EMPTY;
        return blockEntity.tank.getFluid().copy();
    }

    public int getTankCapacity() {
        if (blockEntity == null) return 2700;
        return blockEntity.tank.getCapacity();
    }

    public boolean isBurning()          { return data.get(0) > 0; }
    public int getScaledFuel()          { return data.get(0) * 14 / Math.max(1, data.get(1)); }
    public int getScaledProgress(int i) { return data.get(3 + i) * 16 / Math.max(1, data.get(6 + i)); }
    public int getState(int i)          { return data.get(9 + i); }

    @Override public boolean stillValid(Player p) { return true; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack item = slot.getItem();
            copy = item.copy();
            if (index < 3) {
                if (!moveItemStackTo(item, 3, this.slots.size(), true)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(item, 0, 3, false)) {
                return ItemStack.EMPTY;
            }
            if (item.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return copy;
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
    }
}