package com.titammods.block;

import com.titammods.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class SmelteryTankBlockEntity extends BlockEntity {

    private final FluidTank tank = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null) {
                updateLight(SmelteryTankBlockEntity.this, this);
                if (!level.isClientSide) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                } else {
                    requestModelDataUpdate();
                }
            }
        }
    };

    public SmelteryTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMELETRY_TANK.get(), pos, state);
    }

    public FluidTank getTank() {
        return tank;
    }

    public static void updateLight(BlockEntity be, FluidTank tank) {
        if (be.getLevel() != null && !be.getLevel().isClientSide) {
            FluidStack fluid = tank.getFluid();
            int light = fluid.isEmpty() ? 0 : fluid.getFluid().getFluidType().getLightLevel(fluid);
            BlockState state = be.getBlockState();
            if (state.hasProperty(SmelteryTankBlock.LIGHT) && state.getValue(SmelteryTankBlock.LIGHT) != light) {
                be.getLevel().setBlock(be.getBlockPos(), state.setValue(SmelteryTankBlock.LIGHT, light), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLight(this, tank);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tank.readFromNBT(registries, tag.getCompound("tank"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("tank", tank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        if (this.level != null && this.level.isClientSide) {
            requestModelDataUpdate();
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}