package com.titammods.datagen;

import com.titammods.TitamMods;
import com.titammods.setup.ModFluids;
import com.titammods.setup.ModItems;
import com.titammods.item.SmelteryTankItem;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TitamMods.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        createFluidBucket("molten_iron", ModFluids.MOLTEN_IRON);
        createFluidBucket("molten_gold", ModFluids.MOLTEN_GOLD);
        createFluidBucket("molten_copper", ModFluids.MOLTEN_COPPER);
        createFluidBucket("molten_steel", ModFluids.MOLTEN_STEEL);
        createFluidBucket("molten_cobalt", ModFluids.MOLTEN_COBALT);
        createFluidBucket("molten_quartz", ModFluids.MOLTEN_QUARTZ);
        createFluidBucket("molten_diamond", ModFluids.MOLTEN_DIAMOND);
        createFluidBucket("molten_emerald", ModFluids.MOLTEN_EMERALD);
        createFluidBucket("molten_amethyst", ModFluids.MOLTEN_AMETHYST);
        createFluidBucket("molten_brass", ModFluids.MOLTEN_BRASS);
        createFluidBucket("molten_zinc", ModFluids.MOLTEN_ZINC);

        createFluidBucket("molten_blaze", ModFluids.MOLTEN_BLAZE);

        basicItem(ModItems.COBALT_NUGGET.get());
        basicItem(ModItems.STEEL_NUGGET.get());

        for (var itemHolder : ModItems.ITEMS.getEntries()) {
            Item item = itemHolder.get();
            String name = itemHolder.getId().getPath();

            if (name.endsWith("_bucket")) continue;

            if (name.endsWith("_wall")) {
                String textureName = name.replace("seared_", "").replace("_wall", "");
                wallInventory(name, modLoc("block/smeltery/seared/" + textureName));
            }
            else if (item instanceof BlockItem || item instanceof SmelteryTankItem) {
                getBuilder(name).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + name)));
            }
            else {
                basicItem(item);
            }
        }
    }

    private void createFluidBucket(String name, ModFluids.MoltenFluid fluid) {
        withExistingParent(name + "_bucket", net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "item/bucket"))
                .customLoader(net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder::begin)
                .fluid(fluid.source.get());
    }
}