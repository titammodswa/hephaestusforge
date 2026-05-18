package com.titammods.compat.jei;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModRecipes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;

public class CastingBasinCategory implements IRecipeCategory<ModRecipes.CastingBasinRecipe> {
    public static final ResourceLocation BACKGROUND_LOC = ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/jei/casting.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable tankOverlay;
    private final IDrawable block;
    private final IGuiHelper guiHelper;

    public CastingBasinCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 117, 54);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SEARED_BASIN.get()));
        this.tankOverlay = guiHelper.createDrawable(BACKGROUND_LOC, 133, 0, 32, 32);
        this.block = guiHelper.createDrawable(BACKGROUND_LOC, 117, 16, 16, 16);
    }

    @Override public RecipeType<ModRecipes.CastingBasinRecipe> getRecipeType() { return TitamModsJEIPlugin.CASTING_BASIN_TYPE; }
    @Override public Component getTitle() { return Component.translatable("block.hephaestus.seared_basin"); }
    @SuppressWarnings("removal") @Override public IDrawable getBackground() { return background; }
    @SuppressWarnings("removal") @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ModRecipes.CastingBasinRecipe recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                .setFluidRenderer(1296, false, 32, 32)
                .setOverlay(tankOverlay, 0, 0)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.input())
                .addRichTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.literal(recipe.input().getAmount() + " mB").withStyle(ChatFormatting.GRAY));
                });

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 43, 8)
                .setFluidRenderer(1, false, 6, 27)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.input());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 18)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(ModRecipes.CastingBasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        int coolingTicks = Math.max(1, recipe.time());
        IDrawableAnimated arrow = guiHelper.drawableBuilder(BACKGROUND_LOC, 117, 32, 24, 17).buildAnimated(coolingTicks, IDrawableAnimated.StartDirection.LEFT, false);

        arrow.draw(graphics, 58, 18);
        block.draw(graphics, 38, 35);

        int seconds = coolingTicks / 20;
        String coolingString = seconds + "s";
        Font fontRenderer = Minecraft.getInstance().font;
        int x = 72 - fontRenderer.width(coolingString) / 2;
        graphics.drawString(fontRenderer, coolingString, x, 2, Color.GRAY.getRGB(), false);
    }
}