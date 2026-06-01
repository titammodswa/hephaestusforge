package com.titammods.common.blockentities.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.neoforged.neoforge.fluids.FluidStack;

public class TableRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState moldRS   = new ItemStackRenderState();
    public final ItemStackRenderState outputRS  = new ItemStackRenderState();
    public final ItemStackRenderState resultRS  = new ItemStackRenderState();
    public FluidStack fluid       = FluidStack.EMPTY;
    public int tankCapacity       = 10000;
    public boolean hasOutput      = false;
    public boolean hasMold        = false;
}