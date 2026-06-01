package com.titammods.common.blockentities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.common.blockentities.FaucetBlockEntity;
import com.titammods.common.blocks.SearedFaucetBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class FaucetRenderer implements BlockEntityRenderer<FaucetBlockEntity, FaucetRenderState> {

    @SuppressWarnings("unused")
    public FaucetRenderer(BlockEntityRendererProvider.Context ctx) {}


    @Override
    public FaucetRenderState createRenderState() {
        return new FaucetRenderState();
    }

    @Override
    public void extractRenderState(FaucetBlockEntity blockEntity,
                                   FaucetRenderState state,
                                   float partialTick,
                                   Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.fluid     = blockEntity.getRenderFluid().copy();
        state.isPouring = blockEntity.isPouring();
        state.facing    = blockEntity.getBlockState().getValue(SearedFaucetBlock.FACING);
    }

    @Override
    public void submit(FaucetRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {

        if (!state.isPouring || state.fluid.isEmpty()) return;

        FluidStack fluid = state.fluid;

        var fluidModel = Minecraft.getInstance().getModelManager()
                .getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
        TextureAtlasSprite sprite = fluidModel.flowingMaterial().sprite();

        var tintSrc = fluidModel.fluidTintSource();
        int color = (tintSrc != null) ? tintSrc.colorAsStack(fluid) : -1;
        int alpha = (color >> 24 & 0xFF) > 0 ? (color >> 24 & 0xFF) : 255;
        int r     = (color >> 16) & 0xFF;
        int g     = (color >> 8)  & 0xFF;
        int b     =  color        & 0xFF;

        int sky        = state.lightCoords >> 16 & 0xFFFF;
        int block      = state.lightCoords        & 0xFFFF;
        int fluidLight = fluid.getFluidType().getLightLevel(fluid);
        final int finalBlock = (fluidLight * 16 > block) ? fluidLight * 16 : block;

        float hMinX = 0.375f, hMaxX = 0.625f;
        float hMinY = 0.375f, hMaxY = 0.625f;
        float hMinZ = 0.375f, hMaxZ = 0.625f;

        switch (state.facing) {
            case NORTH -> hMaxZ = 1.0f;
            case SOUTH -> hMinZ = 0.0f;
            case WEST  -> hMaxX = 1.0f;
            case EAST  -> hMinX = 0.0f;
        }

        float vMinX = 0.375f, vMaxX = 0.625f;
        float vMinZ = 0.375f, vMaxZ = 0.625f;
        float vMaxY = 0.375f;

        float vMinY = -0.75f;

        poseStack.pushPose();

        final float fhMinX = hMinX, fhMinY = hMinY, fhMinZ = hMinZ;
        final float fhMaxX = hMaxX, fhMaxY = hMaxY, fhMaxZ = hMaxZ;
        final float fvMinX = vMinX, fvMinY = vMinY, fvMinZ = vMinZ;
        final float fvMaxX = vMaxX, fvMaxY = vMaxY, fvMaxZ = vMaxZ;

        collector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(),
                (pose, buf) -> {
                    renderCuboid(buf, pose.pose(), sprite, r, g, b, alpha, sky, finalBlock,
                            fhMinX, fhMinY, fhMinZ, fhMaxX, fhMaxY, fhMaxZ);
                    renderCuboid(buf, pose.pose(), sprite, r, g, b, alpha, sky, finalBlock,
                            fvMinX, fvMinY, fvMinZ, fvMaxX, fvMaxY, fvMaxZ);
                });

        poseStack.popPose();
    }

    private static void renderCuboid(VertexConsumer buf, Matrix4f m,
                                     TextureAtlasSprite spr,
                                     int r, int g, int b, int a,
                                     int sky, int block,
                                     float x0, float y0, float z0,
                                     float x1, float y1, float z1) {
        float u0 = spr.getU0(), u1 = spr.getU1();
        float v0 = spr.getV0(), v1 = spr.getV1();

        // Top
        v(buf, m, x0, y1, z0,  r, g, b, a,  u0, v0,  sky, block);
        v(buf, m, x0, y1, z1,  r, g, b, a,  u0, v1,  sky, block);
        v(buf, m, x1, y1, z1,  r, g, b, a,  u1, v1,  sky, block);
        v(buf, m, x1, y1, z0,  r, g, b, a,  u1, v0,  sky, block);

        // Bottom
        v(buf, m, x0, y0, z1,  r, g, b, a,  u0, v1,  sky, block);
        v(buf, m, x0, y0, z0,  r, g, b, a,  u0, v0,  sky, block);
        v(buf, m, x1, y0, z0,  r, g, b, a,  u1, v0,  sky, block);
        v(buf, m, x1, y0, z1,  r, g, b, a,  u1, v1,  sky, block);

        // North (z-)
        v(buf, m, x1, y1, z0,  r, g, b, a,  u0, v0,  sky, block);
        v(buf, m, x1, y0, z0,  r, g, b, a,  u0, v1,  sky, block);
        v(buf, m, x0, y0, z0,  r, g, b, a,  u1, v1,  sky, block);
        v(buf, m, x0, y1, z0,  r, g, b, a,  u1, v0,  sky, block);

        // South (z+)
        v(buf, m, x0, y1, z1,  r, g, b, a,  u0, v0,  sky, block);
        v(buf, m, x0, y0, z1,  r, g, b, a,  u0, v1,  sky, block);
        v(buf, m, x1, y0, z1,  r, g, b, a,  u1, v1,  sky, block);
        v(buf, m, x1, y1, z1,  r, g, b, a,  u1, v0,  sky, block);

        // West (x-)
        v(buf, m, x0, y1, z0,  r, g, b, a,  u0, v0,  sky, block);
        v(buf, m, x0, y0, z0,  r, g, b, a,  u0, v1,  sky, block);
        v(buf, m, x0, y0, z1,  r, g, b, a,  u1, v1,  sky, block);
        v(buf, m, x0, y1, z1,  r, g, b, a,  u1, v0,  sky, block);

        // East (x+)
        v(buf, m, x1, y1, z1,  r, g, b, a,  u0, v0,  sky, block);
        v(buf, m, x1, y0, z1,  r, g, b, a,  u0, v1,  sky, block);
        v(buf, m, x1, y0, z0,  r, g, b, a,  u1, v1,  sky, block);
        v(buf, m, x1, y1, z0,  r, g, b, a,  u1, v0,  sky, block);
    }

    private static void v(VertexConsumer buf, Matrix4f m,
                          float x, float y, float z,
                          int r, int g, int bv, int a,
                          float u, float v,
                          int sky, int block) {
        buf.addVertex(m, x, y, z)
                .setColor(r, g, bv, a)
                .setUv(u, v)
                .setUv1(10, 10)
                .setUv2(block, sky)
                .setNormal(0, 1, 0);
    }
}