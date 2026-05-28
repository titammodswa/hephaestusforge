package com.titammods.network;

import com.titammods.TitamMods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ScrollSyncPayload(int rowOffset) implements CustomPacketPayload {
    public static final Type<ScrollSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TitamMods.MODID, "scroll_sync"));

    public static final StreamCodec<FriendlyByteBuf, ScrollSyncPayload> STREAM_CODEC = StreamCodec.ofMember(
            ScrollSyncPayload::write, ScrollSyncPayload::new
    );

    public ScrollSyncPayload(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(rowOffset);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}