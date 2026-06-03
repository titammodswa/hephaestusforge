package com.titammods.compat.jei;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class TitamModsJEIPlugin implements IModPlugin {

    @SuppressWarnings("removal")
    public static final RecipeType<ModRecipes.AlloyRecipe> ALLOY_TYPE =
            RecipeType.create(TitamMods.MODID, "alloy", ModRecipes.AlloyRecipe.class);

    @SuppressWarnings("removal")
    public static final RecipeType<ModRecipes.MeltingRecipe> SMELTERY_TYPE =
            RecipeType.create(TitamMods.MODID, "smeltery_melting", ModRecipes.MeltingRecipe.class);

    @SuppressWarnings("removal")
    public static final RecipeType<ModRecipes.MeltingRecipe> MELTING_TYPE =
            RecipeType.create(TitamMods.MODID, "melting", ModRecipes.MeltingRecipe.class);

    @SuppressWarnings("removal")
    public static final RecipeType<ModRecipes.CastingTableRecipe> CASTING_TABLE_TYPE =
            RecipeType.create(TitamMods.MODID, "casting_table", ModRecipes.CastingTableRecipe.class);

    @SuppressWarnings("removal")
    public static final RecipeType<ModRecipes.CastingBasinRecipe> CASTING_BASIN_TYPE =
            RecipeType.create(TitamMods.MODID, "casting_basin", ModRecipes.CastingBasinRecipe.class);

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(TitamMods.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new MelterCategory(gui),
                new CastingTableCategory(gui),
                new CastingBasinCategory(gui),
                new AlloyCategory(gui),
                new SmelteryCategory(gui)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) return;
        RecipeManager rm = server.getRecipeManager();

        List<ModRecipes.MeltingRecipe> melting = rm.recipeMap()
                .byType(ModRecipes.MELTING_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(MELTING_TYPE, melting);

        List<ModRecipes.CastingTableRecipe> table = rm.recipeMap()
                .byType(ModRecipes.CASTING_TABLE_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(CASTING_TABLE_TYPE, table);

        List<ModRecipes.CastingBasinRecipe> basin = rm.recipeMap()
                .byType(ModRecipes.CASTING_BASIN_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(CASTING_BASIN_TYPE, basin);

        List<ModRecipes.AlloyRecipe> alloy = rm.recipeMap()
                .byType(ModRecipes.ALLOY_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(ALLOY_TYPE, alloy);
        registration.addRecipes(SMELTERY_TYPE, melting);
    }

    @SuppressWarnings("removal")
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new net.minecraft.world.item.ItemStack(ModBlocks.SEARED_MELTER.get()),
                MELTING_TYPE);
        registration.addRecipeCatalyst(
                new net.minecraft.world.item.ItemStack(ModBlocks.SEARED_TABLE.get()),
                CASTING_TABLE_TYPE);
        registration.addRecipeCatalyst(
                new net.minecraft.world.item.ItemStack(ModBlocks.SEARED_BASIN.get()),
                CASTING_BASIN_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()), ALLOY_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()), SMELTERY_TYPE);
    }
}