package com.titammods.block;

import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class BasinBlockEntity extends BlockEntity {

    public int coolingTime = 0;

    public int renderTimer = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (renderTimer > 0) return ItemStack.EMPTY; // Item congelado na bacia!
            return super.extractItem(slot, amount, simulate);
        }
    };

    public final FluidTank tank = new FluidTank(900) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIN.get(), pos, state);
    }

    public void extractItem(Player player) {
        if (renderTimer > 0) return;

        ItemStack output = inventory.getStackInSlot(0);
        if (!output.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, output, player.getInventory().selected);
            inventory.setStackInSlot(0, ItemStack.EMPTY);
            tank.setFluid(FluidStack.EMPTY);
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

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

        ModRecipes.CastingBasinRecipe matchedRecipe = null;
        for (var recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipes.CASTING_BASIN_TYPE.get())) {
            ModRecipes.CastingBasinRecipe recipe = recipeHolder.value();
            if (recipe.input().getFluid() == currentFluid.getFluid() && currentFluid.getAmount() >= recipe.input().getAmount()) {
                matchedRecipe = recipe;
                break;
            }
        }

        if (matchedRecipe != null) {
            coolingTime++;
            if (coolingTime >= matchedRecipe.time()) {
                coolingTime = 0;

                tank.drain(matchedRecipe.input().getAmount(), net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);

                inventory.setStackInSlot(0, matchedRecipe.output().copy());

                renderTimer = 20;

                level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.LAVA_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

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
        tag.putInt("renderTimer", renderTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        coolingTime = tag.getInt("coolingTime");
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        } else {
            inventory.setStackInSlot(0, ItemStack.EMPTY);
        }
        tank.readFromNBT(registries, tag.getCompound("tank"));
        renderTimer = tag.getInt("renderTimer");
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { CompoundTag tag = new CompoundTag(); saveAdditional(tag, registries); return tag; }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}