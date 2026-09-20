package com.titammods.compat.jade;

import com.titammods.TitamMods;
import com.titammods.block.SmelteryControllerBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum SmelteryProgressProvider implements IBlockComponentProvider {

    INSTANCE;

    private static final int MAX_ENTRIES = 5;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof SmelteryControllerBlockEntity smeltery)) return;

        int slots = smeltery.itemHandler.getSlots();
        if (slots == 0) return;

        IElementHelper helper = IElementHelper.get();
        int shown = 0;
        int totalActive = 0;

        for (int i = 0; i < slots; i++) {
            ItemStack stack = smeltery.itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && i < smeltery.meltingTime.length && smeltery.meltingTime[i] > 0) {
                totalActive++;
            }
        }

        for (int i = 0; i < slots && shown < MAX_ENTRIES; i++) {
            ItemStack stack = smeltery.itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (i >= smeltery.meltingState.length || i >= smeltery.meltingTime.length
                    || i >= smeltery.meltingProgress.length) continue;
            if (smeltery.meltingTime[i] <= 0) continue;

            if (smeltery.meltingState[i] == 0) {
                int percent = (int) ((float) smeltery.meltingProgress[i] / smeltery.meltingTime[i] * 100);
                tooltip.add(helper.smallItem(stack));
                tooltip.append(helper.text(Component.translatable(
                        "jade.hephaestus.melting_entry", stack.getHoverName(), percent)));
                shown++;
            } else if (smeltery.meltingState[i] == 3) {
                tooltip.add(helper.smallItem(stack));
                tooltip.append(helper.text(Component.translatable(
                        "jade.hephaestus.melting_full_tank", stack.getHoverName())));
                shown++;
            } else if (smeltery.meltingState[i] == 2) {
                tooltip.add(helper.smallItem(stack));
                tooltip.append(helper.text(Component.translatable(
                        "jade.hephaestus.melting_too_cold", stack.getHoverName())));
                shown++;
            }
        }

        int remaining = totalActive - shown;
        if (remaining > 0) {
            tooltip.add(Component.translatable("jade.hephaestus.melting_more", remaining));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery_progress");
    }
}