package com.titammods.common.blockentities.multiblock;

import net.neoforged.neoforge.fluids.FluidStack;

public interface IDisplayFluidListener {
    void notifyDisplayFluidUpdated(FluidStack fluid);
    FluidStack getDisplayFluid();
}