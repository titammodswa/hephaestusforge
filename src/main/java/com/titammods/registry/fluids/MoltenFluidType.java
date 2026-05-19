package com.titammods.registry.fluids;

import net.neoforged.neoforge.fluids.FluidType;

public class MoltenFluidType extends FluidType {
    public final int tintColor;

    public MoltenFluidType(Properties properties, int tintColor) {
        super(properties);
        this.tintColor = tintColor;
    }
}