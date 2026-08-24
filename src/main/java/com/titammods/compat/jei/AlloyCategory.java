package com.titammods.compat.jei;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModFluids;
import com.titammods.setup.ModRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class AlloyCategory implements IRecipeCategory<ModRecipes.AlloyRecipe> {

    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/jei/alloy.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable tankOverlay;
    private final IGuiHelper guiHelper;

    public AlloyCategory(IGuiHelper guiHelper) {
        this.guiHelper   = guiHelper;
        this.background  = guiHelper.createDrawable(TEXTURE, 0, 0, 172, 62);
        this.icon        = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()));
        this.tankOverlay = guiHelper.createDrawable(TEXTURE, 172, 17, 16, 16);
    }

    @SuppressWarnings("removal")
    @Override public RecipeType<ModRecipes.AlloyRecipe> getRecipeType() { return TitamModsJEIPlugin.ALLOY_TYPE; }
    @Override public Component getTitle() { return Component.translatable("gui.hephaestus.alloying"); }
    @Override public int getWidth()  { return 172; }
    @Override public int getHeight() { return 62; }

    @SuppressWarnings("removal")
    public IDrawable getBackground() { return background; }
    @SuppressWarnings("removal")
    @Override public IDrawable getIcon() { return icon; }
    @SuppressWarnings("removal")
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ModRecipes.AlloyRecipe recipe, IFocusGroup focuses) {
        List<FluidStack> inputs = recipe.inputFluids();
        int count = inputs.size();

        if (count > 0) {
            int totalW = 48, h = 32, x = 19, y = 11;
            int maxAmt = recipe.output().getAmount();
            for (FluidStack in : inputs) if (in.getAmount() > maxAmt) maxAmt = in.getAmount();

            int w = totalW / count;
            for (int i = 0; i < count; i++) {
                int fx = x + i * w;
                int fw = (i == count - 1) ? (totalW - w * i) : w;
                FluidStack in = inputs.get(i);
                builder.addSlot(RecipeIngredientRole.INPUT, fx, y)
                        .setFluidRenderer(maxAmt, false, fw, h)
                        .addIngredient(NeoForgeTypes.FLUID_STACK, in)
                        .addRichTooltipCallback((view, tooltip) ->
                                tooltip.add(Component.translatable("gui.hephaestus.fluid_mb", in.getAmount())
                                        .withStyle(ChatFormatting.GRAY)));
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 137, 11)
                .setFluidRenderer(recipe.output().getAmount(), false, 16, 32)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.output())
                .addRichTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("gui.hephaestus.fluid_mb", recipe.output().getAmount())
                                .withStyle(ChatFormatting.GRAY)));

        List<FluidStack> fuels = new ArrayList<>();
        if (recipe.temperature() <= 1000) fuels.add(new FluidStack(Fluids.LAVA, 1000));
        fuels.add(new FluidStack(ModFluids.MOLTEN_BLAZE.source.get(), 1000));

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 94, 43)
                .setFluidRenderer(1000, false, 16, 16)
                .setOverlay(tankOverlay, 0, 0)
                .addIngredients(NeoForgeTypes.FLUID_STACK, fuels)
                .addRichTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("gui.hephaestus.temperature", recipe.temperature())
                                .withStyle(ChatFormatting.GOLD)));
    }

    @SuppressWarnings("removal")
    @Override
    public void draw(ModRecipes.AlloyRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphicsExtractor graphics, double mx, double my) {
        background.draw(graphics);

        IDrawableAnimated arrow = guiHelper.drawableBuilder(TEXTURE, 172, 0, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        arrow.draw(graphics, 90, 21);

        Font font = Minecraft.getInstance().font;
        String temp = Component.translatable("gui.hephaestus.temperature_value", recipe.temperature()).getString();
        int tx = 102 - font.width(temp) / 2;
        graphics.text(font, Component.translatable("gui.hephaestus.temperature_value", recipe.temperature()),
                tx, 5, Color.GRAY.getRGB());
    }
}