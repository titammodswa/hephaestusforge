package com.titammods.common.blockentities.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState; // NOTA: subpacote .state.
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;

public class FaucetRenderState extends BlockEntityRenderState {
    public FluidStack fluid    = FluidStack.EMPTY;
    public boolean    isPouring = false;
    public Direction  facing   = Direction.NORTH;
}