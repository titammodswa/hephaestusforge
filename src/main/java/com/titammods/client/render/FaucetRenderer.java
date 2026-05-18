package com.titammods.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.block.FaucetBlockEntity;
import com.titammods.block.SearedFaucetBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class FaucetRenderer implements BlockEntityRenderer<FaucetBlockEntity> {

    public FaucetRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(FaucetBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!entity.isPouring() || entity.getRenderFluid().isEmpty()) return;

        FluidStack fluidStack = entity.getRenderFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite flowingSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(clientFluid.getFlowingTexture(fluidStack));

        int color = clientFluid.getTintColor(fluidStack);
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        Level level = entity.getLevel();
        BlockPos pos = entity.getBlockPos();
        int fluidLuminosity = fluidStack.getFluid().getFluidType().getLightLevel(fluidStack);
        int blockLight = Math.max(packedLight & 0xFFFF, fluidLuminosity << 4);
        int skyLight = (packedLight >> 16) & 0xFFFF;
        int light = blockLight | (skyLight << 16);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();
        Direction facing = entity.getBlockState().getValue(SearedFaucetBlock.FACING);

        float hMinX = 0.375f, hMaxX = 0.625f;
        float hMinY = 0.375f, hMaxY = 0.625f;
        float hMinZ = 0.375f, hMaxZ = 0.625f;

        switch (facing) {
            case NORTH -> hMaxZ = 1.0f;
            case SOUTH -> hMinZ = 0.0f;
            case WEST  -> hMaxX = 1.0f;
            case EAST  -> hMinX = 0.0f;
        }
        renderCuboid(builder, matrix, hMinX, hMinY, hMinZ, hMaxX, hMaxY, hMaxZ, flowingSprite, r, g, b, a, light, true);

        float vMinX = 0.375f, vMaxX = 0.625f;
        float vMinZ = 0.375f, vMaxZ = 0.625f;
        float vMaxY = 0.375f;

        float vMinY = -0.75f;

        if (level != null) {
            BlockEntity belowEntity = level.getBlockEntity(pos.below());
            if (belowEntity instanceof com.titammods.block.TableBlockEntity) {
                vMinY = -0.0625f;
            }
        }

        renderCuboid(builder, matrix, vMinX, vMinY, vMinZ, vMaxX, vMaxY, vMaxZ, flowingSprite, r, g, b, a, light, false);
    }

    private void renderCuboid(VertexConsumer builder, Matrix4f matrix, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite sprite, float r, float g, float b, float a, int light, boolean isHorizontal) {

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // Face Superior
        addVertex(builder, matrix, minX, maxY, maxZ, u(u0, u1, minX), v(v0, v1, maxZ), r, g, b, a, light, 0, 1, 0);
        addVertex(builder, matrix, maxX, maxY, maxZ, u(u0, u1, maxX), v(v0, v1, maxZ), r, g, b, a, light, 0, 1, 0);
        addVertex(builder, matrix, maxX, maxY, minZ, u(u0, u1, maxX), v(v0, v1, minZ), r, g, b, a, light, 0, 1, 0);
        addVertex(builder, matrix, minX, maxY, minZ, u(u0, u1, minX), v(v0, v1, minZ), r, g, b, a, light, 0, 1, 0);

        // Face Inferior
        addVertex(builder, matrix, minX, minY, minZ, u(u0, u1, minX), v(v0, v1, minZ), r, g, b, a, light, 0, -1, 0);
        addVertex(builder, matrix, maxX, minY, minZ, u(u0, u1, maxX), v(v0, v1, minZ), r, g, b, a, light, 0, -1, 0);
        addVertex(builder, matrix, maxX, minY, maxZ, u(u0, u1, maxX), v(v0, v1, maxZ), r, g, b, a, light, 0, -1, 0);
        addVertex(builder, matrix, minX, minY, maxZ, u(u0, u1, minX), v(v0, v1, maxZ), r, g, b, a, light, 0, -1, 0);

        float topV = v0;
        float botV = v1;

        // Norte
        addVertex(builder, matrix, maxX, minY, minZ, u(u0, u1, 1), botV, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, matrix, minX, minY, minZ, u(u0, u1, 0), botV, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, matrix, minX, maxY, minZ, u(u0, u1, 0), topV, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, matrix, maxX, maxY, minZ, u(u0, u1, 1), topV, r, g, b, a, light, 0, 0, -1);

        // Sul
        addVertex(builder, matrix, minX, minY, maxZ, u(u0, u1, 0), botV, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, matrix, maxX, minY, maxZ, u(u0, u1, 1), botV, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, matrix, maxX, maxY, maxZ, u(u0, u1, 1), topV, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, matrix, minX, maxY, maxZ, u(u0, u1, 0), topV, r, g, b, a, light, 0, 0, 1);

        // Oeste
        addVertex(builder, matrix, minX, minY, minZ, u(u0, u1, 0), botV, r, g, b, a, light, -1, 0, 0);
        addVertex(builder, matrix, minX, minY, maxZ, u(u0, u1, 1), botV, r, g, b, a, light, -1, 0, 0);
        addVertex(builder, matrix, minX, maxY, maxZ, u(u0, u1, 1), topV, r, g, b, a, light, -1, 0, 0);
        addVertex(builder, matrix, minX, maxY, minZ, u(u0, u1, 0), topV, r, g, b, a, light, -1, 0, 0);

        // Leste
        addVertex(builder, matrix, maxX, minY, maxZ, u(u0, u1, 1), botV, r, g, b, a, light, 1, 0, 0);
        addVertex(builder, matrix, maxX, minY, minZ, u(u0, u1, 0), botV, r, g, b, a, light, 1, 0, 0);
        addVertex(builder, matrix, maxX, maxY, minZ, u(u0, u1, 0), topV, r, g, b, a, light, 1, 0, 0);
        addVertex(builder, matrix, maxX, maxY, maxZ, u(u0, u1, 1), topV, r, g, b, a, light, 1, 0, 0);
    }

    private float u(float u0, float u1, float percent) { return u0 + (u1 - u0) * percent; }
    private float v(float v0, float v1, float percent) { return v0 + (v1 - v0) * percent; }

    private void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float u, float v, float r, float g, float b, float a, int light, float nx, float ny, float nz) {
        builder.addVertex(matrix, x, y, z).setColor(r, g, b, a).setUv(u, v).setLight(light).setNormal(nx, ny, nz);
    }
}