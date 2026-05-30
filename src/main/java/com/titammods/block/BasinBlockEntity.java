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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class BasinBlockEntity extends BlockEntity {

    public int coolingTime = 0;

    public int renderTimer = 0;

    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int slot, ItemStack previousContents) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }

        @Override
        public boolean isValid(int slot, ItemResource itemResource) {
            return false;
        }

        @Override
        public int extract(int slot, ItemResource itemResource, int amount, @NonNull TransactionContext transaction) {
            if (renderTimer > 0) return 0; // Item congelado na bacia!
            return super.extract(slot, itemResource, amount, transaction);
        }
    };

    public final FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1, 900) {
        @Override
        protected void onContentsChanged(int index, FluidStack stack) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIN.get(), pos, state);
    }

    public void extractItem(Player player) {
        if (renderTimer > 0) return;

        ItemStack output = inventory.copyToList().getFirst();
        if (!output.isEmpty()) {
            Inventory playerInventory = player.getInventory();
            playerInventory.placeItemBackInInventory(output);
            inventory.copyToList().set(0, ItemStack.EMPTY);
            tank.copyToList().clear(); // mesma coisa de? tank.setFluid(FluidStack.EMPTY);
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        if (renderTimer > 0) {
            renderTimer--;
            return;
        }

        if (!inventory.copyToList().getFirst().isEmpty()) {
            coolingTime = 0;
            return;
        }

        FluidStack currentFluid = tank.copyToList().getFirst();
        if (currentFluid.isEmpty()) {
            coolingTime = 0;
            return;
        }

        ModRecipes.CastingBasinRecipe matchedRecipe = null;

        RecipeManager recipeManager = Objects.requireNonNull(level.getServer()).getRecipeManager();
        for (RecipeHolder<?> recipeHolder : recipeManager.getRecipes()) {
            if (recipeHolder.value().getType() != ModRecipes.CASTING_BASIN_TYPE.get()) {
                continue;
            }
            ModRecipes.CastingBasinRecipe recipe = (ModRecipes.CastingBasinRecipe) recipeHolder.value();
            if (recipe.input().getFluid() == currentFluid.getFluid() && currentFluid.getAmount() >= recipe.input().getAmount()) {
                matchedRecipe = recipe;
                break;
            }
        }

        if (matchedRecipe != null) {
            coolingTime++;
            if (coolingTime >= matchedRecipe.time()) {
                coolingTime = 0;

                try (var tx = Transaction.openRoot()) {
                    tank.extract(
                            0,
                            tank.getResource(0),
                            matchedRecipe.input().getAmount(),
                            tx
                    );

                    tx.commit();
                }

                inventory.copyToList().set(0, matchedRecipe.output().copy());

                renderTimer = 20;

                level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);

            }
        } else {
            coolingTime = 0;
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("coolingTime", coolingTime);
        output.putChild("inventory", inventory);
        output.putChild("tank", tank);
        output.putInt("renderTimer", renderTimer);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        coolingTime = input.getInt("coolingTime").orElseThrow();
        if (input.keySet().contains("inventory")) {
            inventory.deserialize(input);
        } else {
            inventory.copyToList().set(0, ItemStack.EMPTY);
        }
        tank.deserialize(input);
        renderTimer = input.getInt("renderTimer").orElseThrow();
    }

    @Override public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
