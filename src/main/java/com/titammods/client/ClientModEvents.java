package com.titammods.client;

import com.titammods.TitamMods;
import com.titammods.setup.ModFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.titammods.setup.ModBlockEntities;
import com.titammods.client.render.SmelteryTankRenderer;
import com.titammods.client.render.MelterRenderer;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.titammods.client.screen.MelterScreen;
import com.titammods.setup.ModMenus;
import net.neoforged.api.distmarker.Dist;
import com.titammods.client.render.FaucetRenderer;
import com.titammods.client.render.BasinRenderer;
import com.titammods.client.render.TableRenderer;
import com.titammods.setup.ModBlocks;

@EventBusSubscriber(modid = TitamMods.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientModEvents::onClientExtensions);
        modEventBus.addListener(ClientModEvents::onClientSetup);
    }

    private static void onClientExtensions(RegisterClientExtensionsEvent event) {
        registerFluidExtensions(event, ModFluids.MOLTEN_IRON, "ore/iron");
        registerFluidExtensions(event, ModFluids.MOLTEN_GOLD, "ore/gold");
        registerFluidExtensions(event, ModFluids.MOLTEN_COPPER, "ore/copper");
        registerFluidExtensions(event, ModFluids.MOLTEN_STEEL, "ore/steel");
        registerFluidExtensions(event, ModFluids.MOLTEN_COBALT, "ore/cobalt");
        registerFluidExtensions(event, ModFluids.MOLTEN_QUARTZ, "ore/quartz");
        registerFluidExtensions(event, ModFluids.MOLTEN_DIAMOND, "ore/diamond");
        registerFluidExtensions(event, ModFluids.MOLTEN_EMERALD, "ore/emerald");
        registerFluidExtensions(event, ModFluids.MOLTEN_AMETHYST, "ore/amethyst");
        registerFluidExtensions(event, ModFluids.MOLTEN_BRASS, "compat_alloy/brass");
        registerFluidExtensions(event, ModFluids.MOLTEN_ZINC, "compat_alloy/zinc");

        registerFluidExtensions(event, ModFluids.MOLTEN_BLAZE, "blaze");
    }

    private static void registerFluidExtensions(RegisterClientExtensionsEvent event, ModFluids.MoltenFluid fluid, String path) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "fluid/molten/" + path + "/still");
            private final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "fluid/molten/" + path + "/flowing");

            @Override
            public ResourceLocation getStillTexture() { return STILL; }

            @Override
            public ResourceLocation getFlowingTexture() { return FLOWING; }
        }, fluid.type.get());
    }

    @SuppressWarnings("deprecation")
    private static void onClientSetup(FMLClientSetupEvent event) {
        setFluidTranslucent(ModFluids.MOLTEN_IRON);
        setFluidTranslucent(ModFluids.MOLTEN_GOLD);
        setFluidTranslucent(ModFluids.MOLTEN_COPPER);
        setFluidTranslucent(ModFluids.MOLTEN_STEEL);
        setFluidTranslucent(ModFluids.MOLTEN_COBALT);
        setFluidTranslucent(ModFluids.MOLTEN_QUARTZ);
        setFluidTranslucent(ModFluids.MOLTEN_DIAMOND);
        setFluidTranslucent(ModFluids.MOLTEN_EMERALD);
        setFluidTranslucent(ModFluids.MOLTEN_AMETHYST);
        setFluidTranslucent(ModFluids.MOLTEN_BRASS);
        setFluidTranslucent(ModFluids.MOLTEN_ZINC);

        setFluidTranslucent(ModFluids.MOLTEN_BLAZE);

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CLEAR_GLASS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SEARED_TINTED_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SEARED_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CLEAR_TINTED_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CLEAR_STAINED_GLASS.get(), RenderType.translucent());
    }

    @SuppressWarnings("deprecation")
    private static void setFluidTranslucent(ModFluids.MoltenFluid fluid) {
        ItemBlockRenderTypes.setRenderLayer(fluid.source.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(fluid.flowing.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FAUCET.get(), FaucetRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SMELETRY_TANK.get(), SmelteryTankRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MELTER.get(), MelterRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BASIN.get(), BasinRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TABLE.get(), TableRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SMELTERY_CONTROLLER.get(), com.titammods.client.render.SmelteryIORenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SEARED_DRAIN.get(), com.titammods.client.render.SmelteryIORenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SEARED_CHUTE.get(), com.titammods.client.render.SmelteryIORenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MELTER_MENU.get(), MelterScreen::new);

        event.register(com.titammods.setup.ModMenus.SMELTERY_MENU.get(), com.titammods.client.screen.SmelteryScreen::new);
    }
}