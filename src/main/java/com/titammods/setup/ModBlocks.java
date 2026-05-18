package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TitamMods.MODID);

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.registerBlockItem(name, toReturn);
        return toReturn;
    }
    // minerios
    public static final DeferredBlock<Block> COBALT_BLOCK = registerBlock("cobalt_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<Block> RAW_COBALT_BLOCK = registerBlock("raw_cobalt_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)));
    public static final DeferredBlock<Block> STEEL_BLOCK = registerBlock("steel_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    // Decorativos
    public static final DeferredBlock<Block> SEARED_STONE = register("seared_stone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> SEARED_COBBLE = register("seared_cobble", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)));
    public static final DeferredBlock<Block> SEARED_PAVER = register("seared_paver", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> SEARED_BRICKS = register("seared_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_CRACKED_BRICKS = register("seared_cracked_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CRACKED_STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_FANCY_BRICKS = register("seared_fancy_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_TRIANGLE_BRICKS = register("seared_triangle_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_CREEPER = register("seared_creeper", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_ROAD = register("seared_road", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_SMALL_BRICKS = register("seared_small_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_SQUARE_BRICKS = register("seared_square_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_TILE = register("seared_tile", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_LADDER = register("seared_ladder", () -> new com.titammods.block.SearedLadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_LAMP = register("seared_lamp", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).lightLevel(state -> 15)));
    // Máquinas / Controladores
    public static final DeferredBlock<Block> SMELTERY_CONTROLLER = register("smeltery_controller", () -> new com.titammods.block.SmelteryControllerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<Block> SEARED_MELTER = registerBlock("seared_melter", () -> new com.titammods.block.MelterBlock(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<Block> SEARED_DRAIN = register("seared_drain", () -> new SearedMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(3.0f).requiresCorrectToolForDrops().noOcclusion(), SearedDrainBlockEntity::new));
    public static final DeferredBlock<Block> SEARED_CHUTE = register("seared_chute", () -> new SearedMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(3.0f).requiresCorrectToolForDrops().noOcclusion(), SearedChuteBlockEntity::new));
    // VIDROS (GLASS)
    public static final DeferredBlock<Block> CLEAR_STAINED_GLASS = register("clear_stained_glass", () -> new net.minecraft.world.level.block.TransparentBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS).noOcclusion().isValidSpawn((state, getter, pos, type) -> false).isRedstoneConductor((state, getter, pos) -> false).isSuffocating((state, getter, pos) -> false).isViewBlocking((state, getter, pos) -> false)));
    public static final DeferredBlock<Block> CLEAR_TINTED_GLASS = register("clear_tinted_glass", () -> new net.minecraft.world.level.block.TransparentBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS).noOcclusion().isValidSpawn((state, getter, pos, type) -> false).isRedstoneConductor((state, getter, pos) -> false).isSuffocating((state, getter, pos) -> false).isViewBlocking((state, getter, pos) -> false)));
    public static final DeferredBlock<Block> SEARED_GLASS = register("seared_glass", () -> new net.minecraft.world.level.block.TransparentBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS).noOcclusion().isValidSpawn((state, getter, pos, type) -> false).isRedstoneConductor((state, getter, pos) -> false).isSuffocating((state, getter, pos) -> false).isViewBlocking((state, getter, pos) -> false)));
    public static final DeferredBlock<Block> CLEAR_GLASS = register("clear_glass", () -> new net.minecraft.world.level.block.TransparentBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS).noOcclusion().isValidSpawn((state, getter, pos, type) -> false).isRedstoneConductor((state, getter, pos) -> false).isSuffocating((state, getter, pos) -> false).isViewBlocking((state, getter, pos) -> false)));
    public static final DeferredBlock<Block> SEARED_TINTED_GLASS = register("seared_tinted_glass", () -> new net.minecraft.world.level.block.TransparentBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS).noOcclusion().isValidSpawn((state, getter, pos, type) -> false).isRedstoneConductor((state, getter, pos) -> false).isSuffocating((state, getter, pos) -> false).isViewBlocking((state, getter, pos) -> false)));
    // ESCADAS
    public static final DeferredBlock<Block> SEARED_STONE_STAIRS = registerStairs("seared_stone_stairs", SEARED_STONE);
    public static final DeferredBlock<Block> SEARED_COBBLE_STAIRS = registerStairs("seared_cobble_stairs", SEARED_COBBLE);
    public static final DeferredBlock<Block> SEARED_PAVER_STAIRS = registerStairs("seared_paver_stairs", SEARED_PAVER);
    public static final DeferredBlock<Block> SEARED_BRICKS_STAIRS = registerStairs("seared_bricks_stairs", SEARED_BRICKS);
    public static final DeferredBlock<Block> SEARED_CRACKED_BRICKS_STAIRS = registerStairs("seared_cracked_bricks_stairs", SEARED_CRACKED_BRICKS);
    public static final DeferredBlock<Block> SEARED_FANCY_BRICKS_STAIRS = registerStairs("seared_fancy_bricks_stairs", SEARED_FANCY_BRICKS);
    public static final DeferredBlock<Block> SEARED_TRIANGLE_BRICKS_STAIRS = registerStairs("seared_triangle_bricks_stairs", SEARED_TRIANGLE_BRICKS);
    public static final DeferredBlock<Block> SEARED_CREEPER_STAIRS = registerStairs("seared_creeper_stairs", SEARED_CREEPER);
    public static final DeferredBlock<Block> SEARED_ROAD_STAIRS = registerStairs("seared_road_stairs", SEARED_ROAD);
    public static final DeferredBlock<Block> SEARED_SMALL_BRICKS_STAIRS = registerStairs("seared_small_bricks_stairs", SEARED_SMALL_BRICKS);
    public static final DeferredBlock<Block> SEARED_SQUARE_BRICKS_STAIRS = registerStairs("seared_square_bricks_stairs", SEARED_SQUARE_BRICKS);
    public static final DeferredBlock<Block> SEARED_TILE_STAIRS = registerStairs("seared_tile_stairs", SEARED_TILE);
    // LAJES
    public static final DeferredBlock<Block> SEARED_STONE_SLAB = registerSlab("seared_stone_slab", SEARED_STONE);
    public static final DeferredBlock<Block> SEARED_COBBLE_SLAB = registerSlab("seared_cobble_slab", SEARED_COBBLE);
    public static final DeferredBlock<Block> SEARED_PAVER_SLAB = registerSlab("seared_paver_slab", SEARED_PAVER);
    public static final DeferredBlock<Block> SEARED_BRICKS_SLAB = registerSlab("seared_bricks_slab", SEARED_BRICKS);
    public static final DeferredBlock<Block> SEARED_CRACKED_BRICKS_SLAB = registerSlab("seared_cracked_bricks_slab", SEARED_CRACKED_BRICKS);
    public static final DeferredBlock<Block> SEARED_FANCY_BRICKS_SLAB = registerSlab("seared_fancy_bricks_slab", SEARED_FANCY_BRICKS);
    public static final DeferredBlock<Block> SEARED_TRIANGLE_BRICKS_SLAB = registerSlab("seared_triangle_bricks_slab", SEARED_TRIANGLE_BRICKS);
    public static final DeferredBlock<Block> SEARED_CREEPER_SLAB = registerSlab("seared_creeper_slab", SEARED_CREEPER);
    public static final DeferredBlock<Block> SEARED_ROAD_SLAB = registerSlab("seared_road_slab", SEARED_ROAD);
    public static final DeferredBlock<Block> SEARED_SMALL_BRICKS_SLAB = registerSlab("seared_small_bricks_slab", SEARED_SMALL_BRICKS);
    public static final DeferredBlock<Block> SEARED_SQUARE_BRICKS_SLAB = registerSlab("seared_square_bricks_slab", SEARED_SQUARE_BRICKS);
    public static final DeferredBlock<Block> SEARED_TILE_SLAB = registerSlab("seared_tile_slab", SEARED_TILE);
    // MUROS
    public static final DeferredBlock<Block> SEARED_STONE_WALL = registerWall("seared_stone_wall", SEARED_STONE);
    public static final DeferredBlock<Block> SEARED_COBBLE_WALL = registerWall("seared_cobble_wall", SEARED_COBBLE);
    public static final DeferredBlock<Block> SEARED_PAVER_WALL = registerWall("seared_paver_wall", SEARED_PAVER);
    public static final DeferredBlock<Block> SEARED_BRICKS_WALL = registerWall("seared_bricks_wall", SEARED_BRICKS);
    public static final DeferredBlock<Block> SEARED_CRACKED_BRICKS_WALL = registerWall("seared_cracked_bricks_wall", SEARED_CRACKED_BRICKS);
    public static final DeferredBlock<Block> SEARED_FANCY_BRICKS_WALL = registerWall("seared_fancy_bricks_wall", SEARED_FANCY_BRICKS);
    public static final DeferredBlock<Block> SEARED_TRIANGLE_BRICKS_WALL = registerWall("seared_triangle_bricks_wall", SEARED_TRIANGLE_BRICKS);
    public static final DeferredBlock<Block> SEARED_CREEPER_WALL = registerWall("seared_creeper_wall", SEARED_CREEPER);
    public static final DeferredBlock<Block> SEARED_ROAD_WALL = registerWall("seared_road_wall", SEARED_ROAD);
    public static final DeferredBlock<Block> SEARED_SMALL_BRICKS_WALL = registerWall("seared_small_bricks_wall", SEARED_SMALL_BRICKS);
    public static final DeferredBlock<Block> SEARED_SQUARE_BRICKS_WALL = registerWall("seared_square_bricks_wall", SEARED_SQUARE_BRICKS);
    public static final DeferredBlock<Block> SEARED_TILE_WALL = registerWall("seared_tile_wall", SEARED_TILE);
    // Fundição
    public static final DeferredBlock<Block> SEARED_TABLE = register("seared_table", () -> new SearedTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredBlock<Block> SEARED_BASIN = register("seared_basin", () -> new SearedBasinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredBlock<Block> SEARED_FAUCET = register("seared_faucet", () -> new SearedFaucetBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    // inativo (por enquanto)
    //public static final DeferredBlock<Block> SEARED_DUCT = register("seared_duct", () -> new SearedMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    //public static final DeferredBlock<Block> SEARED_HEATER = register("seared_heater", () -> new SearedMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    //public static final DeferredBlock<Block> SEARED_CHANNEL = register("seared_channel", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block) {
        DeferredBlock<T> registeredBlock = BLOCKS.register(name, block);
        ModItems.ITEMS.registerSimpleBlockItem(name, registeredBlock);
        return registeredBlock;
    }
    private static DeferredBlock<Block> registerWall(String name, DeferredBlock<Block> baseBlock) {
        return register(name, () -> new net.minecraft.world.level.block.WallBlock(BlockBehaviour.Properties.ofFullCopy(baseBlock.get())));
    }
    private static DeferredBlock<Block> registerSlab(String name, DeferredBlock<Block> baseBlock) {
        return register(name, () -> new net.minecraft.world.level.block.SlabBlock(BlockBehaviour.Properties.ofFullCopy(baseBlock.get())));
    }
    private static DeferredBlock<Block> registerStairs(String name, DeferredBlock<Block> baseBlock) {
        return register(name, () -> new net.minecraft.world.level.block.StairBlock(baseBlock.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(baseBlock.get())));
    }
}