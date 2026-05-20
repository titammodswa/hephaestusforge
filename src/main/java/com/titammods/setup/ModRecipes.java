package com.titammods.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.TitamMods;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, TitamMods.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, TitamMods.MODID);
    public static final java.util.function.Supplier<net.minecraft.world.item.crafting.RecipeType<com.titammods.recipe.AlloyRecipe>> ALLOY_TYPE =
            TYPES.register("alloying", () -> new net.minecraft.world.item.crafting.RecipeType<com.titammods.recipe.AlloyRecipe>() {
                @Override
                public String toString() { return "alloying"; }
            });
    public static final java.util.function.Supplier<net.minecraft.world.item.crafting.RecipeSerializer<com.titammods.recipe.AlloyRecipe>> ALLOY_SERIALIZER =
            SERIALIZERS.register("alloying", () -> new net.minecraft.world.item.crafting.RecipeSerializer<com.titammods.recipe.AlloyRecipe>() {
                @Override
                public com.mojang.serialization.MapCodec<com.titammods.recipe.AlloyRecipe> codec() { return com.titammods.recipe.AlloyRecipe.CODEC; }
                @Override
                public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, com.titammods.recipe.AlloyRecipe> streamCodec() { return com.titammods.recipe.AlloyRecipe.STREAM_CODEC; }
            });

    public static final Supplier<RecipeType<MeltingRecipe>> MELTING_TYPE = TYPES.register("melting", () -> new RecipeType<MeltingRecipe>() {
        @Override public String toString() { return "melting"; }
    });

    public static final Supplier<RecipeSerializer<MeltingRecipe>> MELTING_SERIALIZER = SERIALIZERS.register("melting", () -> new RecipeSerializer<MeltingRecipe>() {
        @Override public MapCodec<MeltingRecipe> codec() { return MeltingRecipe.CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> streamCodec() { return MeltingRecipe.STREAM_CODEC; }
    });

    public record MeltingRecipe(Ingredient input, FluidStack output, FluidStack fuel, int temperature, int time) implements Recipe<SingleRecipeInput> {

        public static final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(MeltingRecipe::input),
                FluidStack.CODEC.fieldOf("result").forGetter(MeltingRecipe::output),
                FluidStack.CODEC.fieldOf("fuel").forGetter(MeltingRecipe::fuel),
                Codec.INT.fieldOf("temperature").forGetter(MeltingRecipe::temperature),
                Codec.INT.fieldOf("time").forGetter(MeltingRecipe::time)
        ).apply(inst, MeltingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, MeltingRecipe::input,
                FluidStack.STREAM_CODEC, MeltingRecipe::output,
                FluidStack.STREAM_CODEC, MeltingRecipe::fuel,
                ByteBufCodecs.INT, MeltingRecipe::temperature,
                ByteBufCodecs.INT, MeltingRecipe::time,
                MeltingRecipe::new
        );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return input.test(inv.item()); }
        @Override public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider lookup) { return ItemStack.EMPTY; }
        @Override public boolean canCraftInDimensions(int w, int h) { return true; }
        @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return ItemStack.EMPTY; }
        @Override public RecipeSerializer<?> getSerializer() { return MELTING_SERIALIZER.get(); }
        @Override public RecipeType<?> getType() { return MELTING_TYPE.get(); }
    }

    public static final Supplier<RecipeType<CastingBasinRecipe>> CASTING_BASIN_TYPE = TYPES.register("casting_basin", () -> new RecipeType<CastingBasinRecipe>() {
        @Override public String toString() { return "casting_basin"; }
    });

    public static final Supplier<RecipeSerializer<CastingBasinRecipe>> CASTING_BASIN_SERIALIZER = SERIALIZERS.register("casting_basin", () -> new RecipeSerializer<CastingBasinRecipe>() {
        @Override public MapCodec<CastingBasinRecipe> codec() { return CastingBasinRecipe.CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, CastingBasinRecipe> streamCodec() { return CastingBasinRecipe.STREAM_CODEC; }
    });

    public record CastingBasinRecipe(FluidStack input, ItemStack output, int time) implements Recipe<SingleRecipeInput> {

        public static final MapCodec<CastingBasinRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidStack.CODEC.fieldOf("input").forGetter(CastingBasinRecipe::input),
                ItemStack.CODEC.fieldOf("result").forGetter(CastingBasinRecipe::output),
                Codec.INT.fieldOf("time").forGetter(CastingBasinRecipe::time)
        ).apply(inst, CastingBasinRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CastingBasinRecipe> STREAM_CODEC = StreamCodec.composite(
                FluidStack.STREAM_CODEC, CastingBasinRecipe::input,
                ItemStack.STREAM_CODEC, CastingBasinRecipe::output,
                ByteBufCodecs.INT, CastingBasinRecipe::time,
                CastingBasinRecipe::new
        );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider lookup) { return output.copy(); }
        @Override public boolean canCraftInDimensions(int w, int h) { return true; }
        @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return output.copy(); }
        @Override public RecipeSerializer<?> getSerializer() { return CASTING_BASIN_SERIALIZER.get(); }
        @Override public RecipeType<?> getType() { return CASTING_BASIN_TYPE.get(); }
    }

    public static final Supplier<RecipeType<CastingTableRecipe>> CASTING_TABLE_TYPE = TYPES.register("casting_table", () -> new RecipeType<CastingTableRecipe>() {
        @Override public String toString() { return "casting_table"; }
    });

    public static final Supplier<RecipeSerializer<CastingTableRecipe>> CASTING_TABLE_SERIALIZER = SERIALIZERS.register("casting_table", () -> new RecipeSerializer<CastingTableRecipe>() {
        @Override public MapCodec<CastingTableRecipe> codec() { return CastingTableRecipe.CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, CastingTableRecipe> streamCodec() { return CastingTableRecipe.STREAM_CODEC; }
    });

    public record CastingTableRecipe(Ingredient cast, boolean castConsumed, FluidStack fluid, ItemStack result, int coolingTime) implements Recipe<SingleRecipeInput> {
        public static final MapCodec<CastingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.optionalFieldOf("cast", Ingredient.EMPTY).forGetter(CastingTableRecipe::cast),
                Codec.BOOL.optionalFieldOf("cast_consumed", false).forGetter(CastingTableRecipe::castConsumed),
                FluidStack.CODEC.fieldOf("fluid").forGetter(CastingTableRecipe::fluid),
                ItemStack.CODEC.fieldOf("result").forGetter(CastingTableRecipe::result),
                Codec.INT.fieldOf("cooling_time").forGetter(CastingTableRecipe::coolingTime)
        ).apply(inst, CastingTableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CastingTableRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CastingTableRecipe::cast,
                ByteBufCodecs.BOOL, CastingTableRecipe::castConsumed,
                FluidStack.STREAM_CODEC, CastingTableRecipe::fluid,
                ItemStack.STREAM_CODEC, CastingTableRecipe::result,
                ByteBufCodecs.INT, CastingTableRecipe::coolingTime,
                CastingTableRecipe::new
        );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider lookup) { return result.copy(); }
        @Override public boolean canCraftInDimensions(int w, int h) { return true; }
        @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return result.copy(); }
        @Override public RecipeSerializer<?> getSerializer() { return CASTING_TABLE_SERIALIZER.get(); }
        @Override public RecipeType<?> getType() { return CASTING_TABLE_TYPE.get(); }
    }
}
