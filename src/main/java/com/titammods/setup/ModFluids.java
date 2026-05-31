package com.titammods.setup;

import com.titammods.TitamMods;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TitamMods.MODID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.FLUID, TitamMods.MODID);

    public static class MoltenFluid {

        public final DeferredHolder<FluidType, FluidType> type;
        public final DeferredHolder<Fluid, MoltenSource>  source;
        public final DeferredHolder<Fluid, MoltenFlowing> flowing;
        public final DeferredBlock<LiquidBlock>            block;
        public final DeferredItem<BucketItem>              bucket;

        public MoltenFluid(String name) {
            final MoltenFluid self = this;

            type = FLUID_TYPES.register(name, () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid." + TitamMods.MODID + "." + name)
                            .fallDistanceModifier(0F)
                            .canExtinguish(false)
                            .canConvertToSource(false)
                            .supportsBoating(false)
                            .sound(SoundActions.BUCKET_FILL,  SoundEvents.BUCKET_FILL_LAVA)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                            .temperature(1300)
                            .density(3000)
                            .viscosity(6000)
                            .lightLevel(15)
            ));

            source  = FLUIDS.register(name,             () -> new MoltenSource(self));
            flowing = FLUIDS.register(name + "_flowing", () -> new MoltenFlowing(self));

            block = ModBlocks.BLOCKS.register(name + "_block", id ->
                    new LiquidBlock(
                            (FlowingFluid) source.get(),
                            BlockBehaviour.Properties.of()
                                    .setId(ResourceKey.create(Registries.BLOCK, id))
                                    .noCollision()
                                    .replaceable()
                                    .randomTicks()
                                    .strength(100.0F)
                                    .lightLevel(state -> 15)
                                    .pushReaction(PushReaction.DESTROY)
                                    .noLootTable()
                                    .liquid()
                                    .sound(SoundType.EMPTY)
                    )
            );

            bucket = ModItems.ITEMS.register(name + "_bucket", id ->
                    new BucketItem(
                            source.get(),
                            new Item.Properties()
                                    .setId(ResourceKey.create(Registries.ITEM, id))
                                    .craftRemainder(Items.BUCKET)
                                    .stacksTo(1)
                    )
            );
        }

        private abstract class MoltenBase extends LavaFluid {
            @Override public FluidType getFluidType() { return type.get(); }
            @Override public Fluid getFlowing()       { return flowing.get(); }
            @Override public Fluid getSource()        { return source.get(); }
            @Override public boolean isSame(Fluid f) {
                return f == source.get() || f == flowing.get();
            }
            @Override public net.minecraft.world.level.block.state.BlockState createLegacyBlock(FluidState state) {
                return block.get().defaultBlockState()
                        .setValue(LiquidBlock.LEVEL, FlowingFluid.getLegacyLevel(state));
            }
        }

        private class MoltenSource extends MoltenBase {
            MoltenSource(MoltenFluid owner) {}
            @Override public boolean isSource(FluidState s) { return true; }
            @Override public int getAmount(FluidState s)    { return 8; }
        }

        private class MoltenFlowing extends MoltenBase {
            MoltenFlowing(MoltenFluid owner) {}
            @Override public boolean isSource(FluidState s) { return false; }
            @Override public int getAmount(FluidState s)    { return s.getValue(FlowingFluid.LEVEL); }
            @Override protected void createFluidStateDefinition(
                    StateDefinition.Builder<Fluid, FluidState> b) {
                super.createFluidStateDefinition(b);
                b.add(FlowingFluid.LEVEL);
            }
        }
    }

    public static final MoltenFluid MOLTEN_COBALT   = new MoltenFluid("molten_cobalt");
    public static final MoltenFluid MOLTEN_QUARTZ   = new MoltenFluid("molten_quartz");
    public static final MoltenFluid MOLTEN_DIAMOND  = new MoltenFluid("molten_diamond");
    public static final MoltenFluid MOLTEN_EMERALD  = new MoltenFluid("molten_emerald");
    public static final MoltenFluid MOLTEN_AMETHYST = new MoltenFluid("molten_amethyst");
    public static final MoltenFluid MOLTEN_BLAZE    = new MoltenFluid("molten_blaze");
}