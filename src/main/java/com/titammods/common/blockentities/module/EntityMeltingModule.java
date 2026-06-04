package com.titammods.common.blockentities.module;

import com.titammods.setup.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EntityMeltingModule {

    private static final Map<EntityType<?>, ModRecipes.EntityMeltingRecipe> RECIPE_CACHE = new HashMap<>();

    private final SmelteryParent parent;
    @SuppressWarnings("removal")
    private final IFluidHandler tank;
    private final Function<ItemStack, ItemStack> insertItem;
    private ModRecipes.@Nullable EntityMeltingRecipe lastRecipe;

    public interface SmelteryParent {
        Level getLevel();
        BlockPos getBlockPos();
        boolean isFormed();
        boolean hasFuel();
    }
    @SuppressWarnings("removal")
    public EntityMeltingModule(SmelteryParent parent, IFluidHandler tank,
                               Function<ItemStack, ItemStack> insertItem) {
        this.parent     = parent;
        this.tank       = tank;
        this.insertItem = insertItem;
    }

    public static void invalidateCache() {
        RECIPE_CACHE.clear();
    }

    private ModRecipes.@Nullable EntityMeltingRecipe findRecipe(EntityType<?> type) {
        if (lastRecipe != null && lastRecipe.matches(type)) return lastRecipe;
        if (RECIPE_CACHE.containsKey(type)) return RECIPE_CACHE.get(type);

        Level level = parent.getLevel();
        if (!(level instanceof ServerLevel sl)) return null;

        for (var holder : sl.getServer().getRecipeManager().recipeMap()
                .byType(ModRecipes.ENTITY_MELTING_TYPE.get())) {
            var recipe = holder.value();
            if (recipe.matches(type)) {
                RECIPE_CACHE.put(type, recipe);
                lastRecipe = recipe;
                return recipe;
            }
        }
        RECIPE_CACHE.put(type, null);
        return null;
    }

    private boolean canMeltEntity(LivingEntity entity) {
        if (entity instanceof Player player && player.getAbilities().invulnerable) return false;
        if (entity.hasEffect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE)) return false;
        return true;
    }

    @SuppressWarnings("removal")
    public boolean interactWithEntities(AABB innerBounds) {
        if (innerBounds == null || !parent.isFormed()) return false;

        Level level = parent.getLevel();
        if (level == null || level.isClientSide()) return false;

        boolean melted = false;
        Boolean canMelt = null;

        for (Entity entity : level.getEntitiesOfClass(Entity.class, innerBounds)) {
            if (!entity.isAlive()) continue;

            EntityType<?> type = entity.getType();

            if (entity instanceof ItemEntity itemEntity) {
                ItemStack remaining = insertItem.apply(itemEntity.getItem().copy());
                if (remaining.isEmpty()) {
                    entity.discard();
                } else {
                    itemEntity.setItem(remaining);
                }
                continue;
            }

            if (entity instanceof LivingEntity living) {
                if (!canMeltEntity(living)) continue;

                if (canMelt == null) canMelt = parent.hasFuel();
                if (!canMelt) continue;

                ModRecipes.EntityMeltingRecipe recipe = findRecipe(type);
                FluidStack fluid;
                int damage;

                if (recipe != null) {
                    fluid  = recipe.output();
                    damage = recipe.damage();
                } else {
                    fluid  = new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, 50);
                    damage = 2;
                }

                DamageSource source = living.fireImmune()
                        ? level.damageSources().magic()
                        : level.damageSources().inFire();

                living.hurt(source, damage);
                if (!fluid.isEmpty()) {
                    tank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                }
                melted = true;
            }
        }
        return melted;
    }
}