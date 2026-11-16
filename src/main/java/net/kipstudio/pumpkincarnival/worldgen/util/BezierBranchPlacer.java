// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.util;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class BezierBranchPlacer {
    private BezierBranchPlacer() {
    }

    public static void traceBranch(WorldGenLevel level, BlockPos p0, BlockPos p1, BlockPos p2, boolean weathered, RandomSource random) {
        int approxDist = (int) Math.sqrt(p0.distSqr(p2));
        int steps = Math.max(6, approxDist * 2);

        BlockPos prev = p0;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double u = 1.0D - t;
            double x = u * u * p0.getX() + 2.0D * u * t * p1.getX() + t * t * p2.getX();
            double y = u * u * p0.getY() + 2.0D * u * t * p1.getY() + t * t * p2.getY();
            double z = u * u * p0.getZ() + 2.0D * u * t * p1.getZ() + t * t * p2.getZ();

            BlockPos current = new BlockPos((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
            if (!current.equals(prev)) {
                Direction.Axis axis = resolveAxis(prev, current);
                BlockState state = resolveSegmentState(i == steps, weathered, axis, random);
                WorldGenHelper.placeTreeBlock(level, current, state);
                prev = current;
            }
        }
    }

    private static Direction.Axis resolveAxis(BlockPos from, BlockPos to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        int dz = Math.abs(to.getZ() - from.getZ());

        if (dy >= dx && dy >= dz) {
            return Direction.Axis.Y;
        }
        return dx >= dz ? Direction.Axis.X : Direction.Axis.Z;
    }

    private static BlockState resolveSegmentState(boolean isTip, boolean weathered, Direction.Axis axis, RandomSource random) {
        if (isTip) {
            return ModBlocks.DEADWOOD_WOOD.get().defaultBlockState();
        }
        if (weathered && random.nextInt(3) == 0) {
            return ModBlocks.STRIPPED_DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
        }
        return ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
    }
}