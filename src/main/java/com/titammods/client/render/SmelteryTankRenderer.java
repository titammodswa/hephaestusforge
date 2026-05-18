package com.titammods.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.block.SmelteryTankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class SmelteryTankRenderer implements BlockEntityRenderer<SmelteryTankBlockEntity> {

    public SmelteryTankRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(SmelteryTankBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidStack fluidStack = entity.getTank().getFluid();
        if (fluidStack.isEmpty()) return;

        float minX = 0.126f;
        float minZ = 0.126f;
        float maxX = 0.874f;
        float maxZ = 0.874f;
        float minY = 0.01f;

        float capacity = entity.getTank().getCapacity();
        float fillPercentage = (float) fluidStack.getAmount() / capacity;
        float height = minY + (fillPercentage * (0.98f - minY));

        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(clientFluid.getStillTexture(fluidStack));
        int color = clientFluid.getTintColor(fluidStack);

        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        Level level = entity.getLevel();
        BlockPos pos = entity.getBlockPos();
        int light = level != null ? LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos)) : packedLight;

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();

        float vBottom = vMax - (vMax - vMin) * minY;
        float vTop = vMax - (vMax - vMin) * height;

        // Norte (Z-)
        addVertex(builder, matrix, maxX, minY, minZ, uMin, vBottom, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, matrix, minX, minY, minZ, uMax, vBottom, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, matrix, minX, height, minZ, uMax, vTop, r, g, b, a, light, 0, 0, -1);
        addVertex(builder, matrix, maxX, height, minZ, uMin, vTop, r, g, b, a, light, 0, 0, -1);

        // Sul (Z+)
        addVertex(builder, matrix, minX, minY, maxZ, uMin, vBottom, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, matrix, maxX, minY, maxZ, uMax, vBottom, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, matrix, maxX, height, maxZ, uMax, vTop, r, g, b, a, light, 0, 0, 1);
        addVertex(builder, matrix, minX, height, maxZ, uMin, vTop, r, g, b, a, light, 0, 0, 1);

        // Oeste (X-)
        addVertex(builder, matrix, minX, minY, minZ, uMin, vBottom, r, g, b, a, light, -1, 0, 0);
        addVertex(builder, matrix, minX, minY, maxZ, uMax, vBottom, r, g, b, a, light, -1, 0, 0);
        addVertex(builder, matrix, minX, height, maxZ, uMax, vTop, r, g, b, a, light, -1, 0, 0);
        addVertex(builder, matrix, minX, height, minZ, uMin, vTop, r, g, b, a, light, -1, 0, 0);

        // Leste (X+)
        addVertex(builder, matrix, maxX, minY, maxZ, uMin, vBottom, r, g, b, a, light, 1, 0, 0);
        addVertex(builder, matrix, maxX, minY, minZ, uMax, vBottom, r, g, b, a, light, 1, 0, 0);
        addVertex(builder, matrix, maxX, height, minZ, uMax, vTop, r, g, b, a, light, 1, 0, 0);
        addVertex(builder, matrix, maxX, height, maxZ, uMin, vTop, r, g, b, a, light, 1, 0, 0);

        // Topo (Y+)
        addVertex(builder, matrix, minX, height, maxZ, uMin, vMax, r, g, b, a, light, 0, 1, 0);
        addVertex(builder, matrix, maxX, height, maxZ, uMax, vMax, r, g, b, a, light, 0, 1, 0);
        addVertex(builder, matrix, maxX, height, minZ, uMax, vMin, r, g, b, a, light, 0, 1, 0);
        addVertex(builder, matrix, minX, height, minZ, uMin, vMin, r, g, b, a, light, 0, 1, 0);

        // Fundo (Y-)
        addVertex(builder, matrix, minX, minY, minZ, uMin, vMin, r, g, b, a, light, 0, -1, 0);
        addVertex(builder, matrix, maxX, minY, minZ, uMax, vMin, r, g, b, a, light, 0, -1, 0);
        addVertex(builder, matrix, maxX, minY, maxZ, uMax, vMax, r, g, b, a, light, 0, -1, 0);
        addVertex(builder, matrix, minX, minY, maxZ, uMin, vMax, r, g, b, a, light, 0, -1, 0);
    }

    private void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float u, float v, float r, float g, float b, float a, int light, float nx, float ny, float nz) {
        builder.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setLight(light)
                .setNormal(nx, ny, nz);
    }
}