package com.titammods.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.TitamMods;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

    public record CastingBasinRecipe(FluidStack input, ResourceLocation resultId, int resultCount, int time) implements Recipe<SingleRecipeInput> {

        public CastingBasinRecipe(FluidStack input, ItemStack output, int time) {
            this(input, BuiltInRegistries.ITEM.getKey(output.getItem()), output.getCount(), time);
        }

        public ItemStack result() {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.get(resultId);
            return item == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(item, resultCount);
        }

        public ItemStack output() { return result(); }

        public static final MapCodec<CastingBasinRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidStack.CODEC.fieldOf("input").forGetter(CastingBasinRecipe::input),
                ResourceLocation.CODEC.fieldOf("result").forGetter(CastingBasinRecipe::resultId),
                Codec.INT.optionalFieldOf("count", 1).forGetter(CastingBasinRecipe::resultCount),
                Codec.INT.fieldOf("time").forGetter(CastingBasinRecipe::time)
        ).apply(inst, CastingBasinRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CastingBasinRecipe> STREAM_CODEC = StreamCodec.composite(
                FluidStack.STREAM_CODEC, CastingBasinRecipe::input,
                net.minecraft.network.codec.ByteBufCodecs.fromCodec(ResourceLocation.CODEC), CastingBasinRecipe::resultId,
                ByteBufCodecs.INT, CastingBasinRecipe::resultCount,
                ByteBufCodecs.INT, CastingBasinRecipe::time,
                CastingBasinRecipe::new
        );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider lookup) { return result(); }
        @Override public boolean canCraftInDimensions(int w, int h) { return true; }
        @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return result(); }
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

    public record CastingTableRecipe(Ingredient cast, boolean castConsumed, FluidStack fluid, ResourceLocation resultId, int resultCount, int coolingTime) implements Recipe<SingleRecipeInput> {

        public CastingTableRecipe(Ingredient cast, boolean castConsumed, FluidStack fluid, ItemStack result, int coolingTime) {
            this(cast, castConsumed, fluid, BuiltInRegistries.ITEM.getKey(result.getItem()), result.getCount(), coolingTime);
        }

        public ItemStack result() {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.get(resultId);
            return item == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(item, resultCount);
        }

        public static final MapCodec<CastingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.optionalFieldOf("cast", Ingredient.EMPTY).forGetter(CastingTableRecipe::cast),
                Codec.BOOL.optionalFieldOf("cast_consumed", false).forGetter(CastingTableRecipe::castConsumed),
                FluidStack.CODEC.fieldOf("fluid").forGetter(CastingTableRecipe::fluid),
                ResourceLocation.CODEC.fieldOf("result").forGetter(CastingTableRecipe::resultId),
                Codec.INT.optionalFieldOf("count", 1).forGetter(CastingTableRecipe::resultCount),
                Codec.INT.fieldOf("cooling_time").forGetter(CastingTableRecipe::coolingTime)
        ).apply(inst, CastingTableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CastingTableRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CastingTableRecipe::cast,
                ByteBufCodecs.BOOL, CastingTableRecipe::castConsumed,
                FluidStack.STREAM_CODEC, CastingTableRecipe::fluid,
                net.minecraft.network.codec.ByteBufCodecs.fromCodec(ResourceLocation.CODEC), CastingTableRecipe::resultId,
                ByteBufCodecs.INT, CastingTableRecipe::resultCount,
                ByteBufCodecs.INT, CastingTableRecipe::coolingTime,
                CastingTableRecipe::new
        );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider lookup) { return result(); }
        @Override public boolean canCraftInDimensions(int w, int h) { return true; }
        @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return result(); }
        @Override public RecipeSerializer<?> getSerializer() { return CASTING_TABLE_SERIALIZER.get(); }
        @Override public RecipeType<?> getType() { return CASTING_TABLE_TYPE.get(); }
    }

    public static final Supplier<RecipeType<EntityMeltingRecipe>> ENTITY_MELTING_TYPE =
            TYPES.register("entity_melting", () -> new RecipeType<EntityMeltingRecipe>() {
                @Override public String toString() { return "entity_melting"; }
            });

    public static final Supplier<RecipeSerializer<EntityMeltingRecipe>> ENTITY_MELTING_SERIALIZER =
            SERIALIZERS.register("entity_melting", () -> new RecipeSerializer<EntityMeltingRecipe>() {
                @Override public MapCodec<EntityMeltingRecipe> codec() { return EntityMeltingRecipe.CODEC; }
                @Override public StreamCodec<RegistryFriendlyByteBuf, EntityMeltingRecipe> streamCodec() { return EntityMeltingRecipe.STREAM_CODEC; }
            });

    public record EntityMeltingRecipe(
            net.minecraft.world.entity.EntityType<?> entityType,
            FluidStack output,
            int damage
    ) implements Recipe<SingleRecipeInput> {

        public boolean matches(net.minecraft.world.entity.EntityType<?> type) {
            return this.entityType == type;
        }

        public static final MapCodec<EntityMeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                        .fieldOf("entity").forGetter(EntityMeltingRecipe::entityType),
                FluidStack.CODEC.fieldOf("result").forGetter(EntityMeltingRecipe::output),
                Codec.INT.optionalFieldOf("damage", 2).forGetter(EntityMeltingRecipe::damage)
        ).apply(inst, EntityMeltingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EntityMeltingRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            net.minecraft.resources.ResourceLocation entityId =
                                    net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(r.entityType());
                            buf.writeResourceLocation(entityId);
                            FluidStack.STREAM_CODEC.encode(buf, r.output());
                            buf.writeVarInt(r.damage());
                        },
                        buf -> new EntityMeltingRecipe(
                                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation()),
                                FluidStack.STREAM_CODEC.decode(buf),
                                buf.readVarInt()
                        )
                );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider lookup) { return ItemStack.EMPTY; }
        @Override public boolean canCraftInDimensions(int w, int h) { return true; }
        @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return ItemStack.EMPTY; }
        @Override public RecipeSerializer<?> getSerializer() { return ENTITY_MELTING_SERIALIZER.get(); }
        @Override public RecipeType<?> getType() { return ENTITY_MELTING_TYPE.get(); }
    }
}