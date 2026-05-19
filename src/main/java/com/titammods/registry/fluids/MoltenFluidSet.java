package com.titammods.registry.fluids;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.titammods.setup.ModFluids;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModItems;

public class MoltenFluidSet {
    public DeferredHolder<FluidType, MoltenFluidType> fluidType;
    public DeferredHolder<Fluid, BaseFlowingFluid.Source> source;
    public DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing;
    public DeferredBlock<LiquidBlock> block;
    public DeferredItem<BucketItem> bucket;

    public MoltenFluidSet(String materialName, int color, int temperature) {

        this.fluidType = ModFluids.FLUID_TYPES.register("molten_" + materialName, () ->
                new MoltenFluidType(FluidType.Properties.create()
                        .temperature(temperature)
                        .density(2000)
                        .viscosity(10000)
                        .lightLevel(10), color)
        );

        BaseFlowingFluid.Properties fluidProps = new BaseFlowingFluid.Properties(
                this.fluidType,
                () -> this.source.get(),
                () -> this.flowing.get()
        ).bucket(() -> this.bucket.get()).block(() -> this.block.get());

        this.source = ModFluids.FLUIDS.register("molten_" + materialName, () -> new BaseFlowingFluid.Source(fluidProps));
        this.flowing = ModFluids.FLUIDS.register("flowing_molten_" + materialName, () -> new BaseFlowingFluid.Flowing(fluidProps));

        this.block = ModBlocks.BLOCKS.register("molten_" + materialName + "_block", () ->
                new LiquidBlock(this.source.get(), BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable())
        );

        this.bucket = ModItems.ITEMS.register("molten_" + materialName + "_bucket", () ->
                new BucketItem(this.source.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))
        );
    }
}