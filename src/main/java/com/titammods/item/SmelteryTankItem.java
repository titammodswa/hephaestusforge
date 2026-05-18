package com.titammods.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class SmelteryTankItem extends BlockItem {

    public SmelteryTankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            var customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (customData != null) {
                var tag = customData.copyTag();

                if (tag.contains("tank")) {
                    FluidTank dummyTank = new FluidTank(4000);
                    dummyTank.readFromNBT(context.registries(), tag.getCompound("tank"));

                    FluidStack fluid = dummyTank.getFluid();
                    if (!fluid.isEmpty()) {
                        tooltip.add(fluid.getHoverName().plainCopy()
                                .withStyle(ChatFormatting.GRAY)
                                .append(Component.literal(": " + fluid.getAmount() + " / " + dummyTank.getCapacity() + " mB")
                                        .withStyle(ChatFormatting.DARK_GRAY)));
                    } else {
                        tooltip.add(Component.literal("Vazio").withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
        } else {
            tooltip.add(Component.literal("Capacidade: 4000 mB").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}