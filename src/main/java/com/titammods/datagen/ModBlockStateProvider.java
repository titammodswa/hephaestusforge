package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.setup.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile; // <-- A importação que faltava!
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TitamMods.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        ModelFile ladderModel = models().withExistingParent("seared_ladder", modLoc("block/template/ladder"))
                .texture("top", modLoc("block/smeltery/seared/fancy_bricks"))
                .texture("side", modLoc("block/smeltery/seared/ladder"))
                .texture("bottom", modLoc("block/smeltery/seared/ladder"));

        ModelFile ladderBaseModel = models().withExistingParent("seared_ladder_base", modLoc("block/template/ladder_base"))
                .texture("top", modLoc("block/smeltery/seared/fancy_bricks"))
                .texture("side", modLoc("block/smeltery/seared/ladder"))
                .texture("bottom", modLoc("block/smeltery/seared/ladder"));

        simpleBlock(ModBlocks.COBALT_BLOCK.get(), models().cubeAll("cobalt_block", modLoc("block/ores/cobalt_block")));
        simpleBlock(ModBlocks.RAW_COBALT_BLOCK.get(), models().cubeAll("raw_cobalt_block", modLoc("block/ores/raw_cobalt_block")));
        simpleBlock(ModBlocks.STEEL_BLOCK.get(), models().cubeAll("steel_block", modLoc("block/ores/steel_block")));

        getVariantBuilder(ModBlocks.SEARED_LADDER.get()).forAllStates(state -> {
            net.minecraft.core.Direction dir = state.getValue(com.titammods.block.SearedLadderBlock.FACING);
            boolean isBottom = state.getValue(com.titammods.block.SearedLadderBlock.BOTTOM);
            int yRot = (int) dir.toYRot();
            return net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder()
                    .modelFile(isBottom ? ladderBaseModel : ladderModel)
                    .rotationY(yRot)
                    .build();
        });

        registerSearedBlock(ModBlocks.SEARED_STONE.get(), "stone");
        registerSearedBlock(ModBlocks.SEARED_COBBLE.get(), "cobble");
        registerSearedBlock(ModBlocks.SEARED_PAVER.get(), "paver");
        registerSearedBlock(ModBlocks.SEARED_BRICKS.get(), "bricks");
        registerSearedBlock(ModBlocks.SEARED_CRACKED_BRICKS.get(), "cracked_bricks");
        registerSearedBlock(ModBlocks.SEARED_FANCY_BRICKS.get(), "fancy_bricks");
        registerSearedBlock(ModBlocks.SEARED_TRIANGLE_BRICKS.get(), "triangle_bricks");
        registerSearedBlock(ModBlocks.SEARED_CREEPER.get(), "creeper");
        registerSearedBlock(ModBlocks.SEARED_LAMP.get(), "lamp");
        registerSearedBlock(ModBlocks.SEARED_ROAD.get(), "road");
        registerSearedBlock(ModBlocks.SEARED_SMALL_BRICKS.get(), "small_bricks");
        registerSearedBlock(ModBlocks.SEARED_SQUARE_BRICKS.get(), "square_bricks");
        registerSearedBlock(ModBlocks.SEARED_TILE.get(), "tile");

        registerSearedStairs((StairBlock) ModBlocks.SEARED_STONE_STAIRS.get(), "stone");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_COBBLE_STAIRS.get(), "cobble");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_PAVER_STAIRS.get(), "paver");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_BRICKS_STAIRS.get(), "bricks");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_CRACKED_BRICKS_STAIRS.get(), "cracked_bricks");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_FANCY_BRICKS_STAIRS.get(), "fancy_bricks");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_TRIANGLE_BRICKS_STAIRS.get(), "triangle_bricks");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_CREEPER_STAIRS.get(), "creeper");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_ROAD_STAIRS.get(), "road");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_SMALL_BRICKS_STAIRS.get(), "small_bricks");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_SQUARE_BRICKS_STAIRS.get(), "square_bricks");
        registerSearedStairs((StairBlock) ModBlocks.SEARED_TILE_STAIRS.get(), "tile");

        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_STONE_SLAB.get(), "stone");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_COBBLE_SLAB.get(), "cobble");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_PAVER_SLAB.get(), "paver");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_BRICKS_SLAB.get(), "bricks");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_CRACKED_BRICKS_SLAB.get(), "cracked_bricks");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_FANCY_BRICKS_SLAB.get(), "fancy_bricks");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_TRIANGLE_BRICKS_SLAB.get(), "triangle_bricks");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_CREEPER_SLAB.get(), "creeper");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_ROAD_SLAB.get(), "road");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_SMALL_BRICKS_SLAB.get(), "small_bricks");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_SQUARE_BRICKS_SLAB.get(), "square_bricks");
        registerSearedSlab((net.minecraft.world.level.block.SlabBlock) ModBlocks.SEARED_TILE_SLAB.get(), "tile");

        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_STONE_WALL.get(), "stone");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_COBBLE_WALL.get(), "cobble");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_PAVER_WALL.get(), "paver");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_BRICKS_WALL.get(), "bricks");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_CRACKED_BRICKS_WALL.get(), "cracked_bricks");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_FANCY_BRICKS_WALL.get(), "fancy_bricks");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_TRIANGLE_BRICKS_WALL.get(), "triangle_bricks");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_CREEPER_WALL.get(), "creeper");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_ROAD_WALL.get(), "road");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_SMALL_BRICKS_WALL.get(), "small_bricks");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_SQUARE_BRICKS_WALL.get(), "square_bricks");
        registerSearedWall((net.minecraft.world.level.block.WallBlock) ModBlocks.SEARED_TILE_WALL.get(), "tile");

    }

    private void registerSearedBlock(Block block, String textureName) {
        simpleBlock(block, models().cubeAll("seared_" + textureName, modLoc("block/smeltery/seared/" + textureName)));
    }

    private void registerSearedStairs(StairBlock block, String textureName) {
        stairsBlock(block, modLoc("block/smeltery/seared/" + textureName));
    }

    private void registerSearedSlab(net.minecraft.world.level.block.SlabBlock block, String textureName) {
        slabBlock(block, modLoc("block/seared_" + textureName), modLoc("block/smeltery/seared/" + textureName));
    }

    private void registerSearedWall(net.minecraft.world.level.block.WallBlock block, String textureName) {
        wallBlock(block, modLoc("block/smeltery/seared/" + textureName));
    }
}