package com.titammods.block;

import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TableBlockEntity extends BlockEntity {

    public int coolingTime = 0;
    public ItemStack renderResult = ItemStack.EMPTY;
    public int renderTimer = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
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

    public final IItemHandler externalHandler = new IItemHandler() {
        @Override public int getSlots() { return 1; }
        @Nonnull @Override public ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(1); }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { return stack; }
        @Nonnull @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return inventory.extractItem(1, amount, simulate);
        }
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return false; }
    };

    public final FluidTank tank = new FluidTank(10000) {
        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!inventory.getStackInSlot(1).isEmpty() || renderTimer > 0) return 0;

            if (!fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(fluid, resource)) return 0;

            ItemStack currentMold = inventory.getStackInSlot(0);
            ModRecipes.CastingTableRecipe matchedRecipe = null;

            for (var recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipes.CASTING_TABLE_TYPE.get())) {
                ModRecipes.CastingTableRecipe recipe = recipeHolder.value();
                boolean moldMatches = (recipe.cast().isEmpty() && currentMold.isEmpty()) || recipe.cast().test(currentMold);

                if (moldMatches && recipe.fluid().getFluid() == resource.getFluid()) {
                    matchedRecipe = recipe;
                    break;
                }
            }

            if (matchedRecipe == null) return 0;

            int requiredAmount = matchedRecipe.fluid().getAmount();
            int spaceLeft = requiredAmount - fluid.getAmount();
            if (spaceLeft <= 0) return 0;

            FluidStack limitedFill = resource.copy();
            limitedFill.setAmount(Math.min(resource.getAmount(), spaceLeft));

            return super.fill(limitedFill, action);
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    public final IFluidHandler externalFluidHandler = new IFluidHandler() {
        @Override public int getTanks() { return 0; }
        @Nonnull @Override public FluidStack getFluidInTank(int tank) { return FluidStack.EMPTY; }
        @Override public int getTankCapacity(int tank) { return 0; }
        @Override public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return false; }

        @Override public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return tank.fill(resource, action);
        }

        @Nonnull @Override public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) { return FluidStack.EMPTY; }
        @Nonnull @Override public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) { return FluidStack.EMPTY; }
    };

    public TableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TABLE.get(), pos, state);
    }

    public void interact(Player player) {
        if (renderTimer > 0) return;

        ItemStack output = inventory.getStackInSlot(1);
        if (!output.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, output, player.getInventory().selected);
            inventory.setStackInSlot(1, ItemStack.EMPTY);
            tank.setFluid(FluidStack.EMPTY);
            setChanged();
            return;
        }

        if (tank.isEmpty()) {
            ItemStack mold = inventory.getStackInSlot(0);
            if (!mold.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, mold, player.getInventory().selected);
                inventory.setStackInSlot(0, ItemStack.EMPTY);
            } else if (!player.getMainHandItem().isEmpty()) {
                ItemStack handItem = player.getMainHandItem().copy();
                handItem.setCount(1);
                inventory.setStackInSlot(0, handItem);
                if (!player.isCreative()) player.getMainHandItem().shrink(1);
            }
            setChanged();
        }
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (renderTimer > 0) {
            renderTimer--;
            if (renderTimer == 0) {
                inventory.setStackInSlot(1, renderResult.copy());
                renderResult = ItemStack.EMPTY;
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return;
        }

        if (!inventory.getStackInSlot(1).isEmpty()) {
            coolingTime = 0;
            return;
        }

        FluidStack currentFluid = tank.getFluid();
        if (currentFluid.isEmpty()) {
            coolingTime = 0;
            return;
        }

        ItemStack currentMold = inventory.getStackInSlot(0);
        ModRecipes.CastingTableRecipe matchedRecipe = null;

        for (var recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipes.CASTING_TABLE_TYPE.get())) {
            ModRecipes.CastingTableRecipe recipe = recipeHolder.value();
            boolean moldMatches = (recipe.cast().isEmpty() && currentMold.isEmpty()) || recipe.cast().test(currentMold);

            if (moldMatches && recipe.fluid().getFluid() == currentFluid.getFluid() && currentFluid.getAmount() >= recipe.fluid().getAmount()) {
                matchedRecipe = recipe;
                break;
            }
        }

        if (matchedRecipe != null && currentFluid.getAmount() == matchedRecipe.fluid().getAmount()) {
            coolingTime++;
            if (coolingTime >= matchedRecipe.coolingTime()) {
                coolingTime = 0;
                renderResult = matchedRecipe.result().copy();
                tank.drain(matchedRecipe.fluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

                if (matchedRecipe.castConsumed()) {
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                }

                renderTimer = 20;
                level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        } else {
            coolingTime = 0;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("coolingTime", coolingTime);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.put("tank", tank.writeToNBT(registries, new CompoundTag()));
        if (!renderResult.isEmpty()) tag.put("renderResult", renderResult.saveOptional(registries));
        tag.putInt("renderTimer", renderTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        coolingTime = tag.getInt("coolingTime");
        if (tag.contains("inventory")) inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        tank.readFromNBT(registries, tag.getCompound("tank"));
        if (tag.contains("renderResult")) renderResult = ItemStack.parseOptional(registries, tag.getCompound("renderResult"));
        else renderResult = ItemStack.EMPTY;
        renderTimer = tag.getInt("renderTimer");
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { CompoundTag tag = new CompoundTag(); saveAdditional(tag, registries); return tag; }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}