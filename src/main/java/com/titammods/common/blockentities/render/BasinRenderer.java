package com.titammods.common.blockentities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.common.blockentities.BasinBlockEntity;
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

public class BasinRenderer implements BlockEntityRenderer<BasinBlockEntity, BasinRenderState> {

    private static final float MIN_X = 0.126f, MAX_X = 0.874f;
    private static final float MIN_Z = 0.126f, MAX_Z = 0.874f;
    private static final float MIN_Y = 0.251f, MAX_Y = 0.95f;

    private final ItemModelResolver itemModelResolver;

    public BasinRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.itemModelResolver();
    }

    @Override
    public BasinRenderState createRenderState() { return new BasinRenderState(); }
    @SuppressWarnings("removal")
    @Override
    public void extractRenderState(BasinBlockEntity blockEntity,
                                   BasinRenderState state,
                                   float partialTick,
                                   Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);

        ItemStack output = blockEntity.inventory.getStackInSlot(0);
        state.fluid        = blockEntity.tank.getFluid().copy();
        state.tankCapacity = blockEntity.tank.getCapacity();
        state.isAnimating  = blockEntity.renderTimer > 0;

        var level = Minecraft.getInstance().level;
        itemModelResolver.updateForTopItem(state.outputRS, output, ItemDisplayContext.NONE, level, null, 0);
    }


    @Override
    public void submit(BasinRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {

        if (!state.outputRS.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.625, 0.5);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            state.outputRS.submit(poseStack, collector, 0x00F000F0, 0x00FF00FF, 0);
            poseStack.popPose();
            return;
        }
        if (!state.fluid.isEmpty() && !state.isAnimating) {
            renderFluid(state, poseStack, collector);
        }
    }


    private void renderFluid(BasinRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
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

        float fillPct = (float) fluid.getAmount() / state.tankCapacity;
        final float height = MIN_Y + (fillPct * (MAX_Y - MIN_Y));

        final TextureAtlasSprite spr = sprite;

        poseStack.pushPose();
        collector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(),
                (pose, buf) -> {
                    Matrix4f m = pose.pose();
                    renderCuboid(buf, m, MIN_X, MIN_Y, MIN_Z, MAX_X, height, MAX_Z,
                            spr, r, g, b, alpha, sky, finalBlock);
                });
        poseStack.popPose();
    }


    private static void renderCuboid(VertexConsumer buf, Matrix4f m,
                                     float x0, float y0, float z0,
                                     float x1, float y1, float z1,
                                     TextureAtlasSprite spr,
                                     int r, int g, int bv, int a,
                                     int sky, int block) {
        // Topo
        v(buf,m, x0,y1,z1, u(spr,x0),v(spr,z1), r,g,bv,a, sky,block,  0, 1, 0);
        v(buf,m, x1,y1,z1, u(spr,x1),v(spr,z1), r,g,bv,a, sky,block,  0, 1, 0);
        v(buf,m, x1,y1,z0, u(spr,x1),v(spr,z0), r,g,bv,a, sky,block,  0, 1, 0);
        v(buf,m, x0,y1,z0, u(spr,x0),v(spr,z0), r,g,bv,a, sky,block,  0, 1, 0);
        // Fundo
        v(buf,m, x0,y0,z0, u(spr,x0),v(spr,z0), r,g,bv,a, sky,block,  0,-1, 0);
        v(buf,m, x1,y0,z0, u(spr,x1),v(spr,z0), r,g,bv,a, sky,block,  0,-1, 0);
        v(buf,m, x1,y0,z1, u(spr,x1),v(spr,z1), r,g,bv,a, sky,block,  0,-1, 0);
        v(buf,m, x0,y0,z1, u(spr,x0),v(spr,z1), r,g,bv,a, sky,block,  0,-1, 0);
        // Norte (z-)
        v(buf,m, x1,y0,z0, u(spr,x1),v(spr,1-y0), r,g,bv,a, sky,block,  0, 0,-1);
        v(buf,m, x0,y0,z0, u(spr,x0),v(spr,1-y0), r,g,bv,a, sky,block,  0, 0,-1);
        v(buf,m, x0,y1,z0, u(spr,x0),v(spr,1-y1), r,g,bv,a, sky,block,  0, 0,-1);
        v(buf,m, x1,y1,z0, u(spr,x1),v(spr,1-y1), r,g,bv,a, sky,block,  0, 0,-1);
        // Sul (z+)
        v(buf,m, x0,y0,z1, u(spr,x0),v(spr,1-y0), r,g,bv,a, sky,block,  0, 0, 1);
        v(buf,m, x1,y0,z1, u(spr,x1),v(spr,1-y0), r,g,bv,a, sky,block,  0, 0, 1);
        v(buf,m, x1,y1,z1, u(spr,x1),v(spr,1-y1), r,g,bv,a, sky,block,  0, 0, 1);
        v(buf,m, x0,y1,z1, u(spr,x0),v(spr,1-y1), r,g,bv,a, sky,block,  0, 0, 1);
        // Oeste (x-)
        v(buf,m, x0,y0,z0, u(spr,z0),v(spr,1-y0), r,g,bv,a, sky,block, -1, 0, 0);
        v(buf,m, x0,y0,z1, u(spr,z1),v(spr,1-y0), r,g,bv,a, sky,block, -1, 0, 0);
        v(buf,m, x0,y1,z1, u(spr,z1),v(spr,1-y1), r,g,bv,a, sky,block, -1, 0, 0);
        v(buf,m, x0,y1,z0, u(spr,z0),v(spr,1-y1), r,g,bv,a, sky,block, -1, 0, 0);
        // Leste (x+)
        v(buf,m, x1,y0,z1, u(spr,z1),v(spr,1-y0), r,g,bv,a, sky,block,  1, 0, 0);
        v(buf,m, x1,y0,z0, u(spr,z0),v(spr,1-y0), r,g,bv,a, sky,block,  1, 0, 0);
        v(buf,m, x1,y1,z0, u(spr,z0),v(spr,1-y1), r,g,bv,a, sky,block,  1, 0, 0);
        v(buf,m, x1,y1,z1, u(spr,z1),v(spr,1-y1), r,g,bv,a, sky,block,  1, 0, 0);
    }

    private static float u(TextureAtlasSprite s, float c) { return s.getU0() + (s.getU1() - s.getU0()) * c; }
    private static float v(TextureAtlasSprite s, float c) { return s.getV0() + (s.getV1() - s.getV0()) * c; }

    private static void v(VertexConsumer buf, Matrix4f m,
                          float x, float y, float z, float u, float vv,
                          int r, int g, int bv, int a,
                          int sky, int block,
                          float nx, float ny, float nz) {
        buf.addVertex(m, x, y, z)
                .setColor(r, g, bv, a)
                .setUv(u, vv)
                .setUv1(10, 10)
                .setUv2(block, sky)
                .setNormal(nx, ny, nz);
    }
}