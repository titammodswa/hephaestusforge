package com.titammods.compat.jade;

import com.titammods.TitamMods;
import com.titammods.common.blockentities.SmelteryControllerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class SmelteryJadeData implements IServerDataProvider<BlockAccessor> {

    public static final SmelteryJadeData INSTANCE = new SmelteryJadeData();
    static final int MAX_DATA = 32;
    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery_progress");

    @Override
    public Identifier getUid() { return UID; }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof SmelteryControllerBlockEntity smeltery)) return;

        int n = 0;
        int slots = smeltery.itemHandler.getSlots();
        for (int i = 0; i < slots && n < MAX_DATA; i++) {
            if (i >= smeltery.meltingTime.length) break;
            ItemStack stack = smeltery.itemHandler.getStackInSlot(i);
            int time = smeltery.meltingTime[i];
            if (stack.isEmpty() || time <= 0 || smeltery.meltingState[i] != 0) continue;

            int pct = Math.max(0, Math.min(100, smeltery.meltingProgress[i] * 100 / time));
            Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            data.putString("heph_i" + n, id.toString());
            data.putInt("heph_p" + n, pct);
            n++;
        }
        if (n > 0) data.putInt("heph_n", n);
    }
}