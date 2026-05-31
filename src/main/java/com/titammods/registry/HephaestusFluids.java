package com.titammods.registry;

import com.titammods.registry.fluids.MoltenFluidSet;

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
        MOLTEN_ZINC    ("zinc",            0xFFA1AC9E,  419);

        public final String name;
        public final int    color;
        public final int    temperature;

        Material(String name, int color, int temperature) {
            this.name        = name;
            this.color       = color;
            this.temperature = temperature;
        }
    }

    public static final Map<Material, MoltenFluidSet> SETS = new HashMap<>();

    public static void registerFluids() {
        for (Material mat : Material.values()) {
            SETS.put(mat, new MoltenFluidSet(mat.name, mat.color, mat.temperature));
        }
    }
}
