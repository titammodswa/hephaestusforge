package com.titammods.menu;

import com.titammods.block.SmelteryControllerBlockEntity;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SmelteryMenu extends AbstractContainerMenu {
    public final SmelteryControllerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    private int currentRowOffset = 0;
    private int lastInventoryVersion = -1;
    private boolean isRefreshing = false;
    private boolean isProcessingBucket = false;

    public final ItemStackHandler bucketHandler = new ItemStackHandler(2) {
        @Override
        public int getSlotLimit(int slot) { return 1; }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0 && !isProcessingBucket) {
                processBucket();
            }
        }
    };

    public final ItemStackHandler windowHandler = new ItemStackHandler(24) {
        @Override
        protected void onContentsChanged(int slot) {
            if (isRefreshing || blockEntity == null) return;
            int realIndex = slot + (currentRowOffset * 3);
            if (realIndex < blockEntity.itemHandler.getSlots()) {
                blockEntity.itemHandler.setStackInSlot(realIndex, this.getStackInSlot(slot));
            }
        }
        @Override
        public int getSlotLimit(int slot) { return 1; }
    };

    public SmelteryMenu(int id, Inventory inv, net.minecraft.network.RegistryFriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public SmelteryMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenus.SMELTERY_MENU.get(), id);
        this.blockEntity = (SmelteryControllerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(inv.player.level(), entity.getBlockPos());

        createSmelteryInventory();
        createPlayerInventory(inv);
        setupDataSlots();
        refreshWindow();
    }

    private void processBucket() {
        if (blockEntity == null || blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide) return;
        ItemStack input = bucketHandler.getStackInSlot(0);
        if (input.isEmpty()) return;

        isProcessingBucket = true;

        FluidActionResult emptyResult = FluidUtil.tryEmptyContainer(input, blockEntity.fluidTank, Integer.MAX_VALUE, null, false);
        if (emptyResult.isSuccess()) {
            ItemStack resultStack = emptyResult.getResult();
            if (bucketHandler.insertItem(1, resultStack, true).isEmpty()) {
                emptyResult = FluidUtil.tryEmptyContainer(input, blockEntity.fluidTank, Integer.MAX_VALUE, null, true);
                bucketHandler.extractItem(0, 1, false);
                bucketHandler.insertItem(1, emptyResult.getResult(), false);
                blockEntity.setChanged();
                isProcessingBucket = false;
                return;
            }
        }

        FluidActionResult fillResult = FluidUtil.tryFillContainer(input, blockEntity.fluidTank, Integer.MAX_VALUE, null, false);
        if (fillResult.isSuccess()) {
            ItemStack resultStack = fillResult.getResult();
            if (bucketHandler.insertItem(1, resultStack, true).isEmpty()) {
                fillResult = FluidUtil.tryFillContainer(input, blockEntity.fluidTank, Integer.MAX_VALUE, null, true);
                bucketHandler.extractItem(0, 1, false);
                bucketHandler.insertItem(1, fillResult.getResult(), false);
                blockEntity.setChanged();
            }
        }

        isProcessingBucket = false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level().isClientSide) return;
        for (int i = 0; i < bucketHandler.getSlots(); i++) {
            ItemStack stack = bucketHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                player.drop(stack, false);
            }
        }
    }

    @Override
    public void broadcastChanges() {
        if (this.lastInventoryVersion != this.blockEntity.inventoryVersion) {
            this.lastInventoryVersion = this.blockEntity.inventoryVersion;
            refreshWindow();
        }
        super.broadcastChanges();
    }

    public void updateScrollOffset(int rowOffset) {
        this.currentRowOffset = rowOffset;
        refreshWindow();
    }

    public int getCurrentRowOffset() {
        return this.currentRowOffset;
    }

    private void refreshWindow() {
        isRefreshing = true;
        for (int i = 0; i < 24; i++) {
            int realIndex = i + (currentRowOffset * 3);
            if (blockEntity != null && realIndex < blockEntity.itemHandler.getSlots()) {
                windowHandler.setStackInSlot(i, blockEntity.itemHandler.getStackInSlot(realIndex).copy());
            } else {
                windowHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        isRefreshing = false;
    }

    private void setupDataSlots() {
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity != null ? blockEntity.inventoryVersion : 0; }
            @Override public void set(int value) {
                if (blockEntity != null) blockEntity.inventoryVersion = value;
            }
        });

        int slotsToSync = Math.min(blockEntity.itemHandler.getSlots(), 150);
        for (int i = 0; i < slotsToSync; i++) {
            final int index = i;
            this.addDataSlot(new DataSlot() {
                @Override public int get() { return blockEntity != null && index < blockEntity.meltingProgress.length ? blockEntity.meltingProgress[index] : 0; }
                @Override public void set(int value) { if (blockEntity != null && index < blockEntity.meltingProgress.length) blockEntity.meltingProgress[index] = value; }
            });
            this.addDataSlot(new DataSlot() {
                @Override public int get() { return blockEntity != null && index < blockEntity.meltingTime.length ? blockEntity.meltingTime[index] : 0; }
                @Override public void set(int value) { if (blockEntity != null && index < blockEntity.meltingTime.length) blockEntity.meltingTime[index] = value; }
            });
            this.addDataSlot(new DataSlot() {
                @Override public int get() { return blockEntity != null && index < blockEntity.meltingState.length ? blockEntity.meltingState[index] : 0; }
                @Override public void set(int value) { if (blockEntity != null && index < blockEntity.meltingState.length) blockEntity.meltingState[index] = value; }
            });
        }
    }

    private void createSmelteryInventory() {
        int slotTextureX = -70;
        int slotTextureY = 12;

        for (int i = 0; i < 24; i++) {
            int col = i % 3;
            int row = i / 3;
            this.addSlot(new SlotItemHandler(windowHandler, i, slotTextureX + col * 22 + 5, slotTextureY + row * 18 + 1));
        }

        this.addSlot(new SlotItemHandler(bucketHandler, 0, 125, 46));
        this.addSlot(new SlotItemHandler(bucketHandler, 1, 125, 104) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return false; }
        });
    }

    private void createPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 196));
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 138 + i * 18));
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, ModBlocks.SMELTERY_CONTROLLER.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int totalSmelterySlots = 24 + 2;
            int playerInvStartIndex = totalSmelterySlots;
            int playerInvEndIndex = playerInvStartIndex + 36;

            if (index < totalSmelterySlots) {
                if (!this.moveItemStackTo(itemstack1, playerInvStartIndex, playerInvEndIndex, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                boolean isFluidContainer = itemstack1.getItem() instanceof net.minecraft.world.item.BucketItem ||
                        itemstack1.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM) != null;
                boolean inserted = false;

                if (isFluidContainer) {
                    if (this.moveItemStackTo(itemstack1, totalSmelterySlots - 2, totalSmelterySlots - 1, false)) {
                        inserted = true;
                    }
                }

                if (!inserted) {
                    for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
                        if (itemstack1.isEmpty()) break;
                        itemstack1 = blockEntity.itemHandler.insertItem(i, itemstack1, false);
                    }
                    if (itemstack1.getCount() != itemstack.getCount()) {
                        inserted = true;
                        if (blockEntity != null) blockEntity.inventoryVersion++;
                        refreshWindow();
                        this.broadcastChanges();
                    }
                }

                if (!inserted) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.set(itemstack1);
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }
}