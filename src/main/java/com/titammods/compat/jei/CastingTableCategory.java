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
import java.util.Collections;
import java.util.List;

public class CastingTableCategory implements IRecipeCategory<ModRecipes.CastingTableRecipe> {
    public static final ResourceLocation BACKGROUND_LOC = ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/jei/casting.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable tankOverlay;
    private final IDrawable tableIcon;
    private final IDrawable checkmarkIcon;
    private final IDrawable xIcon;
    private final IGuiHelper guiHelper;

    public CastingTableCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 117, 54);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SEARED_TABLE.get()));
        this.tankOverlay = guiHelper.createDrawable(BACKGROUND_LOC, 133, 0, 32, 32);
        this.tableIcon = guiHelper.createDrawable(BACKGROUND_LOC, 117, 0, 16, 16);

        this.xIcon = guiHelper.createDrawable(BACKGROUND_LOC, 141, 32, 13, 11);
        this.checkmarkIcon = guiHelper.createDrawable(BACKGROUND_LOC, 141, 43, 13, 11);
    }

    @Override public RecipeType<ModRecipes.CastingTableRecipe> getRecipeType() { return TitamModsJEIPlugin.CASTING_TABLE_TYPE; }
    @Override public Component getTitle() { return Component.translatable("block.hephaestus.seared_table"); }
    @SuppressWarnings("removal") @Override public IDrawable getBackground() { return background; }
    @SuppressWarnings("removal") @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ModRecipes.CastingTableRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                .setFluidRenderer(1296, false, 32, 32)
                .setOverlay(tankOverlay, 0, 0)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.fluid());

        if (recipe.cast() != null && !recipe.cast().isEmpty()) {
            RecipeIngredientRole role = recipe.castConsumed() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.CATALYST;
            builder.addSlot(role, 38, 19)
                    .addIngredients(recipe.cast());
        }

        int faucetHeight = (recipe.cast() == null || recipe.cast().isEmpty()) ? 27 : 11;
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 43, 8)
                .setFluidRenderer(1, false, 6, faucetHeight)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.fluid());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 18)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(ModRecipes.CastingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        int coolingTicks = Math.max(1, recipe.coolingTime());
        IDrawableAnimated arrow = guiHelper.drawableBuilder(BACKGROUND_LOC, 117, 32, 24, 17)
                .buildAnimated(coolingTicks, IDrawableAnimated.StartDirection.LEFT, false);

        arrow.draw(graphics, 58, 18);
        tableIcon.draw(graphics, 38, 35);

        if (recipe.cast() != null && !recipe.cast().isEmpty()) {
            if (recipe.castConsumed()) {
                checkmarkIcon.draw(graphics, 63, 39);
            } else {
                xIcon.draw(graphics, 63, 39);
            }
        }

        Font font = Minecraft.getInstance().font;
        String timeStr = (coolingTicks / 20) + "s";
        int x = 72 - font.width(timeStr) / 2;
        graphics.drawString(font, timeStr, x, 2, Color.GRAY.getRGB(), false);
    }

    @SuppressWarnings("removal")
    @Override
    public List<Component> getTooltipStrings(ModRecipes.CastingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (recipe.cast() != null && !recipe.cast().isEmpty()) {
            if (mouseX >= 63 && mouseX <= 63 + 13 && mouseY >= 39 && mouseY <= 39 + 11) {
                return Collections.singletonList(Component.literal("Cast Consumed: " + recipe.castConsumed()).withStyle(ChatFormatting.GRAY));
            }
        }
        return Collections.emptyList();
    }
}
