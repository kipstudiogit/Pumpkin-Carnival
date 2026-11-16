// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature.tree;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.worldgen.util.BezierBranchPlacer;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AncientOakArchetype implements DeadTreeArchetype {
    @Override
    public void generate(WorldGenLevel level, BlockPos origin, RandomSource random) {
        BlockState logY = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        BlockState woodBark = ModBlocks.DEADWOOD_WOOD.get().defaultBlockState();
        BlockState strippedLog = ModBlocks.STRIPPED_DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);

        placeButtressRoots(level, origin, woodBark, random);
        int forkHeight = 6 + random.nextInt(2);
        placeTrunk(level, origin, forkHeight, logY, strippedLog, random);
        placeScaffoldLimbs(level, origin.above(forkHeight), woodBark, random);
    }

    private void placeButtressRoots(WorldGenLevel level, BlockPos origin, BlockState woodBark, RandomSource random) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            int rootLength = 3 + random.nextInt(2);
            BlockPos rootStart = origin.above(1 + random.nextInt(2)).relative(dir);
            for (int r = 0; r < rootLength; r++) {
                BlockPos rootPos = rootStart.relative(dir, r).below(r);
                WorldGenHelper.placeTreeBlock(level, rootPos, woodBark);
                groundRootColumn(level, rootPos.below(), woodBark);
            }
        }
    }

    private void groundRootColumn(WorldGenLevel level, BlockPos startPos, BlockState woodBark) {
        BlockPos current = startPos;
        for (int depth = 0; depth < 3; depth++) {
            if (!level.ensureCanWrite(current)) {
                return;
            }
            BlockState existing = level.getBlockState(current);
            if (WorldGenHelper.isNaturalSoil(existing)) {
                level.setBlock(current, Blocks.COARSE_DIRT.defaultBlockState(), 2);
                return;
            }
            if (!level.isEmptyBlock(current) && !existing.getMaterial().isReplaceable()) {
                return;
            }
            level.setBlock(current, woodBark, 2);
            current = current.below();
        }
    }

    private void placeTrunk(WorldGenLevel level, BlockPos origin, int forkHeight, BlockState logY, BlockState strippedLog, RandomSource random) {
        Direction hollowFace = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        for (int y = 0; y < forkHeight; y++) {
            BlockPos center = origin.above(y);
            int radius = y < 2 ? 1 : 0;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > 1) {
                        continue;
                    }
                    BlockPos trunkPart = center.offset(dx, 0, dz);
                    if (y <= 1 && trunkPart.equals(center.relative(hollowFace))) {
                        if (level.ensureCanWrite(trunkPart) && level.getBlockState(trunkPart).getMaterial().isReplaceable()) {
                            level.setBlock(trunkPart, Blocks.AIR.defaultBlockState(), 2);
                        }
                    } else {
                        BlockState state = (y == 0 && dx == 0 && dz == 0) ? strippedLog : logY;
                        WorldGenHelper.placeTreeBlock(level, trunkPart, state);
                    }
                }
            }
        }
    }

    private void placeScaffoldLimbs(WorldGenLevel level, BlockPos forkCenter, BlockState woodBark, RandomSource random) {
        WorldGenHelper.placeTreeBlock(level, forkCenter, woodBark);

        Direction[] dirs = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        int startIndex = random.nextInt(dirs.length);

        for (int i = 0; i < 3; i++) {
            Direction dir = dirs[(startIndex + i) % dirs.length];
            int reachX = dir.getStepX() * (4 + random.nextInt(3));
            int reachZ = dir.getStepZ() * (4 + random.nextInt(3));
            int liftY = 4 + random.nextInt(3);

            BlockPos limbStart = forkCenter.relative(dir);
            BlockPos controlPoint = forkCenter.offset(reachX / 2, liftY / 2 + 1, reachZ / 2);
            BlockPos limbEnd = forkCenter.offset(reachX, liftY, reachZ);

            BezierBranchPlacer.traceBranch(level, limbStart, controlPoint, limbEnd, true, random);

            Direction stubDir = random.nextBoolean() ? dir.getClockWise() : dir.getCounterClockWise();
            WorldGenHelper.placeTreeBlock(level, controlPoint.relative(stubDir), woodBark);
            WorldGenHelper.placeTreeBlock(level, controlPoint.relative(stubDir).above(), woodBark);

            for (int t = 0; t < 2; t++) {
                Direction twigDir = t == 0 ? dir.getClockWise() : dir.getCounterClockWise();
                BlockPos twigEnd = limbEnd.relative(twigDir, 1 + random.nextInt(2)).above(random.nextInt(2));
                BezierBranchPlacer.traceBranch(level, limbEnd, limbEnd.above(), twigEnd, false, random);
            }
        }
    }
}