package com.titammods.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.TitamMods;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
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

    public static final Supplier<RecipeType<CastingBasinRecipe>> CASTING_BASIN_TYPE =
            TYPES.register("casting_basin", () -> new RecipeType<CastingBasinRecipe>() {
                @Override public String toString() { return "casting_basin"; }
            });

    public static final Supplier<RecipeSerializer<CastingBasinRecipe>> CASTING_BASIN_SERIALIZER =
            SERIALIZERS.register("casting_basin",
                    () -> new RecipeSerializer<>(CastingBasinRecipe.CODEC, CastingBasinRecipe.STREAM_CODEC));

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

        public record FluidRef(Identifier id, int amount) {
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
                new MeltingRecipe(ingredient, result.id(), result.amount(), fuel.id(), fuel.amount(), temp, t)));

        public static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, r.input());
                            buf.writeUtf(r.resultId().getNamespace()); buf.writeUtf(r.resultId().getPath());
                            buf.writeVarInt(r.resultAmount());
                            buf.writeUtf(r.fuelId().getNamespace());   buf.writeUtf(r.fuelId().getPath());
                            buf.writeVarInt(r.fuelAmount());
                            buf.writeVarInt(r.temperature());
                            buf.writeVarInt(r.time());
                        },
                        buf -> new MeltingRecipe(
                                Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                                Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()), buf.readVarInt(),
                                Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt())
                );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return input.test(inv.item()); }
        @Override public ItemStack assemble(SingleRecipeInput inv) { return ItemStack.EMPTY; }
        @Override public RecipeSerializer<MeltingRecipe> getSerializer() { return MELTING_SERIALIZER.get(); }
        @Override public RecipeType<MeltingRecipe> getType() { return MELTING_TYPE.get(); }
        @Override public RecipeBookCategory recipeBookCategory() { return HEPHAESTUS_CATEGORY.get(); }
        @Override public String group() { return ""; }
        @Override public boolean showNotification() { return false; }
        @Override public List<net.minecraft.world.item.crafting.display.RecipeDisplay> display() { return List.of(); }
        private static final PlacementInfo PLACEMENT = PlacementInfo.createFromOptionals(List.of());
        @Override public PlacementInfo placementInfo() { return PLACEMENT; }
    }

    public record CastingBasinRecipe(
            Identifier fluidId,
            int fluidAmount,
            Identifier resultId,
            int resultCount,
            int coolingTime
    ) implements Recipe<SingleRecipeInput> {

        public FluidStack fluidStack() {
            Fluid f = BuiltInRegistries.FLUID.getValue(fluidId);
            return (f == null || f.isSame(Fluids.EMPTY)) ? FluidStack.EMPTY : new FluidStack(f, fluidAmount);
        }

        public ItemStack result() {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.getValue(resultId);
            return (item == null || item == net.minecraft.world.item.Items.AIR)
                    ? ItemStack.EMPTY : new ItemStack(item, resultCount);
        }

        public record FluidRef(Identifier id, int amount) {
            static final Codec<FluidRef> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Identifier.CODEC.fieldOf("id").forGetter(FluidRef::id),
                    Codec.INT.fieldOf("amount").forGetter(FluidRef::amount)
            ).apply(inst, FluidRef::new));
        }

        private record ItemRef(Identifier id, int count) {
            static final Codec<ItemRef> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Identifier.CODEC.fieldOf("id").forGetter(ItemRef::id),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(ItemRef::count)
            ).apply(inst, ItemRef::new));
        }

        public static final MapCodec<CastingBasinRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidRef.CODEC.fieldOf("fluid").forGetter(r -> new FluidRef(r.fluidId(), r.fluidAmount())),
                ItemRef.CODEC.fieldOf("result").forGetter(r -> new ItemRef(r.resultId(), r.resultCount())),
                Codec.INT.fieldOf("cooling_time").forGetter(CastingBasinRecipe::coolingTime)
        ).apply(inst, (fluid, result, time) ->
                new CastingBasinRecipe(fluid.id(), fluid.amount(), result.id(), result.count(), time)));

        public static final StreamCodec<RegistryFriendlyByteBuf, CastingBasinRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            buf.writeUtf(r.fluidId().getNamespace()); buf.writeUtf(r.fluidId().getPath());
                            buf.writeVarInt(r.fluidAmount());
                            buf.writeUtf(r.resultId().getNamespace()); buf.writeUtf(r.resultId().getPath());
                            buf.writeVarInt(r.resultCount());
                            buf.writeVarInt(r.coolingTime());
                        },
                        buf -> new CastingBasinRecipe(
                                Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()), buf.readVarInt(),
                                Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()), buf.readVarInt(),
                                buf.readVarInt())
                );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv) { return result(); }
        @Override public RecipeSerializer<CastingBasinRecipe> getSerializer() { return CASTING_BASIN_SERIALIZER.get(); }
        @Override public RecipeType<CastingBasinRecipe> getType() { return CASTING_BASIN_TYPE.get(); }
        @Override public RecipeBookCategory recipeBookCategory() { return HEPHAESTUS_CATEGORY.get(); }
        @Override public String group() { return ""; }
        @Override public boolean showNotification() { return false; }
        @Override public List<net.minecraft.world.item.crafting.display.RecipeDisplay> display() { return List.of(); }
        private static final PlacementInfo PLACEMENT = PlacementInfo.createFromOptionals(List.of());
        @Override public PlacementInfo placementInfo() { return PLACEMENT; }
    }

    public static final Supplier<RecipeType<EntityMeltingRecipe>> ENTITY_MELTING_TYPE =
            TYPES.register("entity_melting", () -> new RecipeType<EntityMeltingRecipe>() {
                @Override public String toString() { return "entity_melting"; }
            });

    public static final Supplier<RecipeSerializer<EntityMeltingRecipe>> ENTITY_MELTING_SERIALIZER =
            SERIALIZERS.register("entity_melting",
                    () -> new RecipeSerializer<>(EntityMeltingRecipe.CODEC, EntityMeltingRecipe.STREAM_CODEC));

    public record EntityMeltingRecipe(
            net.minecraft.world.entity.EntityType<?> entityType,
            Identifier resultId,
            int resultAmount,
            int damage
    ) implements Recipe<SingleRecipeInput> {

        public net.neoforged.neoforge.fluids.FluidStack output() {
            net.minecraft.world.level.material.Fluid f = BuiltInRegistries.FLUID.getValue(resultId);
            return (f == null || f.isSame(net.minecraft.world.level.material.Fluids.EMPTY))
                    ? net.neoforged.neoforge.fluids.FluidStack.EMPTY
                    : new net.neoforged.neoforge.fluids.FluidStack(f, resultAmount);
        }

        public boolean matches(net.minecraft.world.entity.EntityType<?> type) {
            return this.entityType == type;
        }

        public static final MapCodec<EntityMeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                        .fieldOf("entity").forGetter(EntityMeltingRecipe::entityType),
                Identifier.CODEC.fieldOf("result_id").forGetter(EntityMeltingRecipe::resultId),
                Codec.INT.fieldOf("result_amount").forGetter(EntityMeltingRecipe::resultAmount),
                Codec.INT.optionalFieldOf("damage", 2).forGetter(EntityMeltingRecipe::damage)
        ).apply(inst, EntityMeltingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EntityMeltingRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(r.entityType());
                            buf.writeUtf(entityId.getNamespace()); buf.writeUtf(entityId.getPath());
                            buf.writeUtf(r.resultId().getNamespace()); buf.writeUtf(r.resultId().getPath());
                            buf.writeVarInt(r.resultAmount());
                            buf.writeVarInt(r.damage());
                        },
                        buf -> new EntityMeltingRecipe(
                                BuiltInRegistries.ENTITY_TYPE.getValue(
                                        Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf())),
                                Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()),
                                buf.readVarInt(),
                                buf.readVarInt()
                        )
                );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv) { return ItemStack.EMPTY; }
        @Override public RecipeSerializer<EntityMeltingRecipe> getSerializer() { return ENTITY_MELTING_SERIALIZER.get(); }
        @Override public RecipeType<EntityMeltingRecipe> getType() { return ENTITY_MELTING_TYPE.get(); }
        @Override public RecipeBookCategory recipeBookCategory() { return HEPHAESTUS_CATEGORY.get(); }
        @Override public String group() { return ""; }
        @Override public boolean showNotification() { return false; }
        @Override public List<net.minecraft.world.item.crafting.display.RecipeDisplay> display() { return List.of(); }
        private static final PlacementInfo PLACEMENT = PlacementInfo.createFromOptionals(List.of());
        @Override public PlacementInfo placementInfo() { return PLACEMENT; }
    }

    public static final Supplier<RecipeType<CastingTableRecipe>> CASTING_TABLE_TYPE =
            TYPES.register("casting_table", () -> new RecipeType<CastingTableRecipe>() {
                @Override public String toString() { return "casting_table"; }
            });

    public static final Supplier<RecipeSerializer<CastingTableRecipe>> CASTING_TABLE_SERIALIZER =
            SERIALIZERS.register("casting_table",
                    () -> new RecipeSerializer<>(CastingTableRecipe.CODEC, CastingTableRecipe.STREAM_CODEC));

    public record CastingTableRecipe(
            java.util.Optional<Ingredient> cast,
            boolean castConsumed,
            Identifier fluidId,
            int fluidAmount,
            Identifier resultId,
            int resultCount,
            int coolingTime
    ) implements Recipe<SingleRecipeInput> {

        public FluidStack fluidStack() {
            Fluid f = BuiltInRegistries.FLUID.getValue(fluidId);
            return (f == null || f.isSame(Fluids.EMPTY)) ? FluidStack.EMPTY : new FluidStack(f, fluidAmount);
        }

        public ItemStack result() {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.getValue(resultId);
            return (item == null || item == net.minecraft.world.item.Items.AIR)
                    ? ItemStack.EMPTY
                    : new ItemStack(item, resultCount);
        }

        public record FluidRef(Identifier id, int amount) {
            static final Codec<FluidRef> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Identifier.CODEC.fieldOf("id").forGetter(FluidRef::id),
                    Codec.INT.fieldOf("amount").forGetter(FluidRef::amount)
            ).apply(inst, FluidRef::new));
        }

        private record ItemRef(Identifier id, int count) {
            static final Codec<ItemRef> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Identifier.CODEC.fieldOf("id").forGetter(ItemRef::id),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(ItemRef::count)
            ).apply(inst, ItemRef::new));
        }

        public static final MapCodec<CastingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.optionalFieldOf("cast").forGetter(CastingTableRecipe::cast),
                Codec.BOOL.optionalFieldOf("cast_consumed", false).forGetter(CastingTableRecipe::castConsumed),
                FluidRef.CODEC.fieldOf("fluid").forGetter(r -> new FluidRef(r.fluidId(), r.fluidAmount())),
                ItemRef.CODEC.fieldOf("result").forGetter(r -> new ItemRef(r.resultId(), r.resultCount())),
                Codec.INT.fieldOf("cooling_time").forGetter(CastingTableRecipe::coolingTime)
        ).apply(inst, (cast, consumed, fluid, result, time) ->
                new CastingTableRecipe(cast, consumed, fluid.id(), fluid.amount(), result.id(), result.count(), time)));

        public static final StreamCodec<RegistryFriendlyByteBuf, CastingTableRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            buf.writeBoolean(r.cast().isPresent());
                            r.cast().ifPresent(ing -> Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing));
                            ByteBufCodecs.BOOL.encode(buf, r.castConsumed());
                            buf.writeUtf(r.fluidId().getNamespace()); buf.writeUtf(r.fluidId().getPath());
                            buf.writeVarInt(r.fluidAmount());
                            buf.writeUtf(r.resultId().getNamespace()); buf.writeUtf(r.resultId().getPath());
                            buf.writeVarInt(r.resultCount());
                            buf.writeVarInt(r.coolingTime());
                        },
                        buf -> {
                            boolean hasCast = buf.readBoolean();
                            java.util.Optional<Ingredient> cast = hasCast
                                    ? java.util.Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf))
                                    : java.util.Optional.empty();
                            return new CastingTableRecipe(
                                    cast,
                                    ByteBufCodecs.BOOL.decode(buf),
                                    Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()),
                                    buf.readVarInt(),
                                    Identifier.fromNamespaceAndPath(buf.readUtf(), buf.readUtf()),
                                    buf.readVarInt(),
                                    buf.readVarInt());
                        }
                );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv) { return result(); }
        @Override public RecipeSerializer<CastingTableRecipe> getSerializer() { return CASTING_TABLE_SERIALIZER.get(); }
        @Override public RecipeType<CastingTableRecipe> getType() { return CASTING_TABLE_TYPE.get(); }
        @Override public RecipeBookCategory recipeBookCategory() { return HEPHAESTUS_CATEGORY.get(); }
        @Override public String group() { return ""; }
        @Override public boolean showNotification() { return false; }
        @Override public List<net.minecraft.world.item.crafting.display.RecipeDisplay> display() { return List.of(); }
        private static final PlacementInfo PLACEMENT = PlacementInfo.createFromOptionals(List.of());
        @Override public PlacementInfo placementInfo() { return PLACEMENT; }
    }

    // ════════════════════════════════════════════════════════════════════════
    // CastingBasinRecipe — DEFERRED (bloco não portado ainda)
    // ════════════════════════════════════════════════════════════════════════

    /*
    public static final Supplier<RecipeType<CastingBasinRecipe>> CASTING_BASIN_TYPE = ...
    public static final Supplier<RecipeSerializer<CastingBasinRecipe>> CASTING_BASIN_SERIALIZER = ...
    public record CastingBasinRecipe(...) implements Recipe<SingleRecipeInput> { ... }
    */

    // ════════════════════════════════════════════════════════════════════════
    // AlloyRecipe — usa Identifier+int (lazy) igual ao MeltingRecipe
    // para evitar "Components not bound yet" no datagen
    // ════════════════════════════════════════════════════════════════════════

    public static final Supplier<RecipeType<AlloyRecipe>> ALLOY_TYPE =
            TYPES.register("alloy", () -> new RecipeType<AlloyRecipe>() {
                @Override public String toString() { return "alloy"; }
            });

    public static final Supplier<RecipeSerializer<AlloyRecipe>> ALLOY_SERIALIZER =
            SERIALIZERS.register("alloy",
                    () -> new RecipeSerializer<>(AlloyRecipe.CODEC, AlloyRecipe.STREAM_CODEC));

    public record AlloyRecipe(
            List<FluidRef> inputs,
            Identifier resultId,
            int resultAmount,
            int temperature
    ) implements Recipe<SingleRecipeInput> {

        /** Resolve inputs para FluidStack em runtime (fora do datagen). */
        public List<net.neoforged.neoforge.fluids.FluidStack> inputFluids() {
            return inputs.stream().map(FluidRef::toStack).filter(s -> !s.isEmpty()).toList();
        }

        /** Resolve output para FluidStack em runtime. */
        public net.neoforged.neoforge.fluids.FluidStack output() {
            net.minecraft.world.level.material.Fluid f = BuiltInRegistries.FLUID.getValue(resultId);
            return (f == null || f.isSame(net.minecraft.world.level.material.Fluids.EMPTY))
                    ? net.neoforged.neoforge.fluids.FluidStack.EMPTY
                    : new net.neoforged.neoforge.fluids.FluidStack(f, resultAmount);
        }

        public record FluidRef(Identifier id, int amount) {
            static final Codec<FluidRef> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Identifier.CODEC.fieldOf("id").forGetter(FluidRef::id),
                    Codec.INT.fieldOf("amount").forGetter(FluidRef::amount)
            ).apply(inst, FluidRef::new));

            net.neoforged.neoforge.fluids.FluidStack toStack() {
                net.minecraft.world.level.material.Fluid f = BuiltInRegistries.FLUID.getValue(id);
                return (f == null || f.isSame(net.minecraft.world.level.material.Fluids.EMPTY))
                        ? net.neoforged.neoforge.fluids.FluidStack.EMPTY
                        : new net.neoforged.neoforge.fluids.FluidStack(f, amount);
            }
        }

        public static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidRef.CODEC.listOf().fieldOf("inputs").forGetter(AlloyRecipe::inputs),
                FluidRef.CODEC.fieldOf("output").forGetter(r -> new FluidRef(r.resultId(), r.resultAmount())),
                Codec.INT.fieldOf("temperature").forGetter(AlloyRecipe::temperature)
        ).apply(inst, (inputs, out, temp) -> new AlloyRecipe(inputs, out.id(), out.amount(), temp)));

        public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            buf.writeVarInt(r.inputs().size());
                            for (FluidRef f : r.inputs()) {
                                buf.writeUtf(f.id().toString());
                                buf.writeVarInt(f.amount());
                            }
                            buf.writeUtf(r.resultId().toString());
                            buf.writeVarInt(r.resultAmount());
                            buf.writeVarInt(r.temperature());
                        },
                        buf -> {
                            int size = buf.readVarInt();
                            List<FluidRef> inputs = new java.util.ArrayList<>();
                            for (int i = 0; i < size; i++)
                                inputs.add(new FluidRef(Identifier.parse(buf.readUtf()), buf.readVarInt()));
                            return new AlloyRecipe(inputs,
                                    Identifier.parse(buf.readUtf()), buf.readVarInt(), buf.readVarInt());
                        }
                );

        @Override public boolean matches(SingleRecipeInput inv, Level level) { return false; }
        @Override public ItemStack assemble(SingleRecipeInput inv) { return ItemStack.EMPTY; }
        @Override public RecipeSerializer<AlloyRecipe> getSerializer() { return ALLOY_SERIALIZER.get(); }
        @Override public RecipeType<AlloyRecipe> getType() { return ALLOY_TYPE.get(); }
        @Override public RecipeBookCategory recipeBookCategory() { return HEPHAESTUS_CATEGORY.get(); }
        @Override public String group() { return ""; }
        @Override public boolean showNotification() { return false; }
        @Override public List<net.minecraft.world.item.crafting.display.RecipeDisplay> display() { return List.of(); }
        private static final PlacementInfo PLACEMENT = PlacementInfo.createFromOptionals(List.of());
        @Override public PlacementInfo placementInfo() { return PLACEMENT; }
    }
}