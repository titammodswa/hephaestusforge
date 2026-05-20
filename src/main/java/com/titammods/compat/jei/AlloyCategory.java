package com.titammods.compat.jei;

import com.titammods.TitamMods;
import com.titammods.recipe.AlloyRecipe;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModFluids;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class AlloyCategory implements IRecipeCategory<AlloyRecipe> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/jei/alloy.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawable tankOverlay;

    public AlloyCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 172, 62);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()));
        this.arrow = guiHelper.drawableBuilder(TEXTURE, 172, 0, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        this.tankOverlay = guiHelper.createDrawable(TEXTURE, 172, 17, 16, 16);
    }

    @Override
    public RecipeType<AlloyRecipe> getRecipeType() {
        return TitamModsJEIPlugin.ALLOY_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.hephaestus.alloying");
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloyRecipe recipe, IFocusGroup focuses) {
        List<FluidStack> inputs = recipe.inputs();
        int count = inputs.size();
        if (count > 0) {
            int totalWidth = 48;
            int height = 32;
            int x = 19;
            int y = 11;

            int maxAmount = recipe.output().getAmount();
            for (FluidStack input : inputs) {
                if (input.getAmount() > maxAmount) {
                    maxAmount = input.getAmount();
                }
            }

            int w = totalWidth / count;
            for (int i = 0; i < count; i++) {
                int fluidX = x + i * w;
                int currentW = (i == count - 1) ? (totalWidth - (w * i)) : w;

                FluidStack input = inputs.get(i);
                builder.addSlot(RecipeIngredientRole.INPUT, fluidX, y)
                        .setFluidRenderer(maxAmount, false, currentW, height)
                        .addIngredient(NeoForgeTypes.FLUID_STACK, input)
                        .addRichTooltipCallback((view, tooltip) -> {
                            tooltip.add(Component.literal(input.getAmount() + " mB").withStyle(ChatFormatting.GRAY));
                        });
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 137, 11)
                .setFluidRenderer(recipe.output().getAmount(), false, 16, 32)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.output())
                .addRichTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.literal(recipe.output().getAmount() + " mB").withStyle(ChatFormatting.GRAY));
                });

        List<FluidStack> validFuels = new ArrayList<>();
        if (recipe.temperature() <= 1000) {
            validFuels.add(new FluidStack(Fluids.LAVA, 1000));
        }
        validFuels.add(new FluidStack(ModFluids.MOLTEN_BLAZE.source.get(), 1000));

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 94, 43)
                .setFluidRenderer(1000, false, 16, 16)
                .setOverlay(tankOverlay, 0, 0)
                .addIngredients(NeoForgeTypes.FLUID_STACK, validFuels)
                .addRichTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.translatable("gui.hephaestus.temperature", recipe.temperature()).withStyle(ChatFormatting.GOLD));
                });
    }

    @Override
    public void draw(AlloyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 90, 21);

        Font fontRenderer = Minecraft.getInstance().font;
        String tempString = recipe.temperature() + "°C";
        int x = 102 - (fontRenderer.width(tempString) / 2);
        graphics.drawString(fontRenderer, tempString, x, 5, Color.GRAY.getRGB(), false);
    }
}