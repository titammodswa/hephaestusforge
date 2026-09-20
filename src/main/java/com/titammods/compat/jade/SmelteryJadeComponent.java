package com.titammods.compat.jade;

import com.titammods.TitamMods;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class SmelteryJadeComponent implements IBlockComponentProvider {

    public static final SmelteryJadeComponent INSTANCE = new SmelteryJadeComponent();
    private static final int MAX_LINES = 12;
    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery_progress");

    @Override
    public Identifier getUid() { return UID; }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data == null) return;
        int n = data.getIntOr("heph_n", 0);
        if (n <= 0) return;

        int shown = 0;
        for (int k = 0; k < n; k++) {
            if (shown >= MAX_LINES) {
                tooltip.add(Component.translatable("jade.hephaestus.melting_more", n - shown));
                break;
            }
            String id = data.getStringOr("heph_i" + k, "");
            if (id.isEmpty()) continue;
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
            if (item == null || item == Items.AIR) continue;
            int pct = data.getIntOr("heph_p" + k, 0);
            tooltip.add(Component.translatable("jade.hephaestus.melting_entry",
                    new ItemStack(item).getHoverName(), pct));
            shown++;
        }
    }
}