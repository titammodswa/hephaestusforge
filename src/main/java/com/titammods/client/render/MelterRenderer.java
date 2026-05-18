package com.titammods.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.titammods.block.MelterBlockEntity;
import com.titammods.block.SearedMachineBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class MelterRenderer implements BlockEntityRenderer<MelterBlockEntity> {

    public MelterRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(MelterBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        int itemLight = packedLight;

        int currentBlockLight = itemLight & 0xFFFF;
        int currentSkyLight = (itemLight >> 16) & 0xFFFF;
        currentBlockLight = Math.max(currentBlockLight, 4 << 4);

        FluidStack fluidStack = entity.tank.getFluid();
        if (!fluidStack.isEmpty() && fluidStack.getFluid() != null) {
            int fluidLuminosity = fluidStack.getFluid().getFluidType().getLightLevel(fluidStack);
            currentBlockLight = Math.max(currentBlockLight, fluidLuminosity << 4);
        }

        if (entity.fuel > 0) {
            currentBlockLight = Math.max(currentBlockLight, 13 << 4);
        }

        itemLight = currentBlockLight | (currentSkyLight << 16);

        Direction facing = entity.getBlockState().getValue(SearedMachineBlock.FACING);

        for (int i = 0; i < 3; i++) {
            ItemStack stack = entity.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                poseStack.pushPose();

                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
                poseStack.translate(-0.5D, -0.5D, -0.5D);

                float x = 0.5f;
                float z = 0.5f;
                if (i == 0) { x = 0.28f; z = 0.28f; }
                else if (i == 1) { x = 0.72f; z = 0.28f; }
                else if (i == 2) { x = 0.50f; z = 0.72f; }

                float y = 0.7125f;

                poseStack.translate(x, y, z);
                poseStack.scale(0.425f, 0.425f, 0.425f);

                Minecraft.getInstance().getItemRenderer().renderStatic(
                        stack, ItemDisplayContext.NONE, itemLight, packedOverlay, poseStack, bufferSource, entity.getLevel(), 0
                );

                poseStack.popPose();
            }
        }

        if (fluidStack.isEmpty() || fluidStack.getFluid() == null) return;

        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation stillTexture = clientFluid.getStillTexture(fluidStack);
        if (stillTexture == null) return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        if (sprite == null) return;

        float offset = 0.005f;
        float minX = offset;
        float minZ = offset;
        float maxX = 1f - offset;
        float maxZ = 1f - offset;
        float minY = 0.500f;
        float maxY = 1f - offset;

        float capacity = entity.tank.getCapacity();
        float fillPercentage = (float) fluidStack.getAmount() / capacity;
        float height = minY + (fillPercentage * (maxY - minY));

        int color = clientFluid.getTintColor(fluidStack);
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        // Face Norte
        addVertex(builder, matrix, maxX, minY, minZ, u(sprite, maxX), v(sprite, 1 - minY), r, g, b, a, itemLight, 0, 0, -1);
        addVertex(builder, matrix, minX, minY, minZ, u(sprite, minX), v(sprite, 1 - minY), r, g, b, a, itemLight, 0, 0, -1);
        addVertex(builder, matrix, minX, height, minZ, u(sprite, minX), v(sprite, 1 - height), r, g, b, a, itemLight, 0, 0, -1);
        addVertex(builder, matrix, maxX, height, minZ, u(sprite, maxX), v(sprite, 1 - height), r, g, b, a, itemLight, 0, 0, -1);

        // Face Sul
        addVertex(builder, matrix, minX, minY, maxZ, u(sprite, minX), v(sprite, 1 - minY), r, g, b, a, itemLight, 0, 0, 1);
        addVertex(builder, matrix, maxX, minY, maxZ, u(sprite, maxX), v(sprite, 1 - minY), r, g, b, a, itemLight, 0, 0, 1);
        addVertex(builder, matrix, maxX, height, maxZ, u(sprite, maxX), v(sprite, 1 - height), r, g, b, a, itemLight, 0, 0, 1);
        addVertex(builder, matrix, minX, height, maxZ, u(sprite, minX), v(sprite, 1 - height), r, g, b, a, itemLight, 0, 0, 1);

        // Face Oeste
        addVertex(builder, matrix, minX, minY, minZ, u(sprite, minZ), v(sprite, 1 - minY), r, g, b, a, itemLight, -1, 0, 0);
        addVertex(builder, matrix, minX, minY, maxZ, u(sprite, maxZ), v(sprite, 1 - minY), r, g, b, a, itemLight, -1, 0, 0);
        addVertex(builder, matrix, minX, height, maxZ, u(sprite, maxZ), v(sprite, 1 - height), r, g, b, a, itemLight, -1, 0, 0);
        addVertex(builder, matrix, minX, height, minZ, u(sprite, minZ), v(sprite, 1 - height), r, g, b, a, itemLight, -1, 0, 0);

        // Face Leste
        addVertex(builder, matrix, maxX, minY, maxZ, u(sprite, maxZ), v(sprite, 1 - minY), r, g, b, a, itemLight, 1, 0, 0);
        addVertex(builder, matrix, maxX, minY, minZ, u(sprite, minZ), v(sprite, 1 - minY), r, g, b, a, itemLight, 1, 0, 0);
        addVertex(builder, matrix, maxX, height, minZ, u(sprite, minZ), v(sprite, 1 - height), r, g, b, a, itemLight, 1, 0, 0);
        addVertex(builder, matrix, maxX, height, maxZ, u(sprite, maxZ), v(sprite, 1 - height), r, g, b, a, itemLight, 1, 0, 0);

        // Face Topo
        addVertex(builder, matrix, minX, height, maxZ, u(sprite, minX), v(sprite, maxZ), r, g, b, a, itemLight, 0, 1, 0);
        addVertex(builder, matrix, maxX, height, maxZ, u(sprite, maxX), v(sprite, maxZ), r, g, b, a, itemLight, 0, 1, 0);
        addVertex(builder, matrix, maxX, height, minZ, u(sprite, maxX), v(sprite, minZ), r, g, b, a, itemLight, 0, 1, 0);
        addVertex(builder, matrix, minX, height, minZ, u(sprite, minX), v(sprite, minZ), r, g, b, a, itemLight, 0, 1, 0);

        // Face Fundo
        addVertex(builder, matrix, minX, minY, minZ, u(sprite, minX), v(sprite, minZ), r, g, b, a, itemLight, 0, -1, 0);
        addVertex(builder, matrix, maxX, minY, minZ, u(sprite, maxX), v(sprite, minZ), r, g, b, a, itemLight, 0, -1, 0);
        addVertex(builder, matrix, maxX, minY, maxZ, u(sprite, maxX), v(sprite, maxZ), r, g, b, a, itemLight, 0, -1, 0);
        addVertex(builder, matrix, minX, minY, maxZ, u(sprite, minX), v(sprite, maxZ), r, g, b, a, itemLight, 0, -1, 0);
    }

    private float u(TextureAtlasSprite s, float coord) { return s.getU0() + (s.getU1() - s.getU0()) * coord; }
    private float v(TextureAtlasSprite s, float coord) { return s.getV0() + (s.getV1() - s.getV0()) * coord; }

    private void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float u, float v, float r, float g, float b, float a, int light, float nx, float ny, float nz) {
        builder.addVertex(matrix, x, y, z).setColor(r, g, b, a).setUv(u, v).setLight(light).setNormal(nx, ny, nz);
    }
}