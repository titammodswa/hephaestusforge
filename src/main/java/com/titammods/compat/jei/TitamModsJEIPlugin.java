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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class TitamModsJEIPlugin implements IModPlugin {

    @SuppressWarnings("removal")
    public static final RecipeType<ModRecipes.MeltingRecipe> MELTING_TYPE =
            RecipeType.create(TitamMods.MODID, "melting", ModRecipes.MeltingRecipe.class);

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(TitamMods.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new MelterCategory(gui));
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
    }

    @SuppressWarnings("removal")
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new net.minecraft.world.item.ItemStack(ModBlocks.SEARED_MELTER.get()),
                MELTING_TYPE);
    }
}