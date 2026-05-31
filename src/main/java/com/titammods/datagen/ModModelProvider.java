package com.titammods.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.titammods.TitamMods;
import com.titammods.registry.HephaestusFluids;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModModelProvider implements DataProvider {

    private static final Map<String, String> BLOCK_TEX = Map.ofEntries(
            Map.entry("cobalt_block",           "block/ores/cobalt_block"),
            Map.entry("raw_cobalt_block",        "block/ores/raw_cobalt_block"),
            Map.entry("steel_block",             "block/ores/steel_block"),
            Map.entry("seared_stone",           "block/smeltery/seared/stone"),
            Map.entry("seared_cobble",          "block/smeltery/seared/cobble"),
            Map.entry("seared_paver",           "block/smeltery/seared/paver"),
            Map.entry("seared_bricks",          "block/smeltery/seared/bricks"),
            Map.entry("seared_cracked_bricks",  "block/smeltery/seared/cracked_bricks"),
            Map.entry("seared_fancy_bricks",    "block/smeltery/seared/fancy_bricks"),
            Map.entry("seared_triangle_bricks", "block/smeltery/seared/triangle_bricks"),
            Map.entry("seared_creeper",         "block/smeltery/seared/creeper"),
            Map.entry("seared_road",            "block/smeltery/seared/road"),
            Map.entry("seared_small_bricks",    "block/smeltery/seared/small_bricks"),
            Map.entry("seared_square_bricks",   "block/smeltery/seared/square_bricks"),
            Map.entry("seared_tile",            "block/smeltery/seared/tile")
    );

    private static final String[] FLAT_ITEMS = {
            "raw_cobalt", "cobalt_ingot", "cobalt_nugget", "cobalt_powder",
            "raw_steel",  "steel_ingot",  "steel_nugget",  "steel_powder",
            "forge_brick",
            "blank_cast", "coin_cast", "gear_cast", "gem_cast",
            "ingot_cast", "nugget_cast", "plate_cast", "rod_cast"
    };

    private static final java.util.Map<String, String> TANK_MODELS = java.util.Map.of(
            "seared_ingot_tank", "block/smeltery/tank/ingot_tank",
            "seared_fuel_tank",  "block/smeltery/tank/fuel_tank"
    );

    // ModFluids: nomes dos fluidos (para neoforge:fluid_container)
    private static final Map<String, String> MOD_FLUID_STILL = Map.of(
            "molten_cobalt",   "molten_cobalt",
            "molten_quartz",   "molten_quartz",
            "molten_diamond",  "molten_diamond",
            "molten_emerald",  "molten_emerald",
            "molten_amethyst", "molten_amethyst",
            "molten_blaze",    "molten_blaze"
    );

    private final PackOutput.PathProvider modelBlockPath;
    private final PackOutput.PathProvider modelItemPath;
    private final PackOutput.PathProvider blockstatePath;
    private final PackOutput.PathProvider itemPath;
    private final PackOutput.PathProvider atlasPath;

    public ModModelProvider(PackOutput output) {
        this.modelBlockPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        this.modelItemPath  = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
        this.blockstatePath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemPath       = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.atlasPath      = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(save(cache, fluidAtlas(),
                atlasPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, "blocks"))));

        for (var entry : TANK_MODELS.entrySet()) {
            String name      = entry.getKey();
            String modelPath = entry.getValue();
            Identifier id = id(name);
            futures.add(save(cache, tankBlockstate(modelPath),        blockstatePath.json(id)));
            futures.add(save(cache, tankClientItem(name, modelPath),  itemPath.json(id)));
        }

        for (var e : BLOCK_TEX.entrySet()) {
            Identifier id = id(e.getKey());
            futures.add(save(cache, blockModel(e.getValue()),   modelBlockPath.json(id)));
            futures.add(save(cache, blockstate(e.getKey()),      blockstatePath.json(id)));
            futures.add(save(cache, blockClientItem(e.getKey()), itemPath.json(id)));
        }

        for (String name : FLAT_ITEMS) {
            Identifier id = id(name);
            futures.add(save(cache, flatItemModel(name),  modelItemPath.json(id)));
            futures.add(save(cache, flatClientItem(name), itemPath.json(id)));
        }

        for (HephaestusFluids.Material mat : HephaestusFluids.Material.values()) {
            String fluidReg   = "molten_" + mat.name;
            String blockName  = fluidReg + "_block";
            String bucketName = fluidReg + "_bucket";

            futures.add(save(cache, blockstate(blockName), blockstatePath.json(id(blockName))));

            futures.add(save(cache,
                    fluidContainerItem(fluidReg),
                    itemPath.json(id(bucketName))));
        }

        for (var e : MOD_FLUID_STILL.entrySet()) {
            String name       = e.getKey();          // "molten_cobalt" etc.
            String blockName  = name + "_block";
            String bucketName = name + "_bucket";

            futures.add(save(cache, blockstate(blockName), blockstatePath.json(id(blockName))));

            futures.add(save(cache,
                    fluidContainerItem(name),
                    itemPath.json(id(bucketName))));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() { return "Hephaestus Models"; }

    private JsonObject fluidAtlas() {
        JsonObject j = new JsonObject();
        JsonArray sources = new JsonArray();
        JsonObject dir = new JsonObject();
        dir.addProperty("type", "directory");
        dir.addProperty("source", "fluid");
        dir.addProperty("prefix", "fluid/");
        sources.add(dir);
        j.add("sources", sources);
        return j;
    }

    private JsonObject blockModel(String tex) {
        JsonObject j = new JsonObject();
        j.addProperty("parent", "minecraft:block/cube_all");
        JsonObject t = new JsonObject();
        t.addProperty("all", TitamMods.MODID + ":" + tex);
        j.add("textures", t);
        return j;
    }

    private JsonObject blockstate(String name) {
        JsonObject j = new JsonObject();
        JsonObject v = new JsonObject();
        JsonObject m = new JsonObject();
        m.addProperty("model", TitamMods.MODID + ":block/" + name);
        v.add("", m);
        j.add("variants", v);
        return j;
    }

    private JsonObject blockClientItem(String name) {
        JsonObject j = new JsonObject();
        JsonObject m = new JsonObject();
        m.addProperty("type", "minecraft:model");
        m.addProperty("model", TitamMods.MODID + ":block/" + name);
        j.add("model", m);
        return j;
    }

    private JsonObject flatItemModel(String name) {
        JsonObject j = new JsonObject();
        j.addProperty("parent", "minecraft:item/generated");
        JsonObject t = new JsonObject();
        t.addProperty("layer0", TitamMods.MODID + ":item/" + name);
        j.add("textures", t);
        return j;
    }

    private JsonObject flatClientItem(String name) {
        JsonObject j = new JsonObject();
        JsonObject m = new JsonObject();
        m.addProperty("type", "minecraft:model");
        m.addProperty("model", TitamMods.MODID + ":item/" + name);
        j.add("model", m);
        return j;
    }

    private JsonObject fluidContainerItem(String fluidRegistryName) {
        JsonObject j = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("type", "neoforge:fluid_container");
        model.addProperty("fluid", TitamMods.MODID + ":" + fluidRegistryName);
        JsonObject textures = new JsonObject();
        textures.addProperty("base",     "minecraft:item/bucket");
        textures.addProperty("fluid",    "neoforge:item/mask/bucket_fluid");
        textures.addProperty("particle", "minecraft:item/bucket");
        model.add("textures", textures);
        j.add("model", model);
        return j;
    }

    private JsonObject tankClientItem(String name, String modelPath) {
        String fullModel = TitamMods.MODID + ":" + modelPath;
        JsonObject j = new JsonObject();
        JsonObject composite = new JsonObject();
        composite.addProperty("type", "minecraft:composite");
        JsonArray models = new JsonArray();
        // Special: renderer do fluido
        JsonObject special = new JsonObject();
        special.addProperty("type", "minecraft:special");
        special.addProperty("base", fullModel);
        JsonObject specialModel = new JsonObject();
        specialModel.addProperty("type", TitamMods.MODID + ":seared_tank_fluid");
        special.add("model", specialModel);
        models.add(special);
        // Model: textura do bloco do tanque
        JsonObject regular = new JsonObject();
        regular.addProperty("type", "minecraft:model");
        regular.addProperty("model", fullModel);
        models.add(regular);
        composite.add("models", models);
        j.add("model", composite);
        return j;
    }

    private JsonObject tankBlockstate(String modelPath) {
        String fullModel = TitamMods.MODID + ":" + modelPath;
        JsonObject j = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("model", fullModel);
        variants.add("emits_light=false", model);
        variants.add("emits_light=true",  model);
        j.add("variants", variants);
        return j;
    }

    private Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(TitamMods.MODID, name);
    }

    private CompletableFuture<?> save(CachedOutput cache, JsonObject json, Path path) {
        return DataProvider.saveStable(cache, json, path);
    }
}