package com.titammods.common.blockentities;

import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.Nullable;

public class BasinBlockEntity extends BlockEntity implements WorldlyContainer {

    public int coolingTime  = 0;
    public int renderTimer  = 0;
    public int ejectCooldown = 0;
    @SuppressWarnings("removal")
    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
    };

    @SuppressWarnings("removal")
    public final FluidTank tank = new FluidTank(900) {

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!inventory.getStackInSlot(0).isEmpty() || renderTimer > 0) return 0;

            if (!fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(fluid, resource)) return 0;

            ModRecipes.@Nullable CastingBasinRecipe recipe = findRecipeByType(resource.getFluid());
            if (recipe == null) return 0;

            int spaceLeft = recipe.fluidAmount() - fluid.getAmount();
            if (spaceLeft <= 0) return 0;

            FluidStack limited = resource.copy();
            limited.setAmount(Math.min(resource.getAmount(), spaceLeft));
            return super.fill(limited, action);
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };
    @SuppressWarnings("removal")
    public final IFluidHandler externalFluidHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return tank.getFluidInTank(t); }
        @Override public int getTankCapacity(int t) { return tank.getTankCapacity(t); }
        @Override public boolean isFluidValid(int t, FluidStack stack) { return tank.isFluidValid(t, stack); }
        @Override public int fill(FluidStack resource, IFluidHandler.FluidAction action) { return tank.fill(resource, action); }
        @Override public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) { return FluidStack.EMPTY; }
    };

    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIN.get(), pos, state);
    }
    @SuppressWarnings("removal")
    public void extractItem(Player player) {
        if (renderTimer > 0) return;
        ItemStack output = inventory.getStackInSlot(0);
        if (!output.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, output, player.getInventory().getSelectedSlot());
            inventory.setStackInSlot(0, ItemStack.EMPTY);
            tank.setFluid(FluidStack.EMPTY);
            ejectCooldown = 0;
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
    @SuppressWarnings("removal")
    public void tick() {
        if (level == null || level.isClientSide()) return;

        if (ejectCooldown > 0) ejectCooldown--;

        if (renderTimer > 0) {
            renderTimer--;
            return;
        }

        if (!inventory.getStackInSlot(0).isEmpty()) {
            coolingTime = 0;
            return;
        }

        FluidStack currentFluid = tank.getFluid();
        if (currentFluid.isEmpty()) {
            coolingTime = 0;
            return;
        }

        ModRecipes.@Nullable CastingBasinRecipe matchedRecipe =
                findRecipe(currentFluid.getFluid(), currentFluid.getAmount());

        if (matchedRecipe != null) {
            coolingTime++;
            if (coolingTime >= matchedRecipe.coolingTime()) {
                coolingTime = 0;
                tank.drain(matchedRecipe.fluidAmount(), IFluidHandler.FluidAction.EXECUTE);
                inventory.setStackInSlot(0, matchedRecipe.result().copy());
                renderTimer   = 20;
                ejectCooldown = 50;
                level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS,
                        0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        } else {
            coolingTime = 0;
        }
    }

    private ModRecipes.@Nullable CastingBasinRecipe findRecipeByType(Fluid fluid) {
        if (!(level instanceof ServerLevel sl)) return null;
        Identifier fid = BuiltInRegistries.FLUID.getKey(fluid);
        for (var holder : sl.getServer().getRecipeManager()
                .recipeMap().byType(ModRecipes.CASTING_BASIN_TYPE.get())) {
            if (holder.value().fluidId().equals(fid)) return holder.value();
        }
        return null;
    }

    private ModRecipes.@Nullable CastingBasinRecipe findRecipe(Fluid fluid, int amount) {
        if (!(level instanceof ServerLevel sl)) return null;
        Identifier fid = BuiltInRegistries.FLUID.getKey(fluid);
        for (var holder : sl.getServer().getRecipeManager()
                .recipeMap().byType(ModRecipes.CASTING_BASIN_TYPE.get())) {
            ModRecipes.CastingBasinRecipe r = holder.value();
            if (r.fluidId().equals(fid) && amount >= r.fluidAmount()) return r;
        }
        return null;
    }

    private static final int[] SLOTS_NONE   = new int[0];
    private static final int[] SLOTS_OUTPUT = new int[]{0};

    @Override public int     getContainerSize()                { return 1; }
    @SuppressWarnings("removal")
    @Override public boolean isEmpty()                         { return inventory.getStackInSlot(0).isEmpty(); }
    @SuppressWarnings("removal")
    @Override public ItemStack getItem(int slot)               { return inventory.getStackInSlot(0); }
    @SuppressWarnings("removal")
    @Override public ItemStack removeItem(int slot, int amt)   { return inventory.extractItem(0, amt, false); }
    @SuppressWarnings("removal")
    @Override public ItemStack removeItemNoUpdate(int slot)    { ItemStack s = inventory.getStackInSlot(0); inventory.setStackInSlot(0, ItemStack.EMPTY); return s; }
    @SuppressWarnings("removal")
    @Override public void   setItem(int slot, ItemStack stack) { inventory.setStackInSlot(0, stack); }
    @Override public boolean stillValid(Player p)              { return true; }
    @SuppressWarnings("removal")
    @Override public void   clearContent()                     { inventory.setStackInSlot(0, ItemStack.EMPTY); }
    @SuppressWarnings("removal")
    @Override
    public int[] getSlotsForFace(Direction side) {
        if (!inventory.getStackInSlot(0).isEmpty() && ejectCooldown <= 0 && renderTimer <= 0) return SLOTS_OUTPUT;
        return SLOTS_NONE;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 0 && ejectCooldown <= 0 && renderTimer <= 0;
    }
    @SuppressWarnings("removal")
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("coolingTime",   coolingTime);
        output.putInt("renderTimer",    renderTimer);
        output.putInt("ejectCooldown",  ejectCooldown);
        output.store("slot0",          ItemStack.OPTIONAL_CODEC, inventory.getStackInSlot(0));
        output.store("tank",           FluidStack.OPTIONAL_CODEC, tank.getFluid());
    }
    @SuppressWarnings("removal")
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        coolingTime   = input.getIntOr("coolingTime",  0);
        renderTimer   = input.getIntOr("renderTimer",   0);
        ejectCooldown = input.getIntOr("ejectCooldown", 0);
        inventory.setStackInSlot(0, input.read("slot0", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        tank.setFluid(input.read("tank", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY));
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