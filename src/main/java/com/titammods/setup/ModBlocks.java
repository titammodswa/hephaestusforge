package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.common.blocks.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.titammods.common.blocks.SmelteryControllerBlock;
import com.titammods.common.blocks.SearedChuteBlock;
import com.titammods.common.blocks.SearedDrainBlock;

import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TitamMods.MODID);

    public static final DeferredBlock<Block> COBALT_BLOCK = registerBlock("cobalt_block",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(ResourceKey.create(Registries.BLOCK, k))));
    public static final DeferredBlock<Block> RAW_COBALT_BLOCK = registerBlock("raw_cobalt_block",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK).setId(ResourceKey.create(Registries.BLOCK, k))));
    public static final DeferredBlock<Block> STEEL_BLOCK = registerBlock("steel_block",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<Block> SEARED_BRICKS          = searedBlock("seared_bricks");
    public static final DeferredBlock<Block> SEARED_STONE           = searedBlock("seared_stone");
    public static final DeferredBlock<Block> SEARED_COBBLE          = searedBlock("seared_cobble");
    public static final DeferredBlock<Block> SEARED_PAVER           = searedBlock("seared_paver");
    public static final DeferredBlock<Block> SEARED_ROAD            = searedBlock("seared_road");
    public static final DeferredBlock<Block> SEARED_TILE            = searedBlock("seared_tile");
    public static final DeferredBlock<Block> SEARED_SMALL_BRICKS    = searedBlock("seared_small_bricks");
    public static final DeferredBlock<Block> SEARED_SQUARE_BRICKS   = searedBlock("seared_square_bricks");
    public static final DeferredBlock<Block> SEARED_TRIANGLE_BRICKS = searedBlock("seared_triangle_bricks");
    public static final DeferredBlock<Block> SEARED_FANCY_BRICKS    = searedBlock("seared_fancy_bricks");
    public static final DeferredBlock<Block> SEARED_CRACKED_BRICKS  = searedBlock("seared_cracked_bricks");
    public static final DeferredBlock<Block> SEARED_CREEPER         = searedBlock("seared_creeper");

    public static final DeferredBlock<Block> CLEAR_GLASS = registerBlock("clear_glass",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .noOcclusion()
                    .isValidSpawn((state, getter, pos, type) -> false)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .isSuffocating((state, getter, pos) -> false)
                    .isViewBlocking((state, getter, pos) -> false)
                    .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<Block> CLEAR_STAINED_GLASS = registerBlock("clear_stained_glass",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .noOcclusion()
                    .isValidSpawn((state, getter, pos, type) -> false)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .isSuffocating((state, getter, pos) -> false)
                    .isViewBlocking((state, getter, pos) -> false)
                    .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<Block> CLEAR_TINTED_GLASS = registerBlock("clear_tinted_glass",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.TINTED_GLASS)
                    .noOcclusion()
                    .isValidSpawn((state, getter, pos, type) -> false)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .isSuffocating((state, getter, pos) -> false)
                    .isViewBlocking((state, getter, pos) -> false)
                    .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<Block> SEARED_GLASS = registerBlock("seared_glass",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .noOcclusion()
                    .isValidSpawn((state, getter, pos, type) -> false)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .isSuffocating((state, getter, pos) -> false)
                    .isViewBlocking((state, getter, pos) -> false)
                    .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<Block> SEARED_TINTED_GLASS = registerBlock("seared_tinted_glass",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.TINTED_GLASS)
                    .noOcclusion()
                    .isValidSpawn((state, getter, pos, type) -> false)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .isSuffocating((state, getter, pos) -> false)
                    .isViewBlocking((state, getter, pos) -> false)
                    .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<SearedTankBlock> SEARED_INGOT_TANK = registerBlock("seared_ingot_tank",
            k -> new SearedTankBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                            .strength(3.0f, 15.0f)
                            .lightLevel(state -> state.getValue(SearedTankBlock.EMITS_LIGHT) ? 15 : 0)
                            .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<SearedTankBlock> SEARED_FUEL_TANK = registerBlock("seared_fuel_tank",
            k -> new SearedTankBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                            .strength(3.0f, 15.0f)
                            .lightLevel(state -> state.getValue(SearedTankBlock.EMITS_LIGHT) ? 15 : 0)
                            .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<MelterBlock> SEARED_MELTER = registerBlock("seared_melter",
            k -> new MelterBlock(
                    BlockBehaviour.Properties.of()
                            .strength(3.0f, 15.0f)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                            .lightLevel(state -> state.getValue(MelterBlock.LIGHT))
                            .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<SearedFaucetBlock> SEARED_FAUCET = registerBlock("seared_faucet",
            k -> new SearedFaucetBlock(
                    BlockBehaviour.Properties.of()
                            .strength(3.0f, 15.0f)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                            .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<SearedTableBlock> SEARED_TABLE = registerBlock("seared_table",
            k -> new SearedTableBlock(
                    BlockBehaviour.Properties.of()
                            .strength(3.0f, 15.0f)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                            .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<SearedBasinBlock> SEARED_BASIN = registerBlock("seared_basin",
            k -> new SearedBasinBlock(
                    BlockBehaviour.Properties.of()
                            .strength(3.0f, 15.0f)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                            .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<SmelteryControllerBlock> SMELTERY_CONTROLLER =
            registerBlock("smeltery_controller", k -> new SmelteryControllerBlock(
                    BlockBehaviour.Properties.of().strength(3f, 15f).requiresCorrectToolForDrops()
                            .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<SearedChuteBlock> SEARED_CHUTE =
            registerBlock("seared_chute", k -> new SearedChuteBlock(
                    BlockBehaviour.Properties.of().strength(3f, 15f).requiresCorrectToolForDrops()
                            .noOcclusion().setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<SearedDrainBlock> SEARED_DRAIN =
            registerBlock("seared_drain", k -> new SearedDrainBlock(
                    BlockBehaviour.Properties.of().strength(3f, 15f).requiresCorrectToolForDrops()
                            .noOcclusion().setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<Block> SEARED_STONE_STAIRS = searedStairs("seared_stone_stairs", SEARED_STONE);
    public static final DeferredBlock<Block> SEARED_COBBLE_STAIRS = searedStairs("seared_cobble_stairs", SEARED_COBBLE);
    public static final DeferredBlock<Block> SEARED_PAVER_STAIRS = searedStairs("seared_paver_stairs", SEARED_PAVER);
    public static final DeferredBlock<Block> SEARED_BRICKS_STAIRS = searedStairs("seared_bricks_stairs", SEARED_BRICKS);
    public static final DeferredBlock<Block> SEARED_CRACKED_BRICKS_STAIRS = searedStairs("seared_cracked_bricks_stairs", SEARED_CRACKED_BRICKS);
    public static final DeferredBlock<Block> SEARED_FANCY_BRICKS_STAIRS = searedStairs("seared_fancy_bricks_stairs", SEARED_FANCY_BRICKS);
    public static final DeferredBlock<Block> SEARED_TRIANGLE_BRICKS_STAIRS = searedStairs("seared_triangle_bricks_stairs", SEARED_TRIANGLE_BRICKS);
    public static final DeferredBlock<Block> SEARED_CREEPER_STAIRS = searedStairs("seared_creeper_stairs", SEARED_CREEPER);
    public static final DeferredBlock<Block> SEARED_ROAD_STAIRS = searedStairs("seared_road_stairs", SEARED_ROAD);
    public static final DeferredBlock<Block> SEARED_SMALL_BRICKS_STAIRS = searedStairs("seared_small_bricks_stairs", SEARED_SMALL_BRICKS);
    public static final DeferredBlock<Block> SEARED_SQUARE_BRICKS_STAIRS = searedStairs("seared_square_bricks_stairs", SEARED_SQUARE_BRICKS);
    public static final DeferredBlock<Block> SEARED_TILE_STAIRS = searedStairs("seared_tile_stairs", SEARED_TILE);

    public static final DeferredBlock<Block> SEARED_STONE_SLAB   = searedSlab("seared_stone_slab",   SEARED_STONE);
    public static final DeferredBlock<Block> SEARED_COBBLE_SLAB   = searedSlab("seared_cobble_slab",   SEARED_COBBLE);
    public static final DeferredBlock<Block> SEARED_PAVER_SLAB   = searedSlab("seared_paver_slab",   SEARED_PAVER);
    public static final DeferredBlock<Block> SEARED_BRICKS_SLAB   = searedSlab("seared_bricks_slab",   SEARED_BRICKS);
    public static final DeferredBlock<Block> SEARED_CRACKED_BRICKS_SLAB   = searedSlab("seared_cracked_bricks_slab",   SEARED_CRACKED_BRICKS);
    public static final DeferredBlock<Block> SEARED_FANCY_BRICKS_SLAB   = searedSlab("seared_fancy_bricks_slab",   SEARED_FANCY_BRICKS);
    public static final DeferredBlock<Block> SEARED_TRIANGLE_BRICKS_SLAB   = searedSlab("seared_triangle_bricks_slab",   SEARED_TRIANGLE_BRICKS);
    public static final DeferredBlock<Block> SEARED_CREEPER_SLAB   = searedSlab("seared_creeper_slab",   SEARED_CREEPER);
    public static final DeferredBlock<Block> SEARED_ROAD_SLAB   = searedSlab("seared_road_slab",   SEARED_ROAD);
    public static final DeferredBlock<Block> SEARED_SMALL_BRICKS_SLAB   = searedSlab("seared_small_bricks_slab",   SEARED_SMALL_BRICKS);
    public static final DeferredBlock<Block> SEARED_SQUARE_BRICKS_SLAB   = searedSlab("seared_square_bricks_slab",   SEARED_SQUARE_BRICKS);
    public static final DeferredBlock<Block> SEARED_TILE_SLAB   = searedSlab("seared_tile_slab",   SEARED_TILE);

    public static final DeferredBlock<Block> SEARED_STONE_WALL   = searedWall("seared_stone_wall",   SEARED_STONE);
    public static final DeferredBlock<Block> SEARED_COBBLE_WALL   = searedWall("seared_cobble_wall",   SEARED_COBBLE);
    public static final DeferredBlock<Block> SEARED_PAVER_WALL   = searedWall("seared_paver_wall",   SEARED_PAVER);
    public static final DeferredBlock<Block> SEARED_BRICKS_WALL   = searedWall("seared_bricks_wall",   SEARED_BRICKS);
    public static final DeferredBlock<Block> SEARED_CRACKED_BRICKS_WALL   = searedWall("seared_cracked_bricks_wall",   SEARED_CRACKED_BRICKS);
    public static final DeferredBlock<Block> SEARED_FANCY_BRICKS_WALL   = searedWall("seared_fancy_bricks_wall",   SEARED_FANCY_BRICKS);
    public static final DeferredBlock<Block> SEARED_TRIANGLE_BRICKS_WALL   = searedWall("seared_triangle_bricks_wall",   SEARED_TRIANGLE_BRICKS);
    public static final DeferredBlock<Block> SEARED_CREEPER_WALL   = searedWall("seared_creeper_wall",   SEARED_CREEPER);
    public static final DeferredBlock<Block> SEARED_ROAD_WALL   = searedWall("seared_road_wall",   SEARED_ROAD);
    public static final DeferredBlock<Block> SEARED_SMALL_BRICKS_WALL   = searedWall("seared_small_bricks_wall",   SEARED_SMALL_BRICKS);
    public static final DeferredBlock<Block> SEARED_SQUARE_BRICKS_WALL   = searedWall("seared_square_bricks_wall",   SEARED_SQUARE_BRICKS);
    public static final DeferredBlock<Block> SEARED_TILE_WALL   = searedWall("seared_tile_wall",   SEARED_TILE);

    public static final DeferredBlock<Block> SEARED_LAMP = registerBlock("seared_lamp",
            k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                    .strength(3.0f, 15.0f)
                    .lightLevel(state -> 15)
                    .setId(ResourceKey.create(Registries.BLOCK, k))));

    public static final DeferredBlock<Block> SEARED_LADDER = registerBlock("seared_ladder",
            k -> new com.titammods.common.blocks.SearedLadderBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                            .strength(3.0f, 15.0f)
                            .noOcclusion()
                            .setId(ResourceKey.create(Registries.BLOCK, k))));


    private static DeferredBlock<Block> searedStairs(String name, DeferredBlock<Block> base) {
        return registerBlock(name,
                k -> new net.minecraft.world.level.block.StairBlock(
                        base.get().defaultBlockState(),
                        BlockBehaviour.Properties.ofFullCopy(base.get()).setId(ResourceKey.create(Registries.BLOCK, k))));
    }

    private static DeferredBlock<Block> searedSlab(String name, DeferredBlock<Block> base) {
        return registerBlock(name,
                k -> new net.minecraft.world.level.block.SlabBlock(
                        BlockBehaviour.Properties.ofFullCopy(base.get()).setId(ResourceKey.create(Registries.BLOCK, k))));
    }

    private static DeferredBlock<Block> searedWall(String name, DeferredBlock<Block> base) {
        return registerBlock(name,
                k -> new net.minecraft.world.level.block.WallBlock(
                        BlockBehaviour.Properties.ofFullCopy(base.get()).setId(ResourceKey.create(Registries.BLOCK, k))));
    }

    private static DeferredBlock<Block> searedBlock(String name) {
        return registerBlock(name,
                k -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)
                        .strength(3.0f, 15.0f)
                        .setId(ResourceKey.create(Registries.BLOCK, k))));
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name,
                                                                    Function<Identifier, T> factory) {
        DeferredBlock<T> block = BLOCKS.register(name, factory);
        ModItems.ITEMS.register(name,
                k -> new BlockItem(block.get(),
                        new Item.Properties().setId(ResourceKey.create(Registries.ITEM, k))));
        return block;
    }
}