package com.titammods.network;

import com.titammods.common.blockentities.SmelteryControllerBlockEntity;
import com.titammods.menu.SmelteryMenu;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar reg = event.registrar("hephaestus");
        reg.playToServer(FluidClickPayload.TYPE,  FluidClickPayload.STREAM_CODEC,  ModNetworking::handleFluidClick);
        reg.playToServer(ScrollSyncPayload.TYPE,  ScrollSyncPayload.STREAM_CODEC,  ModNetworking::handleScrollSync);
    }

    public static void handleFluidClick(FluidClickPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player().level().getBlockEntity(payload.pos()) instanceof SmelteryControllerBlockEntity c) {
                c.fluidTank.moveFluidToBottom(payload.fluidIndex());
                c.setChanged();
                c.getLevel().sendBlockUpdated(c.getBlockPos(), c.getBlockState(), c.getBlockState(), 3);
            }
        });
    }

    public static void handleScrollSync(ScrollSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player().containerMenu instanceof SmelteryMenu menu)
                menu.updateScrollOffset(payload.rowOffset());
        });
    }
}