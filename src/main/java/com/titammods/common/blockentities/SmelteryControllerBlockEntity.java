package com.titammods.common.blockentities;

import com.titammods.common.blockentities.multiblock.IDisplayFluidListener;
import com.titammods.common.blockentities.multiblock.SmelteryFluidHandler;
import com.titammods.common.blockentities.multiblock.SmelteryMultiblock;
import com.titammods.common.blocks.SmelteryControllerBlock;
import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.phys.AABB;
import com.titammods.common.blockentities.module.EntityMeltingModule;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.Nullable;

public class SmelteryControllerBlockEntity extends BlockEntity implements MenuProvider, IDisplayFluidListener {

    private SmelteryMultiblock multiblock;

    public SmelteryMultiblock getMultiblock() { return multiblock; }
    public BlockPos syncedMinInner = null;
    public BlockPos syncedMaxInner = null;
    private int  tickCounter = 0;
    private boolean isFormed = false;

    public final SmelteryFluidHandler fluidTank = new SmelteryFluidHandler();

    private EntityMeltingModule entityMeltingModule;

    public int inventoryVersion = 0;
    @SuppressWarnings("removal")
    public ItemStackHandler itemHandler = new ItemStackHandler(0) {
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override protected void onContentsChanged(int slot) { inventoryVersion++; setChanged(); }
    };

    public int[] meltingProgress = new int[0];
    public int[] meltingTime     = new int[0];
    public int[] meltingState    = new int[0];

    public int fuel       = 0;
    public int maxFuel    = 0;
    public int temperature = 0;

