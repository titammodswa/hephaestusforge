package com.titammods.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.titammods.TitamMods;
import com.titammods.block.SmelteryControllerBlockEntity;
import com.titammods.menu.SmelteryMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SmelteryScreen extends AbstractContainerScreen<SmelteryMenu> {

    public static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/heating_structure.png");
    private static final int TEX = 256;

    private static final int SLOT_W       = 22;
    private static final int SLOT_H       = 18;
    private static final int MAX_VIS_ROWS = 8;

    public final SmelteryControllerBlockEntity blockEntity;

    private boolean shiftHeld      = false;
    private float   scrollProgress = 0f;
    private boolean isScrolling    = false;

    public SmelteryScreen(SmelteryMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.blockEntity = menu.blockEntity;
        this.imageWidth  = 176;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
    }

    private int totalSlots()  { return blockEntity != null ? blockEntity.itemHandler.getSlots() : 0; }
    private int getCols()     { return SmelteryMenu.calcColumns(totalSlots()); }
    private int getTotalRows(){ int c = getCols(); return c > 0 ? (int) Math.ceil((double) totalSlots() / c) : 0; }
    private int getVisRows()  { return Math.min(getTotalRows(), MAX_VIS_ROWS); }
    private boolean hasScroll(){ return getTotalRows() > MAX_VIS_ROWS; }

    private int panelW() { return getCols() * SLOT_W + 9; }
    private int panelH() { return 4 + 7 + getVisRows() * SLOT_H + 7; }
    private int panelX() { return this.leftPos - panelW(); }
    private int panelY() { return this.topPos + 16; }
    private int slotsBaseX() { return this.leftPos - 22; }
    private int slotsBaseY() { return panelY() + 7 + 1; }

    private void drawDynamicBorder(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x + 7, y + 7, x + w - 7, y + h - 7, 0xFFC6C6C6);

        blit(g, x,         y,         0,   0,   7, 7);
        blit(g, x,         y + h - 7, 0,   213, 7, 7);
        blit(g, x + 7,     y,         7,   0,   w - 7, 7);
        blit(g, x + 7,     y + h - 7, 7,   213, w - 7, 7);
        blit(g, x,         y + 7,     0,   7,   7, h - 14);
    }

    private void blit(GuiGraphics g, int x, int y, int u, int v, int w, int h) {
        g.blit(BACKGROUND, x, y, u, v, w, h);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int x = this.leftPos;
        int y = this.topPos;

        blit(graphics, x, y, 0, 0, 176, 220);

        int total   = totalSlots();
        int cols    = getCols();
        int visRows = getVisRows();
        int rowOff  = menu.getCurrentRowOffset();

        if (visRows > 0) {
            int pw = panelW(), ph = panelH();
            int px = panelX(), py = panelY();
            int bx = slotsBaseX(), by = slotsBaseY();

            drawDynamicBorder(graphics, px, py, pw, ph);

            int fullRows = Math.min(visRows, (int) Math.ceil((double) total / cols) - rowOff);
            for (int row = 0; row < fullRows; row++) {
                int slotsInRow = (row < fullRows - 1) ? cols
                        : Math.min(cols, total - (rowOff + row) * cols);
                int rowY = by + row * SLOT_H;

                for (int col = 0; col < slotsInRow; col++)
                    blit(graphics, bx - col * SLOT_W, rowY, 0, 238, SLOT_W, SLOT_H);
                for (int col = slotsInRow; col < cols; col++)
                    blit(graphics, bx - col * SLOT_W, rowY, 22, 238, SLOT_W, SLOT_H);

                for (int col = 0; col < slotsInRow; col++) {
                    int realIdx = (rowOff + row) * cols + col;
                    drawSlotProgress(graphics, bx - col * SLOT_W, rowY, realIdx);
                }
            }

            if (hasScroll()) {
                int totalRows = getTotalRows();
                int tw     = 6;
                int tx     = px - tw + 8;
                int ty     = py + 7;
                int trackH = visRows * SLOT_H;

                graphics.fill(tx, ty, tx + tw, ty + trackH, 0xFF373737);
                graphics.fill(tx + 1, ty + 1, tx + tw - 1, ty + trackH - 1, 0xFF181818);

                int thumbH = Math.max(10, trackH * visRows / totalRows);
                int travel = trackH - thumbH;
                int hidden = totalRows - visRows;
                int sly    = ty + (hidden > 0 ? Math.round(scrollProgress * travel) : 0);

                graphics.fill(tx + 1,      sly,             tx + tw - 1, sly + thumbH, 0xFFC6C6C6);
                graphics.fill(tx + 1,      sly,             tx + tw - 1, sly + 1,      0xFFFFFFFF);
                graphics.fill(tx + 1,      sly,             tx + 2,      sly + thumbH, 0xFFFFFFFF);
                graphics.fill(tx + tw - 1, sly + 1,         tx + tw,     sly + thumbH, 0xFF555555);
                graphics.fill(tx + 2,      sly + thumbH - 1, tx + tw - 1, sly + thumbH, 0xFF555555);
            }
        }

        List<FluidStack> fluids    = blockEntity.fluidTank.getFluids();
        int totalCapacity          = blockEntity.fluidTank.getCapacity();
        int tankX = x + 8, tankY = y + 16, tankW = 106, tankH = 106;

        if (totalCapacity > 0 && !fluids.isEmpty()) {
            int[] heights  = calcLiquidHeights(fluids, totalCapacity, tankH, 3);
            int currentY   = tankY + tankH;
            for (int i = 0; i < fluids.size(); i++) {
                currentY -= heights[i];
                renderFluidLayer(graphics, fluids.get(i), tankX, currentY, tankW, heights[i]);
            }
        }
        blit(graphics, tankX, tankY, 176, 0, 80, 106);

        if (blockEntity.fuelCapacity > 0 && !blockEntity.currentFuel.isEmpty()) {
            int fuelX = x + 152, fuelY = y + 32, fuelW = 16, fuelH = 90;
            float pct = (float) blockEntity.currentFuel.getAmount() / blockEntity.fuelCapacity;
            int fh    = Math.max(1, Math.round(pct * fuelH));
            renderFluidLayer(graphics, blockEntity.currentFuel, fuelX, fuelY + fuelH - fh, fuelW, fh);
        }

        if (menu.bucketHandler.getStackInSlot(0).isEmpty())
            blit(graphics, x + 125, y + 46, 224, 186, 16, 16);

        if (mouseX >= x + 124 && mouseX < x + 142 && mouseY >= y + 69 && mouseY < y + 87)
            blit(graphics, x + 124, y + 69, 176, 202, 18, 18);
        blit(graphics, x + 125, y + 70, 176, 186, 16, 16);

        if (blockEntity.fuel > 0 && blockEntity.maxFuel > 0) {
            int fh2 = (int)(((float) blockEntity.fuel / blockEntity.maxFuel) * 14);
            blit(graphics, x + 153, y + 15 + (14 - fh2), 176, 136 + (14 - fh2), 14, fh2);
        }
    }

    private void drawSlotProgress(GuiGraphics g, int slotX, int slotY, int realIdx) {
        if (realIdx >= blockEntity.meltingState.length) return;
        int state    = blockEntity.meltingState[realIdx];
        int progress = blockEntity.meltingProgress[realIdx];
        int maxTime  = blockEntity.meltingTime[realIdx];

        if (state == 1) {
            blit(g, slotX + 1, slotY + 1, 185, 150, 3, 16);
        } else if (state == 2) {
            blit(g, slotX + 1, slotY + 1, 179, 150, 3, 16);
        } else if (state == 3) {
            blit(g, slotX + 1, slotY + 1, 182, 150, 3, 16);
        } else if (state == 0 && maxTime > 0 && progress > 0) {
            int barH = (int)(((float) progress / maxTime) * 16);
            blit(g, slotX + 1, slotY + 1 + (16 - barH), 176, 150 + (16 - barH), 3, barH);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 124 && mouseX < x + 142 && mouseY >= y + 69 && mouseY < y + 87)
            graphics.renderTooltip(font,
                    Component.translatable("gui.hephaestus.tank.bucket.auto.title"), mouseX, mouseY);

        int tankX = x + 8, tankY = y + 16, tankW = 106, tankH = 106;
        if (mouseX >= tankX && mouseX < tankX + tankW && mouseY >= tankY && mouseY < tankY + tankH) {
            List<FluidStack> fluids = blockEntity.fluidTank.getFluids();
            int cap                 = blockEntity.fluidTank.getCapacity();
            if (cap > 0 && !fluids.isEmpty()) {
                int[] heights  = calcLiquidHeights(fluids, cap, tankH, 3);
                int currentY   = tankY + tankH;
                int total      = fluids.stream().mapToInt(FluidStack::getAmount).sum();

                for (int i = 0; i < fluids.size(); i++) {
                    currentY -= heights[i];
                    if (mouseY >= currentY && mouseY < currentY + heights[i]) {
                        graphics.renderComponentTooltip(font,
                                fluidTooltip(fluids.get(i), cap, shiftHeld), mouseX, mouseY);
                        return;
                    }
                }
                if (!fluids.isEmpty()) {
                    List<Component> tips = new ArrayList<>();
                    tips.add(Component.translatable("gui.hephaestus.tank_capacity", total, cap)
                            .withStyle(ChatFormatting.GRAY));
                    if (shiftHeld) {
                        int free = cap - total;
                        tips.add(Component.translatable("gui.hephaestus.tank_free", free)
                                .withStyle(ChatFormatting.DARK_GRAY));
                    } else {
                        tips.add(Component.translatable("gui.hephaestus.shift_hint")
                                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                    }
                    graphics.renderComponentTooltip(font, tips, mouseX, mouseY);
                }
            } else if (cap > 0) {
                graphics.renderComponentTooltip(font,
                        List.of(Component.translatable("gui.hephaestus.tank_capacity", 0, cap)
                                .withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
            }
        }

        if (mouseX >= x + 152 && mouseX < x + 168 && mouseY >= y + 32 && mouseY < y + 122) {
            List<Component> tips = new ArrayList<>();
            if (blockEntity.fuelCapacity > 0) {
                if (!blockEntity.currentFuel.isEmpty()) {
                    tips.add(Component.translatable(blockEntity.currentFuel.getDescriptionId())
                            .withStyle(ChatFormatting.GOLD));
                    tips.add(Component.translatable("gui.hephaestus.fluid_amount",
                                    blockEntity.currentFuel.getAmount(), blockEntity.fuelCapacity)
                            .withStyle(ChatFormatting.GRAY));
                } else {
                    tips.add(Component.translatable("gui.hephaestus.melting.no_fuel")
                            .withStyle(ChatFormatting.RED));
                    tips.add(Component.literal("0 / " + blockEntity.fuelCapacity + " mB")
                            .withStyle(ChatFormatting.GRAY));
                }
            } else {
                tips.add(Component.translatable("gui.hephaestus.melting.no_tank")
                        .withStyle(ChatFormatting.DARK_RED));
            }
            graphics.renderComponentTooltip(font, tips, mouseX, mouseY);
        }
    }

    private List<Component> fluidTooltip(FluidStack fluid, int totalCap, boolean shift) {
        List<Component> tips = new ArrayList<>();
        tips.add(Component.translatable(fluid.getDescriptionId()).withStyle(ChatFormatting.GOLD));
        int amt = fluid.getAmount();
        if (shift) {
            tips.add(fluidBreakdown(amt).withStyle(ChatFormatting.GRAY));
            tips.add(Component.translatable("gui.hephaestus.fluid_amount", amt, totalCap)
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tips.add(Component.translatable("gui.hephaestus.fluid_amount", amt, totalCap)
                    .withStyle(ChatFormatting.GRAY));
            tips.add(Component.translatable("gui.hephaestus.shift_hint")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
        return tips;
    }

    private net.minecraft.network.chat.MutableComponent fluidBreakdown(int mb) {
        int blocks  = mb / 1296;
        int ingots  = (mb % 1296) / 144;
        int nuggets = (mb % 144) / 16;
        int rem     = mb % 16;
        var sb = new StringBuilder();
        if (blocks  > 0) sb.append(blocks).append(" ")
                .append(Component.translatable("gui.hephaestus.unit.blocks").getString()).append("  ");
        if (ingots  > 0) sb.append(ingots).append(" ")
                .append(Component.translatable("gui.hephaestus.unit.ingots").getString()).append("  ");
        if (nuggets > 0) sb.append(nuggets).append(" ")
                .append(Component.translatable("gui.hephaestus.unit.nuggets").getString()).append("  ");
        if (rem > 0 || sb.isEmpty()) sb.append(rem).append(" mB");
        return Component.literal(sb.toString().trim());
    }

    private void renderFluidLayer(GuiGraphics graphics, FluidStack fluid, int x, int y, int width, int height) {
        if (fluid.isEmpty() || height <= 0) return;
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation tex = ext.getStillTexture(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(tex);

        int color = ext.getTintColor(fluid);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >>  8) & 0xFF) / 255f;
        float b = (color         & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a == 0f ? 1f : a);
        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < height; j += 16) {
                int dw = Math.min(16, width - i);
                int dh = Math.min(16, height - j);
                graphics.blit(x + i, y + j, 0, dw, dh, sprite);
            }
        }
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private int[] calcLiquidHeights(List<FluidStack> fluids, int capacity, int height, int min) {
        int[] h     = new int[fluids.size()];
        int total   = 0;
        for (int i = 0; i < fluids.size(); i++) {
            total += fluids.get(i).getAmount();
            h[i] = Math.max(min, (int) Math.ceil((float) fluids.get(i).getAmount() / capacity * height));
        }
        if (total < capacity) height -= min;
        int sum;
        do {
            sum = 0; int big = -1, bigI = 0;
            for (int i = 0; i < h.length; i++) { sum += h[i]; if (h[i] > big) { big = h[i]; bigI = i; } }
            if (sum > height && big > min) h[bigI]--;
            else break;
        } while (sum > height);
        return h;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)
            shiftHeld = true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)
            shiftHeld = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = this.leftPos, y = this.topPos;

        if (hasScroll()) {
            int tw = 6, tx = panelX() - tw + 8;
            int ty = panelY() + 7;
            int trackH  = getVisRows() * SLOT_H;
            if (button == 0 && mouseX >= tx && mouseX < tx + tw
                    && mouseY >= ty && mouseY < ty + trackH) {
                isScrolling = true;
                updateScrollProgress(mouseY);
                return true;
            }
        }

        int tankX = x + 8, tankY = y + 16, tankW = 106, tankH = 106;
        if (button == 0 && mouseX >= tankX && mouseX < tankX + tankW
                && mouseY >= tankY && mouseY < tankY + tankH) {
            List<FluidStack> fluids = blockEntity.fluidTank.getFluids();
            int cap = blockEntity.fluidTank.getCapacity();
            if (cap > 0 && fluids.size() > 1) {
                int[] heights  = calcLiquidHeights(fluids, cap, tankH, 3);
                int currentY   = tankY + tankH;
                for (int i = 0; i < fluids.size(); i++) {
                    currentY -= heights[i];
                    if (mouseY >= currentY && mouseY < currentY + heights[i]) {
                        net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                                new com.titammods.network.FluidClickPayload(blockEntity.getBlockPos(), i));
                        Minecraft.getInstance().getSoundManager().play(
                                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1f));
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) isScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (isScrolling) { updateScrollProgress(mouseY); return true; }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int totalRows = getTotalRows();
        int visRows   = getVisRows();
        if (totalRows > MAX_VIS_ROWS) {
            int hiddenRows = totalRows - visRows;
            int trackH = visRows * SLOT_H;
            int thumbH = Math.max(10, trackH * visRows / totalRows);
            int travel = trackH - thumbH;

            if (travel > 0)
                scrollProgress -= (float)(scrollY * thumbH) / travel / hiddenRows;
            scrollProgress = Math.max(0f, Math.min(scrollProgress, 1f));

            int rowOffset = Math.round(scrollProgress * hiddenRows);
            if (rowOffset != menu.getCurrentRowOffset()) {
                menu.updateScrollOffset(rowOffset);
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                        new com.titammods.network.ScrollSyncPayload(rowOffset));
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void updateScrollProgress(double mouseY) {
        int visRows   = getVisRows();
        int totalRows = getTotalRows();
        if (visRows == 0 || totalRows <= visRows) return;

        int ty     = panelY() + 7;
        int trackH = visRows * SLOT_H;
        int thumbH = Math.max(10, trackH * visRows / totalRows);
        int travel = trackH - thumbH;

        scrollProgress = (float)(mouseY - ty - thumbH / 2.0) / travel;
        scrollProgress = Math.max(0f, Math.min(scrollProgress, 1f));

        int rowOffset = Math.round(scrollProgress * (totalRows - visRows));
        if (rowOffset != menu.getCurrentRowOffset()) {
            menu.updateScrollOffset(rowOffset);
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                    new com.titammods.network.ScrollSyncPayload(rowOffset));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}
}