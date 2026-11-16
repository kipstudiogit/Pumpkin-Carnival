// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature;

import com.mojang.serialization.Codec;
import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CarnivalVegetationFeature extends Feature<NoneFeatureConfiguration> {
    private static final int ATTEMPTS = 36;

    public CarnivalVegetationFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        for (int i = 0; i < ATTEMPTS; i++) {
            BlockPos target = origin.offset(
                    random.nextInt(6) - random.nextInt(6),
                    random.nextInt(3) - random.nextInt(3),
                    random.nextInt(6) - random.nextInt(6)
            );

            if (!WorldGenHelper.isDrySurface(level, target) || !level.isEmptyBlock(target)) {
                continue;
            }

            BlockState soil = level.getBlockState(target.below());
            if (soil.is(Blocks.GRASS_BLOCK) || soil.is(Blocks.PODZOL) || soil.is(Blocks.COARSE_DIRT) || soil.is(Blocks.ROOTED_DIRT)) {
                placePlant(level, target, random);
            }
        }

        return true;
    }

    private static void placePlant(WorldGenLevel level, BlockPos target, RandomSource random) {
        int roll = random.nextInt(14);
        if (roll < 3) {
            level.setBlock(target, Blocks.FERN.defaultBlockState(), 2);
        } else if (roll < 5 && canPlaceDoublePlant(level, target)) {
            DoublePlantBlock.placeAt(level, Blocks.LARGE_FERN.defaultBlockState(), target, 2);
        } else if (roll < 7) {
            level.setBlock(target, Blocks.DEAD_BUSH.defaultBlockState(), 2);
        } else if (roll < 9) {
            level.setBlock(target, ModBlocks.DEADWOOD_SAPLING.get().defaultBlockState(), 2);
        } else if (roll == 9 && canPlaceDoublePlant(level, target)) {
            DoublePlantBlock.placeAt(level, Blocks.TALL_GRASS.defaultBlockState(), target, 2);
        } else if (roll == 10 && !level.canSeeSky(target)) {
            BlockState mushroom = random.nextBoolean() ? Blocks.BROWN_MUSHROOM.defaultBlockState() : Blocks.RED_MUSHROOM.defaultBlockState();
            level.setBlock(target, mushroom, 2);
        } else {
            level.setBlock(target, Blocks.GRASS.defaultBlockState(), 2);
        }
    }

    private static boolean canPlaceDoublePlant(WorldGenLevel level, BlockPos target) {
        return level.ensureCanWrite(target.above()) && level.isEmptyBlock(target.above());
    }
}