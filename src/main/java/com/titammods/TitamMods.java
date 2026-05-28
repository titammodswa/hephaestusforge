package com.titammods;

import com.titammods.setup.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import com.titammods.client.ClientModEvents;
import com.titammods.datagen.DataGenerators;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import com.titammods.block.SmelteryControllerBlockEntity;
import com.titammods.block.SearedDrainBlockEntity;
import com.titammods.block.SearedChuteBlockEntity;
import com.titammods.network.ModNetworking;
import net.neoforged.fml.ModList;
import com.titammods.registry.HephaestusFluids;

@Mod(TitamMods.MODID)
public class TitamMods {
    public static final String MODID = "hephaestus";

    public static boolean hasConflict = false;

    public TitamMods(IEventBus modEventBus) {

        if (ModList.get().isLoaded("alltheores") && ModList.get().isLoaded("ftbmaterials")) {
            hasConflict = true;
            if (FMLEnvironment.getDist() == Dist.CLIENT) {
                net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(TitamMods::showErrorScreen);
            }
            return;
        }

        HephaestusFluids.registerFluids();

        ModBlocks.BLOCKS.register(modEventBus);
        ModTanks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModRecipes.TYPES.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);

        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);

        modEventBus.addListener(ModNetworking::register);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ClientModEvents.register(modEventBus);
        }
        DataGenerators.register(modEventBus);
    }

    public static void showErrorScreen(net.neoforged.neoforge.client.event.ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof net.minecraft.client.gui.screens.TitleScreen) {
            net.minecraft.client.Minecraft.getInstance().setScreen(new net.minecraft.client.gui.screens.ConfirmScreen(
                    (clique) -> net.minecraft.client.Minecraft.getInstance().stop(),
                    net.minecraft.network.chat.Component.translatable("gui.hephaestus.conflict.title"),
                    net.minecraft.network.chat.Component.translatable("gui.hephaestus.conflict.description")
            ));
        }
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.SMELETRY_TANK.get(), (be, side) -> be.getTank());

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BASIN.get(), (be, side) -> be.tank);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BASIN.get(), (be, side) -> be.inventory);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.TABLE.get(), (be, side) -> be.externalFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.TABLE.get(), (be, side) -> be.externalHandler);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MELTER.get(), (be, side) -> be.tank);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MELTER.get(), (be, side) -> be.externalItemHandler);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.SMELTERY_CONTROLLER.get(), (be, side) -> be.fluidTank);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SMELTERY_CONTROLLER.get(), (be, side) -> be.itemHandler);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.SEARED_DRAIN.get(), (be, side) -> {
            if (be instanceof SearedDrainBlockEntity drain && drain.getControllerPos() != null) {
                if (drain.getLevel().getBlockEntity(drain.getControllerPos()) instanceof SmelteryControllerBlockEntity controller) {
                    return controller.fluidTank;
                }
            }
            return null;
        });
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SEARED_CHUTE.get(), (be, side) -> {
            if (be instanceof SearedChuteBlockEntity chute && chute.getControllerPos() != null) {
                if (chute.getLevel().getBlockEntity(chute.getControllerPos()) instanceof SmelteryControllerBlockEntity controller) {
                    return controller.itemHandler;
                }
            }
            return null;
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}