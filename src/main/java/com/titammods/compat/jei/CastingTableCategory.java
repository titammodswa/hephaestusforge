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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class CastingTableCategory implements IRecipeCategory<ModRecipes.CastingTableRecipe> {

    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/jei/casting.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable tankOverlay;
    private final IDrawable tableIcon;
    private final IDrawable checkmarkIcon;
    private final IDrawable xIcon;
    private final IGuiHelper guiHelper;

    public CastingTableCategory(IGuiHelper guiHelper) {
        this.guiHelper     = guiHelper;
        this.background    = guiHelper.createDrawable(TEXTURE, 0, 0, 117, 54);
        this.icon          = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SEARED_TABLE.get()));
        this.tankOverlay   = guiHelper.createDrawable(TEXTURE, 133, 0, 32, 32);
        this.tableIcon     = guiHelper.createDrawable(TEXTURE, 117, 0, 16, 16);
        this.xIcon         = guiHelper.createDrawable(TEXTURE, 141, 32, 13, 11);
        this.checkmarkIcon = guiHelper.createDrawable(TEXTURE, 141, 43, 13, 11);
    }

    @SuppressWarnings("removal")
    @Override public RecipeType<ModRecipes.CastingTableRecipe> getRecipeType() { return TitamModsJEIPlugin.CASTING_TABLE_TYPE; }
    @Override public Component getTitle() { return Component.translatable("block.hephaestus.seared_table"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth()  { return 117; }
    @Override public int getHeight() { return 54; }

    @SuppressWarnings("removal")
    public IDrawable getBackground() { return background; }
    @SuppressWarnings("removal")
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ModRecipes.CastingTableRecipe recipe, IFocusGroup focuses) {
        var fluidStack = recipe.fluidStack();
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                .setFluidRenderer(1296, false, 32, 32)
                .setOverlay(tankOverlay, 0, 0)
                .addIngredient(NeoForgeTypes.FLUID_STACK, fluidStack)
                .addRichTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("gui.hephaestus.fluid_mb", fluidStack.getAmount())
                                .withStyle(ChatFormatting.GRAY)));

        boolean hasCast = recipe.cast().isPresent();
        if (hasCast) {
            RecipeIngredientRole role = recipe.castConsumed()
                    ? RecipeIngredientRole.INPUT
                    : RecipeIngredientRole.RENDER_ONLY;
            builder.addSlot(role, 38, 19)
                    .addIngredients(recipe.cast().get());
        }

        int faucetHeight = hasCast ? 11 : 27;
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 43, 8)
                .setFluidRenderer(1, false, 6, faucetHeight)
                .addIngredient(NeoForgeTypes.FLUID_STACK, fluidStack);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 18)
                .addItemStack(recipe.result());
    }

    @SuppressWarnings("removal")
    @Override
    public void draw(ModRecipes.CastingTableRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);

        int coolingTicks = Math.max(1, recipe.coolingTime());

        IDrawableAnimated arrow = guiHelper.drawableBuilder(TEXTURE, 117, 32, 24, 17)
                .buildAnimated(coolingTicks, IDrawableAnimated.StartDirection.LEFT, false);
        arrow.draw(graphics, 58, 18);

        tableIcon.draw(graphics, 38, 35);

        boolean hasCast = recipe.cast().isPresent();
        if (hasCast) {
            if (recipe.castConsumed()) {
                checkmarkIcon.draw(graphics, 63, 39);
            } else {
                xIcon.draw(graphics, 63, 39);
            }
        }

        Font font = Minecraft.getInstance().font;
        String timeStr = Component.translatable("gui.hephaestus.time_seconds", coolingTicks / 20).getString();
        int x = 72 - font.width(timeStr) / 2;
        graphics.text(font, Component.translatable("gui.hephaestus.time_seconds", coolingTicks / 20), x, 2, Color.GRAY.getRGB());
    }

    @SuppressWarnings("removal")
    public List<Component> getTooltipStrings(ModRecipes.CastingTableRecipe recipe,
                                             IRecipeSlotsView recipeSlotsView,
                                             double mouseX, double mouseY) {
        boolean hasCast = recipe.cast().isPresent();
        if (hasCast && mouseX >= 63 && mouseX <= 76 && mouseY >= 39 && mouseY <= 50) {
            String label = recipe.castConsumed()
                    ? Component.translatable("gui.hephaestus.cast_consumed").getString()
                    : Component.translatable("gui.hephaestus.cast_reusable").getString();
            return Collections.singletonList(
                    Component.literal(label).withStyle(ChatFormatting.GRAY));
        }
        return Collections.emptyList();
    }
}