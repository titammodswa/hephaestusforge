package com.titammods.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.titammods.TitamMods;
import com.titammods.block.SmelteryControllerBlockEntity;
import com.titammods.menu.SmelteryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class SmelteryScreen extends AbstractContainerScreen<SmelteryMenu> {

    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/heating_structure.png");

    public final SmelteryControllerBlockEntity blockEntity;

    private float scrollProgress = 0.0F;
    private boolean isScrolling = false;

    public SmelteryScreen(SmelteryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.blockEntity = menu.blockEntity;
        this.imageWidth = 176;
        this.imageHeight = 220;
    }

    private int getMaxRows() {
        return (int) Math.ceil((double) this.blockEntity.itemHandler.getSlots() / 3.0);
    }

    private int getVisibleRows() {
        return Math.min(getMaxRows(), 8);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int maxRows = getMaxRows();
        int visibleRows = getVisibleRows();

        if (visibleRows > 0) {
            boolean hasScrollbar = maxRows > 8;
            int panelWidth = 74 + (hasScrollbar ? 14 : 0);
            int panelHeight = visibleRows * 18 + 8;
            int panelStartX = x - panelWidth;
            int panelStartY = y + 8;

            drawDynamicBorder(graphics, panelStartX, panelStartY, panelWidth, panelHeight);

            int slotTextureX = x - 70;
            int slotTextureY = y + 12;
            int currentRowOffset = this.menu.getCurrentRowOffset();
            int slotsLeftToDraw = this.blockEntity.itemHandler.getSlots() - (currentRowOffset * 3);

            for (int i = 0; i < (visibleRows * 3); i++) {
                int col = i % 3;
                int row = i / 3;
                int boxX = slotTextureX + col * 22;
                int boxY = slotTextureY + row * 18;
                int realIndex = i + (currentRowOffset * 3);

                if (i < slotsLeftToDraw) {
                    graphics.blit(BACKGROUND, boxX, boxY, 0, 238, 22, 18); // Fundo normal

                    if (realIndex < this.blockEntity.meltingState.length) {
                        int state = this.blockEntity.meltingState[realIndex];
                        int progress = this.blockEntity.meltingProgress[realIndex];
                        int maxTime = this.blockEntity.meltingTime[realIndex];

                        if (state == 1) {
                            graphics.blit(BACKGROUND, boxX + 1, boxY + 1, 185, 150, 3, 16);
                        } else if (state == 2) {
                            graphics.blit(BACKGROUND, boxX + 1, boxY + 1, 179, 150, 3, 16);
                        } else if (state == 3) {
                            graphics.blit(BACKGROUND, boxX + 1, boxY + 1, 182, 150, 3, 16);
                        } else if (state == 0 && maxTime > 0 && progress > 0) {
                            int barHeight = (int) (((float) progress / maxTime) * 16);
                            graphics.blit(BACKGROUND, boxX + 1, boxY + 1 + (16 - barHeight), 176, 150 + (16 - barHeight), 3, barHeight);
                        }
                    }
                } else {
                    graphics.blit(BACKGROUND, boxX, boxY, 22, 238, 22, 18); // Fundo Bloqueado (X)
                }
            }

            if (hasScrollbar) {
                int scrollTrackX = panelStartX + 4;
                int scrollTrackY = panelStartY + 4;
                int trackHeight = visibleRows * 18;

                // Fundo Sólido do Scroll Escuro (Sem Buracos de Textura)
                graphics.fill(scrollTrackX, scrollTrackY, scrollTrackX + 12, scrollTrackY + trackHeight, 0xFF373737);
                graphics.fill(scrollTrackX + 1, scrollTrackY + 1, scrollTrackX + 11, scrollTrackY + trackHeight - 1, 0xFF181818);

                int sliderHeight = 15;
                int maxScrollY = trackHeight - sliderHeight;
                int currentSliderY = scrollTrackY + (int) (this.scrollProgress * maxScrollY);

                // Botão de Scroll Estilo Vanilla 3D (Substitui textura inexistente)
                graphics.fill(scrollTrackX + 1, currentSliderY, scrollTrackX + 11, currentSliderY + sliderHeight, 0xFFC6C6C6); // Cinza Base
                graphics.fill(scrollTrackX + 1, currentSliderY, scrollTrackX + 11, currentSliderY + 1, 0xFFFFFFFF); // Brilho Topo
                graphics.fill(scrollTrackX + 1, currentSliderY, scrollTrackX + 2, currentSliderY + sliderHeight, 0xFFFFFFFF); // Brilho Esquerda
                graphics.fill(scrollTrackX + 10, currentSliderY + 1, scrollTrackX + 11, currentSliderY + sliderHeight, 0xFF555555); // Sombra Direita
                graphics.fill(scrollTrackX + 2, currentSliderY + sliderHeight - 1, scrollTrackX + 11, currentSliderY + sliderHeight, 0xFF555555); // Sombra Baixo
            }
        }

        List<FluidStack> fluids = this.blockEntity.fluidTank.getFluids();
        int totalCapacity = this.blockEntity.fluidTank.getCapacity();

        int tankX = x + 8;
        int tankY = y + 16;
        int tankWidth = 106;
        int tankHeight = 106;

        if (totalCapacity > 0 && !fluids.isEmpty()) {
            int[] heights = calcLiquidHeights(fluids, totalCapacity, tankHeight, 3);
            int currentY = tankY + tankHeight;

            for (int i = 0; i < fluids.size(); i++) {
                int h = heights[i];
                currentY -= h;
                renderFluidLayer(graphics, fluids.get(i), tankX, currentY, tankWidth, h);
            }
        }

        graphics.blit(BACKGROUND, tankX, tankY, 176, 0, 80, 106);

        if (this.blockEntity.fuelCapacity > 0 && !this.blockEntity.currentFuel.isEmpty()) {
            int fuelX = x + 152;
            int fuelY = y + 32;
            int fuelWidth = 16;
            int fuelHeight = 90;
            float fuelPercentage = (float) this.blockEntity.currentFuel.getAmount() / this.blockEntity.fuelCapacity;
            int fluidHeight = Math.max(1, Math.round(fuelPercentage * fuelHeight));
            renderFluidLayer(graphics, this.blockEntity.currentFuel, fuelX, fuelY + fuelHeight - fluidHeight, fuelWidth, fluidHeight);
        }

        if (this.menu.bucketHandler.getStackInSlot(0).isEmpty()) {
            graphics.blit(BACKGROUND, x + 125, y + 46, 224, 186, 16, 16);
        }

        if (mouseX >= x + 124 && mouseX < x + 142 && mouseY >= y + 69 && mouseY < y + 87) {
            graphics.blit(BACKGROUND, x + 124, y + 69, 176, 202, 18, 18);
        }
        graphics.blit(BACKGROUND, x + 125, y + 70, 176, 186, 16, 16);

        if (this.blockEntity.fuel > 0 && this.blockEntity.maxFuel > 0) {
            int flameHeight = (int) (((float) this.blockEntity.fuel / this.blockEntity.maxFuel) * 14);
            int fireX = x + 153;
            int fireY = y + 15;
            graphics.blit(BACKGROUND, fireX, fireY + (14 - flameHeight), 176, 136 + (14 - flameHeight), 14, flameHeight);
        }
    }

    private void drawDynamicBorder(GuiGraphics graphics, int x, int y, int width, int height) {
        // Preenchimento Sólido (Cobre o fundo sem puxar texturas do tanque)
        graphics.fill(x + 7, y + 7, x + width - 7, y + height - 7, 0xFFC6C6C6);

        graphics.blit(BACKGROUND, x, y, 0, 0, 7, 7);
        graphics.blit(BACKGROUND, x + width - 7, y, 169, 0, 7, 7);
        graphics.blit(BACKGROUND, x, y + height - 7, 0, 213, 7, 7);
        graphics.blit(BACKGROUND, x + width - 7, y + height - 7, 169, 213, 7, 7);
        graphics.blit(BACKGROUND, x + 7, y, 7, 0, width - 14, 7);
        graphics.blit(BACKGROUND, x + 7, y + height - 7, 7, 213, width - 14, 7);
        graphics.blit(BACKGROUND, x, y + 7, 0, 7, 7, height - 14);
        graphics.blit(BACKGROUND, x + width - 7, y + 7, 169, 7, 7, height - 14);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 124 && mouseX < x + 142 && mouseY >= y + 69 && mouseY < y + 87) {
            graphics.renderTooltip(this.font, Component.translatable("gui.hephaestus.tank.bucket.auto.title"), mouseX, mouseY);
        }

        if (mouseX >= x + 8 && mouseX < x + 114 && mouseY >= y + 16 && mouseY < y + 122) {
            List<FluidStack> fluids = this.blockEntity.fluidTank.getFluids();
            int totalCapacity = this.blockEntity.fluidTank.getCapacity();

            if (totalCapacity > 0 && !fluids.isEmpty()) {
                int tankHeight = 106;
                int currentY = y + 16 + tankHeight;
                int[] heights = calcLiquidHeights(fluids, totalCapacity, tankHeight, 3);

                for (int i = 0; i < fluids.size(); i++) {
                    int h = heights[i];
                    currentY -= h;

                    if (mouseY >= currentY && mouseY < currentY + h) {
                        FluidStack fluid = fluids.get(i);
                        List<Component> textComponents = new ArrayList<>();
                        textComponents.add(Component.translatable(fluid.getDescriptionId()).withStyle(net.minecraft.ChatFormatting.GOLD));
                        textComponents.add(Component.literal(fluid.getAmount() + " / " + totalCapacity + " mB").withStyle(net.minecraft.ChatFormatting.GRAY));
                        graphics.renderComponentTooltip(this.font, textComponents, mouseX, mouseY);
                        break;
                    }
                }
            }
        }

        if (mouseX >= x + 152 && mouseX < x + 168 && mouseY >= y + 32 && mouseY < y + 122) {
            if (this.blockEntity.fuelCapacity > 0) {
                List<Component> textComponents = new ArrayList<>();
                if (!this.blockEntity.currentFuel.isEmpty()) {
                    textComponents.add(Component.translatable(this.blockEntity.currentFuel.getDescriptionId()).withStyle(net.minecraft.ChatFormatting.GOLD));
                    textComponents.add(Component.literal(this.blockEntity.currentFuel.getAmount() + " / " + this.blockEntity.fuelCapacity + " mB").withStyle(net.minecraft.ChatFormatting.GRAY));
                } else {
                    textComponents.add(Component.translatable("gui.hephaestus.melting.no_fuel").withStyle(net.minecraft.ChatFormatting.RED));
                    textComponents.add(Component.literal("0 / " + this.blockEntity.fuelCapacity + " mB").withStyle(net.minecraft.ChatFormatting.GRAY));
                }
                graphics.renderComponentTooltip(this.font, textComponents, mouseX, mouseY);
            } else {
                List<Component> textComponents = new ArrayList<>();
                textComponents.add(Component.translatable("gui.hephaestus.melting.no_tank").withStyle(net.minecraft.ChatFormatting.DARK_RED));
                graphics.renderComponentTooltip(this.font, textComponents, mouseX, mouseY);
            }
        }
    }

    private int[] calcLiquidHeights(List<FluidStack> liquids, int capacity, int height, int min) {
        int[] fluidHeights = new int[liquids.size()];
        int totalFluidAmount = 0;
        if (!liquids.isEmpty()) {
            for (int i = 0; i < liquids.size(); i++) {
                FluidStack liquid = liquids.get(i);
                float h = (float) liquid.getAmount() / (float) capacity;
                totalFluidAmount += liquid.getAmount();
                fluidHeights[i] = Math.max(min, (int) Math.ceil(h * height));
            }
            if (totalFluidAmount < capacity) height -= min;
            int sum;
            do {
                sum = 0;
                int biggest = -1;
                int m = 0;
                for (int i = 0; i < fluidHeights.length; i++) {
                    sum += fluidHeights[i];
                    if (fluidHeights[i] > biggest) { biggest = fluidHeights[i]; m = i; }
                }
                if (sum > height && biggest > min) { fluidHeights[m]--; }
                else if (sum > height) { break; }
            } while (sum > height);
        }
        return fluidHeights;
    }

    private void renderFluidLayer(GuiGraphics graphics, FluidStack fluid, int x, int y, int width, int height) {
        if (fluid.isEmpty() || height <= 0) return;
        IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation texture = fluidExt.getStillTexture(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS).apply(texture);

        int color = fluidExt.getTintColor(fluid);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);
        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < height; j += 16) {
                int drawWidth = Math.min(16, width - i);
                int drawHeight = Math.min(16, height - j);
                graphics.blit(x + i, y + j, 0, drawWidth, drawHeight, sprite);
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = this.leftPos;
        int y = this.topPos;

        int visibleRows = getVisibleRows();
        int maxRows = getMaxRows();

        if (visibleRows > 0 && maxRows > 8) {
            int scrollTrackX = x - 84;
            int scrollTrackY = y + 12;
            int trackHeight = visibleRows * 18;

            if (button == 0 && mouseX >= scrollTrackX && mouseX < scrollTrackX + 12 && mouseY >= scrollTrackY && mouseY < scrollTrackY + trackHeight) {
                this.isScrolling = true;
                this.updateScrollProgress(mouseY);
                return true;
            }
        }

        if (button == 0 && mouseX >= x + 124 && mouseX < x + 142 && mouseY >= y + 69 && mouseY < y + 87) {
            Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        int tankX = x + 8;
        int tankY = y + 16;
        int tankWidth = 106;
        int tankHeight = 106;

        if (button == 0 && mouseX >= tankX && mouseX < tankX + tankWidth && mouseY >= tankY && mouseY < tankY + tankHeight) {
            List<FluidStack> fluids = this.blockEntity.fluidTank.getFluids();
            int totalCapacity = this.blockEntity.fluidTank.getCapacity();

            if (totalCapacity > 0 && fluids.size() > 1) {
                int[] heights = calcLiquidHeights(fluids, totalCapacity, tankHeight, 3);
                int currentY = tankY + tankHeight;

                for (int i = 0; i < fluids.size(); i++) {
                    int h = heights[i];
                    currentY -= h;

                    if (mouseY >= currentY && mouseY < currentY + h) {
                        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.titammods.network.FluidClickPayload(this.blockEntity.getBlockPos(), i));
                        Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) this.isScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isScrolling) {
            this.updateScrollProgress(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxRows = getMaxRows();
        if (maxRows > 8) {
            int hiddenRows = maxRows - 8;
            this.scrollProgress -= (float) (scrollY / (double) hiddenRows);
            this.scrollProgress = Math.max(0.0F, Math.min(this.scrollProgress, 1.0F));
            int rowOffset = Math.round(this.scrollProgress * hiddenRows);

            if (rowOffset != this.menu.getCurrentRowOffset()) {
                this.menu.updateScrollOffset(rowOffset);
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.titammods.network.ScrollSyncPayload(rowOffset));
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void updateScrollProgress(double mouseY) {
        int visibleRows = getVisibleRows();
        int maxRows = getMaxRows();

        if (visibleRows == 0 || maxRows <= visibleRows) return;

        int scrollTrackY = this.topPos + 12;
        int trackHeight = visibleRows * 18;

        this.scrollProgress = ((float) mouseY - scrollTrackY - 7.5F) / (trackHeight - 15.0F);
        this.scrollProgress = Math.max(0.0F, Math.min(this.scrollProgress, 1.0F));

        int hiddenRows = maxRows - visibleRows;
        int rowOffset = Math.round(this.scrollProgress * hiddenRows);

        if (rowOffset != this.menu.getCurrentRowOffset()) {
            this.menu.updateScrollOffset(rowOffset);
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.titammods.network.ScrollSyncPayload(rowOffset));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}
}