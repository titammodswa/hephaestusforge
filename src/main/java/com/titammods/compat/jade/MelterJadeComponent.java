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

public class MelterJadeComponent implements IBlockComponentProvider {

    public static final MelterJadeComponent INSTANCE = new MelterJadeComponent();
    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "melter_progress");

    @Override
    public Identifier getUid() { return UID; }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data == null) return;
        int n = data.getIntOr("heph_n", 0);
        if (n <= 0) return;

        for (int k = 0; k < n; k++) {
            String id = data.getStringOr("heph_i" + k, "");
            if (id.isEmpty()) continue;
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
            if (item == null || item == Items.AIR) continue;
            int pct = data.getIntOr("heph_p" + k, 0);
            tooltip.add(Component.translatable("jade.hephaestus.melting_entry",
                    new ItemStack(item).getHoverName(), pct));
        }
    }
}