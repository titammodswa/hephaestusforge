package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.common.blocks.MelterBlock;
import com.titammods.common.blocks.SearedTankBlock;
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

    public static final DeferredBlock<Block> SMELTERY_CONTROLLER = registerBlock("smeltery_controller",
            k -> new net.minecraft.world.level.block.Block(
                    BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS)
                            .setId(ResourceKey.create(Registries.BLOCK, k))));

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
