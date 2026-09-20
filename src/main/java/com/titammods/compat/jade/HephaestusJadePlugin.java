package com.titammods.compat.jade;

import com.titammods.block.MelterBlock;
import com.titammods.block.MelterBlockEntity;
import com.titammods.block.SmelteryControllerBlock;
import com.titammods.block.SmelteryControllerBlockEntity;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class HephaestusJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerItemStorage(HiddenItemStorageProvider.INSTANCE, MelterBlockEntity.class);
        registration.registerItemStorage(HiddenItemStorageProvider.INSTANCE, SmelteryControllerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MelterProgressProvider.INSTANCE, MelterBlock.class);
        registration.registerBlockComponent(SmelteryProgressProvider.INSTANCE, SmelteryControllerBlock.class);
    }
}