package com.titammods.compat.jei;

import com.titammods.TitamMods;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class TitamModsJEIPlugin implements IModPlugin {

    public static final RecipeType<ModRecipes.MeltingRecipe> MELTING_TYPE = RecipeType.create(TitamMods.MODID, "melting", ModRecipes.MeltingRecipe.class);
    public static final RecipeType<ModRecipes.CastingTableRecipe> CASTING_TABLE_TYPE = RecipeType.create(TitamMods.MODID, "casting_table", ModRecipes.CastingTableRecipe.class);
    public static final RecipeType<ModRecipes.CastingBasinRecipe> CASTING_BASIN_TYPE = RecipeType.create(TitamMods.MODID, "casting_basin", ModRecipes.CastingBasinRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new MelterCategory(guiHelper),
                new CastingTableCategory(guiHelper),
                new CastingBasinCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) return;
        RecipeManager rm = Minecraft.getInstance().level.getRecipeManager();

        List<ModRecipes.MeltingRecipe> melterRecipes = rm.getAllRecipesFor(ModRecipes.MELTING_TYPE.get()).stream().map(RecipeHolder::value).toList();
        List<ModRecipes.CastingTableRecipe> tableRecipes = rm.getAllRecipesFor(ModRecipes.CASTING_TABLE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        List<ModRecipes.CastingBasinRecipe> basinRecipes = rm.getAllRecipesFor(ModRecipes.CASTING_BASIN_TYPE.get()).stream().map(RecipeHolder::value).toList();

        registration.addRecipes(MELTING_TYPE, melterRecipes);
        registration.addRecipes(CASTING_TABLE_TYPE, tableRecipes);
        registration.addRecipes(CASTING_BASIN_TYPE, basinRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SEARED_MELTER.get()), MELTING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SEARED_TABLE.get()), CASTING_TABLE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SEARED_BASIN.get()), CASTING_BASIN_TYPE);
    }
}