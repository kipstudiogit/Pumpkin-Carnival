// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature;

import com.mojang.serialization.Codec;
import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FallenDeadwoodFeature extends Feature<NoneFeatureConfiguration> {
    public FallenDeadwoodFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        if (!WorldGenHelper.isDrySurface(level, origin)) {
            return false;
        }

        BlockState belowState = level.getBlockState(origin.below());
        if (!belowState.is(Blocks.GRASS_BLOCK) && !belowState.is(Blocks.DIRT)) {
            return false;
        }

        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Direction.Axis axis = direction.getAxis();
        int length = 4 + random.nextInt(3);

        BlockState logState = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
        BlockState strippedState = ModBlocks.STRIPPED_DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);

        BlockPos current = origin;
        for (int i = 0; i < length; i++) {
            BlockPos ground = current.below();
            if (!WorldGenHelper.isDrySurface(level, current)) {
                break;
            }

            BlockState groundState = level.getBlockState(ground);
            boolean canReplace = level.isEmptyBlock(current) || level.getBlockState(current).getMaterial().isReplaceable();
            if (groundState.isSolidRender(level, ground) && canReplace) {
                level.setBlock(current, (i == length - 1 && random.nextBoolean()) ? strippedState : logState, 2);
                if (WorldGenHelper.isNaturalSoil(groundState)) {
                    level.setBlock(ground, Blocks.COARSE_DIRT.defaultBlockState(), 2);
                }

                BlockPos above = current.above();
                if (level.ensureCanWrite(above) && level.isEmptyBlock(above)) {
                    if (random.nextInt(4) == 0 && !level.canSeeSky(above)) {
                        level.setBlock(above, Blocks.BROWN_MUSHROOM.defaultBlockState(), 2);
                    } else if (random.nextInt(5) == 0) {
                        level.setBlock(above, Blocks.COBWEB.defaultBlockState(), 2);
                    }
                }
            }
            current = current.relative(direction);
        }

        return true;
    }
}