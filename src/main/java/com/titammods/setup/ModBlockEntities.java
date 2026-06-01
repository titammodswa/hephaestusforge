package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.common.blockentities.FaucetBlockEntity;
import com.titammods.common.blockentities.MelterBlockEntity;
import com.titammods.common.blockentities.SearedTankBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
}