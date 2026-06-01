package com.titammods.common.blockentities;

import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.WorldlyContainer;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class TableBlockEntity extends BlockEntity implements net.minecraft.world.WorldlyContainer {

    public int coolingTime   = 0;
    public ItemStack renderResult = ItemStack.EMPTY;
    public int renderTimer   = 0;

    public int ejectCooldown = 0;
    @SuppressWarnings("removal")
    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
        @Override public int getSlotLimit(int slot) { return 1; }
    };
    @SuppressWarnings("removal")
    public final IItemHandler externalHandler = new IItemHandler() {
        @Override public int getSlots() { return 1; }
        @Override public ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(1); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (ejectCooldown > 0) return ItemStack.EMPTY;
            return inventory.extractItem(1, amount, simulate);
        }
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
    };
    @SuppressWarnings("removal")
    public final FluidTank tank = new FluidTank(10000) {
        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!inventory.getStackInSlot(1).isEmpty() || renderTimer > 0) return 0;
            if (!fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(fluid, resource)) return 0;

            ItemStack mold = inventory.getStackInSlot(0);
            ModRecipes.@Nullable CastingTableRecipe recipe = findRecipeByType(mold, resource.getFluid());
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

    private static final int[] SLOTS_NONE   = new int[0];
    private static final int[] SLOTS_OUTPUT = new int[]{1};

    @Override public int   getContainerSize()                { return 2; }
    @SuppressWarnings("removal")
    @Override public boolean isEmpty()                       { return inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty(); }
    @SuppressWarnings("removal")
    @Override public ItemStack getItem(int slot)             { return inventory.getStackInSlot(slot); }
    @SuppressWarnings("removal")
    @Override public ItemStack removeItem(int slot, int amt) { return inventory.extractItem(slot, amt, false); }
    @SuppressWarnings("removal")
    @Override public ItemStack removeItemNoUpdate(int slot)  { ItemStack s = inventory.getStackInSlot(slot); inventory.setStackInSlot(slot, ItemStack.EMPTY); return s; }
    @SuppressWarnings("removal")
    @Override public void setItem(int slot, ItemStack stack) { inventory.setStackInSlot(slot, stack); }
    @Override public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
    @SuppressWarnings("removal")
    @Override public void clearContent() { inventory.setStackInSlot(0, ItemStack.EMPTY); inventory.setStackInSlot(1, ItemStack.EMPTY); }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        if (!inventory.getStackInSlot(1).isEmpty() && ejectCooldown <= 0) return SLOTS_OUTPUT;
        return SLOTS_NONE;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, net.minecraft.core.@Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction dir) {
        return slot == 1 && ejectCooldown <= 0;
    }

    public TableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TABLE.get(), pos, state);
    }
    @SuppressWarnings("removal")
    public void interact(Player player) {
        if (renderTimer > 0) return;

        ItemStack output = inventory.getStackInSlot(1);
        if (!output.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, output, player.getInventory().getSelectedSlot());
            inventory.setStackInSlot(1, ItemStack.EMPTY);
            tank.setFluid(FluidStack.EMPTY);
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            return;
        }

        if (tank.isEmpty()) {
            ItemStack mold = inventory.getStackInSlot(0);
            if (!mold.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, mold, player.getInventory().getSelectedSlot());
                inventory.setStackInSlot(0, ItemStack.EMPTY);
            } else if (!player.getMainHandItem().isEmpty()) {
                ItemStack handItem = player.getMainHandItem().copy();
                handItem.setCount(1);
                inventory.setStackInSlot(0, handItem);
                if (!player.isCreative()) player.getMainHandItem().shrink(1);
            }
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
    @SuppressWarnings("removal")
    public void tick() {
        if (level == null || level.isClientSide()) return;

        if (renderTimer > 0) {
            renderTimer--;
            if (renderTimer == 0) {
                inventory.setStackInSlot(1, renderResult.copy());
                renderResult = ItemStack.EMPTY;
                ejectCooldown = 30;
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return;
        }

        if (ejectCooldown > 0) ejectCooldown--;

        if (!inventory.getStackInSlot(1).isEmpty()) { coolingTime = 0; return; }

        FluidStack currentFluid = tank.getFluid();
        if (currentFluid.isEmpty()) { coolingTime = 0; return; }

        ItemStack currentMold = inventory.getStackInSlot(0);
        ModRecipes.@Nullable CastingTableRecipe matchedRecipe =
                findRecipe(currentMold, currentFluid.getFluid(), currentFluid.getAmount());

        if (matchedRecipe != null && currentFluid.getAmount() == matchedRecipe.fluidAmount()) {
            coolingTime++;
            if (coolingTime >= matchedRecipe.coolingTime()) {
                coolingTime  = 0;
                renderResult = matchedRecipe.result().copy();
                tank.drain(matchedRecipe.fluidAmount(), IFluidHandler.FluidAction.EXECUTE);
                if (matchedRecipe.castConsumed()) inventory.setStackInSlot(0, ItemStack.EMPTY);

                renderTimer = 20;
                level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS,
                        0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);

                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        } else {
            coolingTime = 0;
        }
    }

    private ModRecipes.@Nullable CastingTableRecipe findRecipeByType(ItemStack mold, Fluid fluid) {
        if (!(level instanceof ServerLevel sl)) return null;
        net.minecraft.resources.Identifier fid = BuiltInRegistries.FLUID.getKey(fluid);
        for (var holder : sl.getServer().getRecipeManager()
                .recipeMap().byType(ModRecipes.CASTING_TABLE_TYPE.get())) {
            ModRecipes.CastingTableRecipe recipe = holder.value();
            java.util.Optional<net.minecraft.world.item.crafting.Ingredient> cast = recipe.cast();
            boolean moldMatches = cast.isEmpty() ? mold.isEmpty() : cast.get().test(mold);
            if (moldMatches && recipe.fluidId().equals(fid)) {
                return recipe;
            }
        }
        return null;
    }

    private ModRecipes.@Nullable CastingTableRecipe findRecipe(ItemStack mold, Fluid fluid, int amount) {
        if (!(level instanceof ServerLevel sl)) return null;
        net.minecraft.resources.Identifier fid = BuiltInRegistries.FLUID.getKey(fluid);
        for (var holder : sl.getServer().getRecipeManager()
                .recipeMap().byType(ModRecipes.CASTING_TABLE_TYPE.get())) {
            ModRecipes.CastingTableRecipe recipe = holder.value();
            java.util.Optional<net.minecraft.world.item.crafting.Ingredient> cast = recipe.cast();
            boolean moldMatches = cast.isEmpty() ? mold.isEmpty() : cast.get().test(mold);
            if (moldMatches && recipe.fluidId().equals(fid) && amount >= recipe.fluidAmount()) {
                return recipe;
            }
        }
        return null;
    }
    @SuppressWarnings("removal")
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("coolingTime",   coolingTime);
        output.putInt("renderTimer",    renderTimer);
        output.putInt("ejectCooldown",  ejectCooldown);
        output.store("slot0",         ItemStack.OPTIONAL_CODEC, inventory.getStackInSlot(0));
        output.store("slot1",         ItemStack.OPTIONAL_CODEC, inventory.getStackInSlot(1));
        output.store("tank",          FluidStack.OPTIONAL_CODEC, tank.getFluid());
        output.store("renderResult",  ItemStack.OPTIONAL_CODEC, renderResult);
    }
    @SuppressWarnings("removal")
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        coolingTime   = input.getIntOr("coolingTime",  0);
        renderTimer   = input.getIntOr("renderTimer",   0);
        ejectCooldown = input.getIntOr("ejectCooldown", 0);
        inventory.setStackInSlot(0, input.read("slot0", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        inventory.setStackInSlot(1, input.read("slot1", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        tank.setFluid(input.read("tank", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY));
        renderResult = input.read("renderResult", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
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