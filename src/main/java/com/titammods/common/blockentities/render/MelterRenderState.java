package com.titammods.common.blockentities.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;

public class MelterRenderState extends BlockEntityRenderState {
    public FluidStack fluid    = FluidStack.EMPTY;
    public int        capacity = 2700;
    public FluidStack fuel     = FluidStack.EMPTY;
    public Direction  facing   = Direction.NORTH;

    public net.minecraft.world.item.ItemStack item0 = net.minecraft.world.item.ItemStack.EMPTY;
    public net.minecraft.world.item.ItemStack item1 = net.minecraft.world.item.ItemStack.EMPTY;
    public net.minecraft.world.item.ItemStack item2 = net.minecraft.world.item.ItemStack.EMPTY;
}
