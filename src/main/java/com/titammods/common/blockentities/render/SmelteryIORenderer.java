// ── SmelteryIORenderer.java ───────────────────────────────────────────────────
package com.titammods.common.blockentities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.titammods.common.blockentities.multiblock.IDisplayFluidListener;
import com.titammods.common.blocks.SearedChuteBlock;
import com.titammods.common.blocks.SearedDrainBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class SmelteryIORenderer<T extends BlockEntity>
        implements BlockEntityRenderer<T, SmelteryIORenderer.IOState> {

    public static class IOState extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState {
        public FluidStack fluid     = FluidStack.EMPTY;
        public Direction  facing    = Direction.NORTH;
        public boolean    inStruct  = false;
    }

    public SmelteryIORenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override public IOState createRenderState() { return new IOState(); }

    @Override
    public void extractRenderState(T entity, IOState state, float partial,
                                   Vec3 cam, ModelFeatureRenderer.CrumblingOverlay crumble) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partial, cam, crumble);

        state.fluid = FluidStack.EMPTY;
        if (entity instanceof IDisplayFluidListener l) state.fluid = l.getDisplayFluid().copy();

        BlockState bs = entity.getBlockState();
        var inStructProp = bs.getBlock().getStateDefinition().getProperty("in_structure");
        if (inStructProp instanceof net.minecraft.world.level.block.state.properties.BooleanProperty bp) {
            state.inStruct = bs.getValue(bp);
        } else {
            state.inStruct = false;
        }
        state.facing   = bs.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? bs.getValue(BlockStateProperties.HORIZONTAL_FACING)
                : Direction.NORTH;
    }

    @Override
    public void submit(IOState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.fluid.isEmpty() || !state.inStruct) return;

        FluidStack fluid = state.fluid;
        FluidState fs    = fluid.getFluid().defaultFluidState();
        var model        = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fs);
        TextureAtlasSprite sprite = model.stillMaterial().sprite();

        var tintSrc = model.fluidTintSource();
        int color   = (tintSrc != null) ? tintSrc.colorAsStack(fluid) : -1;
        int a       = (color >> 24 & 0xFF) > 0 ? (color >> 24 & 0xFF) : 210;
        int r       = (color >> 16) & 0xFF;
        int g       = (color >>  8) & 0xFF;
        int b       =  color        & 0xFF;

        int sky        = state.lightCoords >> 16 & 0xFFFF;
        int block      = state.lightCoords        & 0xFFFF;
        int fluidLight = fluid.getFluidType().getLightLevel(fluid);
        final int finalBlock = Math.max(block, fluidLight * 16);

        float w     = 0.375f;
        float z     = -0.502f;
        float margin = 2f / 16f;
        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();
        float uMin = u0 + (u1 - u0) * margin, uMax = u0 + (u1 - u0) * (1f - margin);
        float vMin = v0 + (v1 - v0) * margin, vMax = v0 + (v1 - v0) * (1f - margin);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));

        collector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(),
                (pose, buf) -> {
                    Matrix4f m = pose.pose();
                    v(buf, m,  w, -w, z, r, g, b, a, uMax, vMax, sky, finalBlock);
                    v(buf, m, -w, -w, z, r, g, b, a, uMin, vMax, sky, finalBlock);
                    v(buf, m, -w,  w, z, r, g, b, a, uMin, vMin, sky, finalBlock);
                    v(buf, m,  w,  w, z, r, g, b, a, uMax, vMin, sky, finalBlock);
                });
        poseStack.popPose();
    }

    private static void v(VertexConsumer buf, Matrix4f m, float x, float y, float z,
                          int r, int g, int b, int a, float u, float v, int sky, int block) {
        buf.addVertex(m, x, y, z).setColor(r, g, b, a).setUv(u, v)
                .setUv1(10, 10).setUv2(block, sky).setNormal(0, 0, -1);
    }
}