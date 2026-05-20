package com.titammods.block;

import com.titammods.menu.MelterMenu;
import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;


public class MelterBlockEntity extends BlockEntity implements MenuProvider {

    public int fuel = 0;
    public int maxFuel = 0;
    public int temperature = 0;
    public int[] progress = new int[3];
    public int[] maxProgress = new int[3];
    public int[] state = new int[3];

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
        @Override
        public int getSlotLimit(int slot) { return 1; }
    };

    public final IItemHandler externalItemHandler = new IItemHandler() {
        @Override public int getSlots() { return 3; }
        @Nonnull @Override public ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return inventory.insertItem(slot, stack, simulate);
        }
        @Nonnull @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return inventory.isItemValid(slot, stack); }
    };

    public final FluidTank tank = new FluidTank(2700) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null) {
                updateLight(MelterBlockEntity.this, this);
                if (!level.isClientSide) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                } else {
                    requestModelDataUpdate();
                }
            }
        }
    };

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> MelterBlockEntity.this.fuel;
                case 1 -> MelterBlockEntity.this.maxFuel;
                case 2 -> MelterBlockEntity.this.temperature;
                case 3 -> MelterBlockEntity.this.progress[0];
                case 4 -> MelterBlockEntity.this.progress[1];
                case 5 -> MelterBlockEntity.this.progress[2];
                case 6 -> MelterBlockEntity.this.maxProgress[0];
                case 7 -> MelterBlockEntity.this.maxProgress[1];
                case 8 -> MelterBlockEntity.this.maxProgress[2];
                case 9 -> MelterBlockEntity.this.state[0];
                case 10 -> MelterBlockEntity.this.state[1];
                case 11 -> MelterBlockEntity.this.state[2];
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> MelterBlockEntity.this.fuel = value;
                case 1 -> MelterBlockEntity.this.maxFuel = value;
                case 2 -> MelterBlockEntity.this.temperature = value;
                case 3 -> MelterBlockEntity.this.progress[0] = value;
                case 4 -> MelterBlockEntity.this.progress[1] = value;
                case 5 -> MelterBlockEntity.this.progress[2] = value;
                case 6 -> MelterBlockEntity.this.maxProgress[0] = value;
                case 7 -> MelterBlockEntity.this.maxProgress[1] = value;
                case 8 -> MelterBlockEntity.this.maxProgress[2] = value;
                case 9 -> MelterBlockEntity.this.state[0] = value;
                case 10 -> MelterBlockEntity.this.state[1] = value;
                case 11 -> MelterBlockEntity.this.state[2] = value;
            }
        }

        @Override
        public int getCount() {
            return 12;
        }
    };

    public MelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MELTER.get(), pos, state);
    }

    @Override
    public Component getDisplayName() { return Component.translatable("block.hephaestus.seared_melter"); }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MelterMenu(containerId, playerInventory, this, this.data);
    }

    public static void updateLight(BlockEntity be, FluidTank tank) {
        if (be.getLevel() != null && !be.getLevel().isClientSide) {
            FluidStack fluid = tank.getFluid();
            int light = fluid.isEmpty() ? 0 : fluid.getFluid().getFluidType().getLightLevel(fluid);
            BlockState state = be.getBlockState();
            if (state.hasProperty(MelterBlock.LIGHT) && state.getValue(MelterBlock.LIGHT) != light) {
                be.getLevel().setBlock(be.getBlockPos(), state.setValue(MelterBlock.LIGHT, light), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.put("tank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("fuel", fuel);
        tag.putInt("maxFuel", maxFuel);
        tag.putInt("temperature", temperature);
        tag.putIntArray("state", state);
        tag.putIntArray("progress", progress);
        tag.putIntArray("maxProgress", maxProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("inventory")) inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        if(tag.contains("tank")) tank.readFromNBT(registries, tag.getCompound("tank"));
        fuel = tag.getInt("fuel");
        maxFuel = tag.getInt("maxFuel");
        temperature = tag.getInt("temperature");

        int[] savedState = tag.getIntArray("state");
        if (savedState.length == 3) state = savedState;

        int[] savedProgress = tag.getIntArray("progress");
        if (savedProgress.length == 3) progress = savedProgress;

        int[] savedMaxProgress = tag.getIntArray("maxProgress");
        if (savedMaxProgress.length == 3) maxProgress = savedMaxProgress;

        updateLight(this, tank);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        if (this.level != null && this.level.isClientSide) {
            requestModelDataUpdate();
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private int getTemperatureForFuel(FluidStack fluid, Level level) {
        if (fluid.isEmpty()) return 0;
        int maxTemp = 0;
        for (var holder : level.getRecipeManager().getAllRecipesFor(ModRecipes.MELTING_TYPE.get())) {
            ModRecipes.MeltingRecipe recipe = holder.value();
            if (recipe.fuel().getFluid().isSame(fluid.getFluid())) {
                maxTemp = Math.max(maxTemp, recipe.temperature());
            }
        }
        return maxTemp;
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide) return;
        boolean isDirty = false;
        boolean hasItemToMelt = false;

        int currentAvailableHeat = this.temperature;
        if (this.fuel <= 0) {
            currentAvailableHeat = 0;
            IFluidHandler tankBelow = level.getCapability(
                    net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                    pos.below(), Direction.UP);
            if (tankBelow != null) {
                FluidStack sim = tankBelow.drain(1, IFluidHandler.FluidAction.SIMULATE);
                if (!sim.isEmpty()) {
                    currentAvailableHeat = getTemperatureForFuel(sim, level);
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack.isEmpty()) {
                if (progress[i] != 0 || maxProgress[i] != 0 || this.state[i] != 0) {
                    progress[i] = 0; maxProgress[i] = 0; this.state[i] = 0;
                    isDirty = true;
                }
                continue;
            }

            var recipeHolder = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.MELTING_TYPE.get(), new SingleRecipeInput(stack), level)
                    .orElse(null);
            if (recipeHolder == null) {
                if (this.state[i] != 3) {
                    progress[i] = 0; maxProgress[i] = 100; this.state[i] = 3;
                    isDirty = true;
                }
                continue;
            }

            ModRecipes.MeltingRecipe recipe = recipeHolder.value();
            if (maxProgress[i] != recipe.time()) {
                maxProgress[i] = recipe.time();
                isDirty = true;
            }

            if (recipe.temperature() > currentAvailableHeat) {
                if (this.state[i] != 4) {
                    this.state[i] = 4;
                    isDirty = true;
                }
                if (progress[i] > 0) {
                    progress[i]--;
                    isDirty = true;
                }
                continue;
            }

            FluidStack output = recipe.output().copy();
            boolean canOutput = tank.fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount();

            if (canOutput) {
                if (progress[i] >= maxProgress[i]) {
                    inventory.extractItem(i, 1, false);
                    tank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                    progress[i] = 0;
                    this.state[i] = 0;
                    isDirty = true;
                } else {
                    if (this.state[i] != 1) { this.state[i] = 1; isDirty = true; }
                    hasItemToMelt = true;
                    if (this.fuel > 0) {
                        progress[i]++;
                        isDirty = true;
                    } else if (progress[i] > 0) {
                        progress[i]--;
                        isDirty = true;
                    }
                }
            } else {
                if (progress[i] >= maxProgress[i]) {
                    if (this.state[i] != 2) { this.state[i] = 2; isDirty = true; }
                } else {
                    if (this.state[i] != 1) { this.state[i] = 1; isDirty = true; }
                    hasItemToMelt = true;
                    if (this.fuel > 0) {
                        progress[i]++;
                        isDirty = true;
                    } else if (progress[i] > 0) {
                        progress[i]--;
                        isDirty = true;
                    }
                }
            }
        }

        if (this.fuel > 0) {
            this.fuel--;
            isDirty = true;
            if (this.fuel == 0) {
                this.temperature = 0;
            }
        } else if (hasItemToMelt) {
            IFluidHandler tankBelow = level.getCapability(
                    net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                    pos.below(), Direction.UP);
            if (tankBelow != null) {
                FluidStack sim = tankBelow.drain(50, IFluidHandler.FluidAction.SIMULATE);
                if (!sim.isEmpty()) {
                    int heat = getTemperatureForFuel(sim, level);
                    if (heat > 0) {
                        tankBelow.drain(50, IFluidHandler.FluidAction.EXECUTE);
                        this.fuel = 200;
                        this.maxFuel = 200;
                        this.temperature = heat;
                        isDirty = true;
                    }
                }
            }
        }

        if (isDirty) setChanged(level, pos, blockState);

        boolean isBurning = this.fuel > 0;
        if (blockState.hasProperty(SearedMachineBlock.ACTIVE)
                && blockState.getValue(SearedMachineBlock.ACTIVE) != isBurning) {
            level.setBlockAndUpdate(pos, blockState.setValue(SearedMachineBlock.ACTIVE, isBurning));
        }
    }
}