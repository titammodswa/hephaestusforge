package com.titammods.menu;

import com.titammods.common.blockentities.SmelteryControllerBlockEntity;
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
    private boolean isRefreshing     = false;
    private boolean isProcessingBucket = false;
    @SuppressWarnings("removal")
    public final ItemStackHandler bucketHandler = new ItemStackHandler(2) {
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override protected void onContentsChanged(int slot) {
            if (slot == 0 && !isProcessingBucket) processBucket();
        }
    };
    @SuppressWarnings("removal")
    public final ItemStackHandler windowHandler = new ItemStackHandler(24) {
        @Override
        protected void onContentsChanged(int slot) {
            if (isRefreshing || blockEntity == null) return;
            int realIndex = slot + (currentRowOffset * 3);
            if (realIndex < blockEntity.itemHandler.getSlots())
                blockEntity.itemHandler.setStackInSlot(realIndex, this.getStackInSlot(slot));
        }
        @Override public int getSlotLimit(int slot) { return 1; }
    };

    public SmelteryMenu(int id, Inventory inv, net.minecraft.network.RegistryFriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public SmelteryMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenus.SMELTERY_MENU.get(), id);
        this.blockEntity  = (SmelteryControllerBlockEntity) entity;
        this.levelAccess  = ContainerLevelAccess.create(inv.player.level(), entity.getBlockPos());

        createSmelteryInventory();
        createPlayerInventory(inv);
        setupDataSlots();
        refreshWindow();
    }
    @SuppressWarnings("removal")
    private void processBucket() {
        if (blockEntity == null || blockEntity.getLevel() == null
                || blockEntity.getLevel().isClientSide()) return;

        ItemStack input = bucketHandler.getStackInSlot(0);
        if (input.isEmpty()) return;

        isProcessingBucket = true;

        FluidActionResult emptyResult = FluidUtil.tryEmptyContainer(
                input, blockEntity.fluidTank, Integer.MAX_VALUE, null, false);
        if (emptyResult.isSuccess()) {
            ItemStack result = emptyResult.getResult();
            if (bucketHandler.insertItem(1, result, true).isEmpty()) {
                emptyResult = FluidUtil.tryEmptyContainer(
                        input, blockEntity.fluidTank, Integer.MAX_VALUE, null, true);
                bucketHandler.extractItem(0, 1, false);
                bucketHandler.insertItem(1, emptyResult.getResult(), false);
                blockEntity.setChanged();
                isProcessingBucket = false;
                return;
            }
        }

        FluidActionResult fillResult = FluidUtil.tryFillContainer(
                input, blockEntity.fluidTank, Integer.MAX_VALUE, null, false);
        if (fillResult.isSuccess()) {
            ItemStack result = fillResult.getResult();
            if (bucketHandler.insertItem(1, result, true).isEmpty()) {
                fillResult = FluidUtil.tryFillContainer(
                        input, blockEntity.fluidTank, Integer.MAX_VALUE, null, true);
                bucketHandler.extractItem(0, 1, false);
                bucketHandler.insertItem(1, fillResult.getResult(), false);
                blockEntity.setChanged();
            }
        }
        isProcessingBucket = false;
    }

    public void updateScrollOffset(int rowOffset) {
        this.currentRowOffset = rowOffset;
        refreshWindow();
    }

    public int getCurrentRowOffset() { return currentRowOffset; }
    @SuppressWarnings("removal")
    private void refreshWindow() {
        isRefreshing = true;
        for (int i = 0; i < 24; i++) {
            int realIndex = i + (currentRowOffset * 3);
            if (blockEntity != null && realIndex < blockEntity.itemHandler.getSlots())
                windowHandler.setStackInSlot(i, blockEntity.itemHandler.getStackInSlot(realIndex).copy());
            else
                windowHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        isRefreshing = false;
    }
    @SuppressWarnings("removal")
    private void setupDataSlots() {
        addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity != null ? blockEntity.inventoryVersion : 0; }
            @Override public void set(int v) { if (blockEntity != null) blockEntity.inventoryVersion = v; }
        });

        int toSync = Math.min(blockEntity.itemHandler.getSlots(), 150);
        for (int i = 0; i < toSync; i++) {
            final int idx = i;
            addDataSlot(new DataSlot() {
                @Override public int get() { return blockEntity != null && idx < blockEntity.meltingProgress.length ? blockEntity.meltingProgress[idx] : 0; }
                @Override public void set(int v) { if (blockEntity != null && idx < blockEntity.meltingProgress.length) blockEntity.meltingProgress[idx] = v; }
            });
            addDataSlot(new DataSlot() {
                @Override public int get() { return blockEntity != null && idx < blockEntity.meltingTime.length ? blockEntity.meltingTime[idx] : 0; }
                @Override public void set(int v) { if (blockEntity != null && idx < blockEntity.meltingTime.length) blockEntity.meltingTime[idx] = v; }
            });
            addDataSlot(new DataSlot() {
                @Override public int get() { return blockEntity != null && idx < blockEntity.meltingState.length ? blockEntity.meltingState[idx] : 0; }
                @Override public void set(int v) { if (blockEntity != null && idx < blockEntity.meltingState.length) blockEntity.meltingState[idx] = v; }
            });
        }
    }
    @SuppressWarnings("removal")
    private void createSmelteryInventory() {
        int sx = -70, sy = 12;
        for (int i = 0; i < 24; i++)
            addSlot(new SlotItemHandler(windowHandler, i, sx + (i % 3) * 22 + 5, sy + (i / 3) * 18 + 1));

        addSlot(new SlotItemHandler(bucketHandler, 0, 125, 46));
        addSlot(new SlotItemHandler(bucketHandler, 1, 125, 104) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return false; }
        });
    }

    private void createPlayerInventory(Inventory playerInv) {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(playerInv, i, 8 + i * 18, 196));
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 138 + i * 18));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, ModBlocks.SMELTERY_CONTROLLER.get());
    }
    @SuppressWarnings("removal")
    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level().isClientSide()) return;
        for (int i = 0; i < bucketHandler.getSlots(); i++) {
            ItemStack s = bucketHandler.getStackInSlot(i);
            if (!s.isEmpty()) player.drop(s, false);
        }
    }

    @Override
    public void broadcastChanges() {
        if (blockEntity != null && lastInventoryVersion != blockEntity.inventoryVersion) {
            lastInventoryVersion = blockEntity.inventoryVersion;
            refreshWindow();
        }
        super.broadcastChanges();
    }
    @SuppressWarnings("removal")
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return result;

        ItemStack stack = slot.getItem();
        result = stack.copy();

        int smeltSlots  = 24 + 2;
        int playerStart = smeltSlots;
        int playerEnd   = playerStart + 36;

        if (index < smeltSlots) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            boolean inserted = false;
            boolean isFluidContainer = stack.getItem() instanceof net.minecraft.world.item.BucketItem;
            if (isFluidContainer && moveItemStackTo(stack, smeltSlots - 2, smeltSlots - 1, false))
                inserted = true;

            if (!inserted) {
                for (int i = 0; i < blockEntity.itemHandler.getSlots() && !stack.isEmpty(); i++)
                    stack = blockEntity.itemHandler.insertItem(i, stack, false);
                if (stack.getCount() != result.getCount()) {
                    inserted = true;
                    if (blockEntity != null) blockEntity.inventoryVersion++;
                    refreshWindow();
                    broadcastChanges();
                }
            }
            if (!inserted) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else { slot.set(stack); slot.setChanged(); }
        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return result;
    }
}