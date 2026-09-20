package com.titammods.compat.jade;

import com.titammods.common.blocks.MelterBlock;
import com.titammods.common.blocks.SmelteryControllerBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class HephaestusJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SmelteryJadeData.INSTANCE, SmelteryControllerBlock.class);
        registration.registerBlockDataProvider(MelterJadeData.INSTANCE, MelterBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(SmelteryJadeComponent.INSTANCE, SmelteryControllerBlock.class);
        registration.registerBlockComponent(MelterJadeComponent.INSTANCE, MelterBlock.class);
    }
}