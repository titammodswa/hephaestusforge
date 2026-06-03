package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.common.blockentities.FaucetBlockEntity;
import com.titammods.common.blockentities.MelterBlockEntity;
import com.titammods.common.blockentities.SearedTankBlockEntity;
import com.titammods.common.blockentities.TableBlockEntity;
import com.titammods.common.blockentities.BasinBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.titammods.common.blockentities.SmelteryControllerBlockEntity;
import com.titammods.common.blockentities.SearedChuteBlockEntity;
import com.titammods.common.blockentities.SearedDrainBlockEntity;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TitamMods.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SearedTankBlockEntity>> SEARED_TANK =
            BLOCK_ENTITY_TYPES.register("seared_tank", () ->
                    new BlockEntityType<>(
                            SearedTankBlockEntity::new,
                            ModBlocks.SEARED_INGOT_TANK.get(),
                            ModBlocks.SEARED_FUEL_TANK.get()
                    )
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MelterBlockEntity>> MELTER =
            BLOCK_ENTITY_TYPES.register("melter", () ->
                    new BlockEntityType<>(
                            MelterBlockEntity::new,
                            ModBlocks.SEARED_MELTER.get()
                    )
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FaucetBlockEntity>> FAUCET =
            BLOCK_ENTITY_TYPES.register("faucet", () ->
                    new BlockEntityType<>(
                            FaucetBlockEntity::new,
                            ModBlocks.SEARED_FAUCET.get()
                    )
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TableBlockEntity>> TABLE =
            BLOCK_ENTITY_TYPES.register("table", () ->
                    new BlockEntityType<>(
                            TableBlockEntity::new,
                            ModBlocks.SEARED_TABLE.get()
                    )
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasinBlockEntity>> BASIN =
            BLOCK_ENTITY_TYPES.register("basin", () ->
                    new BlockEntityType<>(BasinBlockEntity::new, ModBlocks.SEARED_BASIN.get())
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmelteryControllerBlockEntity>> SMELTERY_CONTROLLER =
            BLOCK_ENTITY_TYPES.register("smeltery_controller", () ->
                    new BlockEntityType<>(SmelteryControllerBlockEntity::new, ModBlocks.SMELTERY_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SearedChuteBlockEntity>> SEARED_CHUTE =
            BLOCK_ENTITY_TYPES.register("seared_chute", () ->
                    new BlockEntityType<>(SearedChuteBlockEntity::new, ModBlocks.SEARED_CHUTE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SearedDrainBlockEntity>> SEARED_DRAIN =
            BLOCK_ENTITY_TYPES.register("seared_drain", () ->
                    new BlockEntityType<>(SearedDrainBlockEntity::new, ModBlocks.SEARED_DRAIN.get()));
}