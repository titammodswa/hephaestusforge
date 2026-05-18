package com.titammods.datagen;

import com.titammods.setup.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock; // Importante para o filtro
import net.minecraft.world.level.block.SlabBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        for (DeferredHolder<Block, ? extends Block> blockHolder : ModBlocks.BLOCKS.getEntries()) {
            Block block = blockHolder.get();

            if (block instanceof LiquidBlock) {
                continue;
            }

            if (block instanceof SlabBlock) {
                this.add(block, block1 -> this.createSlabItemTable(block1));
            } else {
                this.dropSelf(block);
            }
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(holder -> (Block) holder.get())
                .filter(block -> !(block instanceof LiquidBlock))
                .toList();
    }
}