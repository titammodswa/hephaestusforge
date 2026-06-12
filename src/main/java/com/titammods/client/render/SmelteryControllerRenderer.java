package com.titammods.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.block.SmelteryControllerBlockEntity;
import com.titammods.block.SmelteryControllerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

import java.util.List;

public class SmelteryControllerRenderer implements BlockEntityRenderer<SmelteryControllerBlockEntity> {

    private static final float FLUID_OFFSET = 0.005f;

    private static final RenderType ERROR_BLOCK = RenderType.create(
            "hephaestus:error_block",
            com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_NORMAL,
            com.mojang.blaze3d.vertex.VertexFormat.Mode.LINES,
            256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
                    .setLineState(new net.minecraft.client.renderer.RenderStateShard.LineStateShard(java.util.OptionalDouble.empty()))
                    .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderType.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
                    .setCullState(RenderType.NO_CULL)
                    .setDepthTestState(RenderType.NO_DEPTH_TEST)
                    .createCompositeState(false));

    public SmelteryControllerRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(SmelteryControllerBlockEntity entity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {

        BlockPos errorPos = entity.errorPos;
        if (errorPos != null && entity.isHighlightError()) {
            BlockPos ctrl = entity.getBlockPos();
            int dx = errorPos.getX() - ctrl.getX();
            int dy = errorPos.getY() - ctrl.getY();
            int dz = errorPos.getZ() - ctrl.getZ();

            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                net.minecraft.core.BlockPos playerPos = mc.player.blockPosition();
                int pdx = playerPos.getX() - ctrl.getX();
                int pdz = playerPos.getZ() - ctrl.getZ();
                if (pdx * pdx + pdz * pdz < 512) {
                    boolean structureValid = entity.getBlockState().getValue(SmelteryControllerBlock.IN_STRUCTURE);
                    float r = 1f, g = structureValid ? 1f : 0f, b = 0f, a = 0.8f;

                    com.mojang.blaze3d.vertex.VertexConsumer vc = bufferSource.getBuffer(ERROR_BLOCK);
                    renderBlockOutline(poseStack, vc, dx, dy, dz, r, g, b, a);
                }
            }
        }

        if (!entity.getBlockState().getValue(SmelteryControllerBlock.IN_STRUCTURE)) return;

        BlockPos minI = entity.syncedMinInner;
        BlockPos maxI = entity.syncedMaxInner;
        if (minI == null || maxI == null) return;

        List<FluidStack> fluids = entity.fluidTank.getFluids();
        if (fluids.isEmpty()) return;

        int totalCapacity = entity.fluidTank.getCapacity();
        if (totalCapacity <= 0) return;

        BlockPos ctrl = entity.getBlockPos();

        int minDX = minI.getX() - ctrl.getX();
        int minDY = minI.getY() - ctrl.getY();
        int minDZ = minI.getZ() - ctrl.getZ();
        int maxDX = maxI.getX() - ctrl.getX();
        int maxDY = maxI.getY() - ctrl.getY();
        int maxDZ = maxI.getZ() - ctrl.getZ();

        int xd = maxDX - minDX;
        int yd = maxDY - minDY;
        int zd = maxDZ - minDZ;
        if (xd < 0 || yd < 0 || zd < 0) return;

        int blockLight = 0, skyLight = 0;
        if (entity.getLevel() != null) {
            blockLight = entity.getLevel().getBrightness(net.minecraft.world.level.LightLayer.BLOCK, ctrl);
            skyLight   = entity.getLevel().getBrightness(net.minecraft.world.level.LightLayer.SKY, ctrl);
        }

        poseStack.pushPose();
        poseStack.translate(minDX, minDY, minDZ);

        int xzArea = (xd + 1) * (zd + 1);
        int totalHeightMb = (yd + 1) * 1000 * xzArea - (int)(FLUID_OFFSET * 2 * 1000);
        int[] heights = calcHeights(fluids, totalCapacity, totalHeightMb, xzArea);

        float x0 = FLUID_OFFSET,        x1 = (xd + 1) - FLUID_OFFSET;
        float z0 = FLUID_OFFSET,        z1 = (zd + 1) - FLUID_OFFSET;
        float curY = FLUID_OFFSET;

        for (int i = 0; i < fluids.size(); i++) {
            FluidStack fluid = fluids.get(i);
            if (fluid.isEmpty() || heights[i] <= 0) continue;

            float fluidH = (float) heights[i] / (float)(1000 * xzArea);
            float y0 = curY;
            float y1 = curY + fluidH;
            curY = y1;

            renderFluidLayer(poseStack, bufferSource, fluid,
                    blockLight, skyLight,
                    x0, y0, z0, x1, y1, z1, yd);
        }

        poseStack.popPose();
    }

