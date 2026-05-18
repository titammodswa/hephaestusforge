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
            BLOCK_ENTITIES.register("melter", () -> BlockEntityType.Builder.of(
                    MelterBlockEntity::new,
                    ModBlocks.SEARED_MELTER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<SmelteryTankBlockEntity>> SMELETRY_TANK =
            BLOCK_ENTITIES.register("smeltery_tank", () -> BlockEntityType.Builder.of(
                    SmelteryTankBlockEntity::new,
                    ModTanks.SEARED_INGOT_TANK.get(),
                    ModTanks.SEARED_FUEL_TANK.get()
            ).build(null));

    public static final Supplier<BlockEntityType<FaucetBlockEntity>> FAUCET =
            BLOCK_ENTITIES.register("faucet", () -> BlockEntityType.Builder.of(
                    FaucetBlockEntity::new,
                    ModBlocks.SEARED_FAUCET.get()
            ).build(null));

    public static final Supplier<BlockEntityType<BasinBlockEntity>> BASIN =
            BLOCK_ENTITIES.register("basin", () -> BlockEntityType.Builder.of(
                    BasinBlockEntity::new,
                    ModBlocks.SEARED_BASIN.get()
            ).build(null));

    public static final Supplier<BlockEntityType<TableBlockEntity>> TABLE =
            BLOCK_ENTITIES.register("table", () -> BlockEntityType.Builder.of(
                    TableBlockEntity::new,
                    ModBlocks.SEARED_TABLE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<SmelteryControllerBlockEntity>> SMELTERY_CONTROLLER =
            BLOCK_ENTITIES.register("smeltery_controller", () -> BlockEntityType.Builder.of(
                    SmelteryControllerBlockEntity::new,
                    ModBlocks.SMELTERY_CONTROLLER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<SearedDrainBlockEntity>> SEARED_DRAIN =
            BLOCK_ENTITIES.register("seared_drain", () -> BlockEntityType.Builder.of(
                    SearedDrainBlockEntity::new,
                    ModBlocks.SEARED_DRAIN.get()
            ).build(null));

    public static final Supplier<BlockEntityType<SearedChuteBlockEntity>> SEARED_CHUTE =
            BLOCK_ENTITIES.register("seared_chute", () -> BlockEntityType.Builder.of(
                    SearedChuteBlockEntity::new,
                    ModBlocks.SEARED_CHUTE.get()
            ).build(null));
}