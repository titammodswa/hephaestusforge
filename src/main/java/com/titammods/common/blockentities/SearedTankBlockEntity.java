package com.titammods.common.blockentities;

import com.titammods.common.blocks.SearedTankBlock;
import com.titammods.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("deprecation")
public class SearedTankBlockEntity extends BlockEntity {

    public static final int CAPACITY = FluidType.BUCKET_VOLUME * 4;

    private final FluidTank fluidTank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                updateLightBlockState(serverLevel);
                var packet = getUpdatePacket();
                if (packet != null) {
                    for (var player : serverLevel.players()) {
                        double dx = worldPosition.getX() - player.getX();
                        double dy = worldPosition.getY() - player.getY();
                        double dz = worldPosition.getZ() - player.getZ();
                        if (dx*dx + dy*dy + dz*dz < 64*64)
                            player.connection.send(packet);
                    }
                }
            }
        }
    };

    public SearedTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEARED_TANK.get(), pos, state);
    }

    public FluidTank getFluidTank() { return fluidTank; }

    private void updateLightBlockState(ServerLevel serverLevel) {
        BlockState current = getBlockState();
        if (!current.hasProperty(SearedTankBlock.EMITS_LIGHT)) return;

        FluidStack fluid = fluidTank.getFluid();
        boolean shouldEmit = !fluid.isEmpty() &&
                fluid.getFluidType().getLightLevel(fluid) > 0;

        if (current.getValue(SearedTankBlock.EMITS_LIGHT) != shouldEmit) {
            serverLevel.setBlock(worldPosition,
                    current.setValue(SearedTankBlock.EMITS_LIGHT, shouldEmit),
                    Block.UPDATE_ALL);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel sl) {
            updateLightBlockState(sl);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("fluid", FluidStack.OPTIONAL_CODEC, fluidTank.getFluid());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fluidTank.setFluid(
                input.read("fluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY)
        );
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
