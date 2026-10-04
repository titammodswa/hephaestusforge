package com.titammods.network;

import com.titammods.TitamMods;
import com.titammods.block.SmelteryControllerBlockEntity;
import com.titammods.menu.SmelteryMenu;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(TitamMods.MODID);
        registrar.playToServer(
                FluidClickPayload.TYPE,
                FluidClickPayload.STREAM_CODEC,
                ModNetworking::handleFluidClick
        );
        registrar.playToServer(
                ScrollSyncPayload.TYPE,
                ScrollSyncPayload.STREAM_CODEC,
                ModNetworking::handleScrollSync
        );
        registrar.playToClient(
                StructureErrorPayload.TYPE,
                StructureErrorPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> StructureErrorPayload.handleClient(payload))
        );
    }

    public static void handleFluidClick(final FluidClickPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player.containerMenu instanceof SmelteryMenu menu
                    && menu.blockEntity.getBlockPos().equals(payload.pos())
                    && menu.stillValid(player)) {
                SmelteryControllerBlockEntity controller = menu.blockEntity;
                if (payload.fluidIndex() < 0 || payload.fluidIndex() >= controller.fluidTank.getFluids().size()) return;
                controller.fluidTank.moveFluidToBottom(payload.fluidIndex());
                controller.setChanged();
                controller.getLevel().sendBlockUpdated(controller.getBlockPos(), controller.getBlockState(), controller.getBlockState(), 3);
            }
        });
    }

    public static void handleScrollSync(final ScrollSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof SmelteryMenu menu) {
                menu.updateScrollOffset(payload.rowOffset());
            }
        });
    }
}