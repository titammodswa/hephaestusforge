package com.titammods.common.blockentities;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;

/** Exposes existing machine tanks to transactional automation. */
@SuppressWarnings("removal")
public class TransactionalFluidTank extends FluidTank implements ResourceHandler<FluidResource> {
    private final boolean allowInsert;
    private final boolean allowExtract;
    private final SnapshotJournal<FluidStack> journal = new SnapshotJournal<>() {
        @Override protected FluidStack createSnapshot() { return getFluid().copy(); }
        @Override protected void revertToSnapshot(FluidStack snapshot) { setFluid(snapshot); }
        @Override protected void onRootCommit(FluidStack original) { onContentsChanged(); }
    };

    public TransactionalFluidTank(int capacity, boolean allowInsert, boolean allowExtract) {
        super(capacity);
        this.allowInsert = allowInsert;
        this.allowExtract = allowExtract;
    }

    @Override public int size() { return 1; }

    @Override
    public FluidResource getResource(int index) {
        Objects.checkIndex(index, 1);
        return FluidResource.of(getFluid());
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, 1);
        return getFluidAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return isValid(index, resource) ? getCapacity() : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        Objects.checkIndex(index, 1);
        return resource.isEmpty() || isFluidValid(resource.toStack(1));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, 1);
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!allowInsert || amount == 0) return 0;
        int inserted = fill(resource.toStack(amount), FluidAction.SIMULATE);
        if (inserted <= 0) return 0;
        journal.updateSnapshots(transaction);
        setFluid(resource.toStack(getFluidAmount() + inserted));
        return inserted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, 1);
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!allowExtract || amount == 0) return 0;
        int extracted = drain(resource.toStack(amount), FluidAction.SIMULATE).getAmount();
        if (extracted <= 0) return 0;
        journal.updateSnapshots(transaction);
        setFluid(getFluid().copyWithAmount(getFluidAmount() - extracted));
        return extracted;
    }
}
