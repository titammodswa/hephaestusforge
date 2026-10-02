package com.titammods.common.blockentities.multiblock;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("removal")
public class SmelteryFluidHandler extends SnapshotJournal<List<FluidStack>> implements IFluidHandler, ResourceHandler<FluidResource> {

    private final List<FluidStack> fluids = new ArrayList<>();
    private int capacity = 0;
    private final Runnable onChanged;

    public SmelteryFluidHandler() { this(() -> {}); }

    public SmelteryFluidHandler(Runnable onChanged) { this.onChanged = onChanged; }

    @Override protected List<FluidStack> createSnapshot() {
        return fluids.stream().map(FluidStack::copy).toList();
    }

    @Override protected void revertToSnapshot(List<FluidStack> snapshot) {
        fluids.clear();
        fluids.addAll(snapshot);
    }

    @Override protected void onRootCommit(List<FluidStack> original) { onChanged.run(); }

    @Override public int size() { return fluids.size(); }
    @Override public FluidResource getResource(int index) { return FluidResource.of(getFluidInTank(index)); }
    @Override public long getAmountAsLong(int index) { return getFluidInTank(index).getAmount(); }
    @Override public long getCapacityAsLong(int index, FluidResource resource) { return capacity; }
    @Override public boolean isValid(int index, FluidResource resource) { return true; }

    // Smeltery drains expose extraction only; internal processing uses the legacy methods below.
    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        FluidStack stored = getFluidInTank(index);
        if (!resource.matches(stored) || amount == 0) return 0;
        int extracted = Math.min(amount, stored.getAmount());
        updateSnapshots(transaction);
        stored.shrink(extracted);
        if (stored.isEmpty()) fluids.remove(index);
        return extracted;
    }

    @Override
    public int extract(FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        for (int index = 0; index < fluids.size(); index++) {
            if (resource.matches(fluids.get(index))) return extract(index, resource, amount, transaction);
        }
        return 0;
    }

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
        output.store("fluids", FluidStack.CODEC.listOf(), fluids);
    }

    public void load(ValueInput input) {
        fluids.clear();
        var stored = input.read("fluids", FluidStack.CODEC.listOf());
        if (stored.isPresent()) {
            fluids.addAll(stored.get());
            return;
        }
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
