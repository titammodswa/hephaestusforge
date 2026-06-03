package com.titammods.common.blockentities.multiblock;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("removal")
public class SmelteryFluidHandler implements IFluidHandler {

    private final List<FluidStack> fluids = new ArrayList<>();
    private int capacity = 0;

    public void setCapacity(int newCapacity) { this.capacity = newCapacity; }
    public int getCapacity()                 { return capacity; }
    public List<FluidStack> getFluids()      { return fluids; }

    public int getTotalFluid() {
        return fluids.stream().mapToInt(FluidStack::getAmount).sum();
    }

    public void moveFluidToBottom(int index) {
        if (index > 0 && index < fluids.size()) {
            FluidStack fluid = fluids.remove(index);
            fluids.add(0, fluid);
        }
    }

    @SuppressWarnings("removal")
    @Override public int getTanks()                        { return fluids.size() + 1; }
    @SuppressWarnings("removal")
    @Override public int getTankCapacity(int tank)         { return capacity; }
    @SuppressWarnings("removal")
    @Override public boolean isFluidValid(int tank, FluidStack stack) { return true; }
    @SuppressWarnings("removal")
    @Override
    public FluidStack getFluidInTank(int tank) {
        return (tank >= 0 && tank < fluids.size()) ? fluids.get(tank) : FluidStack.EMPTY;
    }
    @SuppressWarnings("removal")
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        int space = capacity - getTotalFluid();
        if (space <= 0) return 0;
        int amount = Math.min(resource.getAmount(), space);
        if (action.execute()) {
            for (FluidStack f : fluids) {
                if (FluidStack.isSameFluidSameComponents(f, resource)) {
                    f.grow(amount);
                    return amount;
                }
            }
            FluidStack copy = resource.copy();
            copy.setAmount(amount);
            fluids.add(copy);
        }
        return amount;
    }
    @SuppressWarnings("removal")
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        for (int i = 0; i < fluids.size(); i++) {
            FluidStack f = fluids.get(i);
            if (FluidStack.isSameFluidSameComponents(f, resource)) {
                int drain = Math.min(resource.getAmount(), f.getAmount());
                FluidStack drained = f.copy();
                drained.setAmount(drain);
                if (action.execute()) {
                    f.shrink(drain);
                    if (f.isEmpty()) fluids.remove(i);
                }
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }
    @SuppressWarnings("removal")
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (fluids.isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;
        FluidStack f = fluids.get(0);
        int drain = Math.min(maxDrain, f.getAmount());
        FluidStack drained = f.copy();
        drained.setAmount(drain);
        if (action.execute()) {
            f.shrink(drain);
            if (f.isEmpty()) fluids.remove(0);
        }
        return drained;
    }

    public void save(ValueOutput output) {
        output.putInt("fluid_count", fluids.size());
        for (int i = 0; i < fluids.size(); i++) {
            FluidStack f = fluids.get(i);
            output.putString("fluid_id_"    + i, BuiltInRegistries.FLUID.getKey(f.getFluid()).toString());
            output.putInt(   "fluid_amount_" + i, f.getAmount());
        }
    }

    public void load(ValueInput input) {
        fluids.clear();
        int count = input.getIntOr("fluid_count", 0);
        for (int i = 0; i < count; i++) {
            String idStr = input.getStringOr("fluid_id_" + i, "");
            int amount   = input.getIntOr("fluid_amount_" + i, 0);
            if (!idStr.isEmpty() && amount > 0) {
                Fluid fluid = BuiltInRegistries.FLUID.getValue(Identifier.parse(idStr));
                if (fluid != null && !fluid.isSame(Fluids.EMPTY)) {
                    fluids.add(new FluidStack(fluid, amount));
                }
            }
        }
    }
}