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
        //decoration
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

        //casts
        createCastRecipe(output, "ingots", ModItems.INGOT_CAST.get(), "ingot_cast");
        createCastRecipe(output, "nuggets", ModItems.NUGGET_CAST.get(), "nugget_cast");
        createCastRecipe(output, "gems", ModItems.GEM_CAST.get(), "gem_cast");
        createCastRecipe(output, "plates", ModItems.PLATE_CAST.get(), "plate_cast");
        createCastRecipe(output, "gears", ModItems.GEAR_CAST.get(), "gear_cast");
        createCastRecipe(output, "rods", ModItems.ROD_CAST.get(), "rod_cast");

        //molten vanilla
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRON).source.get(), 1538, "iron", Items.IRON_BLOCK, Items.IRON_INGOT, Items.RAW_IRON, Items.IRON_NUGGET, null, null, null, null, "");
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GOLD).source.get(), 1060, "gold", Items.GOLD_BLOCK, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_NUGGET, null, null, null, null, "");
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 1080, "copper", Items.COPPER_BLOCK, Items.COPPER_INGOT, Items.RAW_COPPER, null, null, null, null, null, "");
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_STEEL).source.get(), 1000, "steel", ModBlocks.STEEL_BLOCK.get(), ModItems.STEEL_INGOT.get(), ModItems.RAW_STEEL.get(), ModItems.STEEL_NUGGET.get(), ModItems.STEEL_POWDER.get(), null, null, null, "");
        registerMetal(output, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NETHERITE).source.get(), 2000, "netherite", Items.NETHERITE_BLOCK, Items.NETHERITE_INGOT, null, null, null, null, null, null, "");

        //molten mod
        registerMetal(output, ModFluids.MOLTEN_COBALT.source.get(), 1100, "cobalt", ModBlocks.COBALT_BLOCK.get(), ModItems.COBALT_INGOT.get(), ModItems.RAW_COBALT.get(), ModItems.COBALT_NUGGET.get(), ModItems.COBALT_POWDER.get(), null, null, null, "");

        //gems
        registerGem(output, ModFluids.MOLTEN_DIAMOND.source.get(), 1400, "diamond", "storage_blocks/diamond", "gems/diamond", Items.DIAMOND_BLOCK, Items.DIAMOND);
        registerGem(output, ModFluids.MOLTEN_EMERALD.source.get(), 1200, "emerald", "storage_blocks/emerald", "gems/emerald", Items.EMERALD_BLOCK, Items.EMERALD);
        registerGem(output, ModFluids.MOLTEN_AMETHYST.source.get(), 1000, "amethyst", "", "gems/amethyst", Items.AMETHYST_BLOCK, Items.AMETHYST_SHARD);
        registerGem(output, ModFluids.MOLTEN_QUARTZ.source.get(), 800, "quartz", "", "gems/quartz", Items.QUARTZ_BLOCK, Items.QUARTZ);

        //alltheores compat
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

        //ftb materials compat
        RecipeOutput ftbOutput = output.withConditions(modLoaded("ftbmaterials"));
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ALUMINUM).source.get(), 660, "aluminum", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRASS).source.get(), 930, "brass", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRONZE).source.get(), 950, "bronze", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LEAD).source.get(), 327, "lead", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(), 1450, "nickel", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_OSMIUM).source.get(), 1990, "osmium", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_PLATINUM).source.get(), 1768, "platinum", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SILVER).source.get(), 960, "silver", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_TIN).source.get(), 230, "tin", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_URANIUM).source.get(), 1130, "uranium", "ftbmaterials", "ftbmaterials_compat/");
        registerExternalMetal(ftbOutput, HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ZINC).source.get(), 419, "zinc", "ftbmaterials", "ftbmaterials_compat/");

        // 1. LATÃO (BRASS) - 1 Cobre + 1 Zinco = 2 Latão
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 100),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ZINC).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRASS).source.get(), 200),
                650,
                "brass");

        // 2. BRONZE - 3 Cobre + 1 Estanho = 4 Bronze
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 300),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_TIN).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRONZE).source.get(), 400),
                700,
                "bronze");

        // 3. ELECTRUM - 1 Ouro + 1 Prata = 2 Electrum
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GOLD).source.get(), 100),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SILVER).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ELECTRUM).source.get(), 200),
                760,
                "electrum");

        // 4. INVAR - 2 Ferro + 1 Níquel = 3 Invar
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRON).source.get(), 200),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_INVAR).source.get(), 300),
                900,
                "invar");

        // 5. CONSTANTAN - 1 Cobre + 1 Níquel = 2 Constantan
        addAlloyRecipe(output,
                List.of(
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 100),
                        new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(), 100)
                ),
                new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CONSTANTAN).source.get(), 200),
                920,
                "constantan");

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

    private void registerMetal(RecipeOutput output, Fluid fluid, int temp, String name,
                               ItemLike block, ItemLike ingot, ItemLike raw, ItemLike nugget, ItemLike dust,
                               ItemLike plate, ItemLike gear, ItemLike rod, String prefix) {
        int baseTime = 100;

        addMeltingTag(output, "storage_blocks/" + name, fluid, 900, temp, baseTime * 2, prefix + "metal/" + name + "/block");
        if (raw != null && !name.equals("steel") && !name.equals("brass")) {

            addMeltingTag(output, "storage_blocks/raw_" + name, fluid, 900, temp, (int)(baseTime * 2.5), prefix + "metal/" + name + "/raw_block"); }
        addMeltingTag(output, "ingots/" + name, fluid, 90, temp, baseTime, prefix + "metal/" + name + "/ingot");
        if (raw != null && !name.equals("brass")) {

            addMeltingTag(output, "raw_materials/" + name, fluid, 90, temp, (int)(baseTime * 1.5), prefix + "metal/" + name + "/raw"); }
        addMeltingTag(output, "nuggets/" + name, fluid, 10, temp, baseTime / 3, prefix + "metal/" + name + "/nugget");
        if (dust != null) addMeltingTag(output, "dusts/" + name, fluid, 90, temp, baseTime, prefix + "metal/" + name + "/dust");
        if (plate != null) addMeltingTag(output, "plates/" + name, fluid, 90, temp, baseTime, prefix + "metal/" + name + "/plate");
        if (gear != null) addMeltingTag(output, "gears/" + name, fluid, 360, temp, baseTime * 2, prefix + "metal/" + name + "/gear");
        if (rod != null) addMeltingTag(output, "rods/" + name, fluid, 45, temp, baseTime / 2, prefix + "metal/" + name + "/rod");
        if (block != null) addCastingBasin(output, fluid, 900, block, baseTime * 2, prefix + "metal/" + name + "/block");
        if (ingot != null) addCastingTable(output, fluid, 90, ModItems.INGOT_CAST.get(), false, ingot, baseTime, prefix + "metal/" + name + "/ingot_cast");
        if (nugget != null) addCastingTable(output, fluid, 10, ModItems.NUGGET_CAST.get(), false, nugget, baseTime / 3, prefix + "metal/" + name + "/nugget_cast");
        if (plate != null) addCastingTable(output, fluid, 90, ModItems.PLATE_CAST.get(), false, plate, baseTime, prefix + "metal/" + name + "/plate_cast");
        if (gear != null) addCastingTable(output, fluid, 360, ModItems.GEAR_CAST.get(), false, gear, baseTime * 2, prefix + "metal/" + name + "/gear_cast");
        if (rod != null) addCastingTable(output, fluid, 45, ModItems.ROD_CAST.get(), false, rod, baseTime / 2, prefix + "metal/" + name + "/rod_cast");
    }

    private void registerGem(RecipeOutput output, Fluid fluid, int temp, String name, String blockTag, String gemTag, ItemLike block, ItemLike gem) {
        int baseTime = 120;
        if (blockTag != null && !blockTag.isEmpty()) {
            addMeltingTag(output, blockTag, fluid, 900, temp, baseTime * 2, "gem/" + name + "/block"); } else if (block != null) {
            addMeltingItem(output, block, fluid, 900, temp, baseTime * 2, "gem/" + name + "/block"); } if (gemTag != null && !gemTag.isEmpty()) {
            addMeltingTag(output, gemTag, fluid, 100, temp, baseTime, "gem/" + name + "/gem"); } else if (gem != null) {
            addMeltingItem(output, gem, fluid, 100, temp, baseTime, "gem/" + name + "/gem"); }
        if (block != null) addCastingBasin(output, fluid, 900, block, baseTime * 2, "gem/" + name + "/block");
        if (gem != null) addCastingTable(output, fluid, 100, ModItems.GEM_CAST.get(), false, gem, baseTime, "gem/" + name + "/gem_cast");
    }

    private void createCastRecipe(RecipeOutput output, String tagPath, ItemLike castResult, String savePath) {
        Ingredient tagIngredient = Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", tagPath)));
        ModRecipes.CastingTableRecipe recipe = new ModRecipes.CastingTableRecipe(tagIngredient, true, new FluidStack(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(), 90), new ItemStack(castResult), 60);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/casts/" + savePath), recipe, null);
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
        if (slab != null) ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, slab, 6).pattern("BBB").define('B', baseBlock).unlockedBy("has_" + baseName, has(baseBlock)).save(output, ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "decoration/" + BuiltInRegistries.BLOCK.getKey(slab).getPath()));
        if (stairs != null) ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4).pattern("B  ").pattern("BB ").pattern("BBB").define('B', baseBlock).unlockedBy("has_" + baseName, has(baseBlock)).save(output, ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "decoration/" + BuiltInRegistries.BLOCK.getKey(stairs).getPath()));
        if (wall != null) ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wall, 6).pattern("BBB").pattern("BBB").define('B', baseBlock).unlockedBy("has_" + baseName, has(baseBlock)).save(output, ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "decoration/" + BuiltInRegistries.BLOCK.getKey(wall).getPath()));
    }

    private void addAlloyRecipe(RecipeOutput output, List<FluidStack> inputs, FluidStack result, int temperature, String savePath) {
        com.titammods.recipe.AlloyRecipe recipe = new com.titammods.recipe.AlloyRecipe(inputs, result, temperature);
        output.accept(ResourceLocation.fromNamespaceAndPath(TitamMods.MODID, "smeltery/alloying/" + savePath), recipe, null);
    }
}
