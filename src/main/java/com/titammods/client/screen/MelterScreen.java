package com.titammods.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.titammods.TitamMods;
import com.titammods.menu.MelterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class MelterScreen extends AbstractContainerScreen<MelterMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/melter.png");

    public MelterScreen(MelterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if (this.menu.isBurning()) {
            int fireHeight = this.menu.getScaledFuel();
            graphics.blit(TEXTURE, x + 152, y + 15 + 14 - fireHeight, 176, 136 + 14 - fireHeight, 14, fireHeight);
        }

        Level level = this.menu.getBlockEntity().getLevel();
        if (level != null) {
            BlockPos posBelow = this.menu.getBlockEntity().getBlockPos().below();
            IFluidHandler fuelTank = level.getCapability(Capabilities.FluidHandler.BLOCK, posBelow, Direction.UP);
            if (fuelTank != null) {
                graphics.blit(TEXTURE, x + 152, y + 31, 194, 52, 14, 38);
                FluidStack fuelFluid = fuelTank.getFluidInTank(0);
                if (!fuelFluid.isEmpty()) {
                    int fuelCap = fuelTank.getTankCapacity(0);
                    int fuelHeight = (int) (((float) fuelFluid.getAmount() / fuelCap) * 36);
                    if (fuelHeight > 0) {
                        drawTiledFluid(graphics, x + 153, y + 32 + 36 - fuelHeight, 12, fuelHeight, fuelFluid);
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            int state = this.menu.getState(i);
            if (state == 0) continue;

            int progressHeight = this.menu.getScaledProgress(i);
            int texX = 176;

            if (state == 4) {
                texX = 179;
                progressHeight = 16;
            } else if (state == 3) {
                texX = 185;
                progressHeight = 16;
            } else if (state == 2) {
                texX = 182;
                progressHeight = 16;
            }

            if (progressHeight > 0) {
                graphics.blit(TEXTURE, x + 18, y + 16 + (i * 18) + 16 - progressHeight, texX, 150 + 16 - progressHeight, 3, progressHeight);
            }
        }

        int tankX = x + 90;
        int tankY = y + 16;
        FluidStack fluid = this.menu.getBlockEntity().tank.getFluid();

        if (!fluid.isEmpty()) {
            float capacity = this.menu.getBlockEntity().tank.getCapacity();
            int fluidHeight = (int) ((fluid.getAmount() / capacity) * 52);
            if (fluidHeight > 0) {
                drawTiledFluid(graphics, tankX, tankY + 52 - fluidHeight, 52, fluidHeight, fluid);
            }
        }
        graphics.blit(TEXTURE, tankX, tankY, 176, 0, 52, 52);
    }

    private void drawTiledFluid(GuiGraphics graphics, int x, int y, int width, int height, FluidStack fluid) {
        if (fluid.isEmpty() || width <= 0 || height <= 0) return;
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation stillTexture = ext.getStillTexture(fluid);
        if (stillTexture == null) return;
        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

        int color = ext.getTintColor(fluid);
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;

        graphics.flush();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.enableBlend();

        Matrix4f matrix = graphics.pose().last().pose();
        com.mojang.blaze3d.vertex.Tesselator tesselator = com.mojang.blaze3d.vertex.Tesselator.getInstance();
        com.mojang.blaze3d.vertex.BufferBuilder builder = tesselator.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < width; i += 16) {
            int drawWidth = Math.min(width - i, 16);
            for (int j = 0; j < height; j += 16) {
                int drawHeight = Math.min(height - j, 16);
                int drawX = x + i;
                int drawY = y + height - j - drawHeight;

                float minU = sprite.getU0();
                float maxU = sprite.getU0() + (sprite.getU1() - sprite.getU0()) * ((float) drawWidth / 16.0F);
                float minV = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * ((16.0F - drawHeight) / 16.0F);
                float maxV = sprite.getV1();

                builder.addVertex(matrix, drawX, drawY + drawHeight, 0).setUv(minU, maxV);
                builder.addVertex(matrix, drawX + drawWidth, drawY + drawHeight, 0).setUv(maxU, maxV);
                builder.addVertex(matrix, drawX + drawWidth, drawY, 0).setUv(maxU, minV);
                builder.addVertex(matrix, drawX, drawY, 0).setUv(minU, minV);
            }
        }
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        int tankX = leftPos + 90;
        int tankY = topPos + 16;

        if (mouseX >= tankX && mouseX < tankX + 52 && mouseY >= tankY && mouseY < tankY + 52) {
            FluidStack fluid = this.menu.getBlockEntity().tank.getFluid();

            if (!fluid.isEmpty()) {
                int amount = fluid.getAmount();
                int cap = this.menu.getBlockEntity().tank.getCapacity();
                List<Component> tooltip = new ArrayList<>();

                tooltip.add(fluid.getHoverName());

                int blocks = amount / 900;
                int ingots = (amount % 900) / 90;
                int nuggets = (amount % 90) / 10;
                int mb = amount % 10;

                StringBuilder sb = new StringBuilder();
                if (blocks > 0) sb.append(blocks).append(" Blocos ");
                if (ingots > 0) sb.append(ingots).append(" Barras ");
                if (nuggets > 0) sb.append(nuggets).append(" Pepitas ");
                if (mb > 0 || sb.length() == 0) sb.append(mb).append(" mB");

                tooltip.add(Component.literal(sb.toString().trim()).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal(amount + " / " + cap + " mB").withStyle(ChatFormatting.DARK_GRAY));

                graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
            } else {
                graphics.renderTooltip(font, Component.literal("Capacidade: 2700 mB").withStyle(ChatFormatting.GRAY), mouseX, mouseY);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }
}