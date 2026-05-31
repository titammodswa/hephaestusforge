package com.titammods;

import com.titammods.client.ClientModEvents;
import com.titammods.datagen.DataGenerators;
import com.titammods.registry.HephaestusFluids;
import com.titammods.setup.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(TitamMods.MODID)
public class TitamMods {

    public static final String MODID = "hephaestus";
    public static boolean hasConflict = false;

    public TitamMods(IEventBus modEventBus) {

        if (ModList.get().isLoaded("alltheores") && ModList.get().isLoaded("ftbmaterials")) {
            hasConflict = true;
            if (FMLEnvironment.getDist() == Dist.CLIENT) {
                net.neoforged.neoforge.common.NeoForge.EVENT_BUS
                        .addListener(TitamMods::showErrorScreen);
            }
            return;
        }

        HephaestusFluids.registerFluids();

        // Registries
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModRecipes.TYPES.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.RECIPE_BOOK_CATEGORIES.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);


        // Client
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ClientModEvents.register(modEventBus);
        }

        DataGenerators.register(modEventBus);
    }

    public static void showErrorScreen(net.neoforged.neoforge.client.event.ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof net.minecraft.client.gui.screens.TitleScreen) {
            net.minecraft.client.Minecraft.getInstance().setScreen(
                    new net.minecraft.client.gui.screens.ConfirmScreen(
                            ignored -> net.minecraft.client.Minecraft.getInstance().stop(),
                            net.minecraft.network.chat.Component
                                    .translatable("gui.hephaestus.conflict.title"),
                            net.minecraft.network.chat.Component
                                    .translatable("gui.hephaestus.conflict.description")
                    )
            );
        }
    }
}
