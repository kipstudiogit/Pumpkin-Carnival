// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature;

import com.mojang.serialization.Codec;
import net.kipstudio.pumpkincarnival.worldgen.util.ScarecrowBuilder;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PumpkinEffigyFeature extends Feature<NoneFeatureConfiguration> {
    public PumpkinEffigyFeature(Codec<NoneFeatureConfiguration> codec) {
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

        BlockPos ground = origin.below();
        BlockState groundState = level.getBlockState(ground);
        if (!groundState.is(Blocks.GRASS_BLOCK) && !groundState.is(Blocks.COARSE_DIRT) && !groundState.is(Blocks.PODZOL)) {
            return false;
        }

        Direction armDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        if (!hasClearance(level, origin, armDir)) {
            return false;
        }

        Direction faceDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        ScarecrowBuilder.build(level, origin, armDir, faceDir, random.nextInt(3) == 0, level::ensureCanWrite);
        level.setBlock(ground, Blocks.COARSE_DIRT.defaultBlockState(), 2);
        return true;
    }

    private static boolean hasClearance(WorldGenLevel level, BlockPos origin, Direction armDir) {
        BlockPos post2 = origin.above();
        BlockPos headPos = origin.above(2);
        BlockPos hatPos = origin.above(3);
        BlockPos arm1 = post2.relative(armDir);
        BlockPos arm2 = post2.relative(armDir.getOpposite());

        return level.ensureCanWrite(post2)
                && level.ensureCanWrite(headPos)
                && level.ensureCanWrite(hatPos)
                && level.ensureCanWrite(arm1)
                && level.ensureCanWrite(arm2)
                && level.isEmptyBlock(origin)
                && level.isEmptyBlock(post2)
                && level.isEmptyBlock(headPos)
                && level.isEmptyBlock(arm1)
                && level.isEmptyBlock(arm2);
    }
}