    private void renderFluidLayer(PoseStack poseStack, MultiBufferSource bufferSource,
                                  FluidStack fluid,
                                  int blockLight, int skyLight,
                                  float x0, float y0, float z0,
                                  float x1, float y1, float z1,
                                  int yd) {

        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(ext.getStillTexture(fluid));

        int color = ext.getTintColor(fluid);
        int a = ((color >> 24) & 0xFF); if (a == 0) a = 255;
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b =  color        & 0xFF;

        int fluidLuminosity = fluid.getFluidType().getLightLevel(fluid);
        int finalBlock = Math.max(blockLight, fluidLuminosity) * 16;
        int light = (skyLight * 16) << 16 | finalBlock;

        float totalInnerH = (yd + 1);
        float fill = Math.min(1f, (y1 - y0) / totalInnerH);

        VertexConsumer buf = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f m = poseStack.last().pose();

        renderCuboid(buf, m, sprite, r, g, b, a, light, x0, y0, z0, x1, y1, z1, fill);
    }

    private static void renderCuboid(VertexConsumer buf, Matrix4f m,
                                     TextureAtlasSprite spr,
                                     int r, int g, int b, int a,
                                     int light,
                                     float x0, float y0, float z0,
                                     float x1, float y1, float z1,
                                     float fill) {
        float u0 = spr.getU0(), u1 = spr.getU1();
        float v0 = spr.getV0(), v1 = spr.getV1();
        float uvH  = v1 - v0;
        float vTop = v0 + uvH * (1f - fill);

        v(buf,m,x0,y1,z0, r,g,b,a, u0,v0, light);
        v(buf,m,x0,y1,z1, r,g,b,a, u0,v1, light);
        v(buf,m,x1,y1,z1, r,g,b,a, u1,v1, light);
        v(buf,m,x1,y1,z0, r,g,b,a, u1,v0, light);

        v(buf,m,x1,y1,z0, r,g,b,a, u1,v0, light);
        v(buf,m,x1,y1,z1, r,g,b,a, u1,v1, light);
        v(buf,m,x0,y1,z1, r,g,b,a, u0,v1, light);
        v(buf,m,x0,y1,z0, r,g,b,a, u0,v0, light);

        v(buf,m,x0,y0,z1, r,g,b,a, u0,v1, light);
        v(buf,m,x0,y0,z0, r,g,b,a, u0,v0, light);
        v(buf,m,x1,y0,z0, r,g,b,a, u1,v0, light);
        v(buf,m,x1,y0,z1, r,g,b,a, u1,v1, light);

        v(buf,m,x1,y1,z0, r,g,b,a, u0,vTop, light);
        v(buf,m,x1,y0,z0, r,g,b,a, u0,v1,   light);
        v(buf,m,x0,y0,z0, r,g,b,a, u1,v1,   light);
        v(buf,m,x0,y1,z0, r,g,b,a, u1,vTop, light);

        v(buf,m,x0,y1,z0, r,g,b,a, u1,vTop, light);
        v(buf,m,x0,y0,z0, r,g,b,a, u1,v1,   light);
        v(buf,m,x1,y0,z0, r,g,b,a, u0,v1,   light);
        v(buf,m,x1,y1,z0, r,g,b,a, u0,vTop, light);

        v(buf,m,x0,y1,z1, r,g,b,a, u0,vTop, light);
        v(buf,m,x0,y0,z1, r,g,b,a, u0,v1,   light);
        v(buf,m,x1,y0,z1, r,g,b,a, u1,v1,   light);
        v(buf,m,x1,y1,z1, r,g,b,a, u1,vTop, light);

        v(buf,m,x1,y1,z1, r,g,b,a, u1,vTop, light);
        v(buf,m,x1,y0,z1, r,g,b,a, u1,v1,   light);
        v(buf,m,x0,y0,z1, r,g,b,a, u0,v1,   light);
        v(buf,m,x0,y1,z1, r,g,b,a, u0,vTop, light);

        v(buf,m,x0,y1,z0, r,g,b,a, u0,vTop, light);
        v(buf,m,x0,y0,z0, r,g,b,a, u0,v1,   light);
        v(buf,m,x0,y0,z1, r,g,b,a, u1,v1,   light);
        v(buf,m,x0,y1,z1, r,g,b,a, u1,vTop, light);

        v(buf,m,x0,y1,z1, r,g,b,a, u1,vTop, light);
        v(buf,m,x0,y0,z1, r,g,b,a, u1,v1,   light);
        v(buf,m,x0,y0,z0, r,g,b,a, u0,v1,   light);
        v(buf,m,x0,y1,z0, r,g,b,a, u0,vTop, light);

        v(buf,m,x1,y1,z1, r,g,b,a, u0,vTop, light);
        v(buf,m,x1,y0,z1, r,g,b,a, u0,v1,   light);
        v(buf,m,x1,y0,z0, r,g,b,a, u1,v1,   light);
        v(buf,m,x1,y1,z0, r,g,b,a, u1,vTop, light);

        v(buf,m,x1,y1,z0, r,g,b,a, u1,vTop, light);
        v(buf,m,x1,y0,z0, r,g,b,a, u1,v1,   light);
        v(buf,m,x1,y0,z1, r,g,b,a, u0,v1,   light);
        v(buf,m,x1,y1,z1, r,g,b,a, u0,vTop, light);
    }

