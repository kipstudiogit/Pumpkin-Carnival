// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.function.Predicate;

public final class WorldGenHelper {
    private WorldGenHelper() {
    }

    public static boolean isNaturalSoil(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.MUD);
    }

    public static boolean isDrySurface(WorldGenLevel level, BlockPos pos) {
        return level.ensureCanWrite(pos)
                && level.ensureCanWrite(pos.below())
                && level.getFluidState(pos).isEmpty()
                && level.getFluidState(pos.below()).isEmpty();
    }

    public static boolean setBlockIfWritable(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (!level.ensureCanWrite(pos)) {
            return false;
        }
        level.setBlock(pos, state, 2);
        return true;
    }

    public static boolean placeTreeBlock(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (!level.ensureCanWrite(pos)) {
            return false;
        }
        if (level.isEmptyBlock(pos) || level.getBlockState(pos).getMaterial().isReplaceable()) {
            level.setBlock(pos, state, 2);
            return true;
        }
        return false;
    }

    public static void fillFoundation(WorldGenLevel level, BlockPos startPos, BlockState state, int maxDepth, Predicate<BlockPos> boundsCheck) {
        BlockPos current = startPos;
        int depth = 0;
        while (depth < maxDepth && current.getY() > level.getMinBuildHeight()) {
            if (!boundsCheck.test(current) || !level.ensureCanWrite(current)) {
                break;
            }
            BlockState existing = level.getBlockState(current);
            if (!level.isEmptyBlock(current) && !existing.getMaterial().isReplaceable()) {
                break;
            }
            level.setBlock(current, state, 2);
            current = current.below();
            depth++;
        }
    }

    public static void attachLootTable(WorldGenLevel level, BlockPos pos, RandomSource random, ResourceLocation lootTable) {
        RandomizableContainerBlockEntity.setLootTable(level, random, pos, lootTable);
    }

    public static void connectFence(WorldGenLevel level, BlockPos pos, Predicate<BlockPos> boundsCheck) {
        if (!boundsCheck.test(pos) || !level.ensureCanWrite(pos)) {
            return;
        }
        BlockState current = level.getBlockState(pos);
        if (!(current.getBlock() instanceof FenceBlock)) {
            return;
        }

        boolean north = canFenceConnect(level, pos.north(), Direction.SOUTH, boundsCheck);
        boolean south = canFenceConnect(level, pos.south(), Direction.NORTH, boundsCheck);
        boolean east = canFenceConnect(level, pos.east(), Direction.WEST, boundsCheck);
        boolean west = canFenceConnect(level, pos.west(), Direction.EAST, boundsCheck);

        level.setBlock(pos, current
                .setValue(FenceBlock.NORTH, north)
                .setValue(FenceBlock.SOUTH, south)
                .setValue(FenceBlock.EAST, east)
                .setValue(FenceBlock.WEST, west), 2);
    }

    private static boolean canFenceConnect(WorldGenLevel level, BlockPos neighborPos, Direction fromNeighbor, Predicate<BlockPos> boundsCheck) {
        if (!boundsCheck.test(neighborPos) || !level.ensureCanWrite(neighborPos)) {
            return false;
        }
        BlockState neighbor = level.getBlockState(neighborPos);
        return neighbor.is(BlockTags.FENCES)
                || neighbor.is(BlockTags.FENCE_GATES)
                || neighbor.isFaceSturdy(level, neighborPos, fromNeighbor, SupportType.FULL);
    }

    public static BooleanProperty getFenceDirectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> FenceBlock.NORTH;
            case SOUTH -> FenceBlock.SOUTH;
            case EAST -> FenceBlock.EAST;
            case WEST -> FenceBlock.WEST;
            default -> throw new IllegalArgumentException("Horizontal direction required: " + direction);
        };
    }
}