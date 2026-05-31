package com.titammods.client.screen;

import com.titammods.TitamMods;
import com.titammods.common.blockentities.SearedTankBlockEntity;
import com.titammods.menu.MelterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class MelterScreen extends AbstractContainerScreen<MelterMenu> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "textures/gui/melter.png");
    private static final int TEX = 256;

    public MelterScreen(MelterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Fundo principal
        blit(graphics, x, y, 0, 0, imageWidth, imageHeight);

        if (getMenu().isBurning()) {
            int fireH = getMenu().getScaledFuel();
            blit(graphics, x + 152, y + 15 + 14 - fireH, 176, 136 + 14 - fireH, 14, fireH);
        }

        if (minecraft != null && minecraft.level != null) {
            BlockEntity below = minecraft.level.getBlockEntity(
                    getMenu().getBlockEntity().getBlockPos().below());
            if (below instanceof SearedTankBlockEntity fuelBE) {
                // Overlay do tanque (borda)
                blit(graphics, x + 152, y + 31, 194, 52, 14, 38);
                FluidStack fuelFluid = fuelBE.getFluidTank().getFluid();
                if (!fuelFluid.isEmpty()) {
                    int fuelCap = fuelBE.getFluidTank().getCapacity();
                    int fuelH   = (int) (((float) fuelFluid.getAmount() / fuelCap) * 36);
                    if (fuelH > 0) {
                        drawFluidFill(graphics, x + 153, y + 32 + 36 - fuelH, 12, fuelH, fuelFluid);
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            int state = getMenu().getState(i);
            if (state == 0) continue;

            int progressH = getMenu().getScaledProgress(i);
            int texX      = 176;

            if (state == 4) { texX = 179; progressH = 16; }
            else if (state == 3) { texX = 185; progressH = 16; }
            else if (state == 2) { texX = 182; progressH = 16; }

            if (progressH > 0) {
                blit(graphics, x + 18, y + 16 + (i * 18) + 16 - progressH,
                        texX, 150 + 16 - progressH, 3, progressH);
            }
        }

        int tankX = x + 90;
        int tankY = y + 16;
        FluidStack fluid = getMenu().getBlockEntity().tank.getFluid();

        if (!fluid.isEmpty()) {
            int fluidH = (int) ((fluid.getAmount() / (float) getMenu().getBlockEntity().tank.getCapacity()) * 52);
            if (fluidH > 0) {
                drawFluidFill(graphics, tankX, tankY + 52 - fluidH, 52, fluidH, fluid);
            }
        }
        blit(graphics, tankX, tankY, 176, 0, 52, 52);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int tankX = leftPos + 90;
        int tankY = topPos + 16;

        if (mouseX >= tankX && mouseX < tankX + 52 && mouseY >= tankY && mouseY < tankY + 52) {
            FluidStack fluid = getMenu().getBlockEntity().tank.getFluid();
            if (!fluid.isEmpty()) {
                int amount = fluid.getAmount();
                int cap    = getMenu().getBlockEntity().tank.getCapacity();

                int blocks  = amount / 900;
                int ingots  = (amount % 900) / 90;
                int nuggets = (amount % 90) / 10;
                int mb      = amount % 10;

                StringBuilder sb = new StringBuilder();
                if (blocks  > 0) sb.append(blocks).append(" Blocos ");
                if (ingots  > 0) sb.append(ingots).append(" Barras ");
                if (nuggets > 0) sb.append(nuggets).append(" Pepitas ");
                if (mb > 0 || sb.isEmpty()) sb.append(mb).append(" mB");

                List<Component> tooltip = new ArrayList<>();
                tooltip.add(fluid.getHoverName());
                tooltip.add(Component.literal(sb.toString().trim()).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal(amount + " / " + cap + " mB").withStyle(ChatFormatting.DARK_GRAY));
                graphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            } else {
                graphics.setComponentTooltipForNextFrame(this.font,
                        List.of(Component.literal("Capacidade: 2700 mB").withStyle(ChatFormatting.GRAY)),
                        mouseX, mouseY);
            }
        }
    }

    private void blit(GuiGraphicsExtractor g, int x, int y, int u, int v, int w, int h) {
        g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) u, (float) v, w, h, TEX, TEX);
    }

    private void drawFluidFill(GuiGraphicsExtractor graphics, int x, int y, int w, int h, FluidStack fluid) {
        if (fluid.isEmpty() || w <= 0 || h <= 0) return;
        int color;
        try {
            FluidState fs = fluid.getFluid().defaultFluidState();
            var model  = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fs);
            var tint   = model.fluidTintSource();
            if (tint != null) {
                int t = tint.colorAsStack(fluid);
                int a = (t >> 24 & 0xFF); if (a == 0) a = 210;
                color = (a << 24) | (t & 0x00FFFFFF);
            } else {
                color = fluid.is(net.minecraft.tags.FluidTags.LAVA) ? 0xD2FF6600 : 0xD23399FF;
            }
        } catch (Exception e) { color = 0xD23399FF; }
        graphics.fill(x, y, x + w, y + h, color);
    }
}
