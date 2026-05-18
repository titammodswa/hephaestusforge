package com.titammods.setup;

import com.titammods.TitamMods;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TitamMods.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TITAMMODS_TAB = TABS.register("titammods_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.hephaestus"))
            .icon(() -> new ItemStack(ModBlocks.SEARED_MELTER.get()))
            .displayItems((parameters, output) -> {
                ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            })
            .build());
}