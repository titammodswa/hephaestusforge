package com.titammods.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class FluidUnits {
    private static final int NUGGET_MB = 10;
    private static final int INGOT_MB = 9 * NUGGET_MB;
    private static final int BLOCK_MB = 9 * INGOT_MB;

    private FluidUnits() {}

    public static MutableComponent breakdown(int amount) {
        int blocks = amount / BLOCK_MB;
        int ingots = amount % BLOCK_MB / INGOT_MB;
        int nuggets = amount % INGOT_MB / NUGGET_MB;
        int remainder = amount % NUGGET_MB;
        MutableComponent result = Component.empty();
        appendUnit(result, blocks, "gui.hephaestus.unit.blocks");
        appendUnit(result, ingots, "gui.hephaestus.unit.ingots");
        appendUnit(result, nuggets, "gui.hephaestus.unit.nuggets");
        if (remainder > 0 || amount == 0) {
            if (!result.getSiblings().isEmpty()) result.append(" ");
            result.append(remainder + " mB");
        }
        return result;
    }

    private static void appendUnit(MutableComponent result, int count, String key) {
        if (count > 0) {
            if (!result.getSiblings().isEmpty()) result.append(" ");
            result.append(count + " ").append(Component.translatable(key));
        }
    }
}
