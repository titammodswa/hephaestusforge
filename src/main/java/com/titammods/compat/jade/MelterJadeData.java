package com.titammods.compat.jade;

import com.titammods.TitamMods;
import com.titammods.common.blockentities.MelterBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class MelterJadeData implements IServerDataProvider<BlockAccessor> {

    public static final MelterJadeData INSTANCE = new MelterJadeData();
    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "melter_progress");

    @Override
    public Identifier getUid() { return UID; }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof MelterBlockEntity melter)) return;

        int n = 0;
        for (int i = 0; i < 3; i++) {
            ItemStack stack = melter.inventory.getStackInSlot(i);
            int max = melter.maxProgress[i];
            boolean active = !stack.isEmpty() && max > 0
                    && (melter.state[i] == 1 || melter.progress[i] > 0);
            if (!active) continue;

            int pct = Math.max(0, Math.min(100, melter.progress[i] * 100 / max));
            Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            data.putString("heph_i" + n, id.toString());
            data.putInt("heph_p" + n, pct);
            n++;
        }
        if (n > 0) data.putInt("heph_n", n);
    }
}