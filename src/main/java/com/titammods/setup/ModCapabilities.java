package com.titammods.setup;

import com.titammods.TitamMods;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = TitamMods.MODID)
public final class ModCapabilities {
    private ModCapabilities() {}

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.SEARED_TANK.get(),
                (tank, side) -> tank.getFluidResourceHandler());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.MELTER.get(),
                (melter, side) -> melter.getFluidResourceHandler());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.TABLE.get(),
                (table, side) -> table.getFluidResourceHandler());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.BASIN.get(),
                (basin, side) -> basin.getFluidResourceHandler());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.SEARED_DRAIN.get(),
                (drain, side) -> drain.getFluidResourceHandler());
    }
}
