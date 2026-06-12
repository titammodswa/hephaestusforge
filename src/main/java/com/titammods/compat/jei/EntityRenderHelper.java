package com.titammods.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public class EntityRenderHelper {

    private static final Map<ResourceLocation, Entity> CACHE = new HashMap<>();

    public static void clearCache() { CACHE.clear(); }

    public static void render(GuiGraphics guiGraphics, int x, int y, int size,
                              EntityType<?> type) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (id == null) return;

        Entity entity = CACHE.computeIfAbsent(id, k -> {
            Entity e = type.create(mc.level);
            if (e instanceof LivingEntity le) {
                le.yBodyRot  = 0; le.yHeadRot  = 0;
                le.yHeadRotO = 0; le.setYRot(0); le.setXRot(0);
            }
            return e;
        });
        if (!(entity instanceof LivingEntity living)) return;

        living.yBodyRot  = 0; living.yHeadRot  = 0;
        living.yHeadRotO = 0; living.setYRot(0); living.setXRot(0);
        if (mc.player != null) living.tickCount = mc.player.tickCount;

        float entitySize = Math.max(living.getBbHeight(), living.getBbWidth());
        float maxPixels  = size * 0.75f;
        float scale      = maxPixels / entitySize;

        float floorY  = y + size * 0.88f;
        float centerX = x + size / 2f;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(centerX, floorY, 50f);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.translate(0.0F, living.getBbHeight() / 5f, 0.0F);

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        dispatcher.overrideCameraOrientation(new Quaternionf(0f, 0f, 0f, 1f));
        dispatcher.setRenderShadow(false);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() ->
                dispatcher.render(living, 0.0, 0.0, 0.0, 0f, 1f, poseStack, bufferSource, 15728880));
        bufferSource.endBatch();

        dispatcher.setRenderShadow(true);
        poseStack.popPose();
    }
}