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

    private static final Map<String, String> GLASS_TEX = Map.of(
            "clear_glass",         "block/smeltery/glass/clear_glass",
            "clear_stained_glass", "block/smeltery/glass/clear_stained_glass",
            "clear_tinted_glass",  "block/smeltery/glass/clear_tinted_glass",
            "seared_glass",        "block/smeltery/glass/seared_glass",
            "seared_tinted_glass", "block/smeltery/glass/seared_tinted_glass"
    );

    private static final String[] FLAT_ITEMS = {
            "raw_cobalt", "cobalt_ingot", "cobalt_nugget", "cobalt_powder",
            "raw_steel",  "steel_ingot",  "steel_nugget",  "steel_powder",
            "forge_brick",
            "blank_cast", "coin_cast", "gear_cast", "gem_cast",
            "ingot_cast", "nugget_cast", "plate_cast", "rod_cast"
    };

    private static final Map<String, String> TANK_MODELS = Map.of(
            "seared_ingot_tank", "block/smeltery/tank/ingot_tank",
            "seared_fuel_tank",  "block/smeltery/tank/fuel_tank"
    );

    private static final Map<String, String> MOD_FLUID_STILL = Map.of(
            "molten_cobalt",   "molten_cobalt",
            "molten_quartz",   "molten_quartz",
            "molten_diamond",  "molten_diamond",
            "molten_emerald",  "molten_emerald",
            "molten_amethyst", "molten_amethyst",
            "molten_blaze",    "molten_blaze"
    );

    private static final String[] COMPLEX_BLOCKS = {
            "seared_melter",
            "seared_table",
            "seared_basin",
            "seared_faucet",
            "smeltery_controller",
            "seared_chute",
            "seared_drain",
    };

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
            futures.add(save(cache, tankBlockstate(modelPath),       blockstatePath.json(id)));
            futures.add(save(cache, tankClientItem(name, modelPath), itemPath.json(id)));
        }

        for (String name : COMPLEX_BLOCKS) {
            futures.add(save(cache, complexBlockClientItem(name), itemPath.json(id(name))));
        }

        for (var e : BLOCK_TEX.entrySet()) {
            Identifier id = id(e.getKey());
            futures.add(save(cache, blockModel(e.getValue()),    modelBlockPath.json(id)));
            futures.add(save(cache, blockstate(e.getKey()),      blockstatePath.json(id)));
            futures.add(save(cache, blockClientItem(e.getKey()), itemPath.json(id)));
        }

        for (var e : GLASS_TEX.entrySet()) {
            String name = e.getKey();
            String tex  = e.getValue();
            Identifier id = id(name);
            futures.add(save(cache, glassBlockModel(tex),  modelBlockPath.json(id)));
            futures.add(save(cache, glassItemModel(name, tex), modelItemPath.json(id)));
            futures.add(save(cache, blockstate(name),       blockstatePath.json(id)));
            futures.add(save(cache, glassClientItem(name),  itemPath.json(id)));
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
            futures.add(save(cache, fluidContainerItem(fluidReg), itemPath.json(id(bucketName))));
        }

        for (var e : MOD_FLUID_STILL.entrySet()) {
            String name       = e.getKey();
            String blockName  = name + "_block";
            String bucketName = name + "_bucket";

            futures.add(save(cache, blockstate(blockName), blockstatePath.json(id(blockName))));
            futures.add(save(cache, fluidContainerItem(name), itemPath.json(id(bucketName))));
        }


        futures.addAll(searedStairs(cache, "seared_stone_stairs", "hephaestus:block/smeltery/seared/stone"));
        futures.addAll(searedStairs(cache, "seared_cobble_stairs", "hephaestus:block/smeltery/seared/cobble"));
        futures.addAll(searedStairs(cache, "seared_paver_stairs", "hephaestus:block/smeltery/seared/paver"));
        futures.addAll(searedStairs(cache, "seared_bricks_stairs", "hephaestus:block/smeltery/seared/bricks"));
        futures.addAll(searedStairs(cache, "seared_cracked_bricks_stairs", "hephaestus:block/smeltery/seared/cracked_bricks"));
        futures.addAll(searedStairs(cache, "seared_fancy_bricks_stairs", "hephaestus:block/smeltery/seared/fancy_bricks"));
        futures.addAll(searedStairs(cache, "seared_triangle_bricks_stairs", "hephaestus:block/smeltery/seared/triangle_bricks"));
        futures.addAll(searedStairs(cache, "seared_creeper_stairs", "hephaestus:block/smeltery/seared/creeper"));
        futures.addAll(searedStairs(cache, "seared_road_stairs", "hephaestus:block/smeltery/seared/road"));
        futures.addAll(searedStairs(cache, "seared_small_bricks_stairs", "hephaestus:block/smeltery/seared/small_bricks"));
        futures.addAll(searedStairs(cache, "seared_square_bricks_stairs", "hephaestus:block/smeltery/seared/square_bricks"));
        futures.addAll(searedStairs(cache, "seared_tile_stairs", "hephaestus:block/smeltery/seared/tile"));

        futures.addAll(searedSlab(cache, "seared_stone_slab", "hephaestus:block/smeltery/seared/stone", "hephaestus:block/seared_stone"));
        futures.addAll(searedSlab(cache, "seared_cobble_slab", "hephaestus:block/smeltery/seared/cobble", "hephaestus:block/seared_cobble"));
        futures.addAll(searedSlab(cache, "seared_paver_slab", "hephaestus:block/smeltery/seared/paver", "hephaestus:block/seared_paver"));
        futures.addAll(searedSlab(cache, "seared_bricks_slab", "hephaestus:block/smeltery/seared/bricks", "hephaestus:block/seared_bricks"));
        futures.addAll(searedSlab(cache, "seared_cracked_bricks_slab", "hephaestus:block/smeltery/seared/cracked_bricks", "hephaestus:block/seared_cracked_bricks"));
        futures.addAll(searedSlab(cache, "seared_fancy_bricks_slab", "hephaestus:block/smeltery/seared/fancy_bricks", "hephaestus:block/seared_fancy_bricks"));
        futures.addAll(searedSlab(cache, "seared_triangle_bricks_slab", "hephaestus:block/smeltery/seared/triangle_bricks", "hephaestus:block/seared_triangle_bricks"));
        futures.addAll(searedSlab(cache, "seared_creeper_slab", "hephaestus:block/smeltery/seared/creeper", "hephaestus:block/seared_creeper"));
        futures.addAll(searedSlab(cache, "seared_road_slab", "hephaestus:block/smeltery/seared/road", "hephaestus:block/seared_road"));
        futures.addAll(searedSlab(cache, "seared_small_bricks_slab", "hephaestus:block/smeltery/seared/small_bricks", "hephaestus:block/seared_small_bricks"));
        futures.addAll(searedSlab(cache, "seared_square_bricks_slab", "hephaestus:block/smeltery/seared/square_bricks", "hephaestus:block/seared_square_bricks"));
        futures.addAll(searedSlab(cache, "seared_tile_slab", "hephaestus:block/smeltery/seared/tile", "hephaestus:block/seared_tile"));

        futures.addAll(searedWall(cache, "seared_stone_wall", "hephaestus:block/smeltery/seared/stone"));
        futures.addAll(searedWall(cache, "seared_cobble_wall", "hephaestus:block/smeltery/seared/cobble"));
        futures.addAll(searedWall(cache, "seared_paver_wall", "hephaestus:block/smeltery/seared/paver"));
        futures.addAll(searedWall(cache, "seared_bricks_wall", "hephaestus:block/smeltery/seared/bricks"));
        futures.addAll(searedWall(cache, "seared_cracked_bricks_wall", "hephaestus:block/smeltery/seared/cracked_bricks"));
        futures.addAll(searedWall(cache, "seared_fancy_bricks_wall", "hephaestus:block/smeltery/seared/fancy_bricks"));
        futures.addAll(searedWall(cache, "seared_triangle_bricks_wall", "hephaestus:block/smeltery/seared/triangle_bricks"));
        futures.addAll(searedWall(cache, "seared_creeper_wall", "hephaestus:block/smeltery/seared/creeper"));
        futures.addAll(searedWall(cache, "seared_road_wall", "hephaestus:block/smeltery/seared/road"));
        futures.addAll(searedWall(cache, "seared_small_bricks_wall", "hephaestus:block/smeltery/seared/small_bricks"));
        futures.addAll(searedWall(cache, "seared_square_bricks_wall", "hephaestus:block/smeltery/seared/square_bricks"));
        futures.addAll(searedWall(cache, "seared_tile_wall", "hephaestus:block/smeltery/seared/tile"));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() { return "Hephaestus Models"; }

    private JsonObject blockModel(String tex) {
        JsonObject j = new JsonObject();
        j.addProperty("parent", "minecraft:block/cube_all");
        JsonObject t = new JsonObject();
        t.addProperty("all", TitamMods.MODID + ":" + tex);
        j.add("textures", t);
        return j;
    }

    private JsonObject glassBlockModel(String tex) {
        JsonObject j = new JsonObject();
        j.addProperty("parent", "minecraft:block/glass");
        j.addProperty("ambientocclusion", false);
        JsonObject t = new JsonObject();
        t.addProperty("all", TitamMods.MODID + ":" + tex);
        j.add("textures", t);
        return j;
    }

    private JsonObject glassItemModel(String name, String tex) {
        JsonObject j = new JsonObject();
        j.addProperty("parent", "minecraft:item/generated");
        JsonObject t = new JsonObject();
        t.addProperty("layer0", TitamMods.MODID + ":" + tex);
        j.add("textures", t);
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

    private JsonObject glassClientItem(String name) {
        JsonObject j = new JsonObject();
        JsonObject m = new JsonObject();
        m.addProperty("type", "minecraft:model");
        m.addProperty("model", TitamMods.MODID + ":item/" + name);
        j.add("model", m);
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

    private JsonObject complexBlockClientItem(String name) {
        JsonObject j = new JsonObject();
        JsonObject m = new JsonObject();
        m.addProperty("type",  "minecraft:model");
        m.addProperty("model", TitamMods.MODID + ":item/" + name);
        j.add("model", m);
        return j;
    }

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
        JsonObject special = new JsonObject();
        special.addProperty("type", "minecraft:special");
        special.addProperty("base", fullModel);
        JsonObject specialModel = new JsonObject();
        specialModel.addProperty("type", TitamMods.MODID + ":seared_tank_fluid");
        special.add("model", specialModel);
        models.add(special);
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

    private List<CompletableFuture<?>> searedStairs(CachedOutput cache, String name, String tex) {
        Identifier id = id(name);
        return List.of(
                save(cache, stairsBlockstate(name),           blockstatePath.json(id)),
                save(cache, stairsModel(name, tex, ""),       modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name))),
                save(cache, stairsModel(name, tex, "_inner"), modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name + "_inner"))),
                save(cache, stairsModel(name, tex, "_outer"), modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name + "_outer"))),
                save(cache, blockClientItem(name),            itemPath.json(id))
        );
    }

    private List<CompletableFuture<?>> searedSlab(CachedOutput cache, String name, String tex, String fullBlock) {
        Identifier id = id(name);
        return List.of(
                save(cache, slabBlockstate(name, fullBlock),      blockstatePath.json(id)),
                save(cache, slabModel(name, tex, ""),             modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name))),
                save(cache, slabModel(name, tex, "_top"),         modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name + "_top"))),
                save(cache, blockClientItem(name),                itemPath.json(id))
        );
    }

    private List<CompletableFuture<?>> searedWall(CachedOutput cache, String name, String tex) {
        Identifier id = id(name);
        return List.of(
                save(cache, wallBlockstate(name),                        blockstatePath.json(id)),
                save(cache, wallModel(name, tex, "_post"),               modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name + "_post"))),
                save(cache, wallModel(name, tex, "_side"),               modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name + "_side"))),
                save(cache, wallModel(name, tex, "_side_tall"),          modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name + "_side_tall"))),
                save(cache, wallInventoryModel(name, tex),               modelBlockPath.json(Identifier.fromNamespaceAndPath(TitamMods.MODID, name + "_inventory"))),
                save(cache, wallClientItem(name),                        itemPath.json(id))
        );
    }

    private com.google.gson.JsonObject stairsBlockstate(String name) {
        var j = new com.google.gson.JsonObject();
        var variants = new com.google.gson.JsonObject();
        String[][] facing = {{"east","90"},{"north","180"},{"south","0"},{"west","270"}};
        String[][] half   = {{"bottom","0"},{"top","180"}};
        String[][] shape  = {{"inner_left","0"},{"inner_right","0"},{"outer_left","0"},{"outer_right","0"},{"straight","0"}};
        for (var f : facing) for (var h : half) for (var s : shape) {
            String key = "facing=" + f[0] + ",half=" + h[0] + ",shape=" + s[0];
            var model = new com.google.gson.JsonObject();
            String mname = "hephaestus:block/" + name;
            boolean inner = s[0].startsWith("inner"), outer = s[0].startsWith("outer");
            if (inner) mname += "_inner"; else if (outer) mname += "_outer";
            model.addProperty("model", mname);
            int y = Integer.parseInt(f[1]);
            boolean top = h[0].equals("top");
            if (outer || s[0].equals("straight")) {
                if (s[0].contains("left")) y = (y + 270) % 360;
            } else if (inner) {
                if (s[0].contains("left")) y = (y + 270) % 360;
            }
            if (y != 0) model.addProperty("y", y);
            if (top) model.addProperty("x", 180);
            if (top && (inner || outer || s[0].equals("straight"))) {
                boolean uv = false;
                if (s[0].equals("straight") || outer) uv = true;
                if (uv) model.addProperty("uvlock", true);
            }
            variants.add(key, model);
        }
        j.add("variants", variants);
        return j;
    }

    private com.google.gson.JsonObject stairsModel(String name, String tex, String suffix) {
        var j = new com.google.gson.JsonObject();
        String parent = suffix.isEmpty() ? "minecraft:block/stairs"
                : suffix.equals("_inner") ? "minecraft:block/inner_stairs"
                : "minecraft:block/outer_stairs";
        j.addProperty("parent", parent);
        var textures = new com.google.gson.JsonObject();
        textures.addProperty("bottom", tex);
        textures.addProperty("top",    tex);
        textures.addProperty("side",   tex);
        j.add("textures", textures);
        return j;
    }

    private com.google.gson.JsonObject slabBlockstate(String name, String fullBlock) {
        var j = new com.google.gson.JsonObject();
        var variants = new com.google.gson.JsonObject();
        var bottom = new com.google.gson.JsonObject(); bottom.addProperty("model", "hephaestus:block/" + name);
        var top    = new com.google.gson.JsonObject(); top.addProperty("model",    "hephaestus:block/" + name + "_top");
        var dbl    = new com.google.gson.JsonObject(); dbl.addProperty("model",    fullBlock);
        variants.add("type=bottom", bottom);
        variants.add("type=top",    top);
        variants.add("type=double", dbl);
        j.add("variants", variants);
        return j;
    }

    private com.google.gson.JsonObject slabModel(String name, String tex, String suffix) {
        var j = new com.google.gson.JsonObject();
        j.addProperty("parent", suffix.isEmpty() ? "minecraft:block/slab" : "minecraft:block/slab_top");
        var textures = new com.google.gson.JsonObject();
        textures.addProperty("bottom", tex);
        textures.addProperty("top",    tex);
        textures.addProperty("side",   tex);
        j.add("textures", textures);
        return j;
    }

    private com.google.gson.JsonObject wallBlockstate(String name) {
        var j = new com.google.gson.JsonObject();
        var mp = new com.google.gson.JsonObject();
        var apply = new com.google.gson.JsonObject();
        apply.addProperty("model", "hephaestus:block/" + name + "_post");
        var when = new com.google.gson.JsonObject();
        when.addProperty("up", "true");
        var postEntry = new com.google.gson.JsonObject();
        postEntry.add("when", when); postEntry.add("apply", apply);
        var multipart = new com.google.gson.JsonArray();
        var post = new com.google.gson.JsonObject();
        post.add("apply", apply);
        multipart.add(post);
        String[][] sides = {{"north","0"},{"east","90"},{"south","180"},{"west","270"}};
        for (var s : sides) {
            var sideApply = new com.google.gson.JsonObject();
            sideApply.addProperty("model", "hephaestus:block/" + name + "_side");
            if (!s[1].equals("0")) sideApply.addProperty("y", Integer.parseInt(s[1]));
            sideApply.addProperty("uvlock", true);
            var sideWhen = new com.google.gson.JsonObject();
            sideWhen.addProperty(s[0], "low");
            var sideEntry = new com.google.gson.JsonObject();
            sideEntry.add("when", sideWhen); sideEntry.add("apply", sideApply);
            multipart.add(sideEntry);
            var tallApply = new com.google.gson.JsonObject();
            tallApply.addProperty("model", "hephaestus:block/" + name + "_side_tall");
            if (!s[1].equals("0")) tallApply.addProperty("y", Integer.parseInt(s[1]));
            tallApply.addProperty("uvlock", true);
            var tallWhen = new com.google.gson.JsonObject();
            tallWhen.addProperty(s[0], "tall");
            var tallEntry = new com.google.gson.JsonObject();
            tallEntry.add("when", tallWhen); tallEntry.add("apply", tallApply);
            multipart.add(tallEntry);
        }
        j.add("multipart", multipart);
        return j;
    }

    private com.google.gson.JsonObject wallModel(String name, String tex, String suffix) {
        var j = new com.google.gson.JsonObject();
        String parent = switch (suffix) {
            case "_post"      -> "minecraft:block/template_wall_post";
            case "_side"      -> "minecraft:block/template_wall_side";
            case "_side_tall" -> "minecraft:block/template_wall_side_tall";
            default           -> "minecraft:block/template_wall_post";
        };
        j.addProperty("parent", parent);
        var textures = new com.google.gson.JsonObject();
        textures.addProperty("wall", tex);
        j.add("textures", textures);
        return j;
    }

    private com.google.gson.JsonObject wallInventoryModel(String name, String tex) {
        var j = new com.google.gson.JsonObject();
        j.addProperty("parent", "minecraft:block/wall_inventory");
        var textures = new com.google.gson.JsonObject();
        textures.addProperty("wall", tex);
        j.add("textures", textures);
        return j;
    }

    private com.google.gson.JsonObject wallClientItem(String name) {
        var j = new com.google.gson.JsonObject();
        var model = new com.google.gson.JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", "hephaestus:block/" + name + "_inventory");
        j.add("model", model);
        return j;
    }

}