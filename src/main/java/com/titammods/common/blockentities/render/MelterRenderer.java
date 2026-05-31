package com.titammods.common.blockentities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.common.blockentities.MelterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class MelterRenderer implements BlockEntityRenderer<MelterBlockEntity, MelterRenderState> {

    @SuppressWarnings("unused")
    public MelterRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public MelterRenderState createRenderState() {
        return new MelterRenderState();
    }

    @Override
    public void extractRenderState(MelterBlockEntity blockEntity,
                                   MelterRenderState state,
                                   float partialTick,
                                   Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick,
                cameraPosition, breakProgress);
        state.fluid    = blockEntity.tank.getFluid().copy();
        state.capacity = blockEntity.tank.getCapacity();
        state.facing   = blockEntity.getBlockState().getValue(
                com.titammods.common.blocks.SearedMachineBlock.FACING);
        state.item0 = blockEntity.inventory.getStackInSlot(0).copy();
        state.item1 = blockEntity.inventory.getStackInSlot(1).copy();
        state.item2 = blockEntity.inventory.getStackInSlot(2).copy();
    }

    @Override
    public void submit(MelterRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {

        FluidStack fluid = state.fluid;
        if (fluid.isEmpty()) return;

        poseStack.pushPose();

        FluidState fluidState = fluid.getFluid().defaultFluidState();
        var fluidModel = Minecraft.getInstance().getModelManager()
                .getFluidStateModelSet().get(fluidState);
        TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();

        var tintSrc = fluidModel.fluidTintSource();
        int color = (tintSrc != null) ? tintSrc.colorAsStack(fluid) : -1;
        int alpha = (color >> 24 & 0xFF) > 0 ? (color >> 24 & 0xFF) : 255;
        int r     = (color >> 16) & 0xFF;
        int g     = (color >> 8)  & 0xFF;
        int b     =  color        & 0xFF;

        int sky        = state.lightCoords >> 16 & 0xFFFF;
        int block      = state.lightCoords       & 0xFFFF;
        int fluidLight = fluid.getFluidType().getLightLevel(fluid);
        final int finalBlock = (fluidLight * 16 > block) ? fluidLight * 16 : block;

        float fill = Mth.clamp((float) fluid.getAmount() / state.capacity, 0.003f, 1f);
        float minY = 0.500f;
        float maxY = minY + (0.495f * fill);
        float x0 = 0.005f, x1 = 0.995f;
        float z0 = 0.005f, z1 = 0.995f;

        collector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(),
                (pose, buf) -> renderBox(buf, pose, sprite, r, g, b, alpha,
                        sky, finalBlock, x0, minY, z0, x1, maxY, z1));

        poseStack.popPose();
    }

    static void renderBox(VertexConsumer buf, PoseStack.Pose pose,
                          TextureAtlasSprite spr,
                          int r, int g, int b, int a,
                          int sky, int block,
                          float x0, float y0, float z0,
                          float x1, float y1, float z1) {
        Matrix4f m = pose.pose();
        float u0 = spr.getU0(), u1 = spr.getU1();
        float v0 = spr.getV0(), v1 = spr.getV1();
        // Top
        v(buf,m,x0,y1,z0, r,g,b,a, u0,v0, sky,block); v(buf,m,x0,y1,z1, r,g,b,a, u0,v1, sky,block);
        v(buf,m,x1,y1,z1, r,g,b,a, u1,v1, sky,block); v(buf,m,x1,y1,z0, r,g,b,a, u1,v0, sky,block);
        // Bottom
        v(buf,m,x0,y0,z1, r,g,b,a, u0,v1, sky,block); v(buf,m,x0,y0,z0, r,g,b,a, u0,v0, sky,block);
        v(buf,m,x1,y0,z0, r,g,b,a, u1,v0, sky,block); v(buf,m,x1,y0,z1, r,g,b,a, u1,v1, sky,block);
        // North
        v(buf,m,x1,y1,z0, r,g,b,a, u0,v0, sky,block); v(buf,m,x1,y0,z0, r,g,b,a, u0,v1, sky,block);
        v(buf,m,x0,y0,z0, r,g,b,a, u1,v1, sky,block); v(buf,m,x0,y1,z0, r,g,b,a, u1,v0, sky,block);
        // South
        v(buf,m,x0,y1,z1, r,g,b,a, u0,v0, sky,block); v(buf,m,x0,y0,z1, r,g,b,a, u0,v1, sky,block);
        v(buf,m,x1,y0,z1, r,g,b,a, u1,v1, sky,block); v(buf,m,x1,y1,z1, r,g,b,a, u1,v0, sky,block);
        // West
        v(buf,m,x0,y1,z0, r,g,b,a, u0,v0, sky,block); v(buf,m,x0,y0,z0, r,g,b,a, u0,v1, sky,block);
        v(buf,m,x0,y0,z1, r,g,b,a, u1,v1, sky,block); v(buf,m,x0,y1,z1, r,g,b,a, u1,v0, sky,block);
        // East
        v(buf,m,x1,y1,z1, r,g,b,a, u0,v0, sky,block); v(buf,m,x1,y0,z1, r,g,b,a, u0,v1, sky,block);
        v(buf,m,x1,y0,z0, r,g,b,a, u1,v1, sky,block); v(buf,m,x1,y1,z0, r,g,b,a, u1,v0, sky,block);
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
}
