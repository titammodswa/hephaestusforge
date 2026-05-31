package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output,
                                CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, TitamMods.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        var pickaxe   = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        var stoneTool = tag(BlockTags.NEEDS_STONE_TOOL);
        var walls     = tag(BlockTags.WALLS);
        var stairs    = tag(BlockTags.STAIRS);
        var slabs     = tag(BlockTags.SLABS);

        for (var holder : ModBlocks.BLOCKS.getEntries()) {
            Block block = holder.get();
            String name = holder.getId().getPath();

            pickaxe.add(block);
            stoneTool.add(block);

            if (name.endsWith("_wall"))        walls.add(block);
            else if (name.endsWith("_stairs")) stairs.add(block);
            else if (name.endsWith("_slab"))   slabs.add(block);
        }

        tag(bTag("c", "storage_blocks"))
                .add(ModBlocks.COBALT_BLOCK.get(),
                     ModBlocks.RAW_COBALT_BLOCK.get(),
                     ModBlocks.STEEL_BLOCK.get());

        tag(bTag("c", "storage_blocks/cobalt"))    .add(ModBlocks.COBALT_BLOCK.get());
        tag(bTag("c", "storage_blocks/raw_cobalt")).add(ModBlocks.RAW_COBALT_BLOCK.get());
        tag(bTag("c", "storage_blocks/steel"))     .add(ModBlocks.STEEL_BLOCK.get());

        var smelteryFloor = bTag("c", "smeltery_floor");
        var smelteryWall  = bTag("c", "smeltery_wall");

        tag(smelteryFloor).add(
                ModBlocks.SEARED_STONE.get(),
                ModBlocks.SEARED_COBBLE.get(),
                ModBlocks.SEARED_PAVER.get(),
                ModBlocks.SEARED_BRICKS.get(),
                ModBlocks.SEARED_CRACKED_BRICKS.get(),
                ModBlocks.SEARED_FANCY_BRICKS.get(),
                ModBlocks.SEARED_TRIANGLE_BRICKS.get(),
                ModBlocks.SEARED_CREEPER.get(),
                ModBlocks.SEARED_ROAD.get(),
                ModBlocks.SEARED_SMALL_BRICKS.get(),
                ModBlocks.SEARED_SQUARE_BRICKS.get(),
                ModBlocks.SEARED_TILE.get()
        );

        tag(smelteryWall).addTag(smelteryFloor);
    }

    private net.minecraft.tags.TagKey<Block> bTag(String namespace, String path) {
        return BlockTags.create(Identifier.fromNamespaceAndPath(namespace, path));
    }
}
