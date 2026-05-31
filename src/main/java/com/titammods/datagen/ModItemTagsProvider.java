package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.Optional;
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
            atoItem("alltheores", name + "_ore_hammer").ifPresent(h -> tag(hammers).add(h));
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

            atoItem("alltheores", name + "_clump")          .ifPresent(i -> { tag(clumps)   .add(i); tag(iTag("c","clumps/"     + name)).add(i); });
            atoItem("alltheores", name + "_crystal")        .ifPresent(i -> { tag(crystals) .add(i); tag(iTag("c","crystals/"   + name)).add(i); });
            atoItem("alltheores", "dirty_" + name + "_dust").ifPresent(i -> { tag(dirtyDust).add(i); tag(iTag("c","dirty_dusts/"+ name)).add(i); });
            atoItem("alltheores", name + "_shard")          .ifPresent(i -> { tag(shards)   .add(i); tag(iTag("c","shards/"     + name)).add(i); });
        }
    }

    private net.minecraft.tags.TagKey<Item> iTag(String namespace, String path) {
        return ItemTags.create(Identifier.fromNamespaceAndPath(namespace, path));
    }

    private Optional<Item> atoItem(String modid, String path) {
        return BuiltInRegistries.ITEM
                .get(Identifier.fromNamespaceAndPath(modid, path))
                .map(Holder::value);
    }

    private static String[] concat(String[] a, String[] b) {
        String[] r = new String[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }
}
