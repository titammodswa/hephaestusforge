package com.titammods.menu;

import com.titammods.block.MelterBlockEntity;
import com.titammods.setup.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MelterMenu extends AbstractContainerMenu {
    private final MelterBlockEntity blockEntity;
    private final ContainerData data;

    public MelterMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, (MelterBlockEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(12));
    }

    public MelterMenu(int id, Inventory playerInventory, MelterBlockEntity entity, ContainerData data) {
        super(ModMenus.MELTER_MENU.get(), id);
        checkContainerDataCount(data, 12);
        this.blockEntity = entity;
        this.data = data;

        this.addSlot(new SlotItemHandler(entity.inventory, 0, 22, 16));
        this.addSlot(new SlotItemHandler(entity.inventory, 1, 22, 34));
        this.addSlot(new SlotItemHandler(entity.inventory, 2, 22, 52));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        this.addDataSlots(data);
    }

    public MelterBlockEntity getBlockEntity() { return blockEntity; }
    public boolean isBurning() { return this.data.get(0) > 0; }
    public int getScaledFuel() { return this.data.get(0) * 14 / (this.data.get(1) == 0 ? 100 : this.data.get(1)); }
    public int getScaledProgress(int i) { return this.data.get(3 + i) * 16 / (this.data.get(6 + i) == 0 ? 100 : this.data.get(6 + i)); }
    public int getState(int i) { return this.data.get(9 + i); }

    @Override
    public boolean stillValid(Player p) { return true; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 3) {
                if (!this.moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    private void addPlayerInventory(Inventory inv) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inv, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }
}