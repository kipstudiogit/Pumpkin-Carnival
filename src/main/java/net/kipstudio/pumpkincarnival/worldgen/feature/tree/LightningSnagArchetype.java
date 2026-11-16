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

public class LightningSnagArchetype implements DeadTreeArchetype {
    @Override
    public void generate(WorldGenLevel level, BlockPos origin, RandomSource random) {
        BlockState logY = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        BlockState strippedLog = ModBlocks.STRIPPED_DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        BlockState woodBark = ModBlocks.DEADWOOD_WOOD.get().defaultBlockState();

        int breakHeight = 8 + random.nextInt(3);

        for (int y = 0; y < breakHeight; y++) {
            BlockPos pos = origin.above(y);
            WorldGenHelper.placeTreeBlock(level, pos, (y > 3 && random.nextInt(3) == 0) ? strippedLog : logY);

            if (y <= 2) {
                Direction flank = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos flankPos = pos.relative(flank);
                WorldGenHelper.placeTreeBlock(level, flankPos, woodBark);
                if (y == 0 && level.ensureCanWrite(flankPos.below()) && WorldGenHelper.isNaturalSoil(level.getBlockState(flankPos.below()))) {
                    level.setBlock(flankPos.below(), Blocks.COARSE_DIRT.defaultBlockState(), 2);
                }
            }
        }

        WorldGenHelper.placeTreeBlock(level, origin.above(breakHeight), strippedLog);
        WorldGenHelper.placeTreeBlock(level, origin.above(breakHeight + 1), woodBark);
        if (random.nextBoolean()) {
            WorldGenHelper.placeTreeBlock(level, origin.above(breakHeight + 2), woodBark);
        }

        Direction liveDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos limbStart = origin.above(breakHeight - 2).relative(liveDir);
        BlockPos control = limbStart.relative(liveDir, 2).above(2);
        BlockPos limbEnd = limbStart.relative(liveDir, 5).above(1);
        BezierBranchPlacer.traceBranch(level, limbStart, control, limbEnd, true, random);

        BlockPos debris = origin.relative(liveDir.getOpposite(), 2);
        if (WorldGenHelper.isDrySurface(level, debris) && WorldGenHelper.isNaturalSoil(level.getBlockState(debris.below()))) {
            WorldGenHelper.placeTreeBlock(level, debris, woodBark);
            level.setBlock(debris.below(), Blocks.COARSE_DIRT.defaultBlockState(), 2);
        }
    }
}