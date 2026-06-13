package com.titammods.compat.jei;

import com.titammods.TitamMods;
import com.titammods.recipe.AlloyRecipe;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class TitamModsJEIPlugin implements IModPlugin {

    public static final RecipeType<ModRecipes.MeltingRecipe> MELTING_TYPE = RecipeType.create(TitamMods.MODID, "melting", ModRecipes.MeltingRecipe.class);
    public static final RecipeType<ModRecipes.MeltingRecipe> SMELTERY_TYPE = RecipeType.create(TitamMods.MODID, "smeltery", ModRecipes.MeltingRecipe.class);
    public static final RecipeType<AlloyRecipe> ALLOY_TYPE = RecipeType.create(TitamMods.MODID, "alloying", AlloyRecipe.class);
    public static final RecipeType<ModRecipes.CastingTableRecipe> CASTING_TABLE_TYPE = RecipeType.create(TitamMods.MODID, "casting_table", ModRecipes.CastingTableRecipe.class);
    public static final RecipeType<ModRecipes.CastingBasinRecipe> CASTING_BASIN_TYPE = RecipeType.create(TitamMods.MODID, "casting_basin", ModRecipes.CastingBasinRecipe.class);
    public static final RecipeType<ModRecipes.EntityMeltingRecipe> ENTITY_MELTING_TYPE = RecipeType.create(TitamMods.MODID, "entity_melting", ModRecipes.EntityMeltingRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new MelterCategory(guiHelper),
                new SmelteryCategory(guiHelper),
                new AlloyCategory(guiHelper),
                new CastingTableCategory(guiHelper),
                new CastingBasinCategory(guiHelper),
                new EntityMeltingCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) return;
        RecipeManager rm = Minecraft.getInstance().level.getRecipeManager();

        List<ModRecipes.MeltingRecipe> melterRecipes = rm.getAllRecipesFor(ModRecipes.MELTING_TYPE.get()).stream().map(RecipeHolder::value).toList();
        List<AlloyRecipe> alloyRecipes = rm.getAllRecipesFor(ModRecipes.ALLOY_TYPE.get()).stream().map(RecipeHolder::value).toList();
        List<ModRecipes.CastingTableRecipe> tableRecipes = rm.getAllRecipesFor(ModRecipes.CASTING_TABLE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        List<ModRecipes.CastingBasinRecipe> basinRecipes = rm.getAllRecipesFor(ModRecipes.CASTING_BASIN_TYPE.get()).stream().map(RecipeHolder::value).toList();
        List<ModRecipes.EntityMeltingRecipe> entityMeltingRecipes = rm.getAllRecipesFor(ModRecipes.ENTITY_MELTING_TYPE.get()).stream().map(RecipeHolder::value).toList();

        registration.addRecipes(MELTING_TYPE, melterRecipes);
        registration.addRecipes(SMELTERY_TYPE, melterRecipes);
        registration.addRecipes(ALLOY_TYPE, alloyRecipes);
        registration.addRecipes(CASTING_TABLE_TYPE, tableRecipes);
        registration.addRecipes(CASTING_BASIN_TYPE, basinRecipes);
        registration.addRecipes(ENTITY_MELTING_TYPE, entityMeltingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SEARED_MELTER.get()), MELTING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()), SMELTERY_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()), ALLOY_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()), ENTITY_MELTING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SEARED_TABLE.get()), CASTING_TABLE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SEARED_BASIN.get()), CASTING_BASIN_TYPE);
    }

    @Override
    public void registerRuntime(mezz.jei.api.registration.IRuntimeRegistration registration) {
        if (!net.neoforged.fml.ModList.get().isLoaded("alltheores")) return;

        mezz.jei.api.runtime.IIngredientManager ingredientManager = registration.getIngredientManager();

        List<net.minecraft.world.item.ItemStack> itemsToHide = new ArrayList<>();
        for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
            net.minecraft.resources.ResourceLocation id =
                    net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
            if (!id.getNamespace().equals("alltheores")) continue;
            String path = id.getPath();
            if (path.endsWith("_bucket") || (path.contains("molten_") && path.endsWith("_block"))) {
                itemsToHide.add(new net.minecraft.world.item.ItemStack(item));
            }
        }
        if (!itemsToHide.isEmpty()) {
            ingredientManager.removeIngredientsAtRuntime(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, itemsToHide);
        }

        List<net.neoforged.neoforge.fluids.FluidStack> fluidsToHide = new ArrayList<>();
        for (net.minecraft.world.level.material.Fluid fluid : net.minecraft.core.registries.BuiltInRegistries.FLUID) {
            net.minecraft.resources.ResourceLocation id =
                    net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluid);
            if (!id.getNamespace().equals("alltheores")) continue;
            String path = id.getPath();
            if (path.startsWith("molten_") && !path.endsWith("_flowing")) {
                fluidsToHide.add(new net.neoforged.neoforge.fluids.FluidStack(fluid, 1000));
            }
        }
        if (!fluidsToHide.isEmpty()) {
            ingredientManager.removeIngredientsAtRuntime(mezz.jei.api.neoforge.NeoForgeTypes.FLUID_STACK, fluidsToHide);
        }
    }
}