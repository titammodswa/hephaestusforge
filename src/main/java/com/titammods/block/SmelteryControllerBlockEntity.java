package com.titammods.block;

import com.titammods.block.multiblock.IDisplayFluidListener;
import com.titammods.block.multiblock.SmelteryFluidHandler;
import com.titammods.block.multiblock.SmelteryMultiblock;
import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nullable;

public class SmelteryControllerBlockEntity extends BlockEntity implements MenuProvider, IDisplayFluidListener {

    private static final boolean EXECUTE = true;
    private static final boolean SIMULATE = false;

    private SmelteryMultiblock multiblock;
    private int tickCounter = 0;
    private boolean isFormed = false;

    public final SmelteryFluidHandler fluidTank = new SmelteryFluidHandler();

    public int inventoryVersion = 0;

    public ItemStackHandler itemHandler = new ItemStackHandler(0) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
        @Override
        protected void onContentsChanged(int slot) {
            inventoryVersion++;
            setChanged();
        }
    };

    public int[] meltingProgress = new int[0];
    public int[] meltingTime = new int[0];
    public int[] meltingState = new int[0];

    public int fuel = 0;
    public int maxFuel = 0;
    public int temperature = 0;

    public FluidStack currentFuel = FluidStack.EMPTY;
    public int fuelCapacity = 0;

    public FluidStack displayFluid = FluidStack.EMPTY;

    public SmelteryControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMELTERY_CONTROLLER.get(), pos, state);
    }

    @Override
    public FluidStack getDisplayFluid() {
        return displayFluid;
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(com.titammods.client.model.ModelProperties.FLUID_STACK, displayFluid)
                .build();
    }

    @Override
    public void notifyDisplayFluidUpdated(FluidStack fluid) {
        if (!FluidStack.isSameFluidSameComponents(this.displayFluid, fluid) || this.displayFluid.isEmpty() != fluid.isEmpty()) {
            this.displayFluid = fluid.isEmpty() ? FluidStack.EMPTY : fluid.copy();
            this.setChanged();

            requestModelDataUpdate();

            if (this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.hephaestus.smeltery_controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new com.titammods.menu.SmelteryMenu(id, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Inventory", itemHandler.serializeNBT(provider));
        tag.putInt("Fuel", fuel);
        tag.putInt("MaxFuel", maxFuel);
        tag.putInt("Temperature", temperature);
        tag.putBoolean("IsFormed", isFormed);
        tag.putIntArray("MeltingProgress", meltingProgress);
        tag.putIntArray("MeltingTime", meltingTime);
        tag.putIntArray("MeltingState", meltingState);

        if (multiblock != null) {
            tag.putInt("InternalVolume", multiblock.internalVolume);
        }

        tag.putInt("FuelCapacity", fuelCapacity);
        if (!currentFuel.isEmpty()) {
            tag.putString("FuelFluid", BuiltInRegistries.FLUID.getKey(currentFuel.getFluid()).toString());
            tag.putInt("FuelAmount", currentFuel.getAmount());
        }

        tag.put("FluidTank", fluidTank.writeToNBT(new CompoundTag()));

        if (!displayFluid.isEmpty()) {
            tag.putString("DisplayFluidId", BuiltInRegistries.FLUID.getKey(displayFluid.getFluid()).toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        itemHandler.deserialize(tag);
        checkInventorySync();

        fuel = tag.getInt("Fuel").get();
        if (tag.getInt("MaxFuel").isPresent()) maxFuel = tag.getInt("MaxFuel").get();
        temperature = tag.getInt("Temperature").get();
        isFormed = tag.getBooleanOr("IsFormed", false);

        int[] savedProgress = tag.getIntArray("MeltingProgress").get();
        int[] savedTime = tag.getIntArray("MeltingTime").get();
        int[] savedState = tag.getIntArray("MeltingState").get();

        if (savedProgress.length == meltingProgress.length) this.meltingProgress = savedProgress;
        if (savedTime.length == meltingTime.length) this.meltingTime = savedTime;
        if (savedState.length == meltingState.length) this.meltingState = savedState;

        fuelCapacity = tag.getInt("FuelCapacity").get();
        if (tag.getString("FuelFluid").isPresent()) {
            Fluid fluid = BuiltInRegistries.FLUID.get(Identifier.parse(tag.getString("FuelFluid").get()));
            this.currentFuel = (fluid != Fluids.EMPTY) ? new FluidStack(fluid, tag.getInt("FuelAmount").get()) : FluidStack.EMPTY;
        } else {
            this.currentFuel = FluidStack.EMPTY;
        }

        if (tag.child("FluidTank").isPresent()) {
            fluidTank.readFromNBT(tag.child("FluidTank").get());
        }

        if (tag.contains("DisplayFluidId")) {
            Fluid fluid = BuiltInRegistries.FLUID.get(Identifier.parse(tag.getString("DisplayFluidId")));
            this.displayFluid = (fluid != Fluids.EMPTY) ? new FluidStack(fluid, 1000) : FluidStack.EMPTY;
        } else {
            this.displayFluid = FluidStack.EMPTY;
        }

        if (isFormed) {
            fluidTank.setCapacity(itemHandler.getSlots() * 8000);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);

        requestModelDataUpdate();

        if (this.level != null && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            checkMultiblockStructure();

            if (isFormed) {
                updateFuelInfo();
                updateDisplayFluidSync();
                processAlloying();
            } else {
                this.currentFuel = FluidStack.EMPTY;
                this.fuelCapacity = 0;
                this.setChanged();
            }
            level.sendBlockUpdated(pos, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }

        if (isFormed) {
            if (multiblock == null) checkMultiblockStructure();
            if (isFormed && multiblock != null) {
                handleHeating();
            }
        }
    }

    private void updateDisplayFluidSync() {
        FluidStack firstFluid = fluidTank.getFluidInTank(0);
        if (!FluidStack.isSameFluidSameComponents(firstFluid, this.displayFluid) || firstFluid.isEmpty() != this.displayFluid.isEmpty()) {
            FluidStack newDisplay = firstFluid.isEmpty() ? FluidStack.EMPTY : new FluidStack(firstFluid.getFluid(), 1000);

            this.notifyDisplayFluidUpdated(newDisplay);

            if (multiblock != null) {
                for (BlockPos wallPos : multiblock.walls) {
                    if (level.getBlockEntity(wallPos) instanceof IDisplayFluidListener listener) {
                        listener.notifyDisplayFluidUpdated(newDisplay);
                    }
                }
            }
        }
    }

    private void updateFuelInfo() {
        if (multiblock == null || multiblock.tanks.isEmpty()) {
            this.currentFuel = FluidStack.EMPTY;
            this.fuelCapacity = 0;
            return;
        }

        int totalAmount = 0;
        int totalCapacity = 0;
        FluidStack activeFuel = FluidStack.EMPTY;

        for (BlockPos tankPos : multiblock.tanks) {
            IFluidHandler tankHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, tankPos, null);
            if (tankHandler != null) {
                FluidStack fluidInTank = tankHandler.getFluidInTank(0);
                int cap = tankHandler.getTankCapacity(0);

                if (!fluidInTank.isEmpty() && getTemperatureForFuel(fluidInTank) > 0) {
                    if (activeFuel.isEmpty()) {
                        activeFuel = fluidInTank.copy();
                    }
                    if (fluidInTank.is(activeFuel.getFluid())) {
                        totalAmount += fluidInTank.getAmount();
                        totalCapacity += cap;
                    }
                } else if (fluidInTank.isEmpty()) {
                    totalCapacity += cap;
                }
            }
        }

        if (activeFuel.isEmpty()) {
            this.currentFuel = FluidStack.EMPTY;
        } else {
            activeFuel.setAmount(totalAmount);
            this.currentFuel = activeFuel;
        }

        this.fuelCapacity = totalCapacity;
        this.setChanged();
    }

    private void updateMeltingTimes() {
        checkInventorySync();
        boolean changed = false;

        int availableTemp = this.temperature;
        if (this.fuel <= 0 && !this.currentFuel.isEmpty()) {
            availableTemp = getTemperatureForFuel(this.currentFuel);
        }

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                if (meltingTime[i] != 0 || meltingProgress[i] != 0 || meltingState[i] != 0) {
                    meltingTime[i] = 0;
                    meltingProgress[i] = 0;
                    meltingState[i] = 0;
                    changed = true;
                }
            } else {
                var recipeHolder = level.getRecipeManager().getRecipeFor(
                        ModRecipes.MELTING_TYPE.get(),
                        new net.minecraft.world.item.crafting.SingleRecipeInput(stack),
                        level
                ).orElse(null);

                if (recipeHolder != null) {
                    var recipe = recipeHolder.value();
                    int reqTime = recipe.time();
                    int state = 0;

                    if (availableTemp < recipe.temperature()) {
                        state = 2;
                    } else {
                        FluidStack output = recipe.output().copy();
                        if (fluidTank.fill(output, SIMULATE) < output.getAmount()) {
                            state = 3;
                        }
                    }

                    if (meltingTime[i] != reqTime || meltingState[i] != state) {
                        meltingTime[i] = reqTime;
                        meltingState[i] = state;
                        changed = true;
                    }
                } else {
                    if (meltingTime[i] != 0 || meltingState[i] != 1) {
                        meltingTime[i] = 0;
                        meltingProgress[i] = 0;
                        meltingState[i] = 1;
                        changed = true;
                    }
                }
            }
        }
        if (changed) this.setChanged();
    }

    private void handleHeating() {
        BlockState currentState = getBlockState();
        boolean wasActive = currentState.getValue(SmelteryControllerBlock.ACTIVE);
        boolean isActiveNow = false;

        updateMeltingTimes();

        boolean hasValidRecipe = false;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (meltingState[i] == 0 && !itemHandler.getStackInSlot(i).isEmpty()) {
                hasValidRecipe = true;
                break;
            }
        }

        if (hasValidRecipe) {
            if (this.fuel <= 0) consumeFuelFromTanks();

            if (this.fuel > 0) {
                this.fuel--;
                isActiveNow = true;
                processMelting();
            }
        }

        if (wasActive != isActiveNow) {
            level.setBlockAndUpdate(worldPosition, currentState.setValue(SmelteryControllerBlock.ACTIVE, isActiveNow));
        }
    }

    private void processMelting() {
        boolean hasChanged = false;
        checkInventorySync();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty() || meltingTime[i] <= 0 || meltingState[i] != 0) continue;

            meltingProgress[i]++;
            if (meltingProgress[i] >= meltingTime[i]) {
                var recipeHolder = level.getRecipeManager().getRecipeFor(
                        ModRecipes.MELTING_TYPE.get(),
                        new net.minecraft.world.item.crafting.SingleRecipeInput(stack),
                        level
                ).orElse(null);

                if (recipeHolder != null) {
                    FluidStack output = recipeHolder.value().output().copy();
                    fluidTank.fill(output, EXECUTE);

                    itemHandler.extractItem(i, 1, false);
                }
                meltingProgress[i] = 0;
            }
            hasChanged = true;
        }
        if (hasChanged) this.setChanged();
    }

    private void processAlloying() {
        if (level == null || level.isClientSide) return;

        boolean changed = false;
        var alloyRecipes = level.getRecipeManager().getAllRecipesFor(com.titammods.setup.ModRecipes.ALLOY_TYPE.get());

        for (var holder : alloyRecipes) {
            com.titammods.recipe.AlloyRecipe recipe = holder.value();

            int availableTemp = this.temperature;
            if (this.fuel <= 0 && !this.currentFuel.isEmpty()) {
                availableTemp = getTemperatureForFuel(this.currentFuel);
            }
            if (availableTemp < recipe.temperature()) continue;

            boolean hasAllInputs = true;
            for (FluidStack input : recipe.inputs()) {
                if (!hasFluid(input)) {
                    hasAllInputs = false;
                    break;
                }
            }
            if (!hasAllInputs) continue;

            FluidStack output = recipe.output().copy();
            int filled = fluidTank.fill(output, SIMULATE);
            if (filled < output.getAmount()) continue;

            for (FluidStack input : recipe.inputs()) {
                drainFluid(input);
            }
            fluidTank.fill(output, EXECUTE);
            changed = true;

            break;
        }

        if (changed) {
            updateDisplayFluidSync();
            this.setChanged();
        }
    }

    private boolean hasFluid(FluidStack required) {
        int amountFound = 0;
        for (FluidStack fluid : fluidTank.getFluids()) {
            if (FluidStack.isSameFluidSameComponents(fluid, required)) {
                amountFound += fluid.getAmount();
            }
        }
        return amountFound >= required.getAmount();
    }

    private void drainFluid(FluidStack required) {
        int amountLeft = required.getAmount();
        for (int i = 0; i < fluidTank.getFluids().size(); i++) {
            FluidStack fluid = fluidTank.getFluids().get(i);
            if (FluidStack.isSameFluidSameComponents(fluid, required)) {
                int toDrain = Math.min(amountLeft, fluid.getAmount());
                fluid.shrink(toDrain);
                amountLeft -= toDrain;
                if (fluid.isEmpty()) {
                    fluidTank.getFluids().remove(i);
                    i--;
                }
                if (amountLeft <= 0) break;
            }
        }
    }

    private void consumeFuelFromTanks() {
        if (multiblock == null) return;
        for (BlockPos tankPos : multiblock.tanks) {
            IFluidHandler tankHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, tankPos, null);
            if (tankHandler != null) {
                FluidStack simulatedDrain = tankHandler.drain(50, SIMULATE);
                if (!simulatedDrain.isEmpty() && simulatedDrain.getAmount() > 0) {
                    int temp = getTemperatureForFuel(simulatedDrain);
                    if (temp > 0) {
                        int toDrain = Math.min(50, simulatedDrain.getAmount());
                        tankHandler.drain(toDrain, EXECUTE);
                        this.fuel += (toDrain * 2);
                        this.maxFuel = this.fuel;
                        this.temperature = temp;
                        updateFuelInfo();
                        this.setChanged();
                        return;
                    }
                }
            }
        }
    }

    private int getTemperatureForFuel(FluidStack fluid) {
        if (fluid.isEmpty() || level == null) return 0;
        int maxTemp = 0;
        for (var holder : level.getRecipeManager().getAllRecipesFor(ModRecipes.MELTING_TYPE.get())) {
            ModRecipes.MeltingRecipe recipe = holder.value();
            if (recipe.fuel().getFluid().isSame(fluid.getFluid())) {
                maxTemp = Math.max(maxTemp, recipe.temperature());
            }
        }
        return maxTemp;
    }

    private void checkMultiblockStructure() {
        BlockState currentState = getBlockState();
        if (multiblock == null) multiblock = new SmelteryMultiblock(level, worldPosition);
        Direction facing = currentState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        multiblock.scanStructure(facing);

        if (multiblock.isValid) {
            int volume = multiblock.internalVolume;
            if (!isFormed || itemHandler.getSlots() != volume) {
                isFormed = true;
                fluidTank.setCapacity(volume * 8000);
                resizeInventory(volume);

                this.setChanged();
                level.setBlockAndUpdate(worldPosition, currentState.setValue(SmelteryControllerBlock.IN_STRUCTURE, true));
                linkIOBlocks(true);
            }
        } else if (!multiblock.isValid && isFormed) {
            isFormed = false;
            fluidTank.setCapacity(0);
            this.fuel = 0;
            this.maxFuel = 0;
            this.temperature = 0;
            this.currentFuel = FluidStack.EMPTY;
            this.fuelCapacity = 0;

            this.setChanged();
            level.setBlockAndUpdate(worldPosition, currentState.setValue(SmelteryControllerBlock.IN_STRUCTURE, false).setValue(SmelteryControllerBlock.ACTIVE, false));
            linkIOBlocks(false);
        }
    }

    private void linkIOBlocks(boolean link) {
        if (multiblock == null || level == null) return;
        for (BlockPos pos : multiblock.walls) updateIOBlock(pos, link);
        for (BlockPos pos : multiblock.floor) updateIOBlock(pos, link);
    }

    private void updateIOBlock(BlockPos pos, boolean link) {
        BlockState state = level.getBlockState(pos);
        net.minecraft.world.level.block.state.properties.Property<?> prop = state.getBlock().getStateDefinition().getProperty("in_structure");
        if (prop instanceof net.minecraft.world.level.block.state.properties.BooleanProperty inStructureProp) {
            if (state.getValue(inStructureProp) != link) {
                level.setBlock(pos, state.setValue(inStructureProp, link), Block.UPDATE_ALL);
            }
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SearedDrainBlockEntity drain) drain.setControllerPos(link ? worldPosition : null);
        if (be instanceof SearedChuteBlockEntity chute) chute.setControllerPos(link ? worldPosition : null);
    }

    private void checkInventorySync() {
        if (meltingProgress.length != itemHandler.getSlots() || meltingTime.length != itemHandler.getSlots() || meltingState.length != itemHandler.getSlots()) {
            resizeInventory(itemHandler.getSlots());
        }
    }

    private void resizeInventory(int newSize) {
        if (newSize == itemHandler.getSlots() && meltingProgress.length == newSize) return;

        ItemStackHandler newHandler = new ItemStackHandler(newSize) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
            @Override
            protected void onContentsChanged(int slot) {
                inventoryVersion++;
                setChanged();
            }
        };

        int[] newProgress = new int[newSize];
        int[] newTime = new int[newSize];
        int[] newState = new int[newSize];

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (i < newSize) {
                    newHandler.setStackInSlot(i, stack);
                    newProgress[i] = (i < meltingProgress.length) ? meltingProgress[i] : 0;
                    newTime[i] = (i < meltingTime.length) ? meltingTime[i] : 0;
                    newState[i] = (i < meltingState.length) ? meltingState[i] : 0;
                } else {
                    net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), stack);
                }
            }
        }
        itemHandler = newHandler;
        inventoryVersion++;
        meltingProgress = newProgress;
        meltingTime = newTime;
        meltingState = newState;
    }
}