    public FluidStack currentFuel  = FluidStack.EMPTY;
    public int fuelCapacity        = 0;
    public FluidStack displayFluid = FluidStack.EMPTY;
    @SuppressWarnings("removal")
    public SmelteryControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMELTERY_CONTROLLER.get(), pos, state);
        entityMeltingModule = new EntityMeltingModule(
                new EntityMeltingModule.SmelteryParent() {
                    @Override public net.minecraft.world.level.Level getLevel() { return level; }
                    @Override public net.minecraft.core.BlockPos getBlockPos() { return worldPosition; }
                    @Override public boolean isFormed() { return isFormed; }
                    @Override public boolean hasFuel() { return !currentFuel.isEmpty() || fuel > 0; }
                },
                fluidTank,
                stack -> {
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        stack = itemHandler.insertItem(i, stack, false);
                        if (stack.isEmpty()) return ItemStack.EMPTY;
                    }
                    return stack;
                }
        );
    }

    @Override public FluidStack getDisplayFluid() { return displayFluid; }

    @Override
    public void notifyDisplayFluidUpdated(FluidStack fluid) {
        if (!FluidStack.isSameFluidSameComponents(this.displayFluid, fluid)
                || this.displayFluid.isEmpty() != fluid.isEmpty()) {
            this.displayFluid = fluid.isEmpty() ? FluidStack.EMPTY : fluid.copy();
            setChanged();
            if (level != null)
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.hephaestus.smeltery_controller");
    }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new com.titammods.menu.SmelteryMenu(id, inventory, this);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) return;

        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            checkMultiblockStructure();
            if (isFormed) {
                updateFuelInfo();
                updateDisplayFluidSync();
                processAlloying();
                if (multiblock != null && multiblock.minInner != null) {
                    BlockPos mn = multiblock.minInner, mx = multiblock.maxInner.offset(1, 1, 1);
                    AABB inner = new AABB(mn.getX(), mn.getY(), mn.getZ(),
                            mx.getX(), mx.getY(), mx.getZ());
                    entityMeltingModule.interactWithEntities(inner);
                }
            } else {
                this.currentFuel  = FluidStack.EMPTY;
                this.fuelCapacity = 0;
                setChanged();
            }
            level.sendBlockUpdated(pos, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }

        if (isFormed) {
            if (multiblock == null) checkMultiblockStructure();
            if (isFormed && multiblock != null) handleHeating();
        }
    }

    private void updateDisplayFluidSync() {
        FluidStack first = fluidTank.getFluidInTank(0);
        if (!FluidStack.isSameFluidSameComponents(first, displayFluid)
                || first.isEmpty() != displayFluid.isEmpty()) {
            FluidStack newDisplay = first.isEmpty() ? FluidStack.EMPTY : new FluidStack(first.getFluid(), 1000);
            notifyDisplayFluidUpdated(newDisplay);
            if (multiblock != null) {
                for (BlockPos wallPos : multiblock.walls) {
                    if (level.getBlockEntity(wallPos) instanceof IDisplayFluidListener listener)
                        listener.notifyDisplayFluidUpdated(newDisplay);
                }
            }
        }
    }

    @SuppressWarnings("removal")
    private void updateFuelInfo() {
        if (multiblock == null || multiblock.tanks.isEmpty()) {
            currentFuel = FluidStack.EMPTY;
            fuelCapacity = 0;
            return;
        }

        int totalAmount = 0, totalCapacity = 0;
        FluidStack activeFuel = FluidStack.EMPTY;

        for (BlockPos tankPos : multiblock.tanks) {
            if (level.getBlockEntity(tankPos) instanceof SearedTankBlockEntity tank) {
                var fluid = tank.getFluidTank().getFluid();
                int cap   = tank.getFluidTank().getCapacity();
                if (!fluid.isEmpty() && getTemperatureForFuel(fluid) > 0) {
                    if (activeFuel.isEmpty()) activeFuel = fluid.copy();
                    if (fluid.is(activeFuel.getFluid())) {
                        totalAmount   += fluid.getAmount();
                        totalCapacity += cap;
                    }
                } else if (fluid.isEmpty()) {
                    totalCapacity += cap;
                }
            }
        }

        if (activeFuel.isEmpty()) {
            currentFuel = FluidStack.EMPTY;
        } else {
            activeFuel.setAmount(totalAmount);
            currentFuel = activeFuel;
        }
        fuelCapacity = totalCapacity;
        setChanged();
    }
    @SuppressWarnings("removal")
    private void consumeFuelFromTanks() {
        if (multiblock == null) return;
        for (BlockPos tankPos : multiblock.tanks) {
            if (level.getBlockEntity(tankPos) instanceof SearedTankBlockEntity tank) {
                var handler = tank.getFluidTank();
                FluidStack sim = handler.drain(50, IFluidHandler.FluidAction.SIMULATE);
                if (!sim.isEmpty() && getTemperatureForFuel(sim) > 0) {
                    int toDrain = Math.min(50, sim.getAmount());
                    handler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
                    this.fuel     += toDrain * 2;
                    this.maxFuel   = this.fuel;
                    this.temperature = getTemperatureForFuel(sim);
                    updateFuelInfo();
                    setChanged();
                    return;
                }
            }
        }
    }

    private int getTemperatureForFuel(FluidStack fluid) {
        if (fluid.isEmpty() || !(level instanceof ServerLevel sl)) return 0;
        int max = 0;
        for (var h : sl.getServer().getRecipeManager().recipeMap().byType(ModRecipes.MELTING_TYPE.get())) {
            var recipe = h.value();
            if (recipe.fuel().getFluid().isSame(fluid.getFluid())) {
                max = Math.max(max, recipe.temperature());
            }
        }
        return max;
    }

    @SuppressWarnings("removal")
    private void handleHeating() {
        BlockState cur = getBlockState();
        boolean wasActive = cur.getValue(SmelteryControllerBlock.ACTIVE);
        boolean isActive  = false;

        updateMeltingTimes();

        boolean hasValidRecipe = false;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (meltingState[i] == 0 && !itemHandler.getStackInSlot(i).isEmpty()) {
                hasValidRecipe = true;
                break;
            }
        }

        if (hasValidRecipe) {
            if (fuel <= 0) consumeFuelFromTanks();
            if (fuel > 0) {
                isActive = true;
                processMelting();
            }
        }

        if (fuel > 0) {
            fuel--;
            if (fuel == 0) {
                temperature = 0;
                setChanged();
            }
        }

        if (wasActive != isActive)
            level.setBlockAndUpdate(worldPosition, cur.setValue(SmelteryControllerBlock.ACTIVE, isActive));
    }
    @SuppressWarnings("removal")
    private void updateMeltingTimes() {
        checkInventorySync();
        boolean changed = false;
        if (!(level instanceof ServerLevel sl)) return;

        int availTemp = temperature > 0 ? temperature : (fuel <= 0 && !currentFuel.isEmpty() ? getTemperatureForFuel(currentFuel) : 0);

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                if (meltingTime[i] != 0 || meltingProgress[i] != 0 || meltingState[i] != 0) {
                    meltingTime[i] = meltingProgress[i] = meltingState[i] = 0;
                    changed = true;
                }
            } else {
                var holder = sl.getServer().getRecipeManager().recipeMap()
                        .byType(ModRecipes.MELTING_TYPE.get()).stream()
                        .filter(h -> h.value().input().test(stack))
                        .findFirst().orElse(null);

                if (holder != null) {
                    var recipe = holder.value();
                    int reqTime = recipe.time();
                    int state   = 0;
                    if (availTemp < recipe.temperature()) {
                        state = 2;
                    } else {
                        FluidStack out = recipe.output().copy();
                        if (fluidTank.fill(out, IFluidHandler.FluidAction.SIMULATE) < out.getAmount())
                            state = 3;
                    }
                    if (meltingTime[i] != reqTime || meltingState[i] != state) {
                        meltingTime[i]  = reqTime;
                        meltingState[i] = state;
                        changed = true;
                    }
                } else {
                    if (meltingTime[i] != 0 || meltingState[i] != 1) {
                        meltingTime[i] = meltingProgress[i] = 0;
                        meltingState[i] = 1;
                        changed = true;
                    }
                }
            }
        }
        if (changed) setChanged();
    }
    @SuppressWarnings("removal")
    private void processMelting() {
        boolean changed = false;
        checkInventorySync();
        if (!(level instanceof ServerLevel sl)) return;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty() || meltingTime[i] <= 0 || meltingState[i] != 0) continue;

            meltingProgress[i]++;
            if (meltingProgress[i] >= meltingTime[i]) {
                var holder = sl.getServer().getRecipeManager().recipeMap()
                        .byType(ModRecipes.MELTING_TYPE.get()).stream()
                        .filter(h -> h.value().input().test(stack))
                        .findFirst().orElse(null);

                if (holder != null) {
                    FluidStack out = holder.value().scaledOutput(stack).copy();
                    fluidTank.fill(out, IFluidHandler.FluidAction.EXECUTE);
                    itemHandler.extractItem(i, 1, false);
                }
                meltingProgress[i] = 0;
            }
            changed = true;
        }
        if (changed) setChanged();
    }

    @SuppressWarnings("removal")
    private void processAlloying() {
        if (!(level instanceof ServerLevel sl)) return;
        boolean changed = false;

        for (var holder : sl.getServer().getRecipeManager().recipeMap()
                .byType(ModRecipes.ALLOY_TYPE.get())) {
            var recipe = holder.value();
            int availTemp = temperature > 0 ? temperature : getTemperatureForFuel(currentFuel);
            if (availTemp < recipe.temperature()) continue;

            boolean hasAll = true;
            for (var input : recipe.inputFluids()) {
                if (!hasFluid(input)) { hasAll = false; break; }
            }
            if (!hasAll) continue;

            FluidStack out    = recipe.output().copy();
            int filled        = fluidTank.fill(out, IFluidHandler.FluidAction.SIMULATE);
            if (filled < out.getAmount()) continue;

            for (var input : recipe.inputFluids()) drainFluid(input);
            fluidTank.fill(out, IFluidHandler.FluidAction.EXECUTE);
            changed = true;
            break;
        }

        if (changed) {
            updateDisplayFluidSync();
            setChanged();
        }
    }

    private boolean hasFluid(FluidStack req) {
        int found = 0;
        for (FluidStack f : fluidTank.getFluids())
            if (FluidStack.isSameFluidSameComponents(f, req)) found += f.getAmount();
        return found >= req.getAmount();
    }

    private void drainFluid(FluidStack req) {
        int left = req.getAmount();
        for (int i = 0; i < fluidTank.getFluids().size() && left > 0; i++) {
            FluidStack f = fluidTank.getFluids().get(i);
            if (FluidStack.isSameFluidSameComponents(f, req)) {
                int drain = Math.min(left, f.getAmount());
                f.shrink(drain);
                left -= drain;
                if (f.isEmpty()) { fluidTank.getFluids().remove(i--); }
            }
        }
    }

    @SuppressWarnings("removal")
    private void checkMultiblockStructure() {
        if (level == null) return;
        BlockState cur = getBlockState();
        if (multiblock == null) multiblock = new SmelteryMultiblock(level, worldPosition);
        Direction facing = cur.getValue(BlockStateProperties.HORIZONTAL_FACING);
        multiblock.scanStructure(facing);

        if (multiblock.isValid) {
            int volume = multiblock.internalVolume;
            if (!isFormed || itemHandler.getSlots() != volume) {
                isFormed = true;
                fluidTank.setCapacity(volume * 8000);
                resizeInventory(volume);
                syncedMinInner = multiblock.minInner;
                syncedMaxInner = multiblock.maxInner;
                setChanged();
                level.setBlockAndUpdate(worldPosition, cur.setValue(SmelteryControllerBlock.IN_STRUCTURE, true));
                level.sendBlockUpdated(worldPosition, cur, cur.setValue(SmelteryControllerBlock.IN_STRUCTURE, true), Block.UPDATE_ALL);
                linkIOBlocks(true);
            }
        } else if (isFormed) {
            isFormed       = false;
            fuel           = 0;
            maxFuel        = 0;
            temperature    = 0;
            currentFuel    = FluidStack.EMPTY;
            fuelCapacity   = 0;
            fluidTank.setCapacity(0);
            syncedMinInner = null;
            syncedMaxInner = null;
            setChanged();
            level.setBlockAndUpdate(worldPosition,
                    cur.setValue(SmelteryControllerBlock.IN_STRUCTURE, false)
                            .setValue(SmelteryControllerBlock.ACTIVE, false));
            linkIOBlocks(false);
        }
    }

    private void linkIOBlocks(boolean link) {
        if (multiblock == null || level == null) return;
        for (BlockPos pos : multiblock.walls) updateIOBlock(pos, link);
        for (BlockPos pos : multiblock.floor) updateIOBlock(pos, link);
    }

    @SuppressWarnings("unchecked")
    private void updateIOBlock(BlockPos pos, boolean link) {
        BlockState st = level.getBlockState(pos);
        var prop = st.getBlock().getStateDefinition().getProperty("in_structure");
        if (prop instanceof net.minecraft.world.level.block.state.properties.BooleanProperty bp) {
            if (st.getValue(bp) != link)
                level.setBlock(pos, st.setValue(bp, link), Block.UPDATE_ALL);
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SearedDrainBlockEntity drain) drain.setControllerPos(link ? worldPosition : null);
        if (be instanceof SearedChuteBlockEntity chute) chute.setControllerPos(link ? worldPosition : null);
    }

    @SuppressWarnings("removal")
    private void checkInventorySync() {
        if (meltingProgress.length != itemHandler.getSlots())
            resizeInventory(itemHandler.getSlots());
    }
    @SuppressWarnings("removal")
    private void resizeInventory(int newSize) {
        ItemStackHandler newHandler = new ItemStackHandler(newSize) {
            @Override public int getSlotLimit(int slot) { return 1; }
            @Override protected void onContentsChanged(int slot) { inventoryVersion++; setChanged(); }
        };
        int[] np = new int[newSize], nt = new int[newSize], ns = new int[newSize];

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (i < newSize) {
                    newHandler.setStackInSlot(i, stack);
                    if (i < meltingProgress.length) { np[i] = meltingProgress[i]; nt[i] = meltingTime[i]; ns[i] = meltingState[i]; }
                } else if (level != null) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), stack);
                }
            }
        }
        itemHandler     = newHandler;
        inventoryVersion++;
        meltingProgress = np;
        meltingTime     = nt;
        meltingState    = ns;
    }

    @SuppressWarnings("removal")
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("slot_count", itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++)
            output.store("slot_" + i, ItemStack.OPTIONAL_CODEC, itemHandler.getStackInSlot(i));

        output.putInt("fuel",          fuel);
        output.putInt("max_fuel",      maxFuel);
        output.putInt("temperature",   temperature);
        output.putBoolean("is_formed", isFormed);
        output.putInt("fuel_capacity", fuelCapacity);

        output.putInt("progress_count", meltingProgress.length);
        for (int i = 0; i < meltingProgress.length; i++) {
            output.putInt("mp_" + i, meltingProgress[i]);
            output.putInt("mt_" + i, meltingTime[i]);
            output.putInt("ms_" + i, meltingState[i]);
        }

        if (!currentFuel.isEmpty()) {
            output.putString("fuel_fluid",  BuiltInRegistries.FLUID.getKey(currentFuel.getFluid()).toString());
            output.putInt(   "fuel_amount", currentFuel.getAmount());
        }

        fluidTank.save(output);

        if (!displayFluid.isEmpty())
            output.putString("display_fluid", BuiltInRegistries.FLUID.getKey(displayFluid.getFluid()).toString());

        if (multiblock != null && multiblock.isValid && multiblock.minInner != null) {
            output.putInt("mb_minX", multiblock.minInner.getX());
            output.putInt("mb_minY", multiblock.minInner.getY());
            output.putInt("mb_minZ", multiblock.minInner.getZ());
            output.putInt("mb_maxX", multiblock.maxInner.getX());
            output.putInt("mb_maxY", multiblock.maxInner.getY());
            output.putInt("mb_maxZ", multiblock.maxInner.getZ());
        }
    }
    @SuppressWarnings("removal")
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        int slotCount = input.getIntOr("slot_count", 0);
        resizeInventory(slotCount);
        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = input.read("slot_" + i, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
            itemHandler.setStackInSlot(i, stack);
        }

        fuel         = input.getIntOr("fuel",          0);
        maxFuel      = input.getIntOr("max_fuel",      0);
        temperature  = input.getIntOr("temperature",   0);
        isFormed     = input.getBooleanOr("is_formed", false);
        fuelCapacity = input.getIntOr("fuel_capacity", 0);

        int pc = input.getIntOr("progress_count", 0);
        meltingProgress = new int[pc];
        meltingTime     = new int[pc];
        meltingState    = new int[pc];
        for (int i = 0; i < pc; i++) {
            meltingProgress[i] = input.getIntOr("mp_" + i, 0);
            meltingTime[i]     = input.getIntOr("mt_" + i, 0);
            meltingState[i]    = input.getIntOr("ms_" + i, 0);
        }

        String fuelId = input.getStringOr("fuel_fluid", "");
        if (!fuelId.isEmpty()) {
            Fluid f = BuiltInRegistries.FLUID.getValue(Identifier.parse(fuelId));
            currentFuel = (f != null && !f.isSame(Fluids.EMPTY))
                    ? new FluidStack(f, input.getIntOr("fuel_amount", 0))
                    : FluidStack.EMPTY;
        }

        fluidTank.load(input);

        if (isFormed) fluidTank.setCapacity(itemHandler.getSlots() * 8000);

        int mbMinY = input.getIntOr("mb_minY", Integer.MIN_VALUE);
        if (mbMinY != Integer.MIN_VALUE) {
            syncedMinInner = new BlockPos(
                    input.getIntOr("mb_minX", 0), mbMinY, input.getIntOr("mb_minZ", 0));
            syncedMaxInner = new BlockPos(
                    input.getIntOr("mb_maxX", 0), input.getIntOr("mb_maxY", 0),
                    input.getIntOr("mb_maxZ", 0));
        } else { syncedMinInner = null; syncedMaxInner = null; }

        String displayId = input.getStringOr("display_fluid", "");
        if (!displayId.isEmpty()) {
            Fluid f = BuiltInRegistries.FLUID.getValue(Identifier.parse(displayId));
            displayFluid = (f != null && !f.isSame(Fluids.EMPTY)) ? new FluidStack(f, 1000) : FluidStack.EMPTY;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide() && isFormed) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}