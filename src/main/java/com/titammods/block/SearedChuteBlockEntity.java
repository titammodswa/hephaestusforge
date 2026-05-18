package com.titammods.block;

import com.titammods.block.multiblock.IDisplayFluidListener;
import com.titammods.client.model.ModelProperties;
import com.titammods.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;

public class SearedChuteBlockEntity extends BlockEntity implements IDisplayFluidListener {
    private BlockPos controllerPos;
    public FluidStack displayFluid = FluidStack.EMPTY;

    public SearedChuteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEARED_CHUTE.get(), pos, state);
    }

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        this.setChanged();
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    @Override
    public FluidStack getDisplayFluid() {
        return displayFluid;
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(ModelProperties.FLUID_STACK, displayFluid)
                .build();
    }

    @Override
    public void notifyDisplayFluidUpdated(FluidStack fluid) {
        if (!FluidStack.isSameFluidSameComponents(this.displayFluid, fluid) || this.displayFluid.isEmpty() != fluid.isEmpty()) {
            this.displayFluid = fluid.isEmpty() ? FluidStack.EMPTY : fluid.copy();
            this.setChanged();
            requestModelDataUpdate();
            if (this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (controllerPos != null) {
            tag.putInt("CtrlX", controllerPos.getX());
            tag.putInt("CtrlY", controllerPos.getY());
            tag.putInt("CtrlZ", controllerPos.getZ());
        }
        if (!displayFluid.isEmpty()) {
            tag.putString("DisplayFluidId", BuiltInRegistries.FLUID.getKey(displayFluid.getFluid()).toString());
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("CtrlX")) {
            controllerPos = new BlockPos(tag.getInt("CtrlX"), tag.getInt("CtrlY"), tag.getInt("CtrlZ"));
        } else {
            controllerPos = null;
        }
        if (tag.contains("DisplayFluidId")) {
            Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(tag.getString("DisplayFluidId")));
            this.displayFluid = (fluid != Fluids.EMPTY) ? new FluidStack(fluid, 1000) : FluidStack.EMPTY;
        } else {
            this.displayFluid = FluidStack.EMPTY;
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        requestModelDataUpdate();
        if (this.level != null && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}