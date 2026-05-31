package com.titammods.common.blockentities.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.neoforged.neoforge.fluids.FluidStack;
import com.titammods.common.blockentities.SearedTankBlockEntity;

public class SearedTankRenderState extends BlockEntityRenderState {
    public FluidStack fluid    = FluidStack.EMPTY;
    public int        capacity = SearedTankBlockEntity.CAPACITY;
}
