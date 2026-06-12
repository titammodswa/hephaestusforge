package com.titammods.network;

import com.titammods.TitamMods;
import com.titammods.block.SmelteryControllerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record StructureErrorPayload(BlockPos controllerPos, @Nullable BlockPos errorPos) implements CustomPacketPayload {

    public static final Type<StructureErrorPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "structure_error"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StructureErrorPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        buf.writeBlockPos(p.controllerPos());
                        buf.writeBoolean(p.errorPos() != null);
                        if (p.errorPos() != null) buf.writeBlockPos(p.errorPos());
                    },
                    buf -> {
                        BlockPos ctrl = buf.readBlockPos();
                        BlockPos err  = buf.readBoolean() ? buf.readBlockPos() : null;
                        return new StructureErrorPayload(ctrl, err);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handleClient(StructureErrorPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (mc.level.getBlockEntity(payload.controllerPos()) instanceof SmelteryControllerBlockEntity be) {
            be.setErrorPos(payload.errorPos());
        }
    }
}
