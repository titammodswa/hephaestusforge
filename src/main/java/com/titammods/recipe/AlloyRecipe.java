package com.titammods.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public record AlloyRecipe(List<FluidStack> inputs, FluidStack output, int temperature) implements Recipe<RecipeInput> {

    public static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            FluidStack.CODEC.listOf().fieldOf("inputs").forGetter(AlloyRecipe::inputs),
            FluidStack.CODEC.fieldOf("output").forGetter(AlloyRecipe::output),
            Codec.INT.fieldOf("temperature").forGetter(AlloyRecipe::temperature)
    ).apply(inst, AlloyRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, recipe) -> {
                buf.writeInt(recipe.inputs().size());
                for (FluidStack stack : recipe.inputs()) {
                    FluidStack.STREAM_CODEC.encode(buf, stack);
                }
                FluidStack.STREAM_CODEC.encode(buf, recipe.output());
                buf.writeInt(recipe.temperature());
            },
            buf -> {
                int size = buf.readInt();
                List<FluidStack> inputs = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    inputs.add(FluidStack.STREAM_CODEC.decode(buf));
                }
                FluidStack output = FluidStack.STREAM_CODEC.decode(buf);
                int temperature = buf.readInt();
                return new AlloyRecipe(inputs, output, temperature);
            }
    );

    @Override
    public boolean matches(RecipeInput input, Level level) { return false; } // Handled manually by Smeltery

//    @Override
//    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) { return ItemStack.EMPTY; }
//
//    @Override
//    public boolean canCraftInDimensions(int width, int height) { return false; }
//
//    @Override
//    public ItemStack getResultItem(HolderLookup.Provider provider) { return ItemStack.EMPTY; }
//
//    @Override
//    public RecipeSerializer<?> getSerializer() { return com.titammods.setup.ModRecipes.ALLOY_SERIALIZER.get(); }
//
//    @Override
//    public RecipeType<?> getType() { return com.titammods.setup.ModRecipes.ALLOY_TYPE.get(); }
}