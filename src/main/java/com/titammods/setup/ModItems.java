package com.titammods.setup;

import com.titammods.TitamMods;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TitamMods.MODID);

    // MATERIAIS
    public static final DeferredItem<Item> RAW_COBALT    = registerItem("raw_cobalt");
    public static final DeferredItem<Item> COBALT_INGOT  = registerItem("cobalt_ingot");
    public static final DeferredItem<Item> COBALT_NUGGET = registerItem("cobalt_nugget");
    public static final DeferredItem<Item> COBALT_POWDER = registerItem("cobalt_powder");
    public static final DeferredItem<Item> RAW_STEEL     = registerItem("raw_steel");
    public static final DeferredItem<Item> STEEL_INGOT   = registerItem("steel_ingot");
    public static final DeferredItem<Item> STEEL_NUGGET  = registerItem("steel_nugget");
    public static final DeferredItem<Item> STEEL_POWDER  = registerItem("steel_powder");

    // TIJOLOS
    public static final DeferredItem<Item> FORGE_BRICK  = registerItem("forge_brick");

    // MOLDES (CASTS)
    public static final DeferredItem<Item> BLANK_CAST  = registerItem("blank_cast");
    public static final DeferredItem<Item> COIN_CAST   = registerItem("coin_cast");
    public static final DeferredItem<Item> GEAR_CAST   = registerItem("gear_cast");
    public static final DeferredItem<Item> GEM_CAST    = registerItem("gem_cast");
    public static final DeferredItem<Item> INGOT_CAST  = registerItem("ingot_cast");
    public static final DeferredItem<Item> NUGGET_CAST = registerItem("nugget_cast");
    public static final DeferredItem<Item> PLATE_CAST  = registerItem("plate_cast");
    public static final DeferredItem<Item> ROD_CAST    = registerItem("rod_cast");

    private static DeferredItem<Item> registerItem(String name) {
        return ITEMS.register(name, k -> new Item(
                new Item.Properties().setId(ResourceKey.create(Registries.ITEM, k))));
    }
}