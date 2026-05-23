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
import com.titammods.registry.HephaestusFluids;
import com.titammods.registry.fluids.MoltenFluidSet;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = TitamMods.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientModEvents::onClientExtensions);
        modEventBus.addListener(ClientModEvents::onClientSetup);
    }

    private static void onClientExtensions(RegisterClientExtensionsEvent event) {
        for (HephaestusFluids.Material material : HephaestusFluids.Material.values()) {
            MoltenFluidSet set = HephaestusFluids.SETS.get(material);

            event.registerFluidType(new IClientFluidTypeExtensions() {
                private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath("hephaestus", "fluid/liquid/molten_metal");
                private static final ResourceLocation FLOW = ResourceLocation.fromNamespaceAndPath("hephaestus", "fluid/liquid/molten_metal_flow");
                @Override
                public ResourceLocation getStillTexture() { return STILL; }
                @Override
                public ResourceLocation getFlowingTexture() { return FLOW; }
                @Override
                public int getTintColor() { return material.color; }
                @Override
                public int getTintColor(net.minecraft.world.level.material.FluidState state, net.minecraft.world.level.BlockAndTintGetter level, net.minecraft.core.BlockPos pos) {
                    return material.color;
                }
            }, set.fluidType.get());
        }

        registerFluidExtensions(event, ModFluids.MOLTEN_COBALT, "ore/cobalt");
        registerFluidExtensions(event, ModFluids.MOLTEN_QUARTZ, "ore/quartz");
        registerFluidExtensions(event, ModFluids.MOLTEN_DIAMOND, "ore/diamond");
        registerFluidExtensions(event, ModFluids.MOLTEN_EMERALD, "ore/emerald");
        registerFluidExtensions(event, ModFluids.MOLTEN_AMETHYST, "ore/amethyst");

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

        for (HephaestusFluids.Material material : HephaestusFluids.Material.values()) {
            MoltenFluidSet set = HephaestusFluids.SETS.get(material);

            ItemBlockRenderTypes.setRenderLayer(set.source.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(set.flowing.get(), RenderType.translucent());
        }

        setFluidTranslucent(ModFluids.MOLTEN_COBALT);
        setFluidTranslucent(ModFluids.MOLTEN_QUARTZ);
        setFluidTranslucent(ModFluids.MOLTEN_DIAMOND);
        setFluidTranslucent(ModFluids.MOLTEN_EMERALD);
        setFluidTranslucent(ModFluids.MOLTEN_AMETHYST);

        setFluidTranslucent(ModFluids.MOLTEN_BLAZE);

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CLEAR_GLASS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SEARED_TINTED_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SEARED_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CLEAR_TINTED_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CLEAR_STAINED_GLASS.get(), RenderType.translucent());
    }

    public static void registerConflictScreen() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ClientModEvents::showErrorScreen);
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

    @SuppressWarnings("deprecation")
    private static void setFluidTranslucent(ModFluids.MoltenFluid fluid) {
        ItemBlockRenderTypes.setRenderLayer(fluid.source.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(fluid.flowing.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        if (TitamMods.hasConflict) return;

        for (com.titammods.registry.HephaestusFluids.Material material : com.titammods.registry.HephaestusFluids.Material.values()) {
            com.titammods.registry.fluids.MoltenFluidSet set = com.titammods.registry.HephaestusFluids.SETS.get(material);

            event.register((stack, tintIndex) -> {
                return tintIndex == 1 ? material.color : 0xFFFFFFFF;
            }, set.bucket.get());
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (TitamMods.hasConflict) return;

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
        if (TitamMods.hasConflict) return;

        event.register(ModMenus.MELTER_MENU.get(), MelterScreen::new);
        event.register(com.titammods.setup.ModMenus.SMELTERY_MENU.get(), com.titammods.client.screen.SmelteryScreen::new);
    }
}