package com.titammods.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.titammods.block.SearedMachineBlock;
import com.titammods.block.multiblock.IDisplayFluidListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class SmelteryIORenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    public SmelteryIORenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(T entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidStack fluidStack = FluidStack.EMPTY;

        if (entity instanceof IDisplayFluidListener listener) {
            fluidStack = listener.getDisplayFluid();
        }

        if (fluidStack.isEmpty() || fluidStack.getFluid() == null) return;

        BlockState state = entity.getBlockState();
        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return;
        if (state.hasProperty(SearedMachineBlock.IN_STRUCTURE) && !state.getValue(SearedMachineBlock.IN_STRUCTURE)) return;

        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation stillTexture = clientFluid.getStillTexture(fluidStack);
        if (stillTexture == null) return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        if (sprite == null) return;

        int color = clientFluid.getTintColor(fluidStack);
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        int fluidLuminosity = fluidStack.getFluid().getFluidType().getLightLevel(fluidStack);
        int currentBlockLight = Math.max(packedLight & 0xFFFF, fluidLuminosity << 4);
        int currentSkyLight = (packedLight >> 16) & 0xFFFF;
        int renderLight = currentBlockLight | (currentSkyLight << 16);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());

        poseStack.pushPose();

        poseStack.translate(0.5D, 0.5D, 0.5D);

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        float rotation = -facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        Matrix4f matrix = poseStack.last().pose();

        float w = 0.375f;

        float z = -0.502f;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        float margin = 2f / 16f;
        float uMin = u0 + (u1 - u0) * margin;
        float uMax = u0 + (u1 - u0) * (1f - margin);
        float vMin = v0 + (v1 - v0) * margin;
        float vMax = v0 + (v1 - v0) * (1f - margin);

        builder.addVertex(matrix, w, -w, z).setColor(r, g, b, a).setUv(uMax, vMax).setLight(renderLight).setNormal(0, 0, -1);
        builder.addVertex(matrix, -w, -w, z).setColor(r, g, b, a).setUv(uMin, vMax).setLight(renderLight).setNormal(0, 0, -1);
        builder.addVertex(matrix, -w, w, z).setColor(r, g, b, a).setUv(uMin, vMin).setLight(renderLight).setNormal(0, 0, -1);
        builder.addVertex(matrix, w, w, z).setColor(r, g, b, a).setUv(uMax, vMin).setLight(renderLight).setNormal(0, 0, -1);

        poseStack.popPose();
    }
}