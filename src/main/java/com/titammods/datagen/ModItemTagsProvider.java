package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output,
                               CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, TitamMods.MODID);
    }

    @Override
    protected void addTags(net.minecraft.core.HolderLookup.Provider provider) {

        tag(iTag("c", "storage_blocks"))
                .add(ModBlocks.COBALT_BLOCK.get().asItem(),
                        ModBlocks.RAW_COBALT_BLOCK.get().asItem(),
                        ModBlocks.STEEL_BLOCK.get().asItem());
        tag(iTag("c", "storage_blocks/cobalt"))    .add(ModBlocks.COBALT_BLOCK.get().asItem());
        tag(iTag("c", "storage_blocks/raw_cobalt")).add(ModBlocks.RAW_COBALT_BLOCK.get().asItem());
        tag(iTag("c", "storage_blocks/steel"))     .add(ModBlocks.STEEL_BLOCK.get().asItem());
        tag(iTag("c", "nuggets"))             .add(ModItems.COBALT_NUGGET.get());
        tag(iTag("c", "nuggets/cobalt"))      .add(ModItems.COBALT_NUGGET.get());
        tag(iTag("c", "raw_materials"))       .add(ModItems.RAW_COBALT.get());
        tag(iTag("c", "raw_materials/cobalt")).add(ModItems.RAW_COBALT.get());
        tag(iTag("c", "dusts"))               .add(ModItems.COBALT_POWDER.get());
        tag(iTag("c", "dusts/cobalt"))        .add(ModItems.COBALT_POWDER.get());
        tag(iTag("c", "ingots"))              .add(ModItems.COBALT_INGOT.get());
        tag(iTag("c", "ingots/cobalt"))       .add(ModItems.COBALT_INGOT.get());
        tag(iTag("c", "nuggets"))            .add(ModItems.STEEL_NUGGET.get());
        tag(iTag("c", "nuggets/steel"))      .add(ModItems.STEEL_NUGGET.get());
        tag(iTag("c", "raw_materials"))      .add(ModItems.RAW_STEEL.get());
        tag(iTag("c", "raw_materials/steel")).add(ModItems.RAW_STEEL.get());
        tag(iTag("c", "dusts"))              .add(ModItems.STEEL_POWDER.get());
        tag(iTag("c", "dusts/steel"))        .add(ModItems.STEEL_POWDER.get());
        tag(iTag("c", "ingots"))             .add(ModItems.STEEL_INGOT.get());
        tag(iTag("c", "ingots/steel"))       .add(ModItems.STEEL_INGOT.get());

        String[] atoMetals = {
                "aluminum", "bronze", "constantan", "electrum", "enderium", "invar", "iridium",
                "lead", "lumium", "nickel", "osmium", "platinum", "signalum", "silver", "tin", "uranium"
        };
        String[] atoVanilla = {"iron", "gold", "copper", "diamond"};

        var clumps    = iTag("c", "clumps");
        var crystals  = iTag("c", "crystals");
        var dirtyDust = iTag("c", "dirty_dusts");
        var shards    = iTag("c", "shards");
        var hammers   = iTag("c", "ore_hammers");

        for (String name : concat(atoMetals, atoVanilla)) {
            optional(hammers, Identifier.fromNamespaceAndPath("alltheores", name + "_ore_hammer"));
        }
        for (String name : concat(atoMetals, atoVanilla)) {
            tag(iTag("c", "storage_blocks/" + name));
            tag(iTag("c", "ingots/" + name));
            tag(iTag("c", "nuggets/" + name));
            tag(iTag("c", "raw_materials/" + name));
            tag(iTag("c", "dusts/" + name));
            tag(iTag("c", "plates/" + name));
            tag(iTag("c", "gears/" + name));
            tag(iTag("c", "rods/" + name));

            Identifier clumpId   = Identifier.fromNamespaceAndPath("alltheores", name + "_clump");
            Identifier crystalId = Identifier.fromNamespaceAndPath("alltheores", name + "_crystal");
            Identifier dirtyId   = Identifier.fromNamespaceAndPath("alltheores", "dirty_" + name + "_dust");
            Identifier shardId   = Identifier.fromNamespaceAndPath("alltheores", name + "_shard");

            optional(clumps,    clumpId);   optional(iTag("c","clumps/"      + name), clumpId);
            optional(crystals,  crystalId); optional(iTag("c","crystals/"    + name), crystalId);
            optional(dirtyDust, dirtyId);   optional(iTag("c","dirty_dusts/" + name), dirtyId);
            optional(shards,    shardId);   optional(iTag("c","shards/"      + name), shardId);
        }
    }

    private void optional(net.minecraft.tags.TagKey<Item> tag, Identifier id) {
        this.getOrCreateRawBuilder(tag).addOptionalElement(id);
    }

    private net.minecraft.tags.TagKey<Item> iTag(String namespace, String path) {
        return ItemTags.create(Identifier.fromNamespaceAndPath(namespace, path));
    }

    private static String[] concat(String[] a, String[] b) {
        String[] r = new String[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }
}