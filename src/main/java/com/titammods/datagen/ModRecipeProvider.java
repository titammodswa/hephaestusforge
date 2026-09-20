package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.setup.ModFluids;
import com.titammods.setup.ModItems;
import com.titammods.setup.ModRecipes;
import com.titammods.setup.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.fluids.FluidStack;
import com.titammods.registry.HephaestusFluids;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries); }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        generateDecorative(output, ModBlocks.SEARED_STONE.get(), ModBlocks.SEARED_STONE_SLAB.get(), ModBlocks.SEARED_STONE_STAIRS.get(), ModBlocks.SEARED_STONE_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_COBBLE.get(), ModBlocks.SEARED_COBBLE_SLAB.get(), ModBlocks.SEARED_COBBLE_STAIRS.get(), ModBlocks.SEARED_COBBLE_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_PAVER.get(), ModBlocks.SEARED_PAVER_SLAB.get(), ModBlocks.SEARED_PAVER_STAIRS.get(), ModBlocks.SEARED_PAVER_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_BRICKS.get(), ModBlocks.SEARED_BRICKS_SLAB.get(), ModBlocks.SEARED_BRICKS_STAIRS.get(), ModBlocks.SEARED_BRICKS_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_CRACKED_BRICKS.get(), ModBlocks.SEARED_CRACKED_BRICKS_SLAB.get(), ModBlocks.SEARED_CRACKED_BRICKS_STAIRS.get(), ModBlocks.SEARED_CRACKED_BRICKS_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_FANCY_BRICKS.get(), ModBlocks.SEARED_FANCY_BRICKS_SLAB.get(), ModBlocks.SEARED_FANCY_BRICKS_STAIRS.get(), ModBlocks.SEARED_FANCY_BRICKS_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_TRIANGLE_BRICKS.get(), ModBlocks.SEARED_TRIANGLE_BRICKS_SLAB.get(), ModBlocks.SEARED_TRIANGLE_BRICKS_STAIRS.get(), ModBlocks.SEARED_TRIANGLE_BRICKS_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_CREEPER.get(), ModBlocks.SEARED_CREEPER_SLAB.get(), ModBlocks.SEARED_CREEPER_STAIRS.get(), ModBlocks.SEARED_CREEPER_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_ROAD.get(), ModBlocks.SEARED_ROAD_SLAB.get(), ModBlocks.SEARED_ROAD_STAIRS.get(), ModBlocks.SEARED_ROAD_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_SMALL_BRICKS.get(), ModBlocks.SEARED_SMALL_BRICKS_SLAB.get(), ModBlocks.SEARED_SMALL_BRICKS_STAIRS.get(), ModBlocks.SEARED_SMALL_BRICKS_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_SQUARE_BRICKS.get(), ModBlocks.SEARED_SQUARE_BRICKS_SLAB.get(), ModBlocks.SEARED_SQUARE_BRICKS_STAIRS.get(), ModBlocks.SEARED_SQUARE_BRICKS_WALL.get());
        generateDecorative(output, ModBlocks.SEARED_TILE.get(), ModBlocks.SEARED_TILE_SLAB.get(), ModBlocks.SEARED_TILE_STAIRS.get(), ModBlocks.SEARED_TILE_WALL.get());

        createCastRecipe(output, "ingots", ModItems.INGOT_CAST.get(), "ingot_cast");
        createCastRecipe(output, "nuggets", ModItems.NUGGET_CAST.get(), "nugget_cast");
        createCastRecipe(output, "gems", ModItems.GEM_CAST.get(), "gem_cast");
        createCastRecipe(output, "plates", ModItems.PLATE_CAST.get(), "plate_cast");
        createCastRecipe(output, "gears", ModItems.GEAR_CAST.get(), "gear_cast");
        createRodCastRecipe(output);

        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRON).source.get(), 900, "iron", Items.IRON_BLOCK, Items.IRON_INGOT, Items.RAW_IRON, Items.IRON_NUGGET, null, null, null, null, "");
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GOLD).source.get(), 900, "gold", Items.GOLD_BLOCK, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_NUGGET, null, null, null, null, "");
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 900, "copper", Items.COPPER_BLOCK, Items.COPPER_INGOT, Items.RAW_COPPER, null, null, null, null, null, "");
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_STEEL).source.get(), 900, "steel", ModBlocks.STEEL_BLOCK.get(), ModItems.STEEL_INGOT.get(), ModItems.RAW_STEEL.get(), ModItems.STEEL_NUGGET.get(), ModItems.STEEL_POWDER.get(), null, null, null, "");
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NETHERITE).source.get(), 1500, "netherite", Items.NETHERITE_BLOCK, Items.NETHERITE_INGOT, null, null, null, null, null, null, "");

        registerMetal(output, ModFluids.MOLTEN_COBALT.source.get(), 1100, "cobalt", ModBlocks.COBALT_BLOCK.get(), ModItems.COBALT_INGOT.get(), ModItems.RAW_COBALT.get(), ModItems.COBALT_NUGGET.get(), ModItems.COBALT_POWDER.get(), null, null, null, "");

        registerGem(output, ModFluids.MOLTEN_DIAMOND.source.get(), 1400, "diamond", "storage_blocks/diamond", "gems/diamond", Items.DIAMOND_BLOCK, Items.DIAMOND);
        registerGem(output, ModFluids.MOLTEN_EMERALD.source.get(), 1200, "emerald", "storage_blocks/emerald", "gems/emerald", Items.EMERALD_BLOCK, Items.EMERALD);
        registerGem(output, ModFluids.MOLTEN_AMETHYST.source.get(), 1000, "amethyst", "", "gems/amethyst", Items.AMETHYST_BLOCK, Items.AMETHYST_SHARD, 360);
        registerGem(output, ModFluids.MOLTEN_QUARTZ.source.get(), 800, "quartz", "", "gems/quartz", Items.QUARTZ_BLOCK, Items.QUARTZ, 360);

        addMeltingItem(output, Items.GLOWSTONE,       HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GLOWSTONE).source.get(), 200, 800,  120, "misc/glowstone/block");
        addMeltingItem(output, Items.GLOWSTONE_DUST,  HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GLOWSTONE).source.get(),  50, 800,  60,  "misc/glowstone/dust");
        addCastingBasin(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GLOWSTONE).source.get(), 200, Items.GLOWSTONE,     120, "misc/glowstone/block");
        addMeltingItem(output, Items.REDSTONE_BLOCK, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REDSTONE).source.get(), 90, 600, 120, "misc/redstone/block");
        addMeltingItem(output, Items.REDSTONE,       HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REDSTONE).source.get(), 10, 600, 40, "misc/redstone/dust");
        addCastingBasin(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REDSTONE).source.get(), 90, Items.REDSTONE_BLOCK, 120, "misc/redstone/block");
        addMeltingItem(output, Items.OBSIDIAN, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_OBSIDIAN).source.get(), 288, 1400, 200, "misc/obsidian");
        addCastingBasin(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_OBSIDIAN).source.get(), 288, Items.OBSIDIAN, 200, "misc/obsidian");
        addMeltingItem(output, Items.GLASS, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GLASS).source.get(), 250, 1000, 100, "misc/glass_from_block");
        addMeltingItem(output, Items.GLASS_PANE, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GLASS).source.get(), 90, 1000, 60, "misc/glass_from_pane");
        addMeltingItem(output, Items.SAND, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GLASS).source.get(), 250, 1000, 100, "misc/glass_from_sand");
        addCastingBasin(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GLASS).source.get(), 250, ModBlocks.CLEAR_GLASS.get(), 100, "misc/glass");
        addMeltingItem(output, Items.LAPIS_LAZULI, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LAPIS).source.get(), 10, 900, 40, "misc/lapis/gem");
        addMeltingTag(output, "storage_blocks/lapis", HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LAPIS).source.get(), 90, 900, 120, "misc/lapis/block");
        addCastingBasin(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LAPIS).source.get(), 90, Items.LAPIS_BLOCK, 120, "misc/lapis/block");
        addMeltingItem(output, Items.ENDER_PEARL, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ENDER).source.get(), 90, 1000, 100, "misc/ender/pearl");
        addMeltingItem(output, Items.ENDER_EYE, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ENDER).source.get(), 90, 1000, 100, "misc/ender/eye");
        addCastingTable(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ENDER).source.get(), 90, ModItems.GEM_CAST.get(), false, Items.ENDER_PEARL, 60, "misc/ender/pearl");
        addMeltingItem(output, Items.ANCIENT_DEBRIS, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ANCIENT_DEBRIS).source.get(), 90, 2000, 300, "misc/ancient_debris");
        addMeltingItem(output, Items.NETHERITE_SCRAP, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ANCIENT_DEBRIS).source.get(), 90, 2000, 200, "misc/ancient_debris_from_scrap");
        addMeltingItem(output, Items.NETHERITE_INGOT, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NETHERITE).source.get(), 90, 2000, 200, "misc/netherite_from_ingot");
        addMeltingItem(output, Items.SHULKER_SHELL, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SHULKER_SHELL).source.get(), 90, 1200, 120, "misc/shulker_shell");
        addCastingTable(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SHULKER_SHELL).source.get(), 90, null, false, Items.SHULKER_SHELL, 120, "misc/shulker_shell");
        addMeltingItem(output, Items.SLIME_BALL,  HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SLIME).source.get(), 50,  300, 60, "misc/slime/ball");
        addMeltingItem(output, Items.SLIME_BLOCK, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SLIME).source.get(), 450, 300, 120, "misc/slime/block");
        addCastingBasin(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SLIME).source.get(), 450, Items.SLIME_BLOCK, 120, "misc/slime/block");
        addMeltingItem(output, Items.MAGMA_CREAM, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_MAGMA_CREAM).source.get(), 90, 700, 80, "misc/magma_cream");
        addMeltingItem(output, Items.MAGMA_BLOCK, net.minecraft.world.level.material.Fluids.LAVA, 250, 700, 120, "misc/magma_block");
        addCastingTable(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_MAGMA_CREAM).source.get(), 90, ModItems.GEM_CAST.get(), false, Items.MAGMA_CREAM, 30, "misc/magma_cream");
        addMeltingItem(output, Items.HEAVY_CORE, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_HEAVY_CORE).source.get(), 810, 2000, 180, "misc/heavy_core");
        addCastingTable(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_HEAVY_CORE).source.get(), 810, null, false, Items.HEAVY_CORE, 180, "misc/heavy_core");
        addMeltingItem(output, Items.CHARCOAL, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CARBON).source.get(), 90, 600, 100, "misc/carbon/charcoal");
        addMeltingItem(output, Items.COAL,     HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CARBON).source.get(), 90, 600, 100, "misc/carbon/coal");
        addMeltingTag(output, "storage_blocks/coal", HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CARBON).source.get(), 810, 600, 200, "misc/carbon/coal_block");
        addCastingBasin(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CARBON).source.get(), 810, Items.COAL_BLOCK, 200, "misc/carbon/coal_block");
        addMeltingItem(output, Items.HONEYCOMB, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_WAX).source.get(), 50, 320, 60, "misc/wax_from_honeycomb");
        addMeltingItem(output, Items.HONEYCOMB_BLOCK, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_WAX).source.get(), 200, 320, 120, "misc/wax_from_block");
        RecipeOutput mekOutput = output.withConditions(modLoaded("mekanism"));
        addMeltingTag(mekOutput, "storage_blocks/refined_glowstone", HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REFINED_GLOWSTONE).source.get(), 810,  900, 120, "misc/refined_glowstone/block");
        addMeltingTag(mekOutput, "ingots/refined_glowstone",         HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REFINED_GLOWSTONE).source.get(),  90,  900,  60, "misc/refined_glowstone/ingot");
        addCastingTableById(mekOutput, net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REFINED_GLOWSTONE).source.get()),  90, ModItems.INGOT_CAST.get(), false, ResourceLocation.fromNamespaceAndPath("mekanism", "ingot_refined_glowstone"),  60, "misc/refined_glowstone/ingot_cast");
        addCastingBasinById(mekOutput, net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REFINED_GLOWSTONE).source.get()), 810, ResourceLocation.fromNamespaceAndPath("mekanism", "block_refined_glowstone"), 120, "misc/refined_glowstone/block_cast");
        addMeltingTag(mekOutput, "storage_blocks/refined_obsidian",  HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REFINED_OBSIDIAN).source.get(),  810, 1400, 120, "misc/refined_obsidian/block");
        addMeltingTag(mekOutput, "ingots/refined_obsidian",          HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REFINED_OBSIDIAN).source.get(),   90, 1400,  60, "misc/refined_obsidian/ingot");
        addCastingTableById(mekOutput, net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REFINED_OBSIDIAN).source.get()),  90, ModItems.INGOT_CAST.get(), false, ResourceLocation.fromNamespaceAndPath("mekanism", "ingot_refined_obsidian"),  60, "misc/refined_obsidian/ingot_cast");
        addCastingBasinById(mekOutput, net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_REFINED_OBSIDIAN).source.get()), 810, ResourceLocation.fromNamespaceAndPath("mekanism", "block_refined_obsidian"), 120, "misc/refined_obsidian/block_cast");
        addMeltingItem(output, Items.PORKCHOP, HephaestusFluids.SETS.get(HephaestusFluids.Material.LIQUID_MEAT).source.get(), 40, 200, 60, "misc/meat/porkchop");
        addMeltingItem(output, Items.BEEF,     HephaestusFluids.SETS.get(HephaestusFluids.Material.LIQUID_MEAT).source.get(), 40, 200, 60, "misc/meat/beef");
        addMeltingItem(output, Items.CHICKEN,  HephaestusFluids.SETS.get(HephaestusFluids.Material.LIQUID_MEAT).source.get(), 40, 200, 60, "misc/meat/chicken");
        addMeltingItem(output, Items.MUTTON,   HephaestusFluids.SETS.get(HephaestusFluids.Material.LIQUID_MEAT).source.get(), 40, 200, 60, "misc/meat/mutton");
        addMeltingItem(output, Items.RABBIT,   HephaestusFluids.SETS.get(HephaestusFluids.Material.LIQUID_MEAT).source.get(), 40, 200, 60, "misc/meat/rabbit");
        addMeltingItem(output, Items.ROTTEN_FLESH, HephaestusFluids.SETS.get(HephaestusFluids.Material.LIQUID_MEAT).source.get(), 20, 200, 40, "misc/meat/rotten_flesh");

        addToolArmorMelting(output);

        RecipeOutput atoOutput = output.withConditions(modLoaded("alltheores"));
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ALUMINUM).source.get(), 660, "aluminum", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRASS).source.get(), 930, "brass", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRONZE).source.get(), 950, "bronze", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CONSTANTAN).source.get(), 1220, "constantan", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ELECTRUM).source.get(), 1000, "electrum", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ENDERIUM).source.get(), 1450, "enderium", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_INVAR).source.get(), 1420, "invar", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRIDIUM).source.get(), 1440, "iridium", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LEAD).source.get(), 327, "lead", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LUMIUM).source.get(), 1000, "lumium", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(), 1450, "nickel", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_OSMIUM).source.get(), 1990, "osmium", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_PLATINUM).source.get(), 1768, "platinum", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SIGNALum).source.get(), 1000, "signalum", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SILVER).source.get(), 960, "silver", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_TIN).source.get(), 230, "tin", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_URANIUM).source.get(), 1130, "uranium", "alltheores", "alltheores_compat/");
        registerExternalMetal(atoOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ZINC).source.get(), 419, "zinc", "alltheores", "alltheores_compat/");

        RecipeOutput ftbOutput = output.withConditions(modLoaded("ftbmaterials"));
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ALUMINUM).source.get(),  660,  "aluminum",   true);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRASS).source.get(),     930,  "brass",      false);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRONZE).source.get(),    950,  "bronze",     false);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CONSTANTAN).source.get(),1220, "constantan", false);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ELECTRUM).source.get(),  1000, "electrum",   false);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_INVAR).source.get(),     1420, "invar",      false);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRIDIUM).source.get(),   2440, "iridium",    true);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LEAD).source.get(),       327, "lead",       true);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(),    1450, "nickel",     true);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SILVER).source.get(),     960, "silver",     true);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_TIN).source.get(),        230, "tin",        true);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_URANIUM).source.get(),   1130, "uranium",    true);
        registerFtbMetalById(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ZINC).source.get(),       419, "zinc",       true);

        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 100),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ZINC).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRASS).source.get(), 200),
                650,
                "brass");
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 300),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_TIN).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRONZE).source.get(), 400),
                700,
                "bronze");
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GOLD).source.get(), 100),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SILVER).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ELECTRUM).source.get(), 200),
                760,
                "electrum");
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRON).source.get(), 200),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_INVAR).source.get(), 300),
                810,
                "invar");
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 100),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CONSTANTAN).source.get(), 200),
                920,
                "constantan");
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ANCIENT_DEBRIS).source.get(), 270),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GOLD).source.get(), 270)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NETHERITE).source.get(), 270),
                2000,
                "netherite");

        addEntityMelting(output, net.minecraft.world.entity.EntityType.BLAZE,
                new FluidStack(ModFluids.MOLTEN_BLAZE.source.get(), 45), 2, "blaze");
        addEntityMelting(output, net.minecraft.world.entity.EntityType.IRON_GOLEM,
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRON).source.get(), 36), 4, "iron_golem");

    }

    private FluidStack fuelForTemp(int temp) {
        if (temp <= 1000) {
            return new FluidStack(Fluids.LAVA, 50);
        } else {
            return new FluidStack(ModFluids.MOLTEN_BLAZE.source.get(), 50);
        }
    }

    private void registerExternalMetal(RecipeOutput output, Fluid fluid, int temp, String name, String modid, String prefix) {
        ItemLike block = getExternalItem(modid, name + "_block");
        ItemLike ingot = getExternalItem(modid, name + "_ingot");
        ItemLike raw = getExternalItem(modid, "raw_" + name);
        ItemLike nugget = getExternalItem(modid, name + "_nugget");
        ItemLike dust = getExternalItem(modid, name + "_dust");
        ItemLike plate = getExternalItem(modid, name + "_plate");
        ItemLike gear = getExternalItem(modid, name + "_gear");
        ItemLike rod = getExternalItem(modid, name + "_rod");
        registerMetal(output, fluid, temp, name, block, ingot, raw, nugget, dust, plate, gear, rod, prefix);
    }

    private void registerExternalVanillaSupplements(RecipeOutput output, String name, Fluid fluid, int temp, String modid, String prefix) {
        int baseTime = 100;
        ItemLike dust = getExternalItem(modid, name + "_dust");
        ItemLike plate = getExternalItem(modid, name + "_plate");
        ItemLike gear = getExternalItem(modid, name + "_gear");
        ItemLike rod = getExternalItem(modid, name + "_rod");

        if (dust != null) addMeltingTag(output, "dusts/" + name, fluid, 90, temp, baseTime, prefix + "vanilla/" + name + "_dust");
        if (plate != null) {
            addMeltingTag(output, "plates/" + name, fluid, 90, temp, baseTime, prefix + "vanilla/" + name + "_plate");
            addCastingTable(output, fluid, 90, ModItems.PLATE_CAST.get(), false, plate, baseTime, prefix + "vanilla/" + name + "_plate_cast"); }
        if (gear != null) {
            addMeltingTag(output, "gears/" + name, fluid, 360, temp, baseTime * 2, prefix + "vanilla/" + name + "_gear");
            addCastingTable(output, fluid, 360, ModItems.GEAR_CAST.get(), false, gear, baseTime * 2, prefix + "vanilla/" + name + "_gear_cast"); }
        if (rod != null) {
            addMeltingTag(output, "rods/" + name, fluid, 45, temp, baseTime / 2, prefix + "vanilla/" + name + "_rod");
            addCastingTable(output, fluid, 45, ModItems.ROD_CAST.get(), false, rod, baseTime / 2, prefix + "vanilla/" + name + "_rod_cast"); }
    }

    private ItemLike getExternalItem(String modid, String path) {
        ItemLike item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(modid, path));
        return item == Items.AIR ? null : item;
    }

    private void registerFtbMetalById(RecipeOutput output, Fluid fluid, int temp,
                                      String name, boolean hasRaw) {
        String prefix = "ftbmaterials_compat/";
        int bt = 100;

        addMeltingTag(output, "storage_blocks/" + name,  fluid, 810, temp, bt * 2,       prefix + "metal/" + name + "/block");
        if (hasRaw) {
            addMeltingTag(output, "storage_blocks/raw_" + name, fluid, 810, temp, (int)(bt * 2.5), prefix + "metal/" + name + "/raw_block");
            addMeltingTag(output, "raw_materials/" + name,      fluid,  90, temp, (int)(bt * 1.5), prefix + "metal/" + name + "/raw");
        }
        addMeltingTag(output, "ingots/"  + name, fluid,  90, temp, bt,       prefix + "metal/" + name + "/ingot");
        addMeltingTag(output, "nuggets/" + name, fluid,  10, temp, bt / 3,   prefix + "metal/" + name + "/nugget");
        addMeltingTag(output, "dusts/"   + name, fluid,  90, temp, bt,       prefix + "metal/" + name + "/dust");
        addMeltingTag(output, "plates/"  + name, fluid,  90, temp, bt,       prefix + "metal/" + name + "/plate");
        addMeltingTag(output, "gears/"   + name, fluid, 360, temp, bt * 2,   prefix + "metal/" + name + "/gear");
        addMeltingTag(output, "rods/"    + name, fluid,  45, temp, bt / 2,   prefix + "metal/" + name + "/rod");

        ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluid);
        ResourceLocation blockId  = ResourceLocation.fromNamespaceAndPath("ftbmaterials", name + "_block");
        ResourceLocation ingotId  = ResourceLocation.fromNamespaceAndPath("ftbmaterials", name + "_ingot");
        ResourceLocation nuggetId = ResourceLocation.fromNamespaceAndPath("ftbmaterials", name + "_nugget");
        ResourceLocation plateId  = ResourceLocation.fromNamespaceAndPath("ftbmaterials", name + "_plate");
        ResourceLocation gearId   = ResourceLocation.fromNamespaceAndPath("ftbmaterials", name + "_gear");
        ResourceLocation rodId    = ResourceLocation.fromNamespaceAndPath("ftbmaterials", name + "_rod");

        addCastingTableById(output, fluidId,  90, ModItems.INGOT_CAST.get(),  false, ingotId,  bt,       prefix + "metal/" + name + "/ingot_cast");
        addCastingTableById(output, fluidId,  10, ModItems.NUGGET_CAST.get(), false, nuggetId, bt / 3,   prefix + "metal/" + name + "/nugget_cast");
        addCastingTableById(output, fluidId,  90, ModItems.PLATE_CAST.get(),  false, plateId,  bt,       prefix + "metal/" + name + "/plate_cast");
        addCastingTableById(output, fluidId, 360, ModItems.GEAR_CAST.get(),   false, gearId,   bt * 2,   prefix + "metal/" + name + "/gear_cast");
        addCastingTableById(output, fluidId,  45, ModItems.ROD_CAST.get(),    false, rodId,    bt / 2,   prefix + "metal/" + name + "/rod_cast");
        addCastingBasinById(output, fluidId, 810, blockId, bt * 2,                             prefix + "metal/" + name + "/block_cast");
    }

    private void addCastingTableById(RecipeOutput output, ResourceLocation fluidId, int fluidAmount,
                                     ItemLike castItem, boolean consumesCast,
                                     ResourceLocation resultId, int time, String savePath) {
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
        Ingredient castIngredient = castItem == null ? Ingredient.EMPTY : Ingredient.of(castItem);
        ModRecipes.CastingTableRecipe recipe = new ModRecipes.CastingTableRecipe(
                castIngredient, consumesCast,
                new FluidStack(fluid, fluidAmount),
                resultId, 1, time);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID,
                "smeltery/casting/table/" + savePath), recipe, null);
    }

    private void addCastingBasinById(RecipeOutput output, ResourceLocation fluidId, int fluidAmount,
                                     ResourceLocation resultId, int time, String savePath) {
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
        ModRecipes.CastingBasinRecipe recipe = new ModRecipes.CastingBasinRecipe(
                new FluidStack(fluid, fluidAmount),
                resultId, 1, time);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID,
                "smeltery/casting/basin/" + savePath), recipe, null);
    }

    private void registerMetal(RecipeOutput output, Fluid fluid, int temp, String name,
                               ItemLike block, ItemLike ingot, ItemLike raw, ItemLike nugget, ItemLike dust,
                               ItemLike plate, ItemLike gear, ItemLike rod, String prefix) {
        int baseTime = 100;

        addMeltingTag(output, "storage_blocks/" + name, fluid, 810, temp, baseTime * 2, prefix + "metal/" + name + "/block");
        if (raw != null && !name.equals("steel") && !name.equals("brass")) {

            addMeltingTag(output, "storage_blocks/raw_" + name, fluid, 810, temp, (int)(baseTime * 2.5), prefix + "metal/" + name + "/raw_block"); }
        addMeltingTag(output, "ingots/" + name, fluid, 90, temp, baseTime, prefix + "metal/" + name + "/ingot");
        if (raw != null && !name.equals("brass")) {

            addMeltingTag(output, "raw_materials/" + name, fluid, 90, temp, (int)(baseTime * 1.5), prefix + "metal/" + name + "/raw"); }
        addMeltingTag(output, "nuggets/" + name, fluid, 10, temp, baseTime / 3, prefix + "metal/" + name + "/nugget");
        if (dust != null) addMeltingTag(output, "dusts/" + name, fluid, 90, temp, baseTime, prefix + "metal/" + name + "/dust");
        if (plate != null) addMeltingTag(output, "plates/" + name, fluid, 90, temp, baseTime, prefix + "metal/" + name + "/plate");
        if (gear != null) addMeltingTag(output, "gears/" + name, fluid, 360, temp, baseTime * 2, prefix + "metal/" + name + "/gear");
        if (rod != null) addMeltingTag(output, "rods/" + name, fluid, 45, temp, baseTime / 2, prefix + "metal/" + name + "/rod");
        if (block != null) addCastingBasin(output, fluid, 810, block, baseTime * 2, prefix + "metal/" + name + "/block");
        if (ingot != null) addCastingTable(output, fluid, 90, ModItems.INGOT_CAST.get(), false, ingot, baseTime, prefix + "metal/" + name + "/ingot_cast");
        if (nugget != null) addCastingTable(output, fluid, 10, ModItems.NUGGET_CAST.get(), false, nugget, baseTime / 3, prefix + "metal/" + name + "/nugget_cast");
        if (plate != null) addCastingTable(output, fluid, 90, ModItems.PLATE_CAST.get(), false, plate, baseTime, prefix + "metal/" + name + "/plate_cast");
        if (gear != null) addCastingTable(output, fluid, 360, ModItems.GEAR_CAST.get(), false, gear, baseTime * 2, prefix + "metal/" + name + "/gear_cast");
        if (rod != null) addCastingTable(output, fluid, 45, ModItems.ROD_CAST.get(), false, rod, baseTime / 2, prefix + "metal/" + name + "/rod_cast");
    }

    private void registerGem(RecipeOutput output, Fluid fluid, int temp, String name, String blockTag, String gemTag, ItemLike block, ItemLike gem) {
        registerGem(output, fluid, temp, name, blockTag, gemTag, block, gem, 810);
    }

    private void registerGem(RecipeOutput output, Fluid fluid, int temp, String name, String blockTag, String gemTag, ItemLike block, ItemLike gem, int blockAmount) {
        int baseTime = 120;
        if (blockTag != null && !blockTag.isEmpty()) {
            addMeltingTag(output, blockTag, fluid, blockAmount, temp, baseTime * 2, "gem/" + name + "/block"); } else if (block != null) {
            addMeltingItem(output, block, fluid, blockAmount, temp, baseTime * 2, "gem/" + name + "/block"); } if (gemTag != null && !gemTag.isEmpty()) {
            addMeltingTag(output, gemTag, fluid, 90, temp, baseTime, "gem/" + name + "/gem"); } else if (gem != null) {
            addMeltingItem(output, gem, fluid, 90, temp, baseTime, "gem/" + name + "/gem"); }
        if (block != null) addCastingBasin(output, fluid, blockAmount, block, baseTime * 2, "gem/" + name + "/block");
        if (gem != null) addCastingTable(output, fluid, 90, ModItems.GEM_CAST.get(), false, gem, baseTime, "gem/" + name + "/gem_cast");
    }

    private void createCastRecipe(RecipeOutput output, String tagPath, ItemLike castResult, String savePath) {
        Ingredient tagIngredient = Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", tagPath)));
        ModRecipes.CastingTableRecipe recipe = new ModRecipes.CastingTableRecipe(tagIngredient, true, new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 90), new ItemStack(castResult), 60);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/casts/" + savePath), recipe, null);
    }

    private void createRodCastRecipe(RecipeOutput output) {
        Ingredient rodOrStick = Ingredient.of(Items.STICK, Items.BLAZE_ROD);
        ModRecipes.CastingTableRecipe recipe = new ModRecipes.CastingTableRecipe(
                rodOrStick, true,
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 90),
                new ItemStack(ModItems.ROD_CAST.get()), 60);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/casts/rod_cast"), recipe, null);
    }

    private void addMeltingTag(RecipeOutput output, String tagPath, Fluid fluid, int amount, int temperature, int time, String savePath) {
        Ingredient ingredient = Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", tagPath)));
        FluidStack fuel = fuelForTemp(temperature);
        ModRecipes.MeltingRecipe recipe = new ModRecipes.MeltingRecipe(ingredient, new FluidStack(fluid, amount), fuel, temperature, time);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/melting/" + savePath), recipe, null);
    }

    private void addMeltingItem(RecipeOutput output, ItemLike item, Fluid fluid, int amount, int temperature, int time, String savePath) {
        Ingredient ingredient = Ingredient.of(item);
        FluidStack fuel = fuelForTemp(temperature);
        ModRecipes.MeltingRecipe recipe = new ModRecipes.MeltingRecipe(ingredient, new FluidStack(fluid, amount), fuel, temperature, time);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/melting/" + savePath), recipe, null);
    }

    private void addMeltingDamageable(RecipeOutput output, ItemLike item, Fluid fluid, int amount, int temperature, int time, String savePath) {
        Ingredient ingredient = Ingredient.of(item);
        FluidStack fuel = fuelForTemp(temperature);
        ModRecipes.MeltingRecipe recipe = new ModRecipes.MeltingRecipe(ingredient, new FluidStack(fluid, amount), fuel, temperature, time, true);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/melting/" + savePath), recipe, null);
    }

    private void addToolArmorMelting(RecipeOutput output) {
        Fluid iron = HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRON).source.get();
        Fluid gold = HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GOLD).source.get();
        Fluid netherite = HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NETHERITE).source.get();
        Fluid diamond = ModFluids.MOLTEN_DIAMOND.source.get();

        addMeltingDamageable(output, Items.IRON_PICKAXE, iron, 270, 900, 200, "tools/iron_pickaxe");
        addMeltingDamageable(output, Items.IRON_AXE,     iron, 270, 900, 200, "tools/iron_axe");
        addMeltingDamageable(output, Items.IRON_SWORD,   iron, 180, 900, 150, "tools/iron_sword");
        addMeltingDamageable(output, Items.IRON_SHOVEL,  iron,  90, 900, 100, "tools/iron_shovel");
        addMeltingDamageable(output, Items.IRON_HOE,     iron, 180, 900, 150, "tools/iron_hoe");
        addMeltingDamageable(output, Items.IRON_HELMET,     iron, 450, 900, 250, "armor/iron_helmet");
        addMeltingDamageable(output, Items.IRON_CHESTPLATE, iron, 720, 900, 350, "armor/iron_chestplate");
        addMeltingDamageable(output, Items.IRON_LEGGINGS,   iron, 630, 900, 300, "armor/iron_leggings");
        addMeltingDamageable(output, Items.IRON_BOOTS,      iron, 360, 900, 200, "armor/iron_boots");
        addMeltingDamageable(output, Items.CHAINMAIL_HELMET,     iron, 450, 900, 250, "armor/chainmail_helmet");
        addMeltingDamageable(output, Items.CHAINMAIL_CHESTPLATE, iron, 720, 900, 350, "armor/chainmail_chestplate");
        addMeltingDamageable(output, Items.CHAINMAIL_LEGGINGS,   iron, 630, 900, 300, "armor/chainmail_leggings");
        addMeltingDamageable(output, Items.CHAINMAIL_BOOTS,      iron, 360, 900, 200, "armor/chainmail_boots");
        addMeltingDamageable(output, Items.GOLDEN_PICKAXE, gold, 270, 900, 200, "tools/golden_pickaxe");
        addMeltingDamageable(output, Items.GOLDEN_AXE,     gold, 270, 900, 200, "tools/golden_axe");
        addMeltingDamageable(output, Items.GOLDEN_SWORD,   gold, 180, 900, 150, "tools/golden_sword");
        addMeltingDamageable(output, Items.GOLDEN_SHOVEL,  gold,  90, 900, 100, "tools/golden_shovel");
        addMeltingDamageable(output, Items.GOLDEN_HOE,     gold, 180, 900, 150, "tools/golden_hoe");
        addMeltingDamageable(output, Items.GOLDEN_HELMET,     gold, 450, 900, 250, "armor/golden_helmet");
        addMeltingDamageable(output, Items.GOLDEN_CHESTPLATE, gold, 720, 900, 350, "armor/golden_chestplate");
        addMeltingDamageable(output, Items.GOLDEN_LEGGINGS,   gold, 630, 900, 300, "armor/golden_leggings");
        addMeltingDamageable(output, Items.GOLDEN_BOOTS,      gold, 360, 900, 200, "armor/golden_boots");
        addMeltingDamageable(output, Items.DIAMOND_PICKAXE, diamond, 270, 1400, 200, "tools/diamond_pickaxe");
        addMeltingDamageable(output, Items.DIAMOND_AXE,     diamond, 270, 1400, 200, "tools/diamond_axe");
        addMeltingDamageable(output, Items.DIAMOND_SWORD,   diamond, 180, 1400, 150, "tools/diamond_sword");
        addMeltingDamageable(output, Items.DIAMOND_SHOVEL,  diamond,  90, 1400, 100, "tools/diamond_shovel");
        addMeltingDamageable(output, Items.DIAMOND_HOE,     diamond, 180, 1400, 150, "tools/diamond_hoe");
        addMeltingDamageable(output, Items.DIAMOND_HELMET,     diamond, 450, 1400, 250, "armor/diamond_helmet");
        addMeltingDamageable(output, Items.DIAMOND_CHESTPLATE, diamond, 720, 1400, 350, "armor/diamond_chestplate");
        addMeltingDamageable(output, Items.DIAMOND_LEGGINGS,   diamond, 630, 1400, 300, "armor/diamond_leggings");
        addMeltingDamageable(output, Items.DIAMOND_BOOTS,      diamond, 360, 1400, 200, "armor/diamond_boots");
        addMeltingDamageable(output, Items.NETHERITE_PICKAXE, netherite, 90, 2000, 300, "tools/netherite_pickaxe");
        addMeltingDamageable(output, Items.NETHERITE_AXE,     netherite, 90, 2000, 300, "tools/netherite_axe");
        addMeltingDamageable(output, Items.NETHERITE_SWORD,   netherite, 90, 2000, 300, "tools/netherite_sword");
        addMeltingDamageable(output, Items.NETHERITE_SHOVEL,  netherite, 90, 2000, 300, "tools/netherite_shovel");
        addMeltingDamageable(output, Items.NETHERITE_HOE,     netherite, 90, 2000, 300, "tools/netherite_hoe");
        addMeltingDamageable(output, Items.NETHERITE_HELMET,     netherite, 90, 2000, 350, "armor/netherite_helmet");
        addMeltingDamageable(output, Items.NETHERITE_CHESTPLATE, netherite, 90, 2000, 350, "armor/netherite_chestplate");
        addMeltingDamageable(output, Items.NETHERITE_LEGGINGS,   netherite, 90, 2000, 350, "armor/netherite_leggings");
        addMeltingDamageable(output, Items.NETHERITE_BOOTS,      netherite, 90, 2000, 350, "armor/netherite_boots");
    }

    private void addCastingBasin(RecipeOutput output, Fluid fluid, int amount, ItemLike result, int time, String savePath) {
        ModRecipes.CastingBasinRecipe recipe = new ModRecipes.CastingBasinRecipe(new FluidStack(fluid, amount), new ItemStack(result), time);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/basin/" + savePath), recipe, null);
    }

    private void addCastingTable(RecipeOutput output, Fluid fluid, int fluidAmount, ItemLike castItem, boolean consumesCast, ItemLike resultItem, int time, String savePath) {
        Ingredient castIngredient = castItem == null ? Ingredient.EMPTY : Ingredient.of(castItem);
        ModRecipes.CastingTableRecipe recipe = new ModRecipes.CastingTableRecipe(castIngredient, consumesCast, new FluidStack(fluid, fluidAmount), new ItemStack(resultItem), time);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/table/" + savePath), recipe, null);
    }

    private void generateDecorative(RecipeOutput output, Block baseBlock, Block slab, Block stairs, Block wall) {
        String baseName = BuiltInRegistries.BLOCK.getKey(baseBlock).getPath();
        if (slab != null) ShapedRecipeBuilder.shaped(RecipeCategory.MISC, slab, 6).pattern("BBB").define('B', baseBlock).unlockedBy("has_" + baseName, has(baseBlock)).save(output, ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "decoration/" + BuiltInRegistries.BLOCK.getKey(slab).getPath()));
        if (stairs != null) ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stairs, 4).pattern("B  ").pattern("BB ").pattern("BBB").define('B', baseBlock).unlockedBy("has_" + baseName, has(baseBlock)).save(output, ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "decoration/" + BuiltInRegistries.BLOCK.getKey(stairs).getPath()));
        if (wall != null) ShapedRecipeBuilder.shaped(RecipeCategory.MISC, wall, 6).pattern("BBB").pattern("BBB").define('B', baseBlock).unlockedBy("has_" + baseName, has(baseBlock)).save(output, ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "decoration/" + BuiltInRegistries.BLOCK.getKey(wall).getPath()));
    }

    private void addAlloyRecipe(RecipeOutput output, List<FluidStack> inputs, FluidStack result, int temperature, String savePath) {
        com.titammods.recipe.AlloyRecipe recipe = new com.titammods.recipe.AlloyRecipe(inputs, result, temperature);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/alloying/" + savePath), recipe, null);
    }

    private void addEntityMelting(RecipeOutput output, net.minecraft.world.entity.EntityType<?> entityType,
                                  FluidStack result, int damage, String savePath) {
        ModRecipes.EntityMeltingRecipe recipe = new ModRecipes.EntityMeltingRecipe(entityType, result, damage);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/entity_melting/" + savePath), recipe, null);
    }
}