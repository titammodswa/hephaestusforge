package com.titammods.client;

import com.titammods.TitamMods;
import com.titammods.common.blockentities.render.SearedTankRenderer;
import com.titammods.registry.HephaestusFluids;
import com.titammods.registry.fluids.MoltenFluidSet;
import com.titammods.setup.ModBlockEntities;
import com.titammods.setup.ModMenus;
import com.titammods.setup.ModFluids;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import com.titammods.common.blockentities.render.MelterRenderer;
import com.titammods.common.blockentities.render.SearedTankItemRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientModEvents {

    private static final Material MOLTEN_METAL_STILL =
            new Material(Identifier.fromNamespaceAndPath(TitamMods.MODID, "fluid/liquid/molten_metal"));
    private static final Material MOLTEN_METAL_FLOW =
            new Material(Identifier.fromNamespaceAndPath(TitamMods.MODID, "fluid/liquid/molten_metal_flow"));
    private static final FluidModel.Unbaked MOLTEN_METAL_MODEL =
            new FluidModel.Unbaked(MOLTEN_METAL_STILL, MOLTEN_METAL_FLOW, null, null);

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientModEvents::onRegisterFluidModels);
        modEventBus.addListener(ClientModEvents::onClientSetup);
        modEventBus.addListener(ClientModEvents::onRegisterSpecialModelRenderer);
        modEventBus.addListener(ClientModEvents::onRegisterMenuScreens);
    }

    private static void onRegisterFluidModels(RegisterFluidModelsEvent event) {
        for (HephaestusFluids.Material mat : HephaestusFluids.Material.values()) {
            MoltenFluidSet set = HephaestusFluids.SETS.get(mat);
            event.register(MOLTEN_METAL_MODEL, set.source, set.flowing);
        }
        registerModFluid(event, ModFluids.MOLTEN_COBALT,   "fluid/molten/ore/cobalt");
        registerModFluid(event, ModFluids.MOLTEN_QUARTZ,   "fluid/molten/ore/quartz");
        registerModFluid(event, ModFluids.MOLTEN_DIAMOND,  "fluid/molten/ore/diamond");
        registerModFluid(event, ModFluids.MOLTEN_EMERALD,  "fluid/molten/ore/emerald");
        registerModFluid(event, ModFluids.MOLTEN_AMETHYST, "fluid/molten/ore/amethyst");
        registerModFluid(event, ModFluids.MOLTEN_BLAZE,    "fluid/molten/blaze");
    }

    private static void registerModFluid(RegisterFluidModelsEvent event,
                                         ModFluids.MoltenFluid fluid, String base) {
        event.register(new FluidModel.Unbaked(
                new Material(Identifier.fromNamespaceAndPath(TitamMods.MODID, base + "/still")),
                new Material(Identifier.fromNamespaceAndPath(TitamMods.MODID, base + "/flowing")),
                null, null), fluid.source, fluid.flowing);
    }

    private static void onRegisterSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
        var unbaked = new SearedTankItemRenderer.Unbaked();
        event.register(SearedTankItemRenderer.RENDERER_ID, unbaked.type());
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MELTER_MENU.get(), com.titammods.client.screen.MelterScreen::new);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                BlockEntityRenderers.register(
                        ModBlockEntities.SEARED_TANK.get(),
                        SearedTankRenderer::new)
        );
        event.enqueueWork(() ->
                BlockEntityRenderers.register(
                        ModBlockEntities.MELTER.get(),
                        MelterRenderer::new)
        );
    }
}
