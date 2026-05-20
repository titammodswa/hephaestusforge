package com.titammods.compat.jei;

import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModRecipes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SmelteryCategory extends MelterCategory {
    private final IDrawable icon;

    public SmelteryCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()));
    }

    @Override
    public RecipeType<ModRecipes.MeltingRecipe> getRecipeType() {
        return TitamModsJEIPlugin.SMELTERY_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.hephaestus.smeltery_controller");
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getIcon() {
        return icon;
    }
}