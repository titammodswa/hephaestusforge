package com.titammods.block.multiblock;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SmelteryFluidHandler implements ResourceHandler<FluidResource> {
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
            fluids.addFirst(fluid);
        }
    }

    @Override
    public int size() {
        return fluids.size() + 1;
    }

    @Override
    public FluidResource getResource(int tank) {
        Objects.checkIndex(tank, size());
        FluidStack stack = fluids.get(tank);

        return stack.isEmpty() ? FluidResource.EMPTY : FluidResource.of(stack);
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return fluids.get(index).getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return capacity;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return resource != null && !resource.isEmpty();
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, @NonNull TransactionContext transaction) {
        Objects.checkIndex(index, size());

        if (resource == null || resource.isEmpty() || amount <= 0) {
            return 0;
        }

        FluidStack current = fluids.get(index);

        if (!current.isEmpty() && !FluidResource.of(current).equals(resource)) {
            return 0;
        }

        int space = Math.min(Integer.MAX_VALUE, capacity - current.getAmount());
        if (space <= 0) {
            return 0;
        }

        int inserted = Math.min(amount, space);

        if (current.isEmpty()) {
            fluids.set(index, new FluidStack(resource.getFluid(), inserted));
        } else {
            current.grow(inserted);
        }

        return inserted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, @NonNull TransactionContext transaction) {
        Objects.checkIndex(index, size());

        if (resource == null || resource.isEmpty() || amount <= 0) {
            return 0;
        }

        FluidStack current = fluids.get(index);
        if (current.isEmpty()) {
            return 0;
        }

        if (!FluidResource.of(current).equals(resource)) {
            return 0;
        }

        int extracted = Math.min(amount, current.getAmount());
        if (extracted <= 0) {
            return 0;
        }

        current.shrink(extracted);
        if (current.isEmpty()) {
            fluids.set(index, FluidStack.EMPTY);
        }

        return extracted;
    }

    // Old Overrides:
    public FluidStack getFluidInTank(int tank) {
        if (tank >= 0 && tank < fluids.size()) {
            return fluids.get(tank);
        }
        return FluidStack.EMPTY;
    }

    public int fill(FluidStack stack, boolean execute) {
        if (stack.isEmpty()) {
            return 0;
        }

        try (var tx = Transaction.open(null)) {
            int inserted = insert(
                    findCompatibleTank(stack),
                    FluidResource.of(stack),
                    stack.getAmount(),
                    tx
            );

            if (execute) {
                tx.commit();
            }

            return inserted;
        }
    }

//    public  FluidStack drain(int maxDrain, boolean execute) {
//        FluidStack fluid = fluids.getFirst().copy();
//        fluid.setAmount(maxDrain);
//        return drain(fluid, execute);
//    }
//
//    public FluidStack drain(FluidStack stack, boolean execute) {
//        if (stack.isEmpty()) {
//            return FluidStack.EMPTY;
//        }
//
//        try (var tx = Transaction.open(null)) {
//            int extracted = extract(
//                    findCompatibleTank(stack),
//                    FluidResource.of(stack),
//                    stack.getAmount(),
//                    tx
//            );
//
//            if (extracted <= 0) {
//                return FluidStack.EMPTY;
//            }
//
//            if (execute) {
//                tx.commit();
//            }
//
//            FluidStack result = stack.copy();
//            result.setAmount(extracted);
//
//            return result;
//        }
//    }

    private int findCompatibleTank(FluidStack stack) {
        for (int i = 0; i < fluids.size(); i++) {
            FluidStack current = fluids.get(i);

            if (current.isEmpty()
                    || FluidStack.isSameFluidSameComponents(current, stack)) {
                return i;
            }
        }

        return 0;
    }
    //

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
            ListTag list = tag.getList("Fluids").orElseThrow();
            for (int i = 0; i < list.size(); i++) {
                CompoundTag fluidTag = list.getCompound(i).orElseThrow();
                Fluid fluid = BuiltInRegistries.FLUID.get(Identifier.parse(fluidTag.getString("FluidName").orElseThrow())).orElseThrow().value();
                if (fluid != Fluids.EMPTY) {
                    fluids.add(new FluidStack(fluid, fluidTag.getInt("Amount").orElseThrow()));
                }
            }
        }
    }
}