    private static void v(VertexConsumer buf, Matrix4f m,
                          float x, float y, float z,
                          int r, int g, int bv, int a,
                          float u, float v, int light) {
        buf.addVertex(m, x, y, z)
                .setColor(r, g, bv, a)
                .setUv(u, v)
                .setLight(light)
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

    private static void renderBlockOutline(PoseStack poseStack, VertexConsumer vc,
                                           double ox, double oy, double oz,
                                           float r, float g, float b, float a) {
        org.joml.Matrix4f m = poseStack.last().pose();

        float x0 = (float) ox,       y0 = (float) oy,       z0 = (float) oz;
        float x1 = (float)(ox + 1),  y1 = (float)(oy + 1),  z1 = (float)(oz + 1);

        line(vc, m, x0,y0,z0, x1,y0,z0, r,g,b,a,  0,-1, 0);
        line(vc, m, x1,y0,z0, x1,y0,z1, r,g,b,a,  0,-1, 0);
        line(vc, m, x1,y0,z1, x0,y0,z1, r,g,b,a,  0,-1, 0);
        line(vc, m, x0,y0,z1, x0,y0,z0, r,g,b,a,  0,-1, 0);
        line(vc, m, x0,y1,z0, x1,y1,z0, r,g,b,a,  0, 1, 0);
        line(vc, m, x1,y1,z0, x1,y1,z1, r,g,b,a,  0, 1, 0);
        line(vc, m, x1,y1,z1, x0,y1,z1, r,g,b,a,  0, 1, 0);
        line(vc, m, x0,y1,z1, x0,y1,z0, r,g,b,a,  0, 1, 0);
        line(vc, m, x0,y0,z0, x0,y1,z0, r,g,b,a, -1, 0,-1);
        line(vc, m, x1,y0,z0, x1,y1,z0, r,g,b,a,  1, 0,-1);
        line(vc, m, x1,y0,z1, x1,y1,z1, r,g,b,a,  1, 0, 1);
        line(vc, m, x0,y0,z1, x0,y1,z1, r,g,b,a, -1, 0, 1);
    }

    private static void line(VertexConsumer vc, org.joml.Matrix4f m,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float r, float g, float b, float a,
                             float nx, float ny, float nz) {
        vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setNormal(nx, ny, nz);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(SmelteryControllerBlockEntity entity) {
        return net.minecraft.world.phys.AABB.INFINITE;
    }

    @Override
    public int getViewDistance() { return 256; }
}