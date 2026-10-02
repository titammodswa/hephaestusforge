package com.titammods;

import com.titammods.client.ClientModEvents;
import com.titammods.common.blockentities.module.EntityMeltingModule;
import com.titammods.datagen.DataGenerators;
import com.titammods.registry.HephaestusFluids;
import com.titammods.setup.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@Mod(TitamMods.MODID)
public class TitamMods {

    public static final String MODID = "hephaestus";
    public static boolean hasConflict = false;

    public TitamMods(IEventBus modEventBus) {

        if (ModList.get().isLoaded("alltheores") && ModList.get().isLoaded("ftbmaterials")) {
            hasConflict = true;

            return;
        }

        HephaestusFluids.registerFluids();

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


        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ClientModEvents.register(modEventBus);
        }

        modEventBus.addListener(com.titammods.network.ModNetworking::register);

        DataGenerators.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(
                (OnDatapackSyncEvent e) -> {
                    EntityMeltingModule.invalidateCache();
                    e.sendRecipes(ModRecipes.MELTING_TYPE.get(), ModRecipes.ALLOY_TYPE.get(),
                            ModRecipes.CASTING_TABLE_TYPE.get(), ModRecipes.CASTING_BASIN_TYPE.get(),
                            ModRecipes.ENTITY_MELTING_TYPE.get());
                });
    }

}
