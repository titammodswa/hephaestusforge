package com.titammods.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.titammods.block.TableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class TableRenderer implements BlockEntityRenderer<TableBlockEntity> {

    public TableRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TableBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStack mold = entity.inventory.getStackInSlot(0);
        ItemStack output = entity.inventory.getStackInSlot(1);
        ItemStack renderResult = entity.renderResult;

        float tableHeight = 0.9375f;

        if (!mold.isEmpty()) {
            renderItemFlat(mold, poseStack, bufferSource, packedLight, packedOverlay, tableHeight, entity);
        }

        if (!output.isEmpty() || !renderResult.isEmpty()) {
            ItemStack itemToDraw = !output.isEmpty() ? output : renderResult;
            float yPos = mold.isEmpty() ? tableHeight : tableHeight + 0.01f;
            renderItemFlat(itemToDraw, poseStack, bufferSource, packedLight, packedOverlay, yPos, entity);
            return;
        }

        FluidStack fluidStack = entity.tank.getFluid();
        if (!fluidStack.isEmpty() && fluidStack.getFluid() != null) {
            IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(clientFluid.getStillTexture(fluidStack));

            float minX = 0.008f, maxX = 1.0f;
            float minZ = 0.001f, maxZ = 1.0f;

            float minY = tableHeight + 0.001f;
            float maxY = tableHeight + 0.005f;

            float capacity = entity.tank.getCapacity();
            float fillPercentage = (float) fluidStack.getAmount() / capacity;
            float height = minY + (fillPercentage * (maxY - minY));

            int color = clientFluid.getTintColor(fluidStack);
            float a = ((color >> 24) & 0xFF) / 255f;
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;

            int fluidLuminosity = fluidStack.getFluid().getFluidType().getLightLevel(fluidStack);
            int blockLight = Math.max(packedLight & 0xFFFF, fluidLuminosity << 4);
            int light = blockLight | ((packedLight >> 16) & 0xFFFF) << 16;

            VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
            Matrix4f matrix = poseStack.last().pose();

            addVertex(builder, matrix, minX, height, maxZ, u(sprite, minX), v(sprite, maxZ), r, g, b, a, light, 0, 1, 0);
            addVertex(builder, matrix, maxX, height, maxZ, u(sprite, maxX), v(sprite, maxZ), r, g, b, a, light, 0, 1, 0);
            addVertex(builder, matrix, maxX, height, minZ, u(sprite, maxX), v(sprite, minZ), r, g, b, a, light, 0, 1, 0);
            addVertex(builder, matrix, minX, height, minZ, u(sprite, minX), v(sprite, minZ), r, g, b, a, light, 0, 1, 0);
        }
    }

    private void renderItemFlat(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, float height, TableBlockEntity entity) {
        poseStack.pushPose();
        poseStack.translate(0.5D, height, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.scale(0.89f, 0.89f, 1.85f);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, entity.getLevel(), 0
        );
        poseStack.popPose();
    }

    private float u(TextureAtlasSprite s, float c) { return s.getU0() + (s.getU1() - s.getU0()) * c; }
    private float v(TextureAtlasSprite s, float c) { return s.getV0() + (s.getV1() - s.getV0()) * c; }
    private void addVertex(VertexConsumer b, Matrix4f m, float x, float y, float z, float u, float v, float r, float g, float bl, float a, int l, float nx, float ny, float nz) {
        b.addVertex(m, x, y, z).setColor(r, g, bl, a).setUv(u, v).setLight(l).setNormal(nx, ny, nz);
    }
}