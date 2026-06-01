package com.titammods.common.blockentities.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.neoforged.neoforge.fluids.FluidStack;

public class BasinRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState outputRS = new ItemStackRenderState();
    public FluidStack fluid    = FluidStack.EMPTY;
    public int tankCapacity    = 900;
    public boolean isAnimating = false;
}