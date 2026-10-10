package com.titammods.registry;

import com.titammods.registry.fluids.MoltenFluidSet;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;

public class HephaestusFluids {

    public enum Material {
        SEARED_STONE   ("seared_stone",    0xFF3F3F3F,  800),
        SCORCHED_STONE ("scorched_stone",  0xFF4A2B27,  800),
        MOLTEN_ALUMINUM("aluminum",        0xFFB5B5D5,  660),
        MOLTEN_BRASS   ("brass",           0xFFE2B736,  930),
        MOLTEN_BRONZE  ("bronze",          0xFFE2A136,  950),
        MOLTEN_CONSTANTAN("constantan",    0xFFD18A58, 1220),
        MOLTEN_COPPER  ("copper",          0xFFE0734D, 1080),
        MOLTEN_ELECTRUM("electrum",        0xFFFFF07A, 1000),
        MOLTEN_ENDERIUM("enderium",        0xFF0B4D42, 1450),
        MOLTEN_GOLD    ("gold",            0xFFFCEE4B, 1060),
        MOLTEN_IESNIUM ("iesnium",         0xFF79ACB2, 1200),
        MOLTEN_INVAR   ("invar",           0xFFA4ADAA, 1420),
        MOLTEN_IRIDIUM ("iridium",         0xFFDFE4E4, 2440),
        MOLTEN_IRON    ("iron",            0xFFD8D8D8, 1538),
        MOLTEN_LEAD    ("lead",            0xFF485065,  327),
        MOLTEN_LUMIUM  ("lumium",          0xFFEAD880, 1000),
        MOLTEN_NETHERITE("netherite",      0xFF403C3D, 2000),
        MOLTEN_NICKEL  ("nickel",          0xFFC7C5A3, 1450),
        MOLTEN_OSMIUM  ("osmium",          0xFF8EAAB7, 3000),
        MOLTEN_PLATINUM("platinum",        0xFF58D2D2, 1768),
        MOLTEN_SIGNALUM("signalum",        0xFFF77620, 1000),
        MOLTEN_SILVER  ("silver",          0xFFC9D6DD,  960),
        MOLTEN_STEEL   ("steel",           0xFF565656, 1370),
        MOLTEN_TIN     ("tin",             0xFF9EACBB,  230),
        MOLTEN_URANIUM ("uranium",         0xFF4A554A, 1130),
        MOLTEN_ZINC    ("zinc",            0xFFA1AC9E,  419),
        MOLTEN_AETERNIUM  ("aeternium",    0xFF7A3EAA, 2000, "ftbarmory"),
        MOLTEN_ADAMANTITE ("adamantite",   0xFF435F64, 2000, "ftbarmory"),
        MOLTEN_AURICHALCUM("aurichalcum",  0xFFD77126, 2000, "ftbarmory"),
        MOLTEN_TITANIUM               ("titanium",               0xFFA28CB6, 1000),
        MOLTEN_CHROMIUM               ("chromium",               0xFFB5A6A6, 1000),
        MOLTEN_WROUGHT_IRON           ("wrought_iron",           0xFF58595D,  800),
        MOLTEN_CONDUCTIVE_ALLOY       ("conductive_alloy",       0xFFD79985,  800),
        MOLTEN_REDSTONE_ALLOY         ("redstone_alloy",         0xFFC7453A,  800),
        MOLTEN_ENERGETIC_ALLOY        ("energetic_alloy",        0xFFE6994A,  800),
        MOLTEN_PULSATING_ALLOY        ("pulsating_alloy",        0xFF5DBA8F,  800),
        MOLTEN_SOULARIUM              ("soularium",              0xFF62543F,  800),
        MOLTEN_VIBRANT_ALLOY          ("vibrant_alloy",          0xFFDDEF8C, 1000),
        MOLTEN_DARK_STEEL             ("dark_steel",             0xFF4C494E, 1000),
        MOLTEN_END_STEEL              ("end_steel",              0xFFB5B77F, 1600),
        MOLTEN_ENERGETIC_SILVER       ("energetic_silver",       0xFF8BAAC2,  800),
        MOLTEN_VIVID_ALLOY            ("vivid_alloy",            0xFF5BACBC, 1000),
        MOLTEN_ENERGIZED_COPPER       ("energized_copper",       0xFFE1A47B,  800),
        MOLTEN_ENERGIZED_GOLD         ("energized_gold",         0xFFE3E693,  800),
        MOLTEN_ADVANCED_ALLOY         ("advanced_alloy",         0xFF6E452D, 1000),
        MOLTEN_ENERGIZED_ALLOY        ("energized_alloy",        0xFF5397C6, 1000),
        MOLTEN_CRYSTALLIZED_ALLOY     ("crystallized_alloy",     0xFF0C5C95, 1000),
        MOLTEN_NIOBIUM                ("niobium",                0xFF625DB9, 1000),
        MOLTEN_SLATESTEEL             ("slatesteel",             0xFF4B4B4B, 1000),
        MOLTEN_TUNGSTEN_SLATESTEEL    ("tungsten_slatesteel",    0xFF746532, 1600),
        MOLTEN_DESH                   ("desh",                   0xFFCA8049, 1000),
        MOLTEN_FERRICORE              ("ferricore",              0xFF7BA3A3,  800),
        MOLTEN_BLAZEGOLD              ("blazegold",              0xFF8E4A45, 1000),
        MOLTEN_ECLIPSE_ALLOY          ("eclipse_alloy",          0xFF313B48, 1600),
        MOLTEN_THAUMIUM               ("thaumium",               0xFF50417B, 1000, "thaumaturge"),
        MOLTEN_VOID_METAL             ("void_metal",             0xFF1F1232, 1600, "thaumaturge"),
        MOLTEN_HELLFORGED             ("hellforged",             0xFF80C2C6, 1600),
        MOLTEN_ATLANTIC_GOLD          ("atlantic_gold",          0xFFC69434, 1200),
        MOLTEN_AQUARINE_STEEL         ("aquarine_steel",         0xFF77A48F, 1200),
        MOLTEN_DRAGONSTEEL_FIRE       ("dragonsteel_fire",       0xFFC9816B, 1600),
        MOLTEN_DRAGONSTEEL_ICE        ("dragonsteel_ice",        0xFF709FAF, 1600),
        MOLTEN_DRAGONSTEEL_LIGHTNING  ("dragonsteel_lightning",  0xFFAB96A9, 1600),
        MOLTEN_ADAMANT                ("adamant",                0xFF8395F1, 1000),
        MOLTEN_DURATIUM               ("duratium",               0xFF7C67A7, 1600),
        MOLTEN_PROMETHEUM             ("prometheum",             0xFF73B1A8, 1600),
        MOLTEN_RUBY                   ("ruby",                   0xFFC14A46, 1000),
        MOLTEN_SAPPHIRE               ("sapphire",               0xFF4360A5, 1000),
        MOLTEN_PERIDOT                ("peridot",                0xFF6DB03E, 1000),
        MOLTEN_TOPAZ                  ("topaz",                  0xFF87C6EC, 1000),
        MOLTEN_ONYX                   ("onyx",                   0xFF38211C, 1000),
        MOLTEN_GARNET                 ("garnet",                 0xFF941716, 1000),
        MOLTEN_MOONSTONE              ("moonstone",              0xFFBBC6D6,  800),
        MOLTEN_PRISMARINE             ("prismarine",             0xFF96BFB2,  800),
        MOLTEN_CERTUS_QUARTZ          ("certus_quartz",          0xFF6791B5,  800),
        MOLTEN_FLUIX                  ("fluix",                  0xFF352E5E, 1000),
        MOLTEN_ENTRO                  ("entro",                  0xFF118F82, 1000),
        MOLTEN_FLUXITE                ("fluxite",                0xFF872DAB, 1000),
        MOLTEN_XYCHORIUM              ("xychorium",              0xFF50D5E5,  800),
        MOLTEN_SPIRIT_ATTUNED         ("spirit_attuned",         0xFFB3ABBA, 1000),
        MOLTEN_DARK_GEM               ("dark_gem",               0xFF2B2B2B,  800),
        MOLTEN_CELESTINE              ("celestine",              0xFFC5CFF2,  800),
        MOLTEN_OLIVINE                ("olivine",                0xFF6A8528,  800),
        MOLTEN_DIMENSIONAL_SHARD      ("dimensional_shard",      0xFF93BCC7, 1000),
        MOLTEN_HEAVY_CORE      ("heavy_core",        0xFF636776, 2000),
        MOLTEN_GLOWSTONE       ("glowstone",         0xFFFBDA74,  800),
        MOLTEN_REDSTONE        ("redstone",          0xFFA41808,  600),
        MOLTEN_OBSIDIAN        ("obsidian",          0xFF100C1C, 1400),
        MOLTEN_GLASS           ("glass",             0xFFD0EAE9, 1000),
        MOLTEN_LAPIS           ("lapis",             0xFF1C3890,  900),
        MOLTEN_CARBON          ("carbon",            0xFF0C0001,  600),
        MOLTEN_ENDER           ("ender",             0xFF105E51, 1000),
        MOLTEN_ANCIENT_DEBRIS  ("ancient_debris",    0xFF4A2C23, 2000),
        MOLTEN_SHULKER_SHELL   ("shulker_shell",     0xFF956895, 1200),
        MOLTEN_SLIME           ("slime",             0xFF568F4E,  300),
        MOLTEN_MAGMA_CREAM     ("magma_cream",       0xFFE97823,  700),
        MOLTEN_WAX             ("wax",               0xFFFFB808,  320),
        LIQUID_MEAT            ("meat",              0xFFFC4E67,  200),
        MOLTEN_REFINED_GLOWSTONE("refined_glowstone",0xFFB1AA56,  900),
        MOLTEN_REFINED_OBSIDIAN ("refined_obsidian", 0xFF654C89, 1400);

        public final String name;
        public final int    color;
        public final int    temperature;
        public final String requiredMod;

        Material(String name, int color, int temperature) {
            this(name, color, temperature, null);
        }

        Material(String name, int color, int temperature, String requiredMod) {
            this.name        = name;
            this.color       = color;
            this.temperature = temperature;
            this.requiredMod = requiredMod;
        }

        public boolean isEnabled() {
            return requiredMod == null || ModList.get().isLoaded(requiredMod);
        }
    }

    public static final Map<Material, MoltenFluidSet> SETS = new HashMap<>();

    public static void registerFluids() {
        for (Material mat : Material.values()) {
            if (!mat.isEnabled()) continue;
            SETS.put(mat, new MoltenFluidSet(mat.name, mat.color, mat.temperature));
        }
    }
}