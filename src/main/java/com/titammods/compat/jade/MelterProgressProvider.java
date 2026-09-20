package com.titammods.compat.jade;

import com.titammods.TitamMods;
import com.titammods.block.MelterBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum MelterProgressProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof MelterBlockEntity melter)) return;

        IElementHelper helper = IElementHelper.get();

        for (int i = 0; i < 3; i++) {
            ItemStack stack = melter.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (melter.maxProgress[i] <= 0) continue;

            int percent = (int) ((float) melter.progress[i] / melter.maxProgress[i] * 100);

            if (melter.state[i] == 1) {
                tooltip.add(helper.smallItem(stack));
                tooltip.append(helper.text(Component.translatable(
                        "jade.hephaestus.melting_entry", stack.getHoverName(), percent)));
            } else if (melter.state[i] == 2) {
                tooltip.add(helper.smallItem(stack));
                tooltip.append(helper.text(Component.translatable(
                        "jade.hephaestus.melting_full_tank", stack.getHoverName())));
            } else if (melter.state[i] == 4) {
                tooltip.add(helper.smallItem(stack));
                tooltip.append(helper.text(Component.translatable(
                        "jade.hephaestus.melting_too_cold", stack.getHoverName())));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "melter_progress");
    }
}