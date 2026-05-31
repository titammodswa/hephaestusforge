package com.titammods.datagen;

import com.titammods.TitamMods;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DataGenerators::onClientData);
        modEventBus.addListener(DataGenerators::onServerData);
    }

    private static void onClientData(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);
    }

    private static void onServerData(GatherDataEvent.Server event) {
        var output = event.getGenerator().getPackOutput();
        var lookup = event.getLookupProvider();

        event.createProvider(ModBlockTagsProvider::new);
        event.createProvider((out, lk) -> ModLootTableProvider.create(out, lk));

        event.getGenerator().addProvider(true, new ModItemTagsProvider(output, lookup));

        event.getGenerator().addProvider(true, new RecipeProvider.Runner(output, lookup) {
            @Override
            public String getName() { return TitamMods.MODID + " recipes"; }

            @Override
            protected RecipeProvider createRecipeProvider(net.minecraft.core.HolderLookup.Provider provider, RecipeOutput recipeOutput) {
                return new ModRecipeProvider(provider, recipeOutput);
            }
        });
    }
}