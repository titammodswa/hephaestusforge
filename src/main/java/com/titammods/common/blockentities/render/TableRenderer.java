package com.titammods.common.blockentities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.titammods.common.blockentities.TableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class TableRenderer implements BlockEntityRenderer<TableBlockEntity, TableRenderState> {

    private static final float TABLE_HEIGHT = 0.9375f;
    private final ItemModelResolver itemModelResolver;

    public TableRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.itemModelResolver();
    }

    @Override
    public TableRenderState createRenderState() { return new TableRenderState(); }
    @SuppressWarnings("removal")
    @Override
    public void extractRenderState(TableBlockEntity blockEntity,
                                   TableRenderState state,
                                   float partialTick,
                                   Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);

        ItemStack mold         = blockEntity.inventory.getStackInSlot(0);
        ItemStack output       = blockEntity.inventory.getStackInSlot(1);
        ItemStack renderResult = blockEntity.renderResult;

        var level = Minecraft.getInstance().level;

        state.hasMold      = !mold.isEmpty();
        state.hasOutput    = !output.isEmpty() || !renderResult.isEmpty();
        state.fluid        = blockEntity.tank.getFluid().copy();
        state.tankCapacity = blockEntity.tank.getCapacity();

        itemModelResolver.updateForTopItem(state.moldRS,   mold,         ItemDisplayContext.FIXED, level, null, 0);
        itemModelResolver.updateForTopItem(state.outputRS, output,       ItemDisplayContext.FIXED, level, null, 0);
        itemModelResolver.updateForTopItem(state.resultRS, renderResult, ItemDisplayContext.FIXED, level, null, 0);
    }

    @Override
    public void submit(TableRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {

        if (state.hasOutput) {
            ItemStackRenderState itemToDraw = state.outputRS.isEmpty() ? state.resultRS : state.outputRS;
            float yPos = state.hasMold ? TABLE_HEIGHT + 0.01f : TABLE_HEIGHT;
            submitItemFlat(itemToDraw, poseStack, collector, yPos);
            if (state.hasMold) {
                submitItemFlat(state.moldRS, poseStack, collector, TABLE_HEIGHT);
            }
            return;
        }

        if (!state.fluid.isEmpty()) {
            renderFluid(state, poseStack, collector);
        }

        if (state.hasMold) {
            submitItemFlat(state.moldRS, poseStack, collector, TABLE_HEIGHT);
        }
    }

    private static void submitItemFlat(ItemStackRenderState rs, PoseStack poseStack,
                                       SubmitNodeCollector collector,
                                       float height) {
        if (rs.isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0.5, height, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.scale(0.89f, 0.89f, 1.85f);
        rs.submit(poseStack, collector, 0x00F000F0, 0x00FF00FF, 0);
        poseStack.popPose();
    }


    private void renderFluid(TableRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        FluidStack fluid = state.fluid;

        var fluidModel = Minecraft.getInstance().getModelManager()
                .getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
        TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();

        var tintSrc = fluidModel.fluidTintSource();
        int color  = (tintSrc != null) ? tintSrc.colorAsStack(fluid) : -1;
        int alpha  = (color >> 24 & 0xFF) > 0 ? (color >> 24 & 0xFF) : 255;
        int r      = (color >> 16) & 0xFF;
        int g      = (color >> 8)  & 0xFF;
        int b      =  color        & 0xFF;

        int sky        = state.lightCoords >> 16 & 0xFFFF;
        int block      = state.lightCoords        & 0xFFFF;
        int fluidLight = fluid.getFluidType().getLightLevel(fluid);
        final int finalBlock = (fluidLight * 16 > block) ? fluidLight * 16 : block;

        float minX = 0.008f, maxX = 1.0f, minZ = 0.001f, maxZ = 1.0f;
        float baseY = TABLE_HEIGHT + 0.001f, topY = TABLE_HEIGHT + 0.005f;
        float fillPct = (float) fluid.getAmount() / state.tankCapacity;
        final float height = baseY + (fillPct * (topY - baseY));
        final TextureAtlasSprite spr = sprite;

        poseStack.pushPose();
        collector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(),
                (pose, buf) -> {
                    Matrix4f m = pose.pose();
                    v(buf, m, minX, height, maxZ, u(spr, minX), v(spr, maxZ), r, g, b, alpha, sky, finalBlock);
                    v(buf, m, maxX, height, maxZ, u(spr, maxX), v(spr, maxZ), r, g, b, alpha, sky, finalBlock);
                    v(buf, m, maxX, height, minZ, u(spr, maxX), v(spr, minZ), r, g, b, alpha, sky, finalBlock);
                    v(buf, m, minX, height, minZ, u(spr, minX), v(spr, minZ), r, g, b, alpha, sky, finalBlock);
                });
        poseStack.popPose();
    }

    private static float u(TextureAtlasSprite s, float c) { return s.getU0() + (s.getU1() - s.getU0()) * c; }
    private static float v(TextureAtlasSprite s, float c) { return s.getV0() + (s.getV1() - s.getV0()) * c; }

    private static void v(VertexConsumer buf, Matrix4f m,
                          float x, float y, float z, float u, float vv,
                          int r, int g, int bv, int a, int sky, int block) {
        buf.addVertex(m, x, y, z).setColor(r, g, bv, a).setUv(u, vv).setUv1(10, 10).setUv2(block, sky).setNormal(0, 1, 0);
    }
}