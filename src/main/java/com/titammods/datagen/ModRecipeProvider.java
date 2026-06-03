package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.registry.HephaestusFluids;
import com.titammods.setup.ModFluids;
import com.titammods.setup.ModItems;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRON).source.get(),
                900, "iron", Items.IRON_BLOCK, Items.IRON_INGOT, Items.RAW_IRON, Items.IRON_NUGGET, null, null, null, null, "");

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GOLD).source.get(),
                900, "gold", Items.GOLD_BLOCK, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_NUGGET, null, null, null, null, "");

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(),
                900, "copper", Items.COPPER_BLOCK, Items.COPPER_INGOT, Items.RAW_COPPER, null, null, null, null, null, "");

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_STEEL).source.get(),
                1000, "steel", ModBlocks.STEEL_BLOCK.get(), ModItems.STEEL_INGOT.get(), ModItems.RAW_STEEL.get(),
                ModItems.STEEL_NUGGET.get(), ModItems.STEEL_POWDER.get(), null, null, null, "");

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NETHERITE).source.get(),
                2000, "netherite", Items.NETHERITE_BLOCK, Items.NETHERITE_INGOT, null, null, null, null, null, null, "");

        registerMetal(ModFluids.MOLTEN_COBALT.source.get(), 1100, "cobalt",
                ModBlocks.COBALT_BLOCK.get(), ModItems.COBALT_INGOT.get(), ModItems.RAW_COBALT.get(),
                ModItems.COBALT_NUGGET.get(), ModItems.COBALT_POWDER.get(), null, null, null, "");

        registerGem(ModFluids.MOLTEN_DIAMOND.source.get(),  1400, "diamond", "storage_blocks/diamond", "gems/diamond",  Items.DIAMOND_BLOCK,  Items.DIAMOND);
        registerGem(ModFluids.MOLTEN_EMERALD.source.get(),  1200, "emerald", "storage_blocks/emerald", "gems/emerald",  Items.EMERALD_BLOCK,  Items.EMERALD);
        registerGem(ModFluids.MOLTEN_AMETHYST.source.get(), 1000, "amethyst", "", "gems/amethyst", Items.AMETHYST_BLOCK, Items.AMETHYST_SHARD);
        registerGem(ModFluids.MOLTEN_QUARTZ.source.get(),    800, "quartz", "", "gems/quartz",   Items.QUARTZ_BLOCK,   Items.QUARTZ);

        registerAllTheOresCompat();
        registerFtbMaterialsCompat();
        addVanillaRecipes();

        createCastRecipe("ingots",   ModItems.INGOT_CAST.get(),  "ingot_cast");
        createCastRecipe("nuggets",  ModItems.NUGGET_CAST.get(), "nugget_cast");
        createCastRecipe("gems",     ModItems.GEM_CAST.get(),    "gem_cast");
        createCastRecipe("plates",   ModItems.PLATE_CAST.get(),  "plate_cast");
        createCastRecipe("gears",    ModItems.GEAR_CAST.get(),   "gear_cast");
        createCastRecipe("rods",     ModItems.ROD_CAST.get(),    "rod_cast");

    }

    private void addVanillaRecipes() {
        var items = this.registries.lookupOrThrow(Registries.ITEM);

        //Forge Brick
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModItems.FORGE_BRICK.get(), 16)
                .pattern("CCS").pattern("GLS").pattern("GCC")
                .define('G', Items.GRAVEL).define('C', Items.COAL)
                .define('S', Items.SAND).define('L', Items.CLAY_BALL)
                .unlockedBy("has_clay", this.has(Items.CLAY_BALL))
                .save(this.output, rk("forge_brick"));

        //Seared Bricks
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_BRICKS.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModItems.FORGE_BRICK.get())
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_bricks"));

        //Seared Cobble
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_COBBLE.get(), 4)
                .pattern("BS").pattern("SB")
                .define('B', ModItems.FORGE_BRICK.get()).define('S', Items.COBBLESTONE)
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_cobble"));

        //Seared Ingot Tank
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_INGOT_TANK.get())
                .pattern("BBB").pattern("B B").pattern("BBB")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_ingot_tank"));

        //Seared Fuel Tank
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_FUEL_TANK.get())
                .pattern("BBB").pattern("BLB").pattern("BBB")
                .define('B', ModBlocks.SEARED_BRICKS.get()).define('L', Items.LAVA_BUCKET)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_fuel_tank"));

        //Seared Melter
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_MELTER.get())
                .pattern("BTB").pattern("BFB").pattern("BSB")
                .define('B', ModItems.FORGE_BRICK.get())
                .define('T', ModBlocks.SEARED_INGOT_TANK.get())
                .define('S', ModBlocks.SEARED_BRICKS.get())
                .define('F', Items.BLAST_FURNACE)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_melter"));

        //Seared Faucet
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_FAUCET.get(), 2)
                .pattern("B B").pattern(" C ")
                .define('B', ModBlocks.SEARED_BRICKS.get()).define('C', Items.COPPER_INGOT)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_faucet"));

        //Seared Table
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_TABLE.get())
                .pattern("BBB").pattern("B B").pattern("B B")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_table"));

        //Seared Basin
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_BASIN.get())
                .pattern("B B").pattern("B B").pattern("BBB")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_basin"));

        //Decorativos
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_SMALL_BRICKS.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_small_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_SQUARE_BRICKS.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModBlocks.SEARED_SMALL_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_square_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_TILE.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModBlocks.SEARED_STONE.get())
                .unlockedBy("has_seared_stone", this.has(ModBlocks.SEARED_STONE.get()))
                .save(this.output, rk("seared_tile"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_FANCY_BRICKS.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModBlocks.SEARED_TILE.get())
                .unlockedBy("has_seared_tile", this.has(ModBlocks.SEARED_TILE.get()))
                .save(this.output, rk("seared_fancy_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_TRIANGLE_BRICKS.get(), 4)
                .pattern("BS").pattern("SB")
                .define('B', ModBlocks.SEARED_BRICKS.get()).define('S', ModBlocks.SEARED_STONE.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_triangle_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_ROAD.get())
                .pattern("B").pattern("B")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_road"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SEARED_COBBLE.get()),
                        RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
                        ModBlocks.SEARED_STONE.get(), 0.1f, 200)
                .unlockedBy("has_seared_cobble", this.has(ModBlocks.SEARED_COBBLE.get()))
                .save(this.output, rk("seared_stone_from_smelting"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SEARED_STONE.get()),
                        RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
                        ModBlocks.SEARED_PAVER.get(), 0.1f, 200)
                .unlockedBy("has_seared_stone", this.has(ModBlocks.SEARED_STONE.get()))
                .save(this.output, rk("seared_paver_from_smelting"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SEARED_BRICKS.get()),
                        RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
                        ModBlocks.SEARED_CRACKED_BRICKS.get(), 0.1f, 200)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_cracked_bricks_from_smelting"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SEARED_SMALL_BRICKS.get()),
                        RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
                        ModBlocks.SEARED_CREEPER.get(), 0.1f, 200)
                .unlockedBy("has_seared_small_bricks", this.has(ModBlocks.SEARED_SMALL_BRICKS.get()))
                .save(this.output, rk("seared_creeper_from_smelting"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_CHUTE.get(), 1)
                .pattern("BGB").pattern("B B").pattern("BGB")
                .define('B', ModItems.FORGE_BRICK.get())
                .define('G', net.minecraft.tags.ItemTags.create(Identifier.fromNamespaceAndPath("c", "ingots/copper")))
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_chute"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SEARED_DRAIN.get(), 1)
                .pattern("BBB").pattern("G G").pattern("BBB")
                .define('B', ModItems.FORGE_BRICK.get())
                .define('G', net.minecraft.tags.ItemTags.create(Identifier.fromNamespaceAndPath("c", "ingots/copper")))
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_drain"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMELTERY_CONTROLLER.get(), 1)
                .pattern("BBB").pattern("CSH").pattern("BTB")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .define('C', ModBlocks.SEARED_CHUTE.get())
                .define('H', ModBlocks.SEARED_DRAIN.get())
                .define('T', ModBlocks.SEARED_FUEL_TANK.get())
                .define('S', Items.BLAST_FURNACE)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("smeltery_controller"));
    }

    private void registerAllTheOresCompat() {
        ICondition cond = new ModLoadedCondition("alltheores");
        String prefix = "alltheores_compat/";

        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ALUMINUM).source.get(),   660, "aluminum",  "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRASS).source.get(),      930, "brass",     "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRONZE).source.get(),     950, "bronze",    "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CONSTANTAN).source.get(),1220, "constantan","alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ELECTRUM).source.get(),  1000, "electrum",  "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ENDERIUM).source.get(),  1450, "enderium",  "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_INVAR).source.get(),     1420, "invar",     "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRIDIUM).source.get(),   1440, "iridium",   "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LEAD).source.get(),       327, "lead",      "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LUMIUM).source.get(),    1000, "lumium",    "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(),    1450, "nickel",    "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_OSMIUM).source.get(),    1990, "osmium",    "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_PLATINUM).source.get(),  1768, "platinum",  "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SIGNALUM).source.get(),  1000, "signalum",  "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SILVER).source.get(),     960, "silver",    "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_TIN).source.get(),        230, "tin",       "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_URANIUM).source.get(),   1130, "uranium",   "alltheores", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ZINC).source.get(),       419, "zinc",      "alltheores", prefix, cond);
    }

    private void registerFtbMaterialsCompat() {
        ICondition cond = new ModLoadedCondition("ftbmaterials");
        String prefix = "ftbmaterials_compat/";

        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ALUMINUM).source.get(),    660, "aluminum",   true,  prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRONZE).source.get(),      950, "bronze",     true,  prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_CONSTANTAN).source.get(), 1220, "constantan", true,  prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRIDIUM).source.get(),    2440, "iridium",    true,  prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LEAD).source.get(),        327, "lead",       true,  prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(),     1450, "nickel",     true,  prefix, cond);
        //registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_OSMIUM).source.get(),     1990, "osmium",     true,  prefix, cond);
        //registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_PLATINUM).source.get(),   1768, "platinum",   true,  prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SILVER).source.get(),      960, "silver",     true,  prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_TIN).source.get(),         230, "tin",        true,  prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_URANIUM).source.get(),    1130, "uranium",    true,  prefix, cond);
        //registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ZINC).source.get(),        419, "zinc",       true,  prefix, cond);
        //registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRASS).source.get(),       930, "brass",      false, prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ELECTRUM).source.get(),   1000, "electrum",   false, prefix, cond);
        registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_INVAR).source.get(),      1420, "invar",      false, prefix, cond);
        //registerExternalMetalById(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LUMIUM).source.get(),     1000, "lumium",     false, prefix, cond);
    }

    private void registerExternalMetalById(Fluid fluid, int temp, String name,
                                           boolean hasRaw, String prefix, ICondition cond) {
        int bt = 100;

        addMeltingTag("storage_blocks/" + name, fluid, 900, temp, bt * 2,
                prefix + "metal/" + name + "/block", cond);
        if (hasRaw && !name.equals("steel") && !name.equals("brass"))
            addMeltingTag("storage_blocks/raw_" + name, fluid, 900, temp, (int)(bt * 2.5),
                    prefix + "metal/" + name + "/raw_block", cond);
        addMeltingTag("ingots/" + name,   fluid,  90, temp, bt,
                prefix + "metal/" + name + "/ingot",  cond);
        if (hasRaw && !name.equals("brass"))
            addMeltingTag("raw_materials/" + name, fluid, 90, temp, (int)(bt * 1.5),
                    prefix + "metal/" + name + "/raw", cond);
        addMeltingTag("nuggets/" + name,  fluid,  10, temp, bt / 3,
                prefix + "metal/" + name + "/nugget", cond);
        addMeltingTag("dusts/" + name,    fluid,  90, temp, bt,
                prefix + "metal/" + name + "/dust",   cond);
        addMeltingTag("plates/" + name,   fluid,  90, temp, bt,
                prefix + "metal/" + name + "/plate",  cond);
        addMeltingTag("gears/" + name,    fluid, 360, temp, bt * 2,
                prefix + "metal/" + name + "/gear",   cond);
        addMeltingTag("rods/" + name,     fluid,  45, temp, bt / 2,
                prefix + "metal/" + name + "/rod",    cond);

        Identifier fluidId  = fluidId(fluid);
        Identifier blockId  = Identifier.fromNamespaceAndPath("ftbmaterials", name + "_block");
        Identifier ingotId  = Identifier.fromNamespaceAndPath("ftbmaterials", name + "_ingot");
        Identifier nuggetId = Identifier.fromNamespaceAndPath("ftbmaterials", name + "_nugget");
        Identifier plateId  = Identifier.fromNamespaceAndPath("ftbmaterials", name + "_plate");
        Identifier gearId   = Identifier.fromNamespaceAndPath("ftbmaterials", name + "_gear");
        Identifier rodId    = Identifier.fromNamespaceAndPath("ftbmaterials", name + "_rod");

        var ingotCast  = java.util.Optional.of(net.minecraft.world.item.crafting.Ingredient.of(ModItems.INGOT_CAST.get()));
        var nuggetCast = java.util.Optional.of(net.minecraft.world.item.crafting.Ingredient.of(ModItems.NUGGET_CAST.get()));
        var plateCast  = java.util.Optional.of(net.minecraft.world.item.crafting.Ingredient.of(ModItems.PLATE_CAST.get()));
        var gearCast   = java.util.Optional.of(net.minecraft.world.item.crafting.Ingredient.of(ModItems.GEAR_CAST.get()));
        var rodCast    = java.util.Optional.of(net.minecraft.world.item.crafting.Ingredient.of(ModItems.ROD_CAST.get()));

        this.output.withConditions(cond).accept(
                rk("smeltery/casting/table/" + prefix + "metal/" + name + "/ingot_cast"),
                new ModRecipes.CastingTableRecipe(ingotCast,  false, fluidId,  90, ingotId,  1, bt),      null);
        this.output.withConditions(cond).accept(
                rk("smeltery/casting/table/" + prefix + "metal/" + name + "/nugget_cast"),
                new ModRecipes.CastingTableRecipe(nuggetCast, false, fluidId,  10, nuggetId, 1, bt / 3),  null);
        this.output.withConditions(cond).accept(
                rk("smeltery/casting/table/" + prefix + "metal/" + name + "/plate_cast"),
                new ModRecipes.CastingTableRecipe(plateCast,  false, fluidId,  90, plateId,  1, bt),      null);
        this.output.withConditions(cond).accept(
                rk("smeltery/casting/table/" + prefix + "metal/" + name + "/gear_cast"),
                new ModRecipes.CastingTableRecipe(gearCast,   false, fluidId, 360, gearId,   1, bt * 2),  null);
        this.output.withConditions(cond).accept(
                rk("smeltery/casting/table/" + prefix + "metal/" + name + "/rod_cast"),
                new ModRecipes.CastingTableRecipe(rodCast,    false, fluidId,  45, rodId,    1, bt / 2),  null);

        this.output.withConditions(cond).accept(
                rk("smeltery/casting/basin/" + prefix + "metal/" + name + "/block_cast"),
                new ModRecipes.CastingBasinRecipe(fluidId, 900, blockId, 1, bt * 2),                      null);
    }

    private Identifier fuelId(int temp) {
        return temp <= 1000
                ? Identifier.fromNamespaceAndPath("minecraft", "lava")
                : Identifier.fromNamespaceAndPath("hephaestus", "molten_blaze");
    }

    private net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> rk(String path) {
        return ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(TitamMods.MODID, path));
    }

    private Identifier fluidId(Fluid fluid) {
        return BuiltInRegistries.FLUID.getResourceKey(fluid)
                .map(ResourceKey::identifier)
                .orElseThrow(() -> new IllegalStateException("Fluid not registered: " + fluid));
    }

    private void addMeltingTag(String tagPath, Fluid fluid, int amount, int temperature,
                               int time, String savePath, ICondition... conditions) {

        Ingredient ingredient = Ingredient.of(
                this.registries.lookupOrThrow(Registries.ITEM).getOrThrow(
                        ItemTags.create(Identifier.fromNamespaceAndPath("c", tagPath))
                )
        );

        ModRecipes.MeltingRecipe recipe = new ModRecipes.MeltingRecipe(
                ingredient, fluidId(fluid), amount, fuelId(temperature), 50, temperature, time);

        this.output.withConditions(conditions).accept(
                ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery/melting/" + savePath)),
                recipe, null);
    }

    private void addMeltingItem(ItemLike item, Fluid fluid, int amount, int temperature,
                                int time, String savePath, ICondition... conditions) {
        Ingredient ingredient = Ingredient.of(item);

        ModRecipes.MeltingRecipe recipe = new ModRecipes.MeltingRecipe(
                ingredient,
                fluidId(fluid), amount,
                fuelId(temperature), 50,
                temperature, time);

        this.output.withConditions(conditions).accept(
                ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery/melting/" + savePath)),
                recipe, null);
    }

    private void createCastRecipe(String tagPath, net.minecraft.world.level.ItemLike castResult, String savePath) {
        Ingredient tagIngredient = Ingredient.of(
                this.registries.lookupOrThrow(Registries.ITEM).getOrThrow(
                        net.minecraft.tags.ItemTags.create(Identifier.fromNamespaceAndPath("c", tagPath))
                )
        );
        Identifier copperFluidId = fluidId(
                com.titammods.registry.HephaestusFluids.SETS.get(
                        com.titammods.registry.HephaestusFluids.Material.MOLTEN_COPPER).source.get()
        );
        Identifier resultId = BuiltInRegistries.ITEM.getKey(castResult.asItem());
        ModRecipes.CastingTableRecipe recipe = new ModRecipes.CastingTableRecipe(
                java.util.Optional.of(tagIngredient), true,
                copperFluidId, 90,
                resultId, 1,
                60);
        this.output.accept(
                net.minecraft.resources.ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/casts/" + savePath)),
                recipe, null);
    }

    private void addCastingTable(Fluid fluid, int fluidAmount,
                                 ItemLike castItem, boolean consumesCast,
                                 ItemLike resultItem, int time,
                                 String savePath, ICondition... conditions) {
        Identifier fluidId  = fluidId(fluid);
        Identifier resultId = BuiltInRegistries.ITEM.getKey(resultItem.asItem());
        java.util.Optional<net.minecraft.world.item.crafting.Ingredient> castOpt =
                (castItem == null)
                        ? java.util.Optional.empty()
                        : java.util.Optional.of(net.minecraft.world.item.crafting.Ingredient.of(castItem));

        ModRecipes.CastingTableRecipe recipe = new ModRecipes.CastingTableRecipe(
                castOpt, consumesCast, fluidId, fluidAmount, resultId, 1, time);

        this.output.withConditions(conditions).accept(
                ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/table/" + savePath)),
                recipe, null);
    }

    private void addCastingBasin(Fluid fluid, int fluidAmount,
                                 ItemLike resultItem, int time,
                                 String savePath, ICondition... conditions) {
        Identifier fluidId  = fluidId(fluid);
        Identifier resultId = BuiltInRegistries.ITEM.getKey(resultItem.asItem());

        ModRecipes.CastingBasinRecipe recipe = new ModRecipes.CastingBasinRecipe(
                fluidId, fluidAmount, resultId, 1, time);

        this.output.withConditions(conditions).accept(
                ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/basin/" + savePath)),
                recipe, null);
    }

    private void registerMetal(Fluid fluid, int temp, String name,
                               ItemLike block, ItemLike ingot, ItemLike raw, ItemLike nugget,
                               ItemLike dust, ItemLike plate, ItemLike gear, ItemLike rod,
                               String prefix, ICondition... conditions) {
        int bt = 100;

        addMeltingTag("storage_blocks/" + name, fluid, 900, temp, bt * 2,        prefix + "metal/" + name + "/block",    conditions);
        if (raw != null && !name.equals("steel") && !name.equals("brass"))
            addMeltingTag("storage_blocks/raw_" + name, fluid, 900, temp, (int)(bt * 2.5), prefix + "metal/" + name + "/raw_block", conditions);
        addMeltingTag("ingots/" + name,           fluid,  90, temp, bt,            prefix + "metal/" + name + "/ingot",   conditions);
        if (raw != null && !name.equals("brass"))
            addMeltingTag("raw_materials/" + name, fluid,  90, temp, (int)(bt * 1.5), prefix + "metal/" + name + "/raw",      conditions);
        if (nugget != null) addMeltingTag("nuggets/" + name, fluid,  10, temp, bt / 3,  prefix + "metal/" + name + "/nugget", conditions);
        if (dust   != null) addMeltingTag("dusts/"   + name, fluid,  90, temp, bt,      prefix + "metal/" + name + "/dust",   conditions);
        if (plate  != null) addMeltingTag("plates/"  + name, fluid,  90, temp, bt,      prefix + "metal/" + name + "/plate",  conditions);
        if (gear   != null) addMeltingTag("gears/"   + name, fluid, 360, temp, bt * 2,  prefix + "metal/" + name + "/gear",   conditions);
        if (rod    != null) addMeltingTag("rods/"    + name, fluid,  45, temp, bt / 2,  prefix + "metal/" + name + "/rod",    conditions);

        if (ingot  != null) addCastingTable(fluid,  90, ModItems.INGOT_CAST.get(),  false, ingot,  bt,       prefix + "metal/" + name + "/ingot_cast",  conditions);
        if (nugget != null) addCastingTable(fluid,  10, ModItems.NUGGET_CAST.get(), false, nugget, bt / 3,   prefix + "metal/" + name + "/nugget_cast", conditions);
        if (plate  != null) addCastingTable(fluid,  90, ModItems.PLATE_CAST.get(),  false, plate,  bt,       prefix + "metal/" + name + "/plate_cast",  conditions);
        if (gear   != null) addCastingTable(fluid, 360, ModItems.GEAR_CAST.get(),   false, gear,   bt * 2,   prefix + "metal/" + name + "/gear_cast",   conditions);
        if (rod    != null) addCastingTable(fluid,  45, ModItems.ROD_CAST.get(),    false, rod,    bt / 2,   prefix + "metal/" + name + "/rod_cast",    conditions);
        if (block  != null) addCastingBasin(fluid, 900, block, bt * 2, prefix + "metal/" + name + "/block_cast", conditions);
    }

    private void registerGem(Fluid fluid, int temp, String name, String blockTag, String gemTag,
                             ItemLike block, ItemLike gem, ICondition... conditions) {
        int bt = 120;

        if (blockTag != null && !blockTag.isEmpty())
            addMeltingTag(blockTag, fluid, 900, temp, bt * 2, "gem/" + name + "/block", conditions);
        else if (block != null)
            addMeltingItem(block, fluid, 900, temp, bt * 2, "gem/" + name + "/block", conditions);

        if (gemTag != null && !gemTag.isEmpty())
            addMeltingTag(gemTag, fluid, 100, temp, bt, "gem/" + name + "/gem", conditions);
        else if (gem != null)
            addMeltingItem(gem, fluid, 100, temp, bt, "gem/" + name + "/gem", conditions);

        if (gem   != null) addCastingTable(fluid, 100, ModItems.GEM_CAST.get(), false, gem,  bt,       "gem/" + name + "/gem_cast",   conditions);
        if (block != null) addCastingBasin(fluid, 900, block, bt * 2, "gem/" + name + "/block_cast", conditions);
    }

    private void registerExternalMetal(Fluid fluid, int temp, String name,
                                       String modid, String prefix, ICondition condition) {
        ItemLike block  = getExternalItem(modid, name + "_block");
        ItemLike ingot  = getExternalItem(modid, name + "_ingot");
        ItemLike raw    = getExternalItem(modid, "raw_" + name);
        ItemLike nugget = getExternalItem(modid, name + "_nugget");
        ItemLike dust   = getExternalItem(modid, name + "_dust");
        ItemLike plate  = getExternalItem(modid, name + "_plate");
        ItemLike gear   = getExternalItem(modid, name + "_gear");
        ItemLike rod    = getExternalItem(modid, name + "_rod");
        registerMetal(fluid, temp, name, block, ingot, raw, nugget, dust, plate, gear, rod, prefix, condition);
    }

    private ItemLike getExternalItem(String modid, String path) {
        var item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(modid, path));
        return item == Items.AIR ? null : item;
    }
}