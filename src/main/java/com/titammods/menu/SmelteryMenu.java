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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("removal")
public class SmelteryMenu extends AbstractContainerMenu {

    public final SmelteryControllerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    private int currentRowOffset    = 0;
    private boolean isProcessingBucket = false;

    public static final int MAX_COLS     = 4;
    public static final int MAX_VIS_ROWS = 8;
    public static final int SLOT_W       = 22;
    public static final int SLOT_H       = 18;
    public static final int SLOT_BASE_X  = -17;
    public static final int SLOT_BASE_Y  = 25;

    public static int calcColumns(int slots) {
        return Math.min(MAX_COLS, (slots + 6) / 7);
    }

    public static int calcVisibleRows(int totalRows) {
        return Math.min(totalRows, MAX_VIS_ROWS);
    }

    public int getColumns() {
        return blockEntity != null ? calcColumns(blockEntity.itemHandler.getSlots()) : 1;
    }

    public int getTotalRows() {
        int slots = blockEntity != null ? blockEntity.itemHandler.getSlots() : 0;
        int cols  = calcColumns(Math.max(slots, 1));
        return (int) Math.ceil((double) slots / cols);
    }

    public int getVisibleRows() {
        return calcVisibleRows(getTotalRows());
    }

    public int getWindowSize() {
        return getColumns() * getVisibleRows();
    }

    @SuppressWarnings("removal")
    private final net.neoforged.neoforge.items.IItemHandlerModifiable scrollWrapper = new net.neoforged.neoforge.items.IItemHandlerModifiable() {
        @Override public int getSlots() { return MAX_COLS * MAX_VIS_ROWS; }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            int ri = realIdx(slot);
            if (ri >= 0) blockEntity.itemHandler.setStackInSlot(ri, stack);
        }

        private int realIdx(int slot) {
            if (blockEntity == null) return -1;
            int total = blockEntity.itemHandler.getSlots();
            int cols  = calcColumns(total);
            int col   = slot % MAX_COLS;
            int row   = slot / MAX_COLS;
            if (col >= cols) return -1;
            int ri = (currentRowOffset + row) * cols + col;
            return ri < total ? ri : -1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            int ri = realIdx(slot);
            return ri >= 0 ? blockEntity.itemHandler.getStackInSlot(ri) : ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            int ri = realIdx(slot);
            return ri >= 0 ? blockEntity.itemHandler.insertItem(ri, stack, simulate) : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int ri = realIdx(slot);
            return ri >= 0 ? blockEntity.itemHandler.extractItem(ri, amount, simulate) : ItemStack.EMPTY;
        }

        @Override public int getSlotLimit(int slot) { return 1; }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            int ri = realIdx(slot);
            return ri >= 0 && blockEntity.itemHandler.isItemValid(ri, stack);
        }
    };

    @SuppressWarnings("removal")
    public final ItemStackHandler bucketHandler = new ItemStackHandler(2) {
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override protected void onContentsChanged(int slot) {
            if (slot == 0 && !isProcessingBucket) processBucket();
        }
    };

    public SmelteryMenu(int id, Inventory inv, net.minecraft.network.RegistryFriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public SmelteryMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenus.SMELTERY_MENU.get(), id);
        this.blockEntity = (SmelteryControllerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(inv.player.level(), entity.getBlockPos());

        createSmelteryInventory();
        createPlayerInventory(inv);
        setupDataSlots();
    }

    @SuppressWarnings("removal")
    private void createSmelteryInventory() {
        int total = blockEntity != null ? blockEntity.itemHandler.getSlots() : 0;

        for (int i = 0; i < MAX_COLS * MAX_VIS_ROWS; i++) {
            final int slotIdx = i;
            int col = i % MAX_COLS;
            int row = i / MAX_COLS;
            int sx  = SLOT_BASE_X - col * SLOT_W;
            int sy  = SLOT_BASE_Y + row * SLOT_H;
            addSlot(new SlotItemHandler(scrollWrapper, i, sx, sy) {
                @Override
                public boolean isActive() {
                    if (blockEntity == null) return false;
                    int t    = blockEntity.itemHandler.getSlots();
                    int cols = calcColumns(t);
                    int col  = slotIdx % MAX_COLS;
                    int row  = slotIdx / MAX_COLS;
                    if (col >= cols) return false;
                    int visRows = calcVisibleRows((int) Math.ceil((double) t / cols));
                    if (row >= visRows) return false;
                    int realIdx = (currentRowOffset + row) * cols + col;
                    return realIdx < t;
                }
            });
        }

        for (int ri = 0; ri < total; ri++) {
            final int realIdx = ri;
            addSlot(new SlotItemHandler(blockEntity.itemHandler, ri, -9999, -9999) {
                @Override public boolean isActive() { return false; }
                @Override public boolean mayPickup(Player player) {
                    return true;
                }
            });
        }

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

    public void updateScrollOffset(int rowOffset) {
        this.currentRowOffset = rowOffset;
    }

    public int getCurrentRowOffset() { return currentRowOffset; }

    @SuppressWarnings("removal")
    private void setupDataSlots() {
        addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity != null ? blockEntity.inventoryVersion : 0; }
            @Override public void set(int v) { if (blockEntity != null) blockEntity.inventoryVersion = v; }
        });
        int toSync = Math.min(blockEntity != null ? blockEntity.itemHandler.getSlots() : 0, 150);
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
    private void processBucket() {
        if (blockEntity == null || blockEntity.getLevel() == null
                || blockEntity.getLevel().isClientSide()) return;
        ItemStack input = bucketHandler.getStackInSlot(0);
        if (input.isEmpty()) return;
        isProcessingBucket = true;
        FluidActionResult er = FluidUtil.tryEmptyContainer(input, blockEntity.fluidTank, Integer.MAX_VALUE, null, false);
        if (er.isSuccess() && bucketHandler.insertItem(1, er.getResult(), true).isEmpty()) {
            er = FluidUtil.tryEmptyContainer(input, blockEntity.fluidTank, Integer.MAX_VALUE, null, true);
            bucketHandler.extractItem(0, 1, false);
            bucketHandler.insertItem(1, er.getResult(), false);
            blockEntity.setChanged();
            isProcessingBucket = false;
            return;
        }
        FluidActionResult fr = FluidUtil.tryFillContainer(input, blockEntity.fluidTank, Integer.MAX_VALUE, null, false);
        if (fr.isSuccess() && bucketHandler.insertItem(1, fr.getResult(), true).isEmpty()) {
            fr = FluidUtil.tryFillContainer(input, blockEntity.fluidTank, Integer.MAX_VALUE, null, true);
            bucketHandler.extractItem(0, 1, false);
            bucketHandler.insertItem(1, fr.getResult(), false);
            blockEntity.setChanged();
        }
        isProcessingBucket = false;
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

    @SuppressWarnings("removal")
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return result;

        ItemStack stack = slot.getItem();
        result = stack.copy();

        int total       = blockEntity != null ? blockEntity.itemHandler.getSlots() : 0;
        int visSlots    = MAX_COLS * MAX_VIS_ROWS;
        int extraStart  = visSlots;
        int extraEnd    = extraStart + total;
        int bucketStart = extraEnd;
        int playerStart = bucketStart + 2;
        int playerEnd   = playerStart + 36;

        if (index < visSlots || (index >= extraStart && index < extraEnd)) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else if (index >= playerStart && index < playerEnd) {
            boolean inserted = false;
            if (stack.getItem() instanceof net.minecraft.world.item.BucketItem
                    && moveItemStackTo(stack, bucketStart, bucketStart + 1, false))
                inserted = true;
            if (!inserted && !moveItemStackTo(stack, extraStart, extraEnd, false))
                return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else { slot.set(stack); slot.setChanged(); }
        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return result;
    }
}