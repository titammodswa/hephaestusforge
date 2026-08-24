package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.registry.HephaestusFluids;
import com.titammods.setup.ModFluids;
import com.titammods.setup.ModItems;
import com.titammods.setup.ModBlocks;
import com.titammods.setup.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
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
        addMeltingMiscRecipes();

        generateDecorative(items, ModBlocks.SEARED_STONE.get(),
                ModBlocks.SEARED_STONE_SLAB.get(),
                ModBlocks.SEARED_STONE_STAIRS.get(),
                ModBlocks.SEARED_STONE_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_COBBLE.get(),
                ModBlocks.SEARED_COBBLE_SLAB.get(),
                ModBlocks.SEARED_COBBLE_STAIRS.get(),
                ModBlocks.SEARED_COBBLE_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_PAVER.get(),
                ModBlocks.SEARED_PAVER_SLAB.get(),
                ModBlocks.SEARED_PAVER_STAIRS.get(),
                ModBlocks.SEARED_PAVER_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_BRICKS.get(),
                ModBlocks.SEARED_BRICKS_SLAB.get(),
                ModBlocks.SEARED_BRICKS_STAIRS.get(),
                ModBlocks.SEARED_BRICKS_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_CRACKED_BRICKS.get(),
                ModBlocks.SEARED_CRACKED_BRICKS_SLAB.get(),
                ModBlocks.SEARED_CRACKED_BRICKS_STAIRS.get(),
                ModBlocks.SEARED_CRACKED_BRICKS_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_FANCY_BRICKS.get(),
                ModBlocks.SEARED_FANCY_BRICKS_SLAB.get(),
                ModBlocks.SEARED_FANCY_BRICKS_STAIRS.get(),
                ModBlocks.SEARED_FANCY_BRICKS_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_TRIANGLE_BRICKS.get(),
                ModBlocks.SEARED_TRIANGLE_BRICKS_SLAB.get(),
                ModBlocks.SEARED_TRIANGLE_BRICKS_STAIRS.get(),
                ModBlocks.SEARED_TRIANGLE_BRICKS_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_CREEPER.get(),
                ModBlocks.SEARED_CREEPER_SLAB.get(),
                ModBlocks.SEARED_CREEPER_STAIRS.get(),
                ModBlocks.SEARED_CREEPER_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_ROAD.get(),
                ModBlocks.SEARED_ROAD_SLAB.get(),
                ModBlocks.SEARED_ROAD_STAIRS.get(),
                ModBlocks.SEARED_ROAD_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_SMALL_BRICKS.get(),
                ModBlocks.SEARED_SMALL_BRICKS_SLAB.get(),
                ModBlocks.SEARED_SMALL_BRICKS_STAIRS.get(),
                ModBlocks.SEARED_SMALL_BRICKS_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_SQUARE_BRICKS.get(),
                ModBlocks.SEARED_SQUARE_BRICKS_SLAB.get(),
                ModBlocks.SEARED_SQUARE_BRICKS_STAIRS.get(),
                ModBlocks.SEARED_SQUARE_BRICKS_WALL.get());
        generateDecorative(items, ModBlocks.SEARED_TILE.get(),
                ModBlocks.SEARED_TILE_SLAB.get(),
                ModBlocks.SEARED_TILE_STAIRS.get(),
                ModBlocks.SEARED_TILE_WALL.get());

        addAlloyRecipes();
        addEntityMeltingRecipes();
        addFixRecipes();

        createCastRecipe("ingots",   ModItems.INGOT_CAST.get(),  "ingot_cast");
        createCastRecipe("nuggets",  ModItems.NUGGET_CAST.get(), "nugget_cast");
        createCastRecipe("gems",     ModItems.GEM_CAST.get(),    "gem_cast");
        createCastRecipe("plates",   ModItems.PLATE_CAST.get(),  "plate_cast");
        createCastRecipe("gears",    ModItems.GEAR_CAST.get(),   "gear_cast");
        createRodCastRecipe();

    }

    private void addVanillaRecipes() {
        var items = this.registries.lookupOrThrow(Registries.ITEM);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.FORGE_BRICK.get(), 16)
                .pattern("CCS").pattern("GLS").pattern("GCC")
                .define('G', Items.GRAVEL).define('C', Items.COAL)
                .define('S', Items.SAND).define('L', Items.CLAY_BALL)
                .unlockedBy("has_clay", this.has(Items.CLAY_BALL))
                .save(this.output, rk("forge_brick"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_BRICKS.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModItems.FORGE_BRICK.get())
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_COBBLE.get(), 4)
                .pattern("BS").pattern("SB")
                .define('B', ModItems.FORGE_BRICK.get()).define('S', Items.COBBLESTONE)
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_cobble"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_INGOT_TANK.get())
                .pattern("BBB").pattern("B B").pattern("BBB")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_ingot_tank"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_FUEL_TANK.get())
                .pattern("BBB").pattern("BLB").pattern("BBB")
                .define('B', ModBlocks.SEARED_BRICKS.get()).define('L', Items.LAVA_BUCKET)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_fuel_tank"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_MELTER.get())
                .pattern("BTB").pattern("BFB").pattern("BSB")
                .define('B', ModItems.FORGE_BRICK.get())
                .define('T', ModBlocks.SEARED_INGOT_TANK.get())
                .define('S', ModBlocks.SEARED_BRICKS.get())
                .define('F', Items.BLAST_FURNACE)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_melter"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_FAUCET.get(), 2)
                .pattern("B B").pattern(" C ")
                .define('B', ModBlocks.SEARED_BRICKS.get()).define('C', Items.COPPER_INGOT)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_faucet"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_TABLE.get())
                .pattern("BBB").pattern("B B").pattern("B B")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_table"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_BASIN.get())
                .pattern("B B").pattern("B B").pattern("BBB")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_basin"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_SMALL_BRICKS.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_small_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_SQUARE_BRICKS.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModBlocks.SEARED_SMALL_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_square_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_TILE.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModBlocks.SEARED_STONE.get())
                .unlockedBy("has_seared_stone", this.has(ModBlocks.SEARED_STONE.get()))
                .save(this.output, rk("seared_tile"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_FANCY_BRICKS.get(), 4)
                .pattern("BB").pattern("BB")
                .define('B', ModBlocks.SEARED_TILE.get())
                .unlockedBy("has_seared_tile", this.has(ModBlocks.SEARED_TILE.get()))
                .save(this.output, rk("seared_fancy_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_TRIANGLE_BRICKS.get(), 4)
                .pattern("BS").pattern("SB")
                .define('B', ModBlocks.SEARED_BRICKS.get()).define('S', ModBlocks.SEARED_STONE.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_triangle_bricks"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_ROAD.get())
                .pattern("B").pattern("B")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_road"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SEARED_COBBLE.get()),
                        RecipeCategory.MISC, CookingBookCategory.BLOCKS,
                        ModBlocks.SEARED_STONE.get(), 0.1f, 200)
                .unlockedBy("has_seared_cobble", this.has(ModBlocks.SEARED_COBBLE.get()))
                .save(this.output, rk("seared_stone_from_smelting"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SEARED_STONE.get()),
                        RecipeCategory.MISC, CookingBookCategory.BLOCKS,
                        ModBlocks.SEARED_PAVER.get(), 0.1f, 200)
                .unlockedBy("has_seared_stone", this.has(ModBlocks.SEARED_STONE.get()))
                .save(this.output, rk("seared_paver_from_smelting"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SEARED_BRICKS.get()),
                        RecipeCategory.MISC, CookingBookCategory.BLOCKS,
                        ModBlocks.SEARED_CRACKED_BRICKS.get(), 0.1f, 200)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("seared_cracked_bricks_from_smelting"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SEARED_SMALL_BRICKS.get()),
                        RecipeCategory.MISC, CookingBookCategory.BLOCKS,
                        ModBlocks.SEARED_CREEPER.get(), 0.1f, 200)
                .unlockedBy("has_seared_small_bricks", this.has(ModBlocks.SEARED_SMALL_BRICKS.get()))
                .save(this.output, rk("seared_creeper_from_smelting"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_CHUTE.get(), 1)
                .pattern("BGB").pattern("B B").pattern("BGB")
                .define('B', ModItems.FORGE_BRICK.get())
                .define('G', net.minecraft.tags.ItemTags.create(Identifier.fromNamespaceAndPath("c", "ingots/copper")))
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_chute"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_DRAIN.get(), 1)
                .pattern("BBB").pattern("G G").pattern("BBB")
                .define('B', ModItems.FORGE_BRICK.get())
                .define('G', net.minecraft.tags.ItemTags.create(Identifier.fromNamespaceAndPath("c", "ingots/copper")))
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_drain"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SMELTERY_CONTROLLER.get(), 1)
                .pattern("BBB").pattern("CSH").pattern("BTB")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .define('C', ModBlocks.SEARED_CHUTE.get())
                .define('H', ModBlocks.SEARED_DRAIN.get())
                .define('T', ModBlocks.SEARED_FUEL_TANK.get())
                .define('S', Items.BLAST_FURNACE)
                .unlockedBy("has_seared_bricks", this.has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, rk("smeltery_controller"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_GLASS.get(), 4)
                .pattern("GGG")
                .pattern("GBG")
                .pattern("GGG")
                .define('G', Items.GLASS)
                .define('B', ModItems.FORGE_BRICK.get())
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_glass"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_TINTED_GLASS.get(), 4)
                .pattern("GGG")
                .pattern("GBG")
                .pattern("GGG")
                .define('G', Items.TINTED_GLASS)
                .define('B', ModItems.FORGE_BRICK.get())
                .unlockedBy("has_forge_brick", this.has(ModItems.FORGE_BRICK.get()))
                .save(this.output, rk("seared_tinted_glass"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.CLEAR_GLASS.get(), 4)
                .pattern("GG")
                .pattern("GG")
                .define('G', Items.GLASS)
                .unlockedBy("has_glass", this.has(Items.GLASS))
                .save(this.output, rk("clear_glass"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.CLEAR_STAINED_GLASS.get(), 8)
                .pattern("GGG")
                .pattern("GDG")
                .pattern("GGG")
                .define('G', ModBlocks.CLEAR_GLASS.get())
                .define('D', ItemTags.create(Identifier.fromNamespaceAndPath("c", "dyes")))
                .unlockedBy("has_clear_glass", this.has(ModBlocks.CLEAR_GLASS.get()))
                .save(this.output, rk("clear_stained_glass"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.CLEAR_TINTED_GLASS.get(), 2)
                .pattern(" A ")
                .pattern("AGA")
                .pattern(" A ")
                .define('G', ModBlocks.CLEAR_GLASS.get())
                .define('A', Items.AMETHYST_SHARD)
                .unlockedBy("has_clear_glass", this.has(ModBlocks.CLEAR_GLASS.get()))
                .save(this.output, rk("clear_tinted_glass"));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_LAMP.get(), 2)
                .pattern(" B ").pattern("BGB").pattern(" B ")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .define('G', net.minecraft.world.item.Items.GLOWSTONE)
                .unlockedBy("has_seared_bricks", has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "seared_lamp")));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.SEARED_LADDER.get(), 3)
                .pattern("B B").pattern("BSB").pattern("B B")
                .define('B', ModBlocks.SEARED_BRICKS.get())
                .define('S', net.minecraft.world.item.Items.STICK)
                .unlockedBy("has_seared_bricks", has(ModBlocks.SEARED_BRICKS.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "seared_ladder")));
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

        addMeltingTag("storage_blocks/" + name, fluid, 810, temp, bt * 2,
                prefix + "metal/" + name + "/block", cond);
        if (hasRaw && !name.equals("steel") && !name.equals("brass"))
            addMeltingTag("storage_blocks/raw_" + name, fluid, 810, temp, (int)(bt * 2.5),
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
                new ModRecipes.CastingBasinRecipe(fluidId, 810, blockId, 1, bt * 2),                      null);
    }

    private void addEntityMeltingRecipes() {
        addEntityMeltingRecipe(
                net.minecraft.world.entity.EntityType.BLAZE,
                Identifier.fromNamespaceAndPath(TitamMods.MODID, "molten_blaze"),
                90, 2, "blaze");

        addEntityMeltingRecipe(
                net.minecraft.world.entity.EntityType.MAGMA_CUBE,
                Identifier.fromNamespaceAndPath("minecraft", "lava"),
                45, 2, "magma_cube");
    }

    private void addEntityMeltingRecipe(net.minecraft.world.entity.EntityType<?> entityType,
                                        Identifier fluidId, int amount, int damage,
                                        String savePath) {
        com.titammods.setup.ModRecipes.EntityMeltingRecipe recipe =
                new com.titammods.setup.ModRecipes.EntityMeltingRecipe(entityType, fluidId, amount, damage);
        this.output.accept(
                ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "entity_melting/" + savePath)),
                recipe, null);
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

    private void addMeltingDamageable(ItemLike item, Fluid fluid, int amount, int temperature,
                                      int time, String savePath, ICondition... conditions) {
        Ingredient ingredient = Ingredient.of(item);
        ModRecipes.MeltingRecipe recipe = new ModRecipes.MeltingRecipe(
                ingredient, fluidId(fluid), amount,
                fuelId(temperature), 50, temperature, time, true);
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

    private void createRodCastRecipe() {
        Ingredient rodOrStick = Ingredient.of(
                net.minecraft.world.item.Items.STICK,
                net.minecraft.world.item.Items.BLAZE_ROD
        );
        Identifier copperFluidId = fluidId(
                com.titammods.registry.HephaestusFluids.SETS.get(
                        com.titammods.registry.HephaestusFluids.Material.MOLTEN_COPPER).source.get()
        );
        Identifier resultId = BuiltInRegistries.ITEM.getKey(ModItems.ROD_CAST.get().asItem());
        ModRecipes.CastingTableRecipe recipe = new ModRecipes.CastingTableRecipe(
                java.util.Optional.of(rodOrStick), true,
                copperFluidId, 90,
                resultId, 1,
                60);
        this.output.accept(
                net.minecraft.resources.ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery/casting/casts/rod_cast")),
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

        addMeltingTag("storage_blocks/" + name, fluid, 810, temp, bt * 2,        prefix + "metal/" + name + "/block",    conditions);
        if (raw != null && !name.equals("steel") && !name.equals("brass"))
            addMeltingTag("storage_blocks/raw_" + name, fluid, 810, temp, (int)(bt * 2.5), prefix + "metal/" + name + "/raw_block", conditions);
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
        if (block  != null) addCastingBasin(fluid, 810, block, bt * 2, prefix + "metal/" + name + "/block_cast", conditions);
    }

    private void registerGem(Fluid fluid, int temp, String name, String blockTag, String gemTag,
                             ItemLike block, ItemLike gem, ICondition... conditions) {
        int bt = 120;

        if (blockTag != null && !blockTag.isEmpty())
            addMeltingTag(blockTag, fluid, 810, temp, bt * 2, "gem/" + name + "/block", conditions);
        else if (block != null)
            addMeltingItem(block, fluid, 810, temp, bt * 2, "gem/" + name + "/block", conditions);

        if (gemTag != null && !gemTag.isEmpty())
            addMeltingTag(gemTag, fluid, 90, temp, bt, "gem/" + name + "/gem", conditions);
        else if (gem != null)
            addMeltingItem(gem, fluid, 90, temp, bt, "gem/" + name + "/gem", conditions);

        if (gem   != null) addCastingTable(fluid, 90, ModItems.GEM_CAST.get(), false, gem,  bt,       "gem/" + name + "/gem_cast",   conditions);
        if (block != null) addCastingBasin(fluid, 810, block, bt * 2, "gem/" + name + "/block_cast", conditions);
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

    private void addFixRecipes() {
        addMeltingItem(net.minecraft.world.item.Items.COAL,
                fluidOf("MOLTEN_CARBON"), 90, 600, 100, "misc/carbon_from_coal");
        addMeltingItem(net.minecraft.world.item.Items.CHARCOAL,
                fluidOf("MOLTEN_CARBON"), 90, 600, 100, "misc/carbon_from_charcoal");
        addMeltingTag("storage_blocks/coal",
                fluidOf("MOLTEN_CARBON"), 810, 600, 200, "misc/carbon_from_coal_block");
        addMeltingItem(net.minecraft.world.item.Items.SLIME_BALL,
                fluidOf("MOLTEN_SLIME"), 50, 300, 60, "misc/slime_from_ball");
        addMeltingItem(net.minecraft.world.item.Items.SLIME_BLOCK,
                fluidOf("MOLTEN_SLIME"), 450, 300, 120, "misc/slime_from_block");
        addMeltingItem(net.minecraft.world.item.Items.MAGMA_CREAM,
                fluidOf("MOLTEN_MAGMA_CREAM"), 90, 700, 80, "misc/magma_cream");
        addMeltingItem(net.minecraft.world.item.Items.MAGMA_BLOCK,
                net.minecraft.world.level.material.Fluids.LAVA, 250, 700, 120, "misc/magma_block");
        addMeltingItem(net.minecraft.world.item.Items.ENDER_PEARL,
                fluidOf("MOLTEN_ENDER"), 90, 1000, 100, "misc/ender_from_pearl");
        addMeltingItem(net.minecraft.world.item.Items.ENDER_EYE,
                fluidOf("MOLTEN_ENDER"), 90, 1000, 100, "misc/ender_from_eye");
        addMeltingItem(net.minecraft.world.item.Items.REDSTONE,
                fluidOf("MOLTEN_REDSTONE"), 10, 600, 40, "misc/redstone_from_dust");
        addMeltingTag("storage_blocks/redstone",
                fluidOf("MOLTEN_REDSTONE"), 90, 600, 120, "misc/redstone_from_block");
        addMeltingItem(net.minecraft.world.item.Items.GLOWSTONE_DUST,
                fluidOf("MOLTEN_GLOWSTONE"), 50, 800, 60, "misc/glowstone_from_dust");
        addMeltingItem(net.minecraft.world.item.Items.GLOWSTONE,
                fluidOf("MOLTEN_GLOWSTONE"), 200, 800, 120, "misc/glowstone_from_block");
        addMeltingItem(net.minecraft.world.item.Items.GLASS,
                fluidOf("MOLTEN_GLASS"), 250, 1000, 100, "misc/glass_from_block");
        addMeltingItem(net.minecraft.world.item.Items.GLASS_PANE,
                fluidOf("MOLTEN_GLASS"), 90, 1000, 60, "misc/glass_from_pane");
        addMeltingItem(net.minecraft.world.item.Items.SAND,
                fluidOf("MOLTEN_GLASS"), 250, 1000, 100, "misc/glass_from_sand");
        addMeltingItem(net.minecraft.world.item.Items.LAPIS_LAZULI,
                fluidOf("MOLTEN_LAPIS"), 10, 900, 40, "misc/lapis_from_gem");
        addMeltingTag("storage_blocks/lapis",
                fluidOf("MOLTEN_LAPIS"), 90, 900, 120, "misc/lapis_from_block");
        addMeltingItem(net.minecraft.world.item.Items.OBSIDIAN,
                fluidOf("MOLTEN_OBSIDIAN"), 288, 1400, 200, "misc/obsidian");
        addMeltingItem(net.minecraft.world.item.Items.ANCIENT_DEBRIS,
                fluidOf("MOLTEN_ANCIENT_DEBRIS"), 90, 2000, 300, "misc/ancient_debris");
        addMeltingItem(net.minecraft.world.item.Items.NETHERITE_SCRAP,
                fluidOf("MOLTEN_ANCIENT_DEBRIS"), 90, 2000, 200, "misc/ancient_debris_from_scrap");
        addMeltingItem(net.minecraft.world.item.Items.NETHERITE_INGOT,
                fluidOf("MOLTEN_NETHERITE"), 90, 2000, 200, "misc/netherite_from_ingot");
        addMeltingItem(net.minecraft.world.item.Items.SHULKER_SHELL,
                fluidOf("MOLTEN_SHULKER_SHELL"), 90, 1200, 120, "misc/shulker_shell");
        addMeltingItem(net.minecraft.world.item.Items.HONEYCOMB,
                fluidOf("MOLTEN_WAX"), 50, 320, 60, "misc/wax_from_honeycomb");
        addMeltingItem(net.minecraft.world.item.Items.HONEYCOMB_BLOCK,
                fluidOf("MOLTEN_WAX"), 200, 320, 120, "misc/wax_from_block");
        addMeltingItem(net.minecraft.world.item.Items.PORKCHOP,
                fluidOf("LIQUID_MEAT"), 40, 200, 60, "misc/meat_from_porkchop");
        addMeltingItem(net.minecraft.world.item.Items.BEEF,
                fluidOf("LIQUID_MEAT"), 40, 200, 60, "misc/meat_from_beef");
        addMeltingItem(net.minecraft.world.item.Items.CHICKEN,
                fluidOf("LIQUID_MEAT"), 40, 200, 60, "misc/meat_from_chicken");
        addMeltingItem(net.minecraft.world.item.Items.MUTTON,
                fluidOf("LIQUID_MEAT"), 40, 200, 60, "misc/meat_from_mutton");
        addMeltingItem(net.minecraft.world.item.Items.RABBIT,
                fluidOf("LIQUID_MEAT"), 40, 200, 60, "misc/meat_from_rabbit");
        addMeltingItem(net.minecraft.world.item.Items.ROTTEN_FLESH,
                fluidOf("LIQUID_MEAT"), 20, 200, 40, "misc/meat_from_rotten_flesh");

        addToolArmorMelting();
    }

    private void addToolArmorMelting() {
        Fluid iron = fluidOf("MOLTEN_IRON");
        Fluid gold = fluidOf("MOLTEN_GOLD");
        Fluid netherite = fluidOf("MOLTEN_NETHERITE");

        addMeltingDamageable(net.minecraft.world.item.Items.IRON_PICKAXE, iron, 270, 900, 200, "tools/iron_pickaxe");
        addMeltingDamageable(net.minecraft.world.item.Items.IRON_AXE,     iron, 270, 900, 200, "tools/iron_axe");
        addMeltingDamageable(net.minecraft.world.item.Items.IRON_SWORD,   iron, 180, 900, 150, "tools/iron_sword");
        addMeltingDamageable(net.minecraft.world.item.Items.IRON_SHOVEL,  iron,  90, 900, 100, "tools/iron_shovel");
        addMeltingDamageable(net.minecraft.world.item.Items.IRON_HOE,     iron, 180, 900, 150, "tools/iron_hoe");
        addMeltingDamageable(net.minecraft.world.item.Items.IRON_HELMET,     iron, 450, 900, 250, "armor/iron_helmet");
        addMeltingDamageable(net.minecraft.world.item.Items.IRON_CHESTPLATE, iron, 720, 900, 350, "armor/iron_chestplate");
        addMeltingDamageable(net.minecraft.world.item.Items.IRON_LEGGINGS,   iron, 630, 900, 300, "armor/iron_leggings");
        addMeltingDamageable(net.minecraft.world.item.Items.IRON_BOOTS,      iron, 360, 900, 200, "armor/iron_boots");
        addMeltingDamageable(net.minecraft.world.item.Items.CHAINMAIL_HELMET,     iron, 450, 900, 250, "armor/chainmail_helmet");
        addMeltingDamageable(net.minecraft.world.item.Items.CHAINMAIL_CHESTPLATE, iron, 720, 900, 350, "armor/chainmail_chestplate");
        addMeltingDamageable(net.minecraft.world.item.Items.CHAINMAIL_LEGGINGS,   iron, 630, 900, 300, "armor/chainmail_leggings");
        addMeltingDamageable(net.minecraft.world.item.Items.CHAINMAIL_BOOTS,      iron, 360, 900, 200, "armor/chainmail_boots");

        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_PICKAXE, gold, 270, 900, 200, "tools/golden_pickaxe");
        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_AXE,     gold, 270, 900, 200, "tools/golden_axe");
        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_SWORD,   gold, 180, 900, 150, "tools/golden_sword");
        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_SHOVEL,  gold,  90, 900, 100, "tools/golden_shovel");
        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_HOE,     gold, 180, 900, 150, "tools/golden_hoe");
        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_HELMET,     gold, 450, 900, 250, "armor/golden_helmet");
        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_CHESTPLATE, gold, 720, 900, 350, "armor/golden_chestplate");
        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_LEGGINGS,   gold, 630, 900, 300, "armor/golden_leggings");
        addMeltingDamageable(net.minecraft.world.item.Items.GOLDEN_BOOTS,      gold, 360, 900, 200, "armor/golden_boots");

        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_PICKAXE, netherite, 90, 2000, 300, "tools/netherite_pickaxe");
        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_AXE,     netherite, 90, 2000, 300, "tools/netherite_axe");
        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_SWORD,   netherite, 90, 2000, 300, "tools/netherite_sword");
        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_SHOVEL,  netherite, 90, 2000, 300, "tools/netherite_shovel");
        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_HOE,     netherite, 90, 2000, 300, "tools/netherite_hoe");
        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_HELMET,     netherite, 90, 2000, 350, "armor/netherite_helmet");
        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_CHESTPLATE, netherite, 90, 2000, 350, "armor/netherite_chestplate");
        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_LEGGINGS,   netherite, 90, 2000, 350, "armor/netherite_leggings");
        addMeltingDamageable(net.minecraft.world.item.Items.NETHERITE_BOOTS,      netherite, 90, 2000, 350, "armor/netherite_boots");

        Fluid diamond = com.titammods.setup.ModFluids.MOLTEN_DIAMOND.source.get();
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_PICKAXE, diamond, 270, 1400, 200, "tools/diamond_pickaxe");
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_AXE,     diamond, 270, 1400, 200, "tools/diamond_axe");
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_SWORD,   diamond, 180, 1400, 150, "tools/diamond_sword");
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_SHOVEL,  diamond,  90, 1400, 100, "tools/diamond_shovel");
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_HOE,     diamond, 180, 1400, 150, "tools/diamond_hoe");
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_HELMET,     diamond, 450, 1400, 250, "armor/diamond_helmet");
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, diamond, 720, 1400, 350, "armor/diamond_chestplate");
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_LEGGINGS,   diamond, 630, 1400, 300, "armor/diamond_leggings");
        addMeltingDamageable(net.minecraft.world.item.Items.DIAMOND_BOOTS,      diamond, 360, 1400, 200, "armor/diamond_boots");
    }

    private Fluid fluidOf(String materialName) {
        return com.titammods.registry.HephaestusFluids.SETS.get(
                com.titammods.registry.HephaestusFluids.Material.valueOf(materialName)).source.get();
    }

    private void addMeltingMiscRecipes() {
        addMeltingItem(net.minecraft.world.item.Items.BLAZE_ROD,
                com.titammods.setup.ModFluids.MOLTEN_BLAZE.source.get(),
                250, 800, 100,
                "misc/molten_blaze");
    }

    private void addAlloyRecipes() {
        Identifier copper    = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_COPPER).source.get());
        Identifier zinc      = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_ZINC).source.get());
        Identifier tin       = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_TIN).source.get());
        Identifier gold      = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_GOLD).source.get());
        Identifier silver    = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_SILVER).source.get());
        Identifier iron      = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_IRON).source.get());
        Identifier nickel    = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_NICKEL).source.get());
        Identifier brass     = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_BRASS).source.get());
        Identifier bronze    = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_BRONZE).source.get());
        Identifier electrum  = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_ELECTRUM).source.get());
        Identifier invar     = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_INVAR).source.get());
        Identifier constantan= fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_CONSTANTAN).source.get());
        Identifier steel     = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_STEEL).source.get());
        Identifier carbon    = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_CARBON).source.get());
        Identifier netherite = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_NETHERITE).source.get());
        Identifier ancientDebris = fluidId(com.titammods.registry.HephaestusFluids.SETS.get(com.titammods.registry.HephaestusFluids.Material.MOLTEN_ANCIENT_DEBRIS).source.get());

        addAlloyRecipe(java.util.List.of(new FluidEntry(copper, 90), new FluidEntry(zinc, 90)),
                brass, 180, 650, "brass");
        addAlloyRecipe(java.util.List.of(new FluidEntry(copper, 270), new FluidEntry(tin, 90)),
                bronze, 360, 700, "bronze");
        addAlloyRecipe(java.util.List.of(new FluidEntry(gold, 90), new FluidEntry(silver, 90)),
                electrum, 180, 760, "electrum");
        addAlloyRecipe(java.util.List.of(new FluidEntry(iron, 180), new FluidEntry(nickel, 90)),
                invar, 270, 900, "invar");
        addAlloyRecipe(java.util.List.of(new FluidEntry(copper, 90), new FluidEntry(nickel, 90)),
                constantan, 180, 920, "constantan");
        addAlloyRecipe(java.util.List.of(new FluidEntry(iron, 90), new FluidEntry(carbon, 90)),
                steel, 90, 1000, "steel");
        addAlloyRecipe(java.util.List.of(new FluidEntry(gold, 270), new FluidEntry(ancientDebris, 270)),
                netherite, 360, 2000, "netherite");
    }

    private record FluidEntry(Identifier id, int amount) {}

    private void addAlloyRecipe(java.util.List<FluidEntry> inputs,
                                Identifier resultId, int resultAmount,
                                int temperature, String savePath) {
        var inputRefs = inputs.stream()
                .map(e -> new com.titammods.setup.ModRecipes.AlloyRecipe.FluidRef(e.id(), e.amount()))
                .toList();
        com.titammods.setup.ModRecipes.AlloyRecipe recipe =
                new com.titammods.setup.ModRecipes.AlloyRecipe(inputRefs, resultId, resultAmount, temperature);
        this.output.accept(
                ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery/alloying/" + savePath)),
                recipe, null);
    }

    private void generateDecorative(net.minecraft.core.HolderGetter<net.minecraft.world.item.Item> items,
                                    Block baseBlock,
                                    Block slab, Block stairs, Block wall) {
        String baseName = BuiltInRegistries.BLOCK.getKey(baseBlock).getPath();
        String base = "has_" + baseName;
        if (slab != null)
            ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, slab, 6)
                    .pattern("BBB").define('B', baseBlock)
                    .unlockedBy(base, has(baseBlock))
                    .save(this.output, ResourceKey.create(Registries.RECIPE,
                            Identifier.fromNamespaceAndPath(TitamMods.MODID,
                                    "decoration/" + BuiltInRegistries.BLOCK.getKey(slab).getPath())));
        if (stairs != null)
            ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, stairs, 4)
                    .pattern("B  ").pattern("BB ").pattern("BBB").define('B', baseBlock)
                    .unlockedBy(base, has(baseBlock))
                    .save(this.output, ResourceKey.create(Registries.RECIPE,
                            Identifier.fromNamespaceAndPath(TitamMods.MODID,
                                    "decoration/" + BuiltInRegistries.BLOCK.getKey(stairs).getPath())));
        if (wall != null)
            ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, wall, 6)
                    .pattern("BBB").pattern("BBB").define('B', baseBlock)
                    .unlockedBy(base, has(baseBlock))
                    .save(this.output, ResourceKey.create(Registries.RECIPE,
                            Identifier.fromNamespaceAndPath(TitamMods.MODID,
                                    "decoration/" + BuiltInRegistries.BLOCK.getKey(wall).getPath())));
    }

}