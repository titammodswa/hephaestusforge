package com.titammods.setup;

import com.titammods.TitamMods;
import com.titammods.menu.MelterMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, TitamMods.MODID);

    public static final Supplier<MenuType<MelterMenu>> MELTER_MENU = MENUS.register("melter",
            () -> IMenuTypeExtension.create(MelterMenu::new));
    public static final Supplier<MenuType<com.titammods.menu.SmelteryMenu>> SMELTERY_MENU =
            MENUS.register("smeltery_menu", () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(com.titammods.menu.SmelteryMenu::new));
}