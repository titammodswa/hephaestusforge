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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.awt.Color;
import java.util.List;

public class EntityMeltingCategory implements IRecipeCategory<ModRecipes.EntityMeltingRecipe> {

    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/jei/melting.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawable tankOverlay;

    public EntityMeltingCategory(IGuiHelper gui) {
        this.background  = gui.createDrawable(TEXTURE, 0, 41, 150, 62);
        this.icon        = gui.createDrawableItemStack(new ItemStack(ModBlocks.SMELTERY_CONTROLLER.get()));
        this.arrow       = gui.drawableBuilder(TEXTURE, 150, 41, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        this.tankOverlay = gui.createDrawable(TEXTURE, 150, 74, 16, 16);
    }

    @Override public RecipeType<ModRecipes.EntityMeltingRecipe> getRecipeType() { return TitamModsJEIPlugin.ENTITY_MELTING_TYPE; }
    @Override public Component getTitle() { return Component.translatable("gui.hephaestus.entity_melting"); }
    @Override public int getWidth()  { return 150; }
    @Override public int getHeight() { return 62; }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon()       { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ModRecipes.EntityMeltingRecipe recipe, IFocusGroup focuses) {
        SpawnEggItem egg = SpawnEggItem.byId(recipe.entityType());
        if (egg != null) {
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                    .addItemStacks(List.of(new ItemStack(egg)));
        }

        FluidStack output = recipe.output();
        if (!output.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 11)
                    .setFluidRenderer(Math.max(output.getAmount(), 144) * 10, false, 16, 32)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(output))
                    .addRichTooltipCallback((view, tooltip) -> {
                        tooltip.add(Component.translatable("gui.hephaestus.fluid_mb", output.getAmount())
                                .withStyle(ChatFormatting.GRAY));
                        float hearts = recipe.damage() / 2f;
                        tooltip.add(Component.translatable("gui.hephaestus.entity_melting.per_heart", hearts)
                                .withStyle(ChatFormatting.RED));
                    });
        }

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 75, 43)
                .setFluidRenderer(1000, false, 16, 16)
                .setOverlay(tankOverlay, 0, 0)
                .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(
                        new FluidStack(Fluids.LAVA, 1000),
                        new FluidStack(ModFluids.MOLTEN_BLAZE.source.get(), 1000)
                ))
                .addRichTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("gui.hephaestus.temperature")
                                .withStyle(ChatFormatting.GOLD)));
    }

    @Override
    public void draw(ModRecipes.EntityMeltingRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics graphics, double mx, double my) {
        background.draw(graphics);
        arrow.draw(graphics, 71, 21);

        EntityRenderHelper.render(graphics, 11, 5, 48, recipe.entityType());

        Font font = Minecraft.getInstance().font;
        String dmg = Float.toString(recipe.damage() / 2f);
        int tx = 68 - font.width(dmg);
        graphics.drawString(font, Component.literal(dmg).withStyle(ChatFormatting.RED), tx, 8, Color.RED.getRGB(), false);
        graphics.drawString(font, Component.literal("♥").withStyle(ChatFormatting.RED),
                tx + font.width(dmg) + 1, 8, Color.RED.getRGB(), false);
    }
}