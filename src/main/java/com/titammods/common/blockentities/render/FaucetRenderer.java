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
        TextureAtlasSprite flowing = fluidModel.flowingMaterial().sprite();
        TextureAtlasSprite still   = fluidModel.stillMaterial().sprite();

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
                    renderCuboid(buf, pose.pose(), flowing, still, state.facing, r, g, b, alpha, sky, finalBlock,
                            fhMinX, fhMinY, fhMinZ, fhMaxX, fhMaxY, fhMaxZ);
                    renderCuboid(buf, pose.pose(), flowing, still, Direction.DOWN, r, g, b, alpha, sky, finalBlock,
                            fvMinX, fvMinY, fvMinZ, fvMaxX, fvMaxY, fvMaxZ);
                });

        poseStack.popPose();
    }

    private static void renderCuboid(VertexConsumer buf, Matrix4f m,
                                     TextureAtlasSprite flowing, TextureAtlasSprite still,
                                     Direction flow,
                                     int r, int g, int b, int a,
                                     int sky, int block,
                                     float x0, float y0, float z0,
                                     float x1, float y1, float z1) {
        float[][][] faces = {
                {{x0, y1, z0}, {x0, y1, z1}, {x1, y1, z1}, {x1, y1, z0}},
                {{x0, y0, z1}, {x0, y0, z0}, {x1, y0, z0}, {x1, y0, z1}},
                {{x1, y1, z0}, {x1, y0, z0}, {x0, y0, z0}, {x0, y1, z0}},
                {{x0, y1, z1}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}},
                {{x0, y1, z0}, {x0, y0, z0}, {x0, y0, z1}, {x0, y1, z1}},
                {{x1, y1, z1}, {x1, y0, z1}, {x1, y0, z0}, {x1, y1, z0}}
        };
        Direction.Axis[] normals = {
                Direction.Axis.Y, Direction.Axis.Y,
                Direction.Axis.Z, Direction.Axis.Z,
                Direction.Axis.X, Direction.Axis.X
        };

        Direction.Axis flowAxis = flow.getAxis();
        boolean positive = flow.getAxisDirection() == Direction.AxisDirection.POSITIVE;

        for (int f = 0; f < faces.length; f++) {
            Direction.Axis normal = normals[f];
            for (float[] p : faces[f]) {
                float u, vv;
                if (normal == flowAxis) {
                    float[] uvCoords = inPlane(p, normal);
                    u  = lerp(still.getU0(), still.getU1(), uvCoords[0]);
                    vv = lerp(still.getV0(), still.getV1(), uvCoords[1]);
                } else {
                    float along  = positive ? coord(p, flowAxis) : 1.0f - coord(p, flowAxis);
                    float across = coord(p, otherAxis(normal, flowAxis));
                    u  = lerp(flowing.getU0(), flowing.getU1(), across * 0.5f);
                    vv = lerp(flowing.getV0(), flowing.getV1(), along * 0.5f);
                }
                v(buf, m, p[0], p[1], p[2], r, g, b, a, u, vv, sky, block);
            }
        }
    }

    private static float coord(float[] p, Direction.Axis axis) {
        return switch (axis) {
            case X -> p[0];
            case Y -> p[1];
            case Z -> p[2];
        };
    }

    private static Direction.Axis otherAxis(Direction.Axis a, Direction.Axis b) {
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != a && axis != b) return axis;
        }
        return a;
    }

    private static float[] inPlane(float[] p, Direction.Axis normal) {
        return switch (normal) {
            case X -> new float[]{p[2], 1.0f - p[1]};
            case Y -> new float[]{p[0], p[2]};
            case Z -> new float[]{p[0], 1.0f - p[1]};
        };
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
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