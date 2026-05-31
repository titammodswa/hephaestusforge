package com.titammods.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.TitamMods;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, TitamMods.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, TitamMods.MODID);

    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_BOOK_CATEGORY, TitamMods.MODID);
    public static final Supplier<RecipeBookCategory> HEPHAESTUS_CATEGORY =
            RECIPE_BOOK_CATEGORIES.register("hephaestus", RecipeBookCategory::new);

    public static final Supplier<RecipeType<MeltingRecipe>> MELTING_TYPE =
            TYPES.register("melting", () -> new RecipeType<MeltingRecipe>() {
                @Override public String toString() { return "melting"; }
            });

    public static final Supplier<RecipeSerializer<MeltingRecipe>> MELTING_SERIALIZER =
            SERIALIZERS.register("melting",
                    () -> new RecipeSerializer<>(MeltingRecipe.CODEC, MeltingRecipe.STREAM_CODEC));

    public record MeltingRecipe(
            Ingredient input,
            Identifier resultId,
            int resultAmount,
            Identifier fuelId,
            int fuelAmount,
            int temperature,
            int time
    ) implements Recipe<SingleRecipeInput> {

        public FluidStack output() {
            Fluid f = BuiltInRegistries.FLUID.getValue(resultId);
            return (f == null || f.isSame(Fluids.EMPTY)) ? FluidStack.EMPTY : new FluidStack(f, resultAmount);
        }

        public FluidStack fuel() {
            Fluid f = BuiltInRegistries.FLUID.getValue(fuelId);
            return (f == null || f.isSame(Fluids.EMPTY)) ? FluidStack.EMPTY : new FluidStack(f, fuelAmount);
        }

        private record FluidRef(Identifier id, int amount) {
            static final Codec<FluidRef> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Identifier.CODEC.fieldOf("id").forGetter(FluidRef::id),
                    Codec.INT.fieldOf("amount").forGetter(FluidRef::amount)
            ).apply(inst, FluidRef::new));
        }

        public static final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(MeltingRecipe::input),
                FluidRef.CODEC.fieldOf("result").forGetter(r -> new FluidRef(r.resultId(), r.resultAmount())),
                FluidRef.CODEC.fieldOf("fuel").forGetter(r -> new FluidRef(r.fuelId(), r.fuelAmount())),
                Codec.INT.fieldOf("temperature").forGetter(MeltingRecipe::temperature),
                Codec.INT.fieldOf("time").forGetter(MeltingRecipe::time)
        ).apply(inst, (ingredient, result, fuel, temp, t) ->
                new MeltingRecipe(ingredient, result.id(), result.amount(),
                        fuel.id(), fuel.amount(), temp, t)));

        public static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, r.input());
                            buf.writeUtf(r.resultId().getNamespace());
                            buf.writeUtf(r.resultId().getPath());
                            buf.writeVarInt(r.resultAmount());
                            buf.writeUtf(r.fuelId().getNamespace());
                            buf.writeUtf(r.fuelId().getPath());
                            buf.writeVarInt(r.fuelAmount());
                            buf.writeVarInt(r.temperature());
                            buf.writeVarInt(r.time());
                        },
                        buf -> new MeltingRecipe(
                                Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                                Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()),
                                buf.readVarInt(),
                                Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()),
                                buf.readVarInt(),
                                buf.readVarInt(),
                                buf.readVarInt()
                        )
                );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return input.test(inv.item()); }
        @Override public ItemStack assemble(SingleRecipeInput inv) { return ItemStack.EMPTY; }
        @Override public RecipeSerializer<MeltingRecipe> getSerializer() { return MELTING_SERIALIZER.get(); }
        @Override public RecipeType<MeltingRecipe> getType() { return MELTING_TYPE.get(); }
        @Override public RecipeBookCategory recipeBookCategory() { return HEPHAESTUS_CATEGORY.get(); }
        @Override public String group() { return ""; }
        @Override public boolean showNotification() { return false; }
        @Override public java.util.List<net.minecraft.world.item.crafting.display.RecipeDisplay> display() { return java.util.List.of(); }

        private static final PlacementInfo PLACEMENT =
                PlacementInfo.createFromOptionals(java.util.List.of());

        @Override public PlacementInfo placementInfo() { return PLACEMENT; }
    }

}
