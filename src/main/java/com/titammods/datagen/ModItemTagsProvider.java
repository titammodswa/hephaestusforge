package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.setup.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), TitamMods.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.copy(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks")), ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks")));
        this.copy(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cobalt")), ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cobalt")));
        this.copy(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_cobalt")), ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_cobalt")));
        this.copy(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/steel")), ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/steel")));

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets"))).add(ModItems.COBALT_NUGGET.get());
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets/cobalt"))).add(ModItems.COBALT_NUGGET.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials"))).add(ModItems.RAW_COBALT.get());
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/cobalt"))).add(ModItems.RAW_COBALT.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts"))).add(ModItems.COBALT_POWDER.get());
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts/cobalt"))).add(ModItems.COBALT_POWDER.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots"))).add(ModItems.COBALT_INGOT.get());
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/cobalt"))).add(ModItems.COBALT_INGOT.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets"))).add(ModItems.STEEL_NUGGET.get());
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets/steel"))).add(ModItems.STEEL_NUGGET.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials"))).add(ModItems.RAW_STEEL.get());
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/steel"))).add(ModItems.RAW_STEEL.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts"))).add(ModItems.STEEL_POWDER.get());
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts/steel"))).add(ModItems.STEEL_POWDER.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots"))).add(ModItems.STEEL_INGOT.get());
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/steel"))).add(ModItems.STEEL_INGOT.get());

        String[] atoMetals = {
                "aluminum", "bronze", "constantan", "electrum", "enderium", "invar", "iridium",
                "lead", "lumium", "nickel", "osmium", "platinum", "signalum", "silver", "tin", "uranium"
        };
        String[] atoVanillaCompat = {"iron", "gold", "copper", "diamond"};

        var clumpsTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "clumps"));
        var crystalsTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "crystals"));
        var dirtyDustsTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dirty_dusts"));
        var shardsTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "shards"));
        var oreHammersTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ore_hammers"));

        for (String name : atoMetals) {
            var hammer = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", name + "_ore_hammer"));
            if (hammer != Items.AIR) tag(oreHammersTag).add(hammer);
        }
        for (String name : atoVanillaCompat) {
            var hammer = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", name + "_ore_hammer"));
            if (hammer != Items.AIR) tag(oreHammersTag).add(hammer);
        }

        for (String name : atoMetals) {
            var clump = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", name + "_clump"));
            if (clump != Items.AIR) {
                tag(clumpsTag).add(clump);
                tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "clumps/" + name))).add(clump);
            }
            var crystal = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", name + "_crystal"));
            if (crystal != Items.AIR) {
                tag(crystalsTag).add(crystal);
                tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "crystals/" + name))).add(crystal);
            }
            var dirtyDust = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", "dirty_" + name + "_dust"));
            if (dirtyDust != Items.AIR) {
                tag(dirtyDustsTag).add(dirtyDust);
                tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dirty_dusts/" + name))).add(dirtyDust);
            }
            var shard = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", name + "_shard"));
            if (shard != Items.AIR) {
                tag(shardsTag).add(shard);
                tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "shards/" + name))).add(shard);
            }
        }

        for (String name : atoVanillaCompat) {
            var clump = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", name + "_clump"));
            if (clump != Items.AIR) {
                tag(clumpsTag).add(clump);
                tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "clumps/" + name))).add(clump);
            }
            var crystal = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", name + "_crystal"));
            if (crystal != Items.AIR) {
                tag(crystalsTag).add(crystal);
                tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "crystals/" + name))).add(crystal);
            }
            var dirtyDust = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", "dirty_" + name + "_dust"));
            if (dirtyDust != Items.AIR) {
                tag(dirtyDustsTag).add(dirtyDust);
                tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dirty_dusts/" + name))).add(dirtyDust);
            }
            var shard = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alltheores", name + "_shard"));
            if (shard != Items.AIR) {
                tag(shardsTag).add(shard);
                tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "shards/" + name))).add(shard);
            }
        }
    }
}