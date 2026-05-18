package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.item.SmelteryTankItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TitamMods.MODID);

    public static final DeferredItem<Item> BLANK_CAST = ITEMS.register("blank_cast", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INGOT_CAST = ITEMS.register("ingot_cast", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NUGGET_CAST = ITEMS.register("nugget_cast", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GEM_CAST = ITEMS.register("gem_cast", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COIN_CAST = ITEMS.register("coin_cast", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GEAR_CAST = ITEMS.register("gear_cast", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PLATE_CAST = ITEMS.register("plate_cast", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ROD_CAST = ITEMS.register("rod_cast", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FORGE_BRICK = ITEMS.register("forge_brick", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_COBALT = ITEMS.register("raw_cobalt", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COBALT_INGOT = ITEMS.register("cobalt_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COBALT_POWDER = ITEMS.register("cobalt_powder", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COBALT_NUGGET = ITEMS.register("cobalt_nugget", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_NUGGET = ITEMS.register("steel_nugget", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_STEEL = ITEMS.register("raw_steel", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_POWDER = ITEMS.register("steel_powder", () -> new Item(new Item.Properties()));

    public static <T extends Block> void registerTankItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new SmelteryTankItem(block.get(), new Item.Properties()));
    }

    public static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}