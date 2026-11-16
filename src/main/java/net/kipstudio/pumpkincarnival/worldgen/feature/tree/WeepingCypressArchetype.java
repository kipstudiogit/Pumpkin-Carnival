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
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WeepingCypressArchetype implements DeadTreeArchetype {
    @Override
    public void generate(WorldGenLevel level, BlockPos origin, RandomSource random) {
        BlockState logY = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);

        int height = 10 + random.nextInt(3);
        Direction leanDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        BlockPos current = origin;
        for (int y = 0; y < height; y++) {
            WorldGenHelper.placeTreeBlock(level, current, logY);
            if (y == 3 || y == 5) {
                current = current.relative(leanDir);
            } else if (y == 8) {
                current = current.relative(leanDir.getOpposite());
            }
            current = current.above();
        }

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos start = current.below(1 + random.nextInt(2));
            BlockPos apex = start.relative(dir, 2).above(1);
            BlockPos tip = apex.relative(dir, 2).below(3 + random.nextInt(2));
            BezierBranchPlacer.traceBranch(level, start, apex, tip, false, random);
        }
    }
}