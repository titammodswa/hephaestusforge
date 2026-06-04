package com.titammods.common.blockentities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.common.blockentities.SmelteryControllerBlockEntity;
import com.titammods.common.blocks.SmelteryControllerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

import java.util.List;

public class SmelteryControllerRenderer
        implements BlockEntityRenderer<SmelteryControllerBlockEntity, SmelteryControllerRenderer.SmelteryRenderState> {

    private static final float FLUID_OFFSET = 0.005f;

    public static class SmelteryRenderState extends BlockEntityRenderState {
        public List<FluidStack> fluids   = List.of();
        public int totalCapacity         = 0;
        public boolean isFormed          = false;
        public int minDX, minDY, minDZ;
        public int maxDX, maxDY, maxDZ;
        public int lightCoords           = 0;
    }

    public SmelteryControllerRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override public SmelteryRenderState createRenderState() { return new SmelteryRenderState(); }

    @Override
    public void extractRenderState(SmelteryControllerBlockEntity entity, SmelteryRenderState state,
                                   float partial, Vec3 cam, ModelFeatureRenderer.CrumblingOverlay crumble) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partial, cam, crumble);

        state.isFormed      = false;
        state.fluids        = List.of();
        state.totalCapacity = 0;

        if (!entity.getBlockState().getValue(SmelteryControllerBlock.IN_STRUCTURE)) return;

        BlockPos minI = entity.syncedMinInner;
        BlockPos maxI = entity.syncedMaxInner;
        if (minI == null || maxI == null) return;

        List<FluidStack> fluids = entity.fluidTank.getFluids();
        if (fluids.isEmpty()) return;

        BlockPos ctrl   = entity.getBlockPos();
        state.isFormed  = true;
        state.fluids    = fluids.stream().map(FluidStack::copy).toList();
        state.totalCapacity = entity.fluidTank.getCapacity();

        state.minDX = minI.getX() - ctrl.getX();
        state.minDY = minI.getY() - ctrl.getY();
        state.minDZ = minI.getZ() - ctrl.getZ();
        state.maxDX = maxI.getX() - ctrl.getX();
        state.maxDY = maxI.getY() - ctrl.getY();
        state.maxDZ = maxI.getZ() - ctrl.getZ();

    }

    @Override
    public void submit(SmelteryRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.isFormed || state.fluids.isEmpty() || state.totalCapacity <= 0) return;

        int xd = state.maxDX - state.minDX;
        int yd = state.maxDY - state.minDY;
        int zd = state.maxDZ - state.minDZ;

        if (xd < 0 || yd < 0 || zd < 0) return;

        int sky   = state.lightCoords >> 16 & 0xFFFF;
        int block = state.lightCoords        & 0xFFFF;

        poseStack.pushPose();
        poseStack.translate(state.minDX, state.minDY, state.minDZ);

        int xzArea = (xd + 1) * (zd + 1);
        int totalHeightMb = (yd + 1) * 1000 * xzArea - (int)(FLUID_OFFSET * 2 * 1000);
        int[] heights = calcHeights(state.fluids, state.totalCapacity, totalHeightMb, xzArea);

        float x0 = FLUID_OFFSET,        x1 = (xd + 1) - FLUID_OFFSET;
        float z0 = FLUID_OFFSET,        z1 = (zd + 1) - FLUID_OFFSET;
        float curY = FLUID_OFFSET;

        for (int i = 0; i < state.fluids.size(); i++) {
            FluidStack fluid = state.fluids.get(i);
            if (fluid.isEmpty() || heights[i] <= 0) continue;

            float fluidH = (float) heights[i] / (float)(1000 * xzArea);
            float y0 = curY;
            float y1 = curY + fluidH;
            curY = y1;

            renderFluidLayer(poseStack, collector, fluid, sky, block,
                    x0, y0, z0, x1, y1, z1, xd, yd, zd);
        }

        poseStack.popPose();
    }

    private void renderFluidLayer(PoseStack poseStack, SubmitNodeCollector collector,
                                  FluidStack fluid, int sky, int block,
                                  float x0, float y0, float z0,
                                  float x1, float y1, float z1,
                                  int xd, int yd, int zd) {
        FluidState fs = fluid.getFluid().defaultFluidState();
        var fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fs);
        TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();

        var tintSrc = fluidModel.fluidTintSource();
        int color = (tintSrc != null) ? tintSrc.colorAsStack(fluid) : -1;
        int a     = (color >> 24 & 0xFF) > 0 ? (color >> 24 & 0xFF) : 255;
        int r     = (color >> 16) & 0xFF;
        int g     = (color >>  8) & 0xFF;
        int b     =  color        & 0xFF;

        int fluidLight = fluid.getFluidType().getLightLevel(fluid);
        final int finalBlock = Math.max(block, fluidLight * 16);

        float totalInnerH = (yd + 1);
        float fill = Math.min(1f, (y1 - y0) / totalInnerH);

        final float fx0 = x0, fx1 = x1, fy0 = y0, fy1 = y1, fz0 = z0, fz1 = z1;
        final int fr = r, fg = g, fb = b, fa = a;

        collector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(),
                (pose, buf) -> renderCuboid(buf, pose.pose(), sprite,
                        fr, fg, fb, fa, sky, finalBlock,
                        fx0, fy0, fz0, fx1, fy1, fz1, fill));
    }

    private static void renderCuboid(VertexConsumer buf, Matrix4f m,
                                     TextureAtlasSprite spr,
                                     int r, int g, int b, int a,
                                     int sky, int block,
                                     float x0, float y0, float z0,
                                     float x1, float y1, float z1,
                                     float fill) {
        float u0 = spr.getU0(), u1 = spr.getU1();
        float v0 = spr.getV0(), v1 = spr.getV1();
        float uvH = v1 - v0;
        float vTop = v0 + uvH * (1f - fill);

        // Top — frente
        v(buf,m,x0,y1,z0, r,g,b,a, u0,v0, sky,block);
        v(buf,m,x0,y1,z1, r,g,b,a, u0,v1, sky,block);
        v(buf,m,x1,y1,z1, r,g,b,a, u1,v1, sky,block);
        v(buf,m,x1,y1,z0, r,g,b,a, u1,v0, sky,block);
        // Top interno
        v(buf,m,x1,y1,z0, r,g,b,a, u1,v0, sky,block);
        v(buf,m,x1,y1,z1, r,g,b,a, u1,v1, sky,block);
        v(buf,m,x0,y1,z1, r,g,b,a, u0,v1, sky,block);
        v(buf,m,x0,y1,z0, r,g,b,a, u0,v0, sky,block);
        // Bottom
        v(buf,m,x0,y0,z1, r,g,b,a, u0,v1, sky,block);
        v(buf,m,x0,y0,z0, r,g,b,a, u0,v0, sky,block);
        v(buf,m,x1,y0,z0, r,g,b,a, u1,v0, sky,block);
        v(buf,m,x1,y0,z1, r,g,b,a, u1,v1, sky,block);
        // North — frente
        v(buf,m,x1,y1,z0, r,g,b,a, u0,vTop, sky,block);
        v(buf,m,x1,y0,z0, r,g,b,a, u0,v1,   sky,block);
        v(buf,m,x0,y0,z0, r,g,b,a, u1,v1,   sky,block);
        v(buf,m,x0,y1,z0, r,g,b,a, u1,vTop, sky,block);
        // North — verso
        v(buf,m,x0,y1,z0, r,g,b,a, u1,vTop, sky,block);
        v(buf,m,x0,y0,z0, r,g,b,a, u1,v1,   sky,block);
        v(buf,m,x1,y0,z0, r,g,b,a, u0,v1,   sky,block);
        v(buf,m,x1,y1,z0, r,g,b,a, u0,vTop, sky,block);
        // South — frente
        v(buf,m,x0,y1,z1, r,g,b,a, u0,vTop, sky,block);
        v(buf,m,x0,y0,z1, r,g,b,a, u0,v1,   sky,block);
        v(buf,m,x1,y0,z1, r,g,b,a, u1,v1,   sky,block);
        v(buf,m,x1,y1,z1, r,g,b,a, u1,vTop, sky,block);
        // South — verso
        v(buf,m,x1,y1,z1, r,g,b,a, u1,vTop, sky,block);
        v(buf,m,x1,y0,z1, r,g,b,a, u1,v1,   sky,block);
        v(buf,m,x0,y0,z1, r,g,b,a, u0,v1,   sky,block);
        v(buf,m,x0,y1,z1, r,g,b,a, u0,vTop, sky,block);
        // West — frente
        v(buf,m,x0,y1,z0, r,g,b,a, u0,vTop, sky,block);
        v(buf,m,x0,y0,z0, r,g,b,a, u0,v1,   sky,block);
        v(buf,m,x0,y0,z1, r,g,b,a, u1,v1,   sky,block);
        v(buf,m,x0,y1,z1, r,g,b,a, u1,vTop, sky,block);
        // West — verso
        v(buf,m,x0,y1,z1, r,g,b,a, u1,vTop, sky,block);
        v(buf,m,x0,y0,z1, r,g,b,a, u1,v1,   sky,block);
        v(buf,m,x0,y0,z0, r,g,b,a, u0,v1,   sky,block);
        v(buf,m,x0,y1,z0, r,g,b,a, u0,vTop, sky,block);
        // East — frente
        v(buf,m,x1,y1,z1, r,g,b,a, u0,vTop, sky,block);
        v(buf,m,x1,y0,z1, r,g,b,a, u0,v1,   sky,block);
        v(buf,m,x1,y0,z0, r,g,b,a, u1,v1,   sky,block);
        v(buf,m,x1,y1,z0, r,g,b,a, u1,vTop, sky,block);
        // East — verso
        v(buf,m,x1,y1,z0, r,g,b,a, u1,vTop, sky,block);
        v(buf,m,x1,y0,z0, r,g,b,a, u1,v1,   sky,block);
        v(buf,m,x1,y0,z1, r,g,b,a, u0,v1,   sky,block);
        v(buf,m,x1,y1,z1, r,g,b,a, u0,vTop, sky,block);
    }

    private static void v(VertexConsumer b, Matrix4f m,
                          float x, float y, float z,
                          int r, int g, int bv, int a,
                          float u, float v, int sky, int block) {
        b.addVertex(m, x, y, z)
                .setColor(r, g, bv, a)
                .setUv(u, v)
                .setUv1(10, 10)
                .setUv2(block, sky)
                .setNormal(0, 1, 0);
    }

    private static int[] calcHeights(List<FluidStack> fluids, int capacity, int totalMb, int xzArea) {
        int[] h = new int[fluids.size()];
        int minH = 100 * xzArea;
        for (int i = 0; i < fluids.size(); i++)
            h[i] = Math.max(minH, (int)((long) fluids.get(i).getAmount() * totalMb / capacity));

        int sum;
        do {
            sum = 0;
            int big = -1, bigI = 0;
            for (int i = 0; i < h.length; i++) { sum += h[i]; if (h[i] > big) { big = h[i]; bigI = i; } }
            if (sum > totalMb && big > minH) h[bigI]--;
            else break;
        } while (sum > totalMb);
        return h;
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(SmelteryControllerBlockEntity entity) {
        return net.minecraft.world.phys.AABB.INFINITE;
    }

    @Override
    public int getViewDistance() { return 256; }
}