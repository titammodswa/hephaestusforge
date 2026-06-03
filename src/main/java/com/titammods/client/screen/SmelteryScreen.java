package com.titammods.client.screen;

import com.titammods.TitamMods;
import com.titammods.common.blockentities.SmelteryControllerBlockEntity;
import com.titammods.menu.SmelteryMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SmelteryScreen extends AbstractContainerScreen<SmelteryMenu> {

    public static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/heating_structure.png");
    private static final int TEX = 256;

    public final SmelteryControllerBlockEntity blockEntity;

    private boolean shiftHeld = false;
    private float scrollProgress = 0f;
    private boolean isScrolling  = false;

    public SmelteryScreen(SmelteryMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.blockEntity = menu.blockEntity;
    }

    @Override
    protected void init() {
        try {
            var f = net.minecraft.client.gui.screens.inventory.AbstractContainerScreen.class
                    .getDeclaredField("imageHeight");
            f.setAccessible(true);
            f.set(this, 220);
        } catch (Exception e) {
            // fallback silencioso
        }
        super.init();
        this.inventoryLabelY = 10000;
    }
    @SuppressWarnings("removal")
    private int getMaxRows()     { return (int) Math.ceil(blockEntity.itemHandler.getSlots() / 3.0); }
    private int getVisibleRows() { return Math.min(getMaxRows(), 8); }

    @SuppressWarnings("removal")
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mx, int my, float partial) {
        int x = this.leftPos;
        int y = this.topPos;
        blit(graphics, x, y, 0, 0, 176, 220);

        int maxRows = getMaxRows(), visRows = getVisibleRows();
        boolean hasScroll = maxRows > 8;

        if (visRows > 0) {
            int panelW = 74 + (hasScroll ? 14 : 0);
            int panelH = visRows * 18 + 8;
            int panelX = x - panelW, panelY = y + 8;
            drawDynamicBorder(graphics, panelX, panelY, panelW, panelH);

            int slotX = x - 70, slotY = y + 12;
            int rowOff = menu.getCurrentRowOffset();
            int slotsLeft = blockEntity.itemHandler.getSlots() - rowOff * 3;

            for (int i = 0; i < visRows * 3; i++) {
                int col = i % 3, row = i / 3;
                int bx = slotX + col * 22, by = slotY + row * 18;
                int real = i + rowOff * 3;
                if (i < slotsLeft) {
                    blit(graphics, bx, by, 0, 238, 22, 18);
                    if (real < blockEntity.meltingState.length) {
                        int state = blockEntity.meltingState[real];
                        int prog  = blockEntity.meltingProgress[real];
                        int max   = blockEntity.meltingTime[real];
                        if      (state == 1) blit(graphics, bx + 1, by + 1, 185, 150, 3, 16);
                        else if (state == 2) blit(graphics, bx + 1, by + 1, 179, 150, 3, 16);
                        else if (state == 3) blit(graphics, bx + 1, by + 1, 182, 150, 3, 16);
                        else if (state == 0 && max > 0 && prog > 0) {
                            int bh = (int)(((float) prog / max) * 16);
                            blit(graphics, bx + 1, by + 1 + (16 - bh), 176, 150 + (16 - bh), 3, bh);
                        }
                    }
                } else {
                    blit(graphics, bx, by, 22, 238, 22, 18);
                }
            }

            if (hasScroll) {
                int tx = panelX + 4, ty = panelY + 4, th = visRows * 18;
                graphics.fill(tx, ty, tx + 12, ty + th, 0xFF373737);
                graphics.fill(tx + 1, ty + 1, tx + 11, ty + th - 1, 0xFF181818);
                int sly = ty + (int)(scrollProgress * (th - 15));
                graphics.fill(tx + 1, sly, tx + 11, sly + 15, 0xFFC6C6C6);
                graphics.fill(tx + 1, sly,      tx + 11, sly + 1,  0xFFFFFFFF);
                graphics.fill(tx + 1, sly,      tx + 2,  sly + 15, 0xFFFFFFFF);
                graphics.fill(tx + 10, sly + 1, tx + 11, sly + 15, 0xFF555555);
                graphics.fill(tx + 2,  sly + 14, tx + 11, sly + 15, 0xFF555555);
            }
        }

        List<FluidStack> fluids = blockEntity.fluidTank.getFluids();
        int totalCap = blockEntity.fluidTank.getCapacity();
        int tankX = x + 8, tankY = y + 16, tankW = 106, tankH = 106;

        if (totalCap > 0 && !fluids.isEmpty()) {
            int[] heights = calcLiquidHeights(fluids, totalCap, tankH, 3);
            int cy = tankY + tankH;
            for (int i = 0; i < fluids.size(); i++) {
                cy -= heights[i];
                renderFluidLayer(graphics, fluids.get(i), tankX, cy, tankW, heights[i]);
            }
        }
        blit(graphics, tankX, tankY, 176, 0, 80, 106);

        if (blockEntity.fuelCapacity > 0 && !blockEntity.currentFuel.isEmpty()) {
            int fh = 90;
            float pct = (float) blockEntity.currentFuel.getAmount() / blockEntity.fuelCapacity;
            int fh2 = Math.max(1, Math.round(pct * fh));
            renderFluidLayer(graphics, blockEntity.currentFuel, x + 152, y + 32 + fh - fh2, 16, fh2);
        }

        if (menu.bucketHandler.getStackInSlot(0).isEmpty())
            blit(graphics, x + 125, y + 46, 224, 186, 16, 16);

        if (mx >= x + 124 && mx < x + 142 && my >= y + 69 && my < y + 87)
            blit(graphics, x + 124, y + 69, 176, 202, 18, 18);
        blit(graphics, x + 125, y + 70, 176, 186, 16, 16);

        if (blockEntity.fuel > 0 && blockEntity.maxFuel > 0) {
            int fh2 = (int)(((float) blockEntity.fuel / blockEntity.maxFuel) * 14);
            blit(graphics, x + 153, y + 15 + (14 - fh2), 176, 136 + (14 - fh2), 14, fh2);
        }
    }

    private void drawDynamicBorder(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x + 7, y + 7, x + w - 7, y + h - 7, 0xFFC6C6C6);
        blit(g, x,         y,         0,   0,   7, 7);
        blit(g, x + w - 7, y,         169, 0,   7, 7);
        blit(g, x,         y + h - 7, 0,   213, 7, 7);
        blit(g, x + w - 7, y + h - 7, 169, 213, 7, 7);
        blit(g, x + 7,     y,         7,   0,   w - 14, 7);
        blit(g, x + 7,     y + h - 7, 7,   213, w - 14, 7);
        blit(g, x,         y + 7,     0,   7,   7, h - 14);
        blit(g, x + w - 7, y + 7,     169, 7,   7, h - 14);
    }

    @SuppressWarnings("removal")
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mx, int my, float partial) {
        super.extractRenderState(graphics, mx, my, partial);
        int x = this.leftPos;
        int y = this.topPos;

        if (mx >= x + 124 && mx < x + 142 && my >= y + 69 && my < y + 87)
            graphics.setComponentTooltipForNextFrame(font,
                    List.of(Component.translatable("gui.hephaestus.tank.bucket.auto.title")), mx, my);

        if (mx >= x + 8 && mx < x + 114 && my >= y + 16 && my < y + 122) {
            List<FluidStack> fluids = blockEntity.fluidTank.getFluids();
            int cap = blockEntity.fluidTank.getCapacity();
            boolean shift = shiftHeld;

            if (cap > 0 && !fluids.isEmpty()) {
                int[] heights = calcLiquidHeights(fluids, cap, 106, 3);
                int cy = y + 122;
                boolean hitFluid = false;
                for (int i = 0; i < fluids.size(); i++) {
                    cy -= heights[i];
                    if (my >= cy && my < cy + heights[i]) {
                        graphics.setComponentTooltipForNextFrame(font,
                                fluidTooltip(fluids.get(i), cap, shift), mx, my);
                        hitFluid = true;
                        break;
                    }
                }
                if (!hitFluid) {
                    int total = fluids.stream().mapToInt(FluidStack::getAmount).sum();
                    int free  = cap - total;
                    List<Component> tips = new ArrayList<>();
                    tips.add(Component.translatable("gui.hephaestus.tank_capacity",
                            total, cap).withStyle(ChatFormatting.GRAY));
                    if (shift) {
                        tips.add(fluidBreakdown(free).withStyle(ChatFormatting.DARK_GRAY));
                        tips.add(Component.translatable("gui.hephaestus.tank_free",
                                free).withStyle(ChatFormatting.DARK_GRAY));
                    } else {
                        tips.add(Component.translatable("gui.hephaestus.shift_hint")
                                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                    }
                    graphics.setComponentTooltipForNextFrame(font, tips, mx, my);
                }
            } else if (cap > 0) {
                List<Component> tips = new ArrayList<>();
                tips.add(Component.translatable("gui.hephaestus.tank_capacity",
                        0, cap).withStyle(ChatFormatting.GRAY));
                graphics.setComponentTooltipForNextFrame(font, tips, mx, my);
            }
        }

        if (mx >= x + 152 && mx < x + 168 && my >= y + 32 && my < y + 122) {
            List<Component> tips = new ArrayList<>();
            if (blockEntity.fuelCapacity > 0) {
                if (!blockEntity.currentFuel.isEmpty()) {
                    tips.add(Component.translatable(blockEntity.currentFuel.getDescriptionId()).withStyle(ChatFormatting.GOLD));
                    tips.add(Component.translatable("gui.hephaestus.fluid_amount",
                            blockEntity.currentFuel.getAmount(), blockEntity.fuelCapacity).withStyle(ChatFormatting.GRAY));
                } else {
                    tips.add(Component.translatable("gui.hephaestus.melting.no_fuel").withStyle(ChatFormatting.RED));
                    tips.add(Component.translatable("gui.hephaestus.fluid_amount",
                            0, blockEntity.fuelCapacity).withStyle(ChatFormatting.GRAY));
                }
            } else {
                tips.add(Component.translatable("gui.hephaestus.melting.no_tank").withStyle(ChatFormatting.DARK_RED));
            }
            graphics.setComponentTooltipForNextFrame(font, tips, mx, my);
        }
    }

    private void renderFluidLayer(GuiGraphicsExtractor graphics, FluidStack fluid,
                                  int x, int y, int width, int height) {
        if (fluid.isEmpty() || height <= 0) return;
        try {
            FluidState fs = fluid.getFluid().defaultFluidState();
            var model  = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fs);
            TextureAtlasSprite sprite = model.stillMaterial().sprite();

            var tintSrc = model.fluidTintSource();
            int r, g2, b, a;
            if (tintSrc != null) {
                int c = tintSrc.colorAsStack(fluid);
                a = (c >> 24 & 0xFF); if (a == 0) a = 210;
                r = (c >> 16) & 0xFF; g2 = (c >> 8) & 0xFF; b = c & 0xFF;
            } else if (fluid.getFluidType() instanceof com.titammods.registry.fluids.MoltenFluidType mft) {
                int c = mft.tintColor;
                a = (c >> 24 & 0xFF); if (a == 0) a = 210;
                r = (c >> 16) & 0xFF; g2 = (c >> 8) & 0xFF; b = c & 0xFF;
            } else if (fluid.is(net.minecraft.tags.FluidTags.LAVA)) {
                r = 0xFF; g2 = 0x66; b = 0x00; a = 210;
            } else {
                r = 0xAA; g2 = 0xAA; b = 0xAA; a = 210;
            }

            int argb = (a << 24) | (r << 16) | (g2 << 8) | b;
            int texW = Math.round(16f / (sprite.getU1() - sprite.getU0()));
            int texH = Math.round(16f / (sprite.getV1() - sprite.getV0()));
            float uPx = sprite.getU0() * texW, vPx = sprite.getV0() * texH;

            graphics.enableScissor(x, y, x + width, y + height);
            for (int ty = y; ty < y + height; ty += 16)
                for (int tx = x; tx < x + width; tx += 16)
                    graphics.blit(RenderPipelines.GUI_TEXTURED, sprite.atlasLocation(),
                            tx, ty, uPx, vPx, 16, 16, texW, texH, argb);
            graphics.disableScissor();
        } catch (Exception ignored) {
            graphics.fill(x, y, x + width, y + height, 0xD2FF6600);
        }
    }

    private List<Component> fluidTooltip(FluidStack fluid, int totalCap, boolean shift) {
        List<Component> tips = new ArrayList<>();
        tips.add(Component.translatable(fluid.getDescriptionId()).withStyle(ChatFormatting.GOLD));
        int amt = fluid.getAmount();
        if (shift) {
            tips.add(fluidBreakdown(amt).withStyle(ChatFormatting.GRAY));
            tips.add(Component.translatable("gui.hephaestus.fluid_amount",
                    amt, totalCap).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tips.add(Component.translatable("gui.hephaestus.fluid_amount",
                    amt, totalCap).withStyle(ChatFormatting.GRAY));
            tips.add(Component.translatable("gui.hephaestus.shift_hint")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
        return tips;
    }

    private net.minecraft.network.chat.MutableComponent fluidBreakdown(int mb) {
        int blocks  = mb / 1296;
        int ingots  = (mb % 1296) / 144;
        int nuggets = (mb % 144)  / 16;
        int rem     = mb % 16;
        var sb = new StringBuilder();
        if (blocks  > 0) sb.append(blocks).append(" ").append(
                Component.translatable("gui.hephaestus.unit.blocks").getString()).append("  ");
        if (ingots  > 0) sb.append(ingots).append(" ").append(
                Component.translatable("gui.hephaestus.unit.ingots").getString()).append("  ");
        if (nuggets > 0) sb.append(nuggets).append(" ").append(
                Component.translatable("gui.hephaestus.unit.nuggets").getString()).append("  ");
        if (rem > 0 || sb.isEmpty()) sb.append(rem).append(" mB");
        return Component.literal(sb.toString().trim());
    }

    private int[] calcLiquidHeights(List<FluidStack> fluids, int cap, int height, int min) {
        int[] h = new int[fluids.size()];
        int total = 0;
        for (int i = 0; i < fluids.size(); i++) {
            total += fluids.get(i).getAmount();
            h[i] = Math.max(min, (int) Math.ceil((float) fluids.get(i).getAmount() / cap * height));
        }
        if (total < cap) height -= min;
        int sum;
        do {
            sum = 0; int big = -1, bigI = 0;
            for (int i = 0; i < h.length; i++) { sum += h[i]; if (h[i] > big) { big = h[i]; bigI = i; } }
            if (sum > height && big > min) h[bigI]--;
            else if (sum > height) break;
        } while (sum > height);
        return h;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        int x = this.leftPos, y = this.topPos;
        int visRows = getVisibleRows(), maxRows = getMaxRows();

        if (visRows > 0 && maxRows > 8 && event.button() == 0) {
            int tx = x - 84, ty = y + 12, th = visRows * 18;
            if (mx >= tx && mx < tx + 12 && my >= ty && my < ty + th) {
                isScrolling = true;
                updateScrollProgress(my);
                return true;
            }
        }

        int tankX = x + 8, tankY = y + 16, tankH = 106;
        if (event.button() == 0 && mx >= tankX && mx < tankX + 106 && my >= tankY && my < tankY + tankH) {
            List<FluidStack> fluids = blockEntity.fluidTank.getFluids();
            int cap = blockEntity.fluidTank.getCapacity();
            if (cap > 0 && fluids.size() > 1) {
                int[] heights = calcLiquidHeights(fluids, cap, tankH, 3);
                int cy = tankY + tankH;
                for (int i = 0; i < fluids.size(); i++) {
                    cy -= heights[i];
                    if (my >= cy && my < cy + heights[i]) {
                        Minecraft.getInstance().getConnection().send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(
                                new com.titammods.network.FluidClickPayload(blockEntity.getBlockPos(), i)));
                        Minecraft.getInstance().getSoundManager().play(
                                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0f));
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) isScrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (isScrolling) { updateScrollProgress(event.y()); return true; }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        int maxRows = getMaxRows();
        if (maxRows > 8) {
            int hidden = maxRows - 8;
            scrollProgress -= (float)(scrollY / hidden);
            scrollProgress = Math.max(0f, Math.min(scrollProgress, 1f));
            int row = Math.round(scrollProgress * hidden);
            if (row != menu.getCurrentRowOffset()) {
                menu.updateScrollOffset(row);
                Minecraft.getInstance().getConnection().send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(
                        new com.titammods.network.ScrollSyncPayload(row)));
            }
            return true;
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    private void updateScrollProgress(double my) {
        int vis = getVisibleRows(), max = getMaxRows();
        if (vis == 0 || max <= vis) return;
        int ty = this.topPos + 12, th = vis * 18;
        scrollProgress = ((float) my - ty - 7.5f) / (th - 15f);
        scrollProgress = Math.max(0f, Math.min(scrollProgress, 1f));
        int row = Math.round(scrollProgress * (max - vis));
        if (row != menu.getCurrentRowOffset()) {
            menu.updateScrollOffset(row);
            Minecraft.getInstance().getConnection().send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(
                    new com.titammods.network.ScrollSyncPayload(row)));
        }
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
                || event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT)
            shiftHeld = true;
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
                || event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT)
            shiftHeld = false;
        return super.keyReleased(event);
    }

    private void blit(GuiGraphicsExtractor g, int x, int y, int u, int v, int w, int h) {
        g.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) u, (float) v, w, h, TEX, TEX);
    }
}