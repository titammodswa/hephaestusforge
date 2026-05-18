package com.titammods.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class SearedLadderBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

    private static final Map<Direction, VoxelShape> COLLISION = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> COLLISION_BOTTOM = new EnumMap<>(Direction.class);

    private static final Map<Direction, VoxelShape> BOUNDS = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> BOUNDS_BOTTOM = new EnumMap<>(Direction.class);

    static {
        VoxelShape block = Shapes.block();

        COLLISION.put(Direction.NORTH, Shapes.join(block, Block.box( 2, 0,  0, 14, 16,  5), BooleanOp.ONLY_FIRST));
        COLLISION.put(Direction.SOUTH, Shapes.join(block, Block.box( 2, 0, 11, 14, 16, 16), BooleanOp.ONLY_FIRST));
        COLLISION.put(Direction.WEST,  Shapes.join(block, Block.box( 0, 0,  2,  5, 16, 14), BooleanOp.ONLY_FIRST));
        COLLISION.put(Direction.EAST,  Shapes.join(block, Block.box(11, 0,  2, 16, 16, 14), BooleanOp.ONLY_FIRST));

        BOUNDS.put(Direction.NORTH, Shapes.join(block, Shapes.or(
                Block.box(2,  0, 0, 14, 16, 2), Block.box(2,  0, 0, 14,  2, 3), Block.box(2,  4, 0, 14,  6, 3),
                Block.box(2,  8, 0, 14, 10, 3), Block.box(2, 12, 0, 14, 14, 3)), BooleanOp.ONLY_FIRST));

        BOUNDS.put(Direction.SOUTH, Shapes.join(block, Shapes.or(
                Block.box(2,  0, 14, 14, 16, 16), Block.box(2,  0, 13, 14,  2, 16), Block.box(2,  4, 13, 14,  6, 16),
                Block.box(2,  8, 13, 14, 10, 16), Block.box(2, 12, 13, 14, 14, 16)), BooleanOp.ONLY_FIRST));

        BOUNDS.put(Direction.WEST, Shapes.join(block, Shapes.or(
                Block.box(0,  0, 2, 2, 16, 14), Block.box(0,  0, 2, 3,  2, 14), Block.box(0,  4, 2, 3,  6, 14),
                Block.box(0,  8, 2, 3, 10, 14), Block.box(0, 12, 2, 3, 14, 14)), BooleanOp.ONLY_FIRST));

        BOUNDS.put(Direction.EAST, Shapes.join(block, Shapes.or(
                Block.box(14,  0, 2, 16, 16, 14), Block.box(13,  0, 2, 16,  2, 14), Block.box(13,  4, 2, 16,  6, 14),
                Block.box(13,  8, 2, 16, 10, 14), Block.box(13, 12, 2, 16, 14, 14)), BooleanOp.ONLY_FIRST));

        VoxelShape base = Block.box(0, 0, 0, 16, 2, 16);
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BOUNDS_BOTTOM.put(side, Shapes.or(BOUNDS.get(side), base));
            COLLISION_BOTTOM.put(side, Shapes.or(COLLISION.get(side), base));
        }
    }

    public SearedLadderBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BOTTOM, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BOTTOM);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateBelow = context.getLevel().getBlockState(context.getClickedPos().below());
        Direction direction = context.getHorizontalDirection().getOpposite();
        boolean isBottom = !(stateBelow.getBlock() instanceof SearedLadderBlock) || stateBelow.getValue(FACING) != direction;
        return this.defaultBlockState().setValue(FACING, direction).setValue(BOTTOM, isBottom);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            boolean isBottom = !(facingState.getBlock() instanceof SearedLadderBlock) || state.getValue(FACING) != facingState.getValue(FACING);
            return state.setValue(BOTTOM, isBottom);
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(BOTTOM) ? BOUNDS_BOTTOM.get(state.getValue(FACING)) : BOUNDS.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(BOTTOM) ? COLLISION_BOTTOM.get(state.getValue(FACING)) : COLLISION.get(state.getValue(FACING));
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }
}