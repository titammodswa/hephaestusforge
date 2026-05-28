package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.block.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TitamMods.MODID);

    public static final Supplier<BlockEntityType<MelterBlockEntity>> MELTER =
            BLOCK_ENTITIES.register("melter", () -> new BlockEntityType<>(
                    MelterBlockEntity::new,
                    ModBlocks.SEARED_MELTER.get()
            ));

    public static final Supplier<BlockEntityType<SmelteryTankBlockEntity>> SMELETRY_TANK =
            BLOCK_ENTITIES.register("smeltery_tank", () -> new BlockEntityType<>(
                    SmelteryTankBlockEntity::new,
                    ModTanks.SEARED_INGOT_TANK.get(),
                    ModTanks.SEARED_FUEL_TANK.get()
            ));

    public static final Supplier<BlockEntityType<FaucetBlockEntity>> FAUCET =
            BLOCK_ENTITIES.register("faucet", () -> new BlockEntityType<>(
                    FaucetBlockEntity::new,
                    ModBlocks.SEARED_FAUCET.get()
            ));

    public static final Supplier<BlockEntityType<BasinBlockEntity>> BASIN =
            BLOCK_ENTITIES.register("basin", () -> new BlockEntityType<>(
                    BasinBlockEntity::new,
                    ModBlocks.SEARED_BASIN.get()
            ));

    public static final Supplier<BlockEntityType<TableBlockEntity>> TABLE =
            BLOCK_ENTITIES.register("table", () -> new BlockEntityType<>(
                    TableBlockEntity::new,
                    ModBlocks.SEARED_TABLE.get()
            ));

    public static final Supplier<BlockEntityType<SmelteryControllerBlockEntity>> SMELTERY_CONTROLLER =
            BLOCK_ENTITIES.register("smeltery_controller", () -> new BlockEntityType<>(
                    SmelteryControllerBlockEntity::new,
                    ModBlocks.SMELTERY_CONTROLLER.get()
            ));

    public static final Supplier<BlockEntityType<SearedDrainBlockEntity>> SEARED_DRAIN =
            BLOCK_ENTITIES.register("seared_drain", () -> new BlockEntityType<>(
                    SearedDrainBlockEntity::new,
                    ModBlocks.SEARED_DRAIN.get()
            ));

    public static final Supplier<BlockEntityType<SearedChuteBlockEntity>> SEARED_CHUTE =
            BLOCK_ENTITIES.register("seared_chute", () -> new BlockEntityType<>(
                    SearedChuteBlockEntity::new,
                    ModBlocks.SEARED_CHUTE.get()
            ));
}