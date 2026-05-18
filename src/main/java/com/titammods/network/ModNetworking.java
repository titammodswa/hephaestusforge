package com.titammods.network;

import com.titammods.TitamMods;
import com.titammods.block.SmelteryControllerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    }

    public static void handleFluidClick(final FluidClickPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level() != null) {
                BlockEntity be = context.player().level().getBlockEntity(payload.pos());
                if (be instanceof SmelteryControllerBlockEntity controller) {
                    controller.fluidTank.moveFluidToBottom(payload.fluidIndex());
                    controller.setChanged();
                    controller.getLevel().sendBlockUpdated(controller.getBlockPos(), controller.getBlockState(), controller.getBlockState(), 3);
                }
            }
        });
    }

    public static void handleScrollSync(final ScrollSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof com.titammods.menu.SmelteryMenu menu) {
                menu.updateScrollOffset(payload.rowOffset());
            }
        });
    }
}