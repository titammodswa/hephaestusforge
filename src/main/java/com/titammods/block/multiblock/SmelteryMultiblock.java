package com.titammods.block.multiblock;

import com.titammods.block.SmelteryTankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SmelteryMultiblock {
    private final Level level;
    private final BlockPos controllerPos;

    public boolean isValid = false;
    public BlockPos minInner;
    public BlockPos maxInner;
    public int internalVolume = 0;

    public final List<BlockPos> tanks = new ArrayList<>();
    public final List<BlockPos> walls = new ArrayList<>();
    public final List<BlockPos> floor = new ArrayList<>();

    public SmelteryMultiblock(Level level, BlockPos controllerPos) {
        this.level = level;
        this.controllerPos = controllerPos;
    }

    public void scanStructure(Direction facing) {
        isValid = false;
        tanks.clear();
        walls.clear();
        floor.clear();
        internalVolume = 0;

        BlockState controllerState = level.getBlockState(controllerPos);
        if (controllerState.isAir()) return;

        BlockPos startInner = controllerPos.relative(facing.getOpposite());
        BlockPos floorCenter = startInner;
        boolean foundFloor = false;

        for (int i = 0; i < 16; i++) {
            if (!isInnerBlock(level.getBlockState(floorCenter))) {
                foundFloor = true;
                break;
            }
            floorCenter = floorCenter.below();
        }

        if (!foundFloor) return;
        if (!isValidFloor(level.getBlockState(floorCenter))) return;

        int floorY = floorCenter.getY();

        int minX = floorCenter.getX(), maxX = floorCenter.getX();
        int minZ = floorCenter.getZ(), maxZ = floorCenter.getZ();

        while (minX > floorCenter.getX() - 7 && isInnerBlock(level.getBlockState(new BlockPos(minX - 1, floorY + 1, floorCenter.getZ())))) minX--;
        while (maxX < floorCenter.getX() + 7 && isInnerBlock(level.getBlockState(new BlockPos(maxX + 1, floorY + 1, floorCenter.getZ())))) maxX++;
        while (minZ > floorCenter.getZ() - 7 && isInnerBlock(level.getBlockState(new BlockPos(floorCenter.getX(), floorY + 1, minZ - 1)))) minZ--;
        while (maxZ < floorCenter.getZ() + 7 && isInnerBlock(level.getBlockState(new BlockPos(floorCenter.getX(), floorY + 1, maxZ + 1)))) maxZ++;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos fPos = new BlockPos(x, floorY, z);
                if (!isValidFloor(level.getBlockState(fPos))) return;
                floor.add(fPos);
            }
        }

        int currentY = floorY + 1;
        boolean controllerFound = false;

        List<BlockPos> tempWalls = new ArrayList<>();
        List<BlockPos> tempTanks = new ArrayList<>();

        while (currentY < floorY + 64) {
            boolean isLayerValid = true;
            boolean hasWallsThisLayer = false;
            tempWalls.clear();
            tempTanks.clear();

            for (int x = minX - 1; x <= maxX + 1; x++) {
                for (int z = minZ - 1; z <= maxZ + 1; z++) {
                    BlockPos pos = new BlockPos(x, currentY, z);
                    BlockState state = level.getBlockState(pos);

                    boolean isXEdge = (x == minX - 1 || x == maxX + 1);
                    boolean isZEdge = (z == minZ - 1 || z == maxZ + 1);

                    if (isXEdge && isZEdge) continue;

                    if (isXEdge || isZEdge) {
                        if (pos.equals(controllerPos)) {
                            controllerFound = true;
                            hasWallsThisLayer = true;
                            continue;
                        }

                        if (isValidWall(state)) {
                            tempWalls.add(pos);
                            hasWallsThisLayer = true;
                            if (isTank(state)) {
                                tempTanks.add(pos);
                            }
                        } else {
                            isLayerValid = false;
                        }
                    } else {
                        if (!isInnerBlock(state)) {
                            isLayerValid = false;
                        }
                    }
                }
            }

            if (hasWallsThisLayer) {
                if (isLayerValid) {
                    walls.addAll(tempWalls);
                    tanks.addAll(tempTanks);
                } else {
                    return;
                }
            } else {
                break;
            }

            currentY++;
        }

        int height = (currentY - 1) - floorY;
        if (height > 0 && controllerFound) {
            this.minInner = new BlockPos(minX, floorY + 1, minZ);
            this.maxInner = new BlockPos(maxX, currentY - 1, maxZ);
            this.internalVolume = ((maxX - minX + 1) * (maxZ - minZ + 1) * height);
            this.isValid = true;
        }
    }

    private boolean isValidFloor(BlockState state) {
        return state.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "smeltery_floor")));
    }

    private boolean isValidWall(BlockState state) {
        return state.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "smeltery_wall"))) ||
                state.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "smeltery_floor"))) ||
                isTank(state);
    }

    private boolean isTank(BlockState state) {
        return state.getBlock() instanceof SmelteryTankBlock ||
                state.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "smeltery_tanks")));
    }

    private boolean isInnerBlock(BlockState state) {
        if (isValidWall(state) || isTank(state)) return false;
        return state.isAir() || !state.getFluidState().isEmpty();
    }
}