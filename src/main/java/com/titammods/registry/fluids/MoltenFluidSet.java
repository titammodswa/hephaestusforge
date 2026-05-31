package com.titammods.registry.fluids;

import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModFluids;
import com.titammods.setup.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class MoltenFluidSet {

    public DeferredHolder<FluidType, MoltenFluidType>         fluidType;
    public DeferredHolder<Fluid, MoltenFluidBase.Source>      source;
    public DeferredHolder<Fluid, MoltenFluidBase.Flowing>     flowing;
    public DeferredBlock<LiquidBlock>                         block;
    public DeferredItem<BucketItem>                           bucket;

    public MoltenFluidSet(String materialName, int color, int temperature) {

        this.fluidType = ModFluids.FLUID_TYPES.register("molten_" + materialName, () ->
                new MoltenFluidType(
                        FluidType.Properties.create()
                                .temperature(temperature)
                                .density(2000)
                                .viscosity(10000)
                                .lightLevel(10)
                                .sound(SoundActions.BUCKET_FILL,  SoundEvents.BUCKET_FILL_LAVA)
                                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA),
                        color
                )
        );

        final MoltenFluidSet self = this;

        this.source  = ModFluids.FLUIDS.register("molten_" + materialName,
                () -> new MoltenFluidBase.Source(self));
        this.flowing = ModFluids.FLUIDS.register("flowing_molten_" + materialName,
                () -> new MoltenFluidBase.Flowing(self));

        this.block = ModBlocks.BLOCKS.register("molten_" + materialName + "_block", id ->
                new LiquidBlock(
                        this.source.get(),
                        BlockBehaviour.Properties.of()
                                .setId(ResourceKey.create(Registries.BLOCK, id))
                                .noCollision()
                                .replaceable()
                                .randomTicks()
                                .strength(100.0F)
                                .lightLevel(state -> 10)
                                .pushReaction(PushReaction.DESTROY)
                                .noLootTable()
                                .liquid()
                                .sound(SoundType.EMPTY)
                )
        );

        this.bucket = ModItems.ITEMS.register("molten_" + materialName + "_bucket", id ->
                new BucketItem(
                        this.source.get(),
                        new Item.Properties()
                                .setId(ResourceKey.create(Registries.ITEM, id))
                                .craftRemainder(Items.BUCKET)
                                .stacksTo(1)
                )
        );
    }
}
