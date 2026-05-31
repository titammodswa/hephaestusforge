package com.titammods.common.blockentities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.titammods.TitamMods;
import com.titammods.common.blockentities.SearedTankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3fc;
import java.util.function.Consumer;

public class SearedTankItemRenderer implements SpecialModelRenderer<CompoundTag> {

    public static final Identifier RENDERER_ID =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "seared_tank_fluid");

    public static class Unbaked implements SpecialModelRenderer.Unbaked<CompoundTag> {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public SpecialModelRenderer<CompoundTag> bake(SpecialModelRenderer.BakingContext ctx) {
            return new SearedTankItemRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<CompoundTag>> type() {
            return CODEC;
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {}

    @Override
    public @Nullable CompoundTag extractArgument(ItemStack stack) {
        var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return (data != null) ? data.copyTagWithoutId() : null;
    }

    @Override
    public void submit(@Nullable CompoundTag tag, PoseStack poseStack,
                       SubmitNodeCollector collector,
                       int packedLight, int packedOverlay,
                       boolean hasFoil, int outlineColor) {
        if (tag == null) return;

        var mc = Minecraft.getInstance();
        if (mc.level == null) return;

        var parseOps = mc.level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        FluidStack fluid = FluidStack.OPTIONAL_CODEC
                .parse(parseOps, tag.get("fluid"))
                .result()
                .orElse(FluidStack.EMPTY);

        if (fluid.isEmpty()) return;

        FluidState fluidState = fluid.getFluid().defaultFluidState();
        var fluidModel = mc.getModelManager().getFluidStateModelSet().get(fluidState);
        TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();
        var tintSrc = fluidModel.fluidTintSource();
        int color = (tintSrc != null) ? tintSrc.colorAsStack(fluid) : -1;
        int alpha = (color >> 24 & 0xFF) > 0 ? (color >> 24 & 0xFF) : 255;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8)  & 0xFF;
        int b =  color        & 0xFF;

        int sky = packedLight >> 16 & 0xFFFF;
        int bk  = packedLight       & 0xFFFF;
        int fluidLight = fluid.getFluidType().getLightLevel(fluid);
        final int finalBlock = (fluidLight * 16 > bk) ? fluidLight * 16 : bk;

        // Bounds (idêntico ao renderer de mundo)
        float d    = 1f / 16f;
        float fill = Mth.clamp((float) fluid.getAmount() / SearedTankBlockEntity.CAPACITY, 0.003f, 1f);
        float x0 = d*2, x1 = 1f - d*2;
        float z0 = d*2, z1 = 1f - d*2;
        float y0 = d*2, y1 = y0 + (d*12) * fill;

        poseStack.pushPose();
        collector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(),
                (pose, buf) -> SearedTankRenderer.renderBox(
                        buf, pose, sprite, r, g, b, alpha,
                        sky, finalBlock, x0, y0, z0, x1, y1, z1));
        poseStack.popPose();
    }
}
