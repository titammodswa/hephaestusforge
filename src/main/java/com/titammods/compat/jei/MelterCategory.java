package com.titammods.compat.jei;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModFluids;
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
import net.minecraft.client.gui.GuiGraphicsExtractor;          // MC 26.1
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;                      // MC 26.1
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class MelterCategory implements IRecipeCategory<ModRecipes.MeltingRecipe> {

    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/jei/melting.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable tankOverlay;
    private final IDrawable blankLava;
    private final IDrawable blankItem;
    private final IDrawable blankOutput;
    private final IGuiHelper guiHelper;

    public MelterCategory(IGuiHelper guiHelper) {
        this.guiHelper  = guiHelper;
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 132, 40);
        this.icon       = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SEARED_MELTER.get()));
        this.tankOverlay = guiHelper.createDrawable(TEXTURE, 132, 0, 32, 32);
        this.blankLava   = guiHelper.createBlankDrawable(12, 32);
        this.blankItem   = guiHelper.createBlankDrawable(18, 18);
        this.blankOutput = guiHelper.createBlankDrawable(32, 32);
    }

    @SuppressWarnings("removal")
    @Override public RecipeType<ModRecipes.MeltingRecipe> getRecipeType() { return TitamModsJEIPlugin.MELTING_TYPE; }
    @Override public Component getTitle() { return Component.translatable("block.hephaestus.seared_melter"); }
    public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth()  { return 132; }
    @Override public int getHeight() { return 40; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ModRecipes.MeltingRecipe recipe, IFocusGroup focuses) {
        List<FluidStack> validFuels = new ArrayList<>();
        if (recipe.temperature() <= 1000) validFuels.add(new FluidStack(Fluids.LAVA, 1000));
        validFuels.add(new FluidStack(ModFluids.MOLTEN_BLAZE.source.get(), 1000));

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 4, 4)
                .setBackground(blankLava, 0, 0)
                .setFluidRenderer(1000, false, 12, 32)
                .addIngredients(NeoForgeTypes.FLUID_STACK, validFuels)
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(
                        Component.translatable("gui.hephaestus.temperature", recipe.temperature())
                                .withStyle(ChatFormatting.GOLD)));

        builder.addSlot(RecipeIngredientRole.INPUT, 24, 18)
                .setBackground(blankItem, -1, -1)
                .addIngredients(recipe.input());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 4)
                .setBackground(blankOutput, 0, 0)
                .setFluidRenderer(1296, false, 32, 32)
                .setOverlay(tankOverlay, 0, 0)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.output())
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(
                        Component.literal(recipe.output().getAmount() + " mB")
                                .withStyle(ChatFormatting.GRAY)));
    }

    @SuppressWarnings("removal")
    @Override
    public void draw(ModRecipes.MeltingRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        int meltingTicks = Math.max(1, recipe.time());
        IDrawableAnimated arrow = guiHelper.drawableBuilder(TEXTURE, 150, 41, 24, 17)
                .buildAnimated(meltingTicks, IDrawableAnimated.StartDirection.LEFT, false);
        arrow.draw(graphics, 56, 18);

        Font font = Minecraft.getInstance().font;
        String tempString = recipe.temperature() + "°C";
        int textX = 68 - font.width(tempString) / 2;
        graphics.text(font, Component.literal(tempString), textX, 3, Color.GRAY.getRGB());
    }
}
