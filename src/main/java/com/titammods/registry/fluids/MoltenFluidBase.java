package com.titammods.registry.fluids;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import net.neoforged.neoforge.fluids.FluidType;

public abstract class MoltenFluidBase extends LavaFluid {

    public abstract MoltenFluidSet set();

    @Override
    public FluidType getFluidType() {
        return set().fluidType.get();
    }

    @Override
    public Fluid getFlowing() {
        return set().flowing.get();
    }

    @Override
    public Fluid getSource() {
        return set().source.get();
    }

    @Override
    public BucketItem getBucket() {
        return (BucketItem) set().bucket.get();
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return set().block.get().defaultBlockState()
                .setValue(LiquidBlock.LEVEL, FlowingFluid.getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid other) {
        return other == set().source.get() || other == set().flowing.get();
    }

    public static final class Source extends MoltenFluidBase {
        private final MoltenFluidSet owner;
        public Source(MoltenFluidSet set) { this.owner = set; }

        @Override public MoltenFluidSet set() { return owner; }
        @Override public boolean isSource(FluidState state) { return true; }
        @Override public int getAmount(FluidState state)    { return 8; }
    }

    public static final class Flowing extends MoltenFluidBase {
        private final MoltenFluidSet owner;
        public Flowing(MoltenFluidSet set) { this.owner = set; }

        @Override public MoltenFluidSet set() { return owner; }
        @Override public boolean isSource(FluidState state) { return false; }
        @Override public int getAmount(FluidState state)    { return state.getValue(FlowingFluid.LEVEL); }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(FlowingFluid.LEVEL);
        }
    }
}
