package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.block.SmelteryTankBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModTanks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TitamMods.MODID);

    private static <T extends Block> DeferredBlock<T> registerTankBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.registerTankItem(name, toReturn);
        return toReturn;
    }

    public static final DeferredBlock<SmelteryTankBlock> SEARED_INGOT_TANK = registerTankBlock("seared_ingot_tank",
            () -> new SmelteryTankBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.0f)
                    .noOcclusion()));

    public static final DeferredBlock<SmelteryTankBlock> SEARED_FUEL_TANK = registerTankBlock("seared_fuel_tank",
            () -> new SmelteryTankBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.0f)
                    .noOcclusion()));

//    public static final DeferredBlock<SmelteryTankBlock> SEARED_CASTING_TANK = registerTankBlock("seared_casting_tank",
//            () -> new SmelteryTankBlock(BlockBehaviour.Properties.of()
//                    .mapColor(MapColor.COLOR_BLACK)
//                    .strength(3.0f)
//                    .noOcclusion()));
}