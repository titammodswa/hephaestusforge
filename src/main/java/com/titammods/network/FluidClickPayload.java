package com.titammods.network;

import com.titammods.TitamMods;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FluidClickPayload(BlockPos pos, int fluidIndex) implements CustomPacketPayload {
    public static final Type<FluidClickPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "fluid_click"));

    public static final StreamCodec<FriendlyByteBuf, FluidClickPayload> STREAM_CODEC = StreamCodec.ofMember(
            FluidClickPayload::write, FluidClickPayload::new
    );

    public FluidClickPayload(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(fluidIndex);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}