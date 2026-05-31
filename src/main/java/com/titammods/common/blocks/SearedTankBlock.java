package com.titammods.common.blocks;

import com.mojang.serialization.MapCodec;
import com.titammods.common.blockentities.SearedTankBlockEntity;
import com.titammods.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.ProblemReporter;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jspecify.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class SearedTankBlock extends BaseEntityBlock {

    public static final BooleanProperty EMITS_LIGHT = BooleanProperty.create("emits_light");

    public static final MapCodec<SearedTankBlock> CODEC = simpleCodec(SearedTankBlock::new);

    public SearedTankBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(EMITS_LIGHT, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EMITS_LIGHT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SearedTankBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                       BlockPos pos, Player player, InteractionHand hand,
                                       BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof SearedTankBlockEntity tank) {
            FluidTank handler = tank.getFluidTank();
            return FluidUtil.interactWithFluidHandler(player, hand, handler)
                    ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state,
                                       boolean includeData) {
        ItemStack stack = super.getCloneItemStack(level, pos, state, includeData);
        if (includeData && level.getBlockEntity(pos) instanceof SearedTankBlockEntity tank
                && !tank.getFluidTank().getFluid().isEmpty()) {
            var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING,
                    level.registryAccess());
            tank.saveWithId(output);
            BlockItem.setBlockEntityData(stack, ModBlockEntities.SEARED_TANK.get(), output);
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        var be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof SearedTankBlockEntity tank && !tank.getFluidTank().getFluid().isEmpty()) {
            if (params.getLevel() instanceof ServerLevel sl) {
                var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING,
                        sl.registryAccess());
                tank.saveWithId(output);
                drops.forEach(drop -> {
                    if (drop.getItem() == asItem())
                        BlockItem.setBlockEntityData(drop, ModBlockEntities.SEARED_TANK.get(), output);
                });
            }
        }
        return drops;
    }
}
