package com.titammods.common.blocks;

import com.mojang.serialization.MapCodec;
import com.titammods.common.blockentities.FaucetBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class SearedFaucetBlock extends BaseEntityBlock {

    public static final Property<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_NORTH = Shapes.join(Block.box(4, 4, 10, 12, 10, 16), Block.box(6, 6, 10, 10, 10, 16), BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_SOUTH = Shapes.join(Block.box(4, 4, 0, 12, 10, 6),   Block.box(6, 6, 0, 10, 10, 6),   BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_WEST  = Shapes.join(Block.box(10, 4, 4, 16, 10, 12), Block.box(10, 6, 6, 16, 10, 10), BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_EAST  = Shapes.join(Block.box(0, 4, 4, 6, 10, 12),   Block.box(0, 6, 6, 6, 10, 10),  BooleanOp.ONLY_FIRST);

    public static final MapCodec<SearedFaucetBlock> CODEC = simpleCodec(SearedFaucetBlock::new);

    @Override
    public MapCodec<SearedFaucetBlock> codec() {
        return CODEC;
    }

    public SearedFaucetBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST  -> SHAPE_WEST;
            case EAST  -> SHAPE_EAST;
            default    -> SHAPE_NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FaucetBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, entity) -> {
            if (entity instanceof FaucetBlockEntity faucet) faucet.tick();
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof FaucetBlockEntity faucet) {
                faucet.activate();
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                                   Block block, @Nullable Orientation orientation, boolean isMoving) {
        if (!level.isClientSide()) {
            boolean hasSignal = level.hasNeighborSignal(pos);
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof FaucetBlockEntity faucet) {
                faucet.handleRedstone(hasSignal);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof FaucetBlockEntity faucet) {
            if (faucet.hasRedstone() && !faucet.isPouring() && rand.nextFloat() < 0.25F) {
                Direction direction = state.getValue(FACING);
                double x = pos.getX() + 0.5D - 0.3D * direction.getStepX();
                double y = pos.getY() + 0.5D - 0.3D * direction.getStepY();
                double z = pos.getZ() + 0.5D - 0.3D * direction.getStepZ();

                level.addParticle(new DustParticleOptions(0xFFFF0000, 0.5f), x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}