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

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_IRON).source.get(),
                1538, "iron", Items.IRON_BLOCK, Items.IRON_INGOT, Items.RAW_IRON, Items.IRON_NUGGET, null, null, null, null, "");

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_GOLD).source.get(),
                1060, "gold", Items.GOLD_BLOCK, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_NUGGET, null, null, null, null, "");

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_COPPER).source.get(),
                1080, "copper", Items.COPPER_BLOCK, Items.COPPER_INGOT, Items.RAW_COPPER, null, null, null, null, null, "");

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_STEEL).source.get(),
                1000, "steel", ModBlocks.STEEL_BLOCK.get(), ModItems.STEEL_INGOT.get(), ModItems.RAW_STEEL.get(),
                ModItems.STEEL_NUGGET.get(), ModItems.STEEL_POWDER.get(), null, null, null, "");

        registerMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NETHERITE).source.get(),
                2000, "netherite", Items.NETHERITE_BLOCK, Items.NETHERITE_INGOT, null, null, null, null, null, null, "");

        registerMetal(ModFluids.MOLTEN_COBALT.source.get(), 1100, "cobalt",
                ModBlocks.COBALT_BLOCK.get(), ModItems.COBALT_INGOT.get(), ModItems.RAW_COBALT.get(),
                ModItems.COBALT_NUGGET.get(), ModItems.COBALT_POWDER.get(), null, null, null, "");

        registerGem(ModFluids.MOLTEN_DIAMOND.source.get(),  1400, "diamond",
                "storage_blocks/diamond", "gems/diamond",  Items.DIAMOND_BLOCK,  Items.DIAMOND);
        registerGem(ModFluids.MOLTEN_EMERALD.source.get(),  1200, "emerald",
                "storage_blocks/emerald", "gems/emerald",  Items.EMERALD_BLOCK,  Items.EMERALD);
        registerGem(ModFluids.MOLTEN_AMETHYST.source.get(), 1000, "amethyst",
                "", "gems/amethyst", Items.AMETHYST_BLOCK, Items.AMETHYST_SHARD);
        registerGem(ModFluids.MOLTEN_QUARTZ.source.get(),    800, "quartz",
                "", "gems/quartz",   Items.QUARTZ_BLOCK,   Items.QUARTZ);

        registerAllTheOresCompat();
        registerFtbMaterialsCompat();

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

        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ALUMINUM).source.get(),   660, "aluminum",  "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRASS).source.get(),      930, "brass",     "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_BRONZE).source.get(),     950, "bronze",    "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_LEAD).source.get(),       327, "lead",      "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_NICKEL).source.get(),    1450, "nickel",    "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_OSMIUM).source.get(),    1990, "osmium",    "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_PLATINUM).source.get(),  1768, "platinum",  "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_SILVER).source.get(),     960, "silver",    "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_TIN).source.get(),        230, "tin",       "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_URANIUM).source.get(),   1130, "uranium",   "ftbmaterials", prefix, cond);
        registerExternalMetal(HephaestusFluids.SETS.get(HephaestusFluids.Material.MOLTEN_ZINC).source.get(),       419, "zinc",      "ftbmaterials", prefix, cond);
    }

    private Identifier fuelId(int temp) {
        return temp <= 1000
                ? Identifier.fromNamespaceAndPath("minecraft", "lava")
                : Identifier.fromNamespaceAndPath("hephaestus", "molten_blaze");
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

        // withConditions é obrigatório no 26.1
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

        // FIX: Mesma correção para o withConditions aqui
        this.output.withConditions(conditions).accept(
                ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(TitamMods.MODID, "smeltery/melting/" + savePath)),
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
        if (nugget != null) addMeltingTag("nuggets/" + name, fluid, 10, temp, bt / 3, prefix + "metal/" + name + "/nugget", conditions);
        if (dust   != null) addMeltingTag("dusts/"   + name, fluid, 90, temp, bt,     prefix + "metal/" + name + "/dust",   conditions);
        if (plate  != null) addMeltingTag("plates/"  + name, fluid, 90, temp, bt,     prefix + "metal/" + name + "/plate",  conditions);
        if (gear   != null) addMeltingTag("gears/"   + name, fluid,360, temp, bt * 2, prefix + "metal/" + name + "/gear",   conditions);
        if (rod    != null) addMeltingTag("rods/"    + name, fluid, 45, temp, bt / 2, prefix + "metal/" + name + "/rod",    conditions);
        // casting comentado até CastingBasin/Table serem portados
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
        // casting comentado
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