package com.titammods.setup;

import com.titammods.TitamMods;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TitamMods.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, TitamMods.MODID);

    public static class MoltenFluid {
        public final Supplier<FluidType> type;
        public final Supplier<FlowingFluid> source;
        public final Supplier<FlowingFluid> flowing;
        public final DeferredBlock<LiquidBlock> block;
        public final Supplier<BucketItem> bucket;

        public MoltenFluid(String name) {
            type = FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid." + TitamMods.MODID + "." + name)
                    .fallDistanceModifier(0F)
                    .canExtinguish(false)
                    .canConvertToSource(false)
                    .supportsBoating(false)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .temperature(1300)
                    .density(3000)
                    .viscosity(6000)
                    .lightLevel(15)));

            source = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(getProperties()));
            flowing = FLUIDS.register(name + "_flowing", () -> new BaseFlowingFluid.Flowing(getProperties()));

            block = ModBlocks.BLOCKS.register(name + "_block", () -> new LiquidBlock((FlowingFluid) source.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).lightLevel((state) -> 15).noLootTable().liquid()));

            bucket = ModItems.ITEMS.register(name + "_bucket", () -> new BucketItem(source.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        }

        private BaseFlowingFluid.Properties getProperties() {
            return new BaseFlowingFluid.Properties(type, source, flowing).block(block).bucket(bucket);
        }
    }

    public static final MoltenFluid MOLTEN_COBALT = new MoltenFluid("molten_cobalt");
    public static final MoltenFluid MOLTEN_QUARTZ = new MoltenFluid("molten_quartz");
    public static final MoltenFluid MOLTEN_DIAMOND = new MoltenFluid("molten_diamond");
    public static final MoltenFluid MOLTEN_EMERALD = new MoltenFluid("molten_emerald");
    public static final MoltenFluid MOLTEN_AMETHYST = new MoltenFluid("molten_amethyst");

    public static final MoltenFluid MOLTEN_BLAZE = new MoltenFluid("molten_blaze");
}