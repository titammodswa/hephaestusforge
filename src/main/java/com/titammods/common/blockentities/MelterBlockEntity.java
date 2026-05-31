package com.titammods.common.blockentities;

import com.titammods.common.blocks.MelterBlock;
import com.titammods.common.blocks.SearedMachineBlock;
import com.titammods.menu.MelterMenu;
import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class MelterBlockEntity extends BlockEntity implements MenuProvider {

    public int fuel = 0;
    public int maxFuel = 0;
    public int temperature = 0;
    public int[] progress    = new int[3];
    public int[] maxProgress = new int[3];
    public int[] state       = new int[3];

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
        @Override public int getSlotLimit(int slot) { return 1; }
    };

    public final IItemHandler externalItemHandler = new IItemHandler() {
        @Override public int getSlots() { return 3; }
        @Nonnull @Override public ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { return inventory.insertItem(slot, stack, simulate); }
        @Nonnull @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return inventory.isItemValid(slot, stack); }
    };

    public final FluidTank tank = new FluidTank(2700) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null) {
                updateLight(MelterBlockEntity.this, this);
                if (!level.isClientSide()) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    };

    protected final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            return switch (i) {
                case 0 -> fuel; case 1 -> maxFuel; case 2 -> temperature;
                case 3 -> progress[0]; case 4 -> progress[1]; case 5 -> progress[2];
                case 6 -> maxProgress[0]; case 7 -> maxProgress[1]; case 8 -> maxProgress[2];
                case 9 -> state[0]; case 10 -> state[1]; case 11 -> state[2];
                default -> 0;
            };
        }
        @Override public void set(int i, int v) {
            switch (i) {
                case 0 -> fuel = v; case 1 -> maxFuel = v; case 2 -> temperature = v;
                case 3 -> progress[0] = v; case 4 -> progress[1] = v; case 5 -> progress[2] = v;
                case 6 -> maxProgress[0] = v; case 7 -> maxProgress[1] = v; case 8 -> maxProgress[2] = v;
                case 9 -> state[0] = v; case 10 -> state[1] = v; case 11 -> state[2] = v;
            }
        }
        @Override public int getCount() { return 12; }
    };

    public MelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MELTER.get(), pos, state);
    }

    @Override public Component getDisplayName() { return Component.translatable("block.hephaestus.seared_melter"); }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new MelterMenu(id, inv, this, this.data);
    }


    public static void updateLight(BlockEntity be, FluidTank tank) {
        if (be.getLevel() != null && !be.getLevel().isClientSide()) {
            FluidStack fluid = tank.getFluid();
            int light = fluid.isEmpty() ? 0 : fluid.getFluid().getFluidType().getLightLevel(fluid);
            BlockState state = be.getBlockState();
            if (state.hasProperty(MelterBlock.LIGHT) && state.getValue(MelterBlock.LIGHT) != light) {
                be.getLevel().setBlock(be.getBlockPos(),
                        state.setValue(MelterBlock.LIGHT, light), Block.UPDATE_CLIENTS);
            }
        }
    }


    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        for (int i = 0; i < 3; i++) {
            output.store("item" + i, ItemStack.OPTIONAL_CODEC, inventory.getStackInSlot(i));
        }
        output.store("fluid", FluidStack.OPTIONAL_CODEC, tank.getFluid());
        output.putInt("fuel", fuel);
        output.putInt("maxFuel", maxFuel);
        output.putInt("temperature", temperature);
        for (int i = 0; i < 3; i++) {
            output.putInt("state"       + i, state[i]);
            output.putInt("progress"    + i, progress[i]);
            output.putInt("maxProgress" + i, maxProgress[i]);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < 3; i++) {
            inventory.setStackInSlot(i, input.read("item" + i, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        }
        tank.setFluid(input.read("fluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY));
        fuel        = input.getIntOr("fuel", 0);
        maxFuel     = input.getIntOr("maxFuel", 0);
        temperature = input.getIntOr("temperature", 0);
        for (int i = 0; i < 3; i++) {
            state[i]       = input.getIntOr("state"       + i, 0);
            progress[i]    = input.getIntOr("progress"    + i, 0);
            maxProgress[i] = input.getIntOr("maxProgress" + i, 0);
        }
        updateLight(this, tank);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private int getTemperatureForFuel(FluidStack fluid, Level level) {
        if (fluid.isEmpty()) return 0;
        int maxTemp = 0;
        for (var holder : ((net.minecraft.server.level.ServerLevel) level).getServer().getRecipeManager().recipeMap().byType(ModRecipes.MELTING_TYPE.get())) {
            ModRecipes.MeltingRecipe recipe = holder.value();
            if (recipe.fuel().getFluid().isSame(fluid.getFluid())) {
                maxTemp = Math.max(maxTemp, recipe.temperature());
            }
        }
        return maxTemp;
    }

    private @Nullable FluidTank getFuelTankBelow(Level level, BlockPos pos) {
        BlockEntity below = level.getBlockEntity(pos.below());
        if (below instanceof SearedTankBlockEntity tank) return tank.getFluidTank();
        return null;
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide()) return;
        boolean dirty = false;
        boolean hasItemToMelt = false;

        int availableHeat = this.temperature;
        if (this.fuel <= 0) {
            availableHeat = 0;
            FluidTank fuelTankBelow = getFuelTankBelow(level, pos);
            if (fuelTankBelow != null) {
                FluidStack sim = fuelTankBelow.drain(1, IFluidHandler.FluidAction.SIMULATE);
                if (!sim.isEmpty()) availableHeat = getTemperatureForFuel(sim, level);
            }
        }

        for (int i = 0; i < 3; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                if (progress[i] != 0 || maxProgress[i] != 0 || this.state[i] != 0) {
                    progress[i] = 0; maxProgress[i] = 0; this.state[i] = 0; dirty = true;
                }
                continue;
            }

            var recipeHolder = ((net.minecraft.server.level.ServerLevel) level).getServer().getRecipeManager()
                    .getRecipeFor(ModRecipes.MELTING_TYPE.get(), new SingleRecipeInput(stack), level)
                    .orElse(null);
            if (recipeHolder == null) {
                if (this.state[i] != 3) { progress[i] = 0; maxProgress[i] = 100; this.state[i] = 3; dirty = true; }
                continue;
            }

            ModRecipes.MeltingRecipe recipe = recipeHolder.value();
            if (maxProgress[i] != recipe.time()) { maxProgress[i] = recipe.time(); dirty = true; }

            if (recipe.temperature() > availableHeat) {
                if (this.state[i] != 4) { this.state[i] = 4; dirty = true; }
                if (progress[i] > 0) { progress[i]--; dirty = true; }
                continue;
            }

            FluidStack output = recipe.output().copy();
            boolean canOutput = tank.fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount();

            if (canOutput) {
                if (progress[i] >= maxProgress[i]) {
                    inventory.extractItem(i, 1, false);
                    tank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                    progress[i] = 0; this.state[i] = 0; dirty = true;
                } else {
                    if (this.state[i] != 1) { this.state[i] = 1; dirty = true; }
                    hasItemToMelt = true;
                    if (this.fuel > 0) { progress[i]++; dirty = true; }
                    else if (progress[i] > 0) { progress[i]--; dirty = true; }
                }
            } else {
                if (progress[i] >= maxProgress[i]) {
                    if (this.state[i] != 2) { this.state[i] = 2; dirty = true; }
                } else {
                    if (this.state[i] != 1) { this.state[i] = 1; dirty = true; }
                    hasItemToMelt = true;
                    if (this.fuel > 0) { progress[i]++; dirty = true; }
                    else if (progress[i] > 0) { progress[i]--; dirty = true; }
                }
            }
        }

        if (this.fuel > 0) {
            this.fuel--; dirty = true;
            if (this.fuel == 0) this.temperature = 0;
        } else if (hasItemToMelt) {
            FluidTank fuelTankBelow = getFuelTankBelow(level, pos);
            if (fuelTankBelow != null) {
                FluidStack sim = fuelTankBelow.drain(50, IFluidHandler.FluidAction.SIMULATE);
                if (!sim.isEmpty()) {
                    int heat = getTemperatureForFuel(sim, level);
                    if (heat > 0) {
                        fuelTankBelow.drain(50, IFluidHandler.FluidAction.EXECUTE);
                        this.fuel = 200; this.maxFuel = 200; this.temperature = heat; dirty = true;
                    }
                }
            }
        }

        if (dirty) setChanged(level, pos, blockState);

        boolean isBurning = this.fuel > 0;
        if (blockState.hasProperty(SearedMachineBlock.ACTIVE)
                && blockState.getValue(SearedMachineBlock.ACTIVE) != isBurning) {
            level.setBlockAndUpdate(pos, blockState.setValue(SearedMachineBlock.ACTIVE, isBurning));
        }
    }
}
