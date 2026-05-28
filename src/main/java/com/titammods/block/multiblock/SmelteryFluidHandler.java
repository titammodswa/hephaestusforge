package com.titammods.block.multiblock;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class SmelteryFluidHandler implements IFluidHandler {
    private final List<FluidStack> fluids = new ArrayList<>();
    private int capacity = 0;

    public void setCapacity(int newCapacity) {
        this.capacity = newCapacity;
    }

    public int getCapacity() { return capacity; }
    public List<FluidStack> getFluids() { return fluids; }

    public int getTotalFluid() {
        return fluids.stream().mapToInt(FluidStack::getAmount).sum();
    }

    public void moveFluidToBottom(int index) {
        if (index > 0 && index < fluids.size()) {
            FluidStack fluid = fluids.remove(index);
            fluids.add(0, fluid);
        }
    }

    @Override
    public int getTanks() {
        return fluids.size() + 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank >= 0 && tank < fluids.size()) {
            return fluids.get(tank);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        int remainingSpace = capacity - getTotalFluid();
        if (remainingSpace <= 0) return 0;

        int fillAmount = Math.min(resource.getAmount(), remainingSpace);

        if (action.execute()) {
            for (FluidStack fluid : fluids) {
                if (FluidStack.isSameFluidSameComponents(fluid, resource)) {
                    fluid.grow(fillAmount);
                    return fillAmount;
                }
            }
            FluidStack newFluid = resource.copy();
            newFluid.setAmount(fillAmount);
            fluids.add(newFluid);
        }
        return fillAmount;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;

        for (int i = 0; i < fluids.size(); i++) {
            FluidStack fluid = fluids.get(i);
            if (FluidStack.isSameFluidSameComponents(fluid, resource)) {
                int drainAmount = Math.min(resource.getAmount(), fluid.getAmount());
                FluidStack drained = fluid.copy();
                drained.setAmount(drainAmount);

                if (action.execute()) {
                    fluid.shrink(drainAmount);
                    if (fluid.isEmpty()) fluids.remove(i);
                }
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (fluids.isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;

        FluidStack fluid = fluids.get(0);
        int drainAmount = Math.min(maxDrain, fluid.getAmount());
        FluidStack drained = fluid.copy();
        drained.setAmount(drainAmount);

        if (action.execute()) {
            fluid.shrink(drainAmount);
            if (fluid.isEmpty()) fluids.remove(0);
        }
        return drained;
    }

    public CompoundTag writeToNBT(CompoundTag tag) {
        ListTag list = new ListTag();
        for (FluidStack fluid : fluids) {
            if (!fluid.isEmpty()) {
                CompoundTag fluidTag = new CompoundTag();
                fluidTag.putString("FluidName", BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString());
                fluidTag.putInt("Amount", fluid.getAmount());
                list.add(fluidTag);
            }
        }
        tag.put("Fluids", list);
        return tag;
    }

    public void readFromNBT(CompoundTag tag) {
        fluids.clear();
        if (tag.contains("Fluids")) {
            ListTag list = tag.getList("Fluids", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag fluidTag = list.getCompound(i);
                Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidTag.getString("FluidName")));
                if (fluid != Fluids.EMPTY) {
                    fluids.add(new FluidStack(fluid, fluidTag.getInt("Amount")));
                }
            }
        }
    }
}