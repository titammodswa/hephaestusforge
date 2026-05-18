package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModTanks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TitamMods.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var pickaxeTag = tag(BlockTags.MINEABLE_WITH_PICKAXE);

        var stoneToolTag = tag(BlockTags.NEEDS_STONE_TOOL);

        var wallsTag = tag(BlockTags.WALLS);
        var stairsTag = tag(BlockTags.STAIRS);
        var slabsTag = tag(BlockTags.SLABS);

        for (var blockHolder : ModBlocks.BLOCKS.getEntries()) {
            Block block = blockHolder.get();
            String name = blockHolder.getId().getPath();

            pickaxeTag.add(block);
            stoneToolTag.add(block);

            if (name.contains("wall")) {
                wallsTag.add(block);
            } else if (name.contains("stairs")) {
                stairsTag.add(block);
            } else if (name.contains("slab")) {
                slabsTag.add(block);
            }
        }

        var storageBlocks = net.minecraft.tags.BlockTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks"));
        var storageBlocksCobalt = net.minecraft.tags.BlockTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cobalt"));
        var storageBlocksRawCobalt = net.minecraft.tags.BlockTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_cobalt"));
        var storageBlocksSteel = net.minecraft.tags.BlockTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/steel"));

        tag(storageBlocks).add(ModBlocks.COBALT_BLOCK.get(), ModBlocks.RAW_COBALT_BLOCK.get(), ModBlocks.STEEL_BLOCK.get());
        tag(storageBlocksCobalt).add(ModBlocks.COBALT_BLOCK.get());
        tag(storageBlocksRawCobalt).add(ModBlocks.RAW_COBALT_BLOCK.get());
        tag(storageBlocksSteel).add(ModBlocks.STEEL_BLOCK.get());

        for (var blockHolder : ModTanks.BLOCKS.getEntries()) {
            Block block = blockHolder.get();

            pickaxeTag.add(block);
            stoneToolTag.add(block);
        }

        var smelteryFloor = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "smeltery_floor"));
        var smelteryWall = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "smeltery_wall"));
        var smelteryTanks = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "smeltery_tanks"));

        tag(smelteryFloor).add(
                ModBlocks.SEARED_STONE.get(), ModBlocks.SEARED_COBBLE.get(), ModBlocks.SEARED_PAVER.get(),
                ModBlocks.SEARED_BRICKS.get(), ModBlocks.SEARED_CRACKED_BRICKS.get(), ModBlocks.SEARED_FANCY_BRICKS.get(),
                ModBlocks.SEARED_TRIANGLE_BRICKS.get(), ModBlocks.SEARED_CREEPER.get(), ModBlocks.SEARED_ROAD.get(),
                ModBlocks.SEARED_SMALL_BRICKS.get(), ModBlocks.SEARED_SQUARE_BRICKS.get(), ModBlocks.SEARED_TILE.get()
        );

        tag(smelteryWall)
                .addTag(smelteryFloor)
                .add(ModBlocks.SEARED_DRAIN.get(), ModBlocks.SEARED_CHUTE.get());

        for (var tankHolder : com.titammods.setup.ModTanks.BLOCKS.getEntries()) {
            net.minecraft.world.level.block.Block block = tankHolder.get();
            tag(smelteryWall).add(block);
            tag(smelteryTanks).add(block);
        }
    }
}