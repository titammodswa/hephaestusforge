package com.titammods.compat.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class EntityRenderHelper {

    private static final Map<Identifier, Entity> CACHE = new HashMap<>();

    public static void clearCache() { CACHE.clear(); }

    public static void render(GuiGraphicsExtractor graphics, int x, int y, int size,
                              EntityType<?> type, Minecraft mc) {
        if (mc.level == null || mc.player == null) return;

        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (id == null) return;

        Entity entity = CACHE.computeIfAbsent(id, k -> {
            Entity e = type.create(mc.level, null);
            if (e instanceof LivingEntity le) {
                le.setYBodyRot(0);
                le.setYRot(0);
                le.setXRot(0);
                le.yHeadRot = 0;
                le.yHeadRotO = 0;
            }
            return e;
        });
        if (!(entity instanceof LivingEntity living)) return;

        living.tickCount = mc.player.tickCount;
        living.setYBodyRot(0);
        living.setYRot(0);
        living.setXRot(0);
        org.joml.Matrix3x2fStack poseStack = graphics.pose();
        org.joml.Vector2f p1 = poseStack.transformPosition(new org.joml.Vector2f(x, y), new org.joml.Vector2f());
        org.joml.Vector2f p2 = poseStack.transformPosition(new org.joml.Vector2f(x + size, y + size), new org.joml.Vector2f());

        int ax1 = Math.round(p1.x), ay1 = Math.round(p1.y);
        int ax2 = Math.round(p2.x), ay2 = Math.round(p2.y);
        int cx  = (ax1 + ax2) / 2,  cy  = (ay1 + ay2) / 2;

        float entityH = Math.max(living.getBbHeight(), living.getBbWidth());
        int dynScale = Math.max(1, (int)(size * 0.6f / entityH));

        InventoryScreen.extractEntityInInventoryFollowsMouse(
                graphics,
                ax1, ay1, ax2, ay2,
                dynScale,
                0,
                (float) cx,
                (float) cy,
                living
        );
    }
}