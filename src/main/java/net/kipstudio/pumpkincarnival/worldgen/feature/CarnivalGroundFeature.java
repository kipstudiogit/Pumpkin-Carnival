// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CarnivalGroundFeature extends Feature<NoneFeatureConfiguration> {
    public CarnivalGroundFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        if (!level.ensureCanWrite(origin)) {
            return false;
        }

        int radius = 3 + random.nextInt(3);
        BlockState targetSoil = selectSoilPatch(random);
        applySoilPatch(level, origin, radius, targetSoil);

        if (random.nextInt(3) == 0) {
            BlockPos boulderPos = origin.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
            tryPlaceBoulder(level, boulderPos, random);
        }

        return true;
    }

    private static BlockState selectSoilPatch(RandomSource random) {
        return switch (random.nextInt(4)) {
            case 0 -> Blocks.PODZOL.defaultBlockState();
            case 1 -> Blocks.COARSE_DIRT.defaultBlockState();
            case 2 -> Blocks.ROOTED_DIRT.defaultBlockState();
            default -> Blocks.MUD.defaultBlockState();
        };
    }

    private static void applySoilPatch(WorldGenLevel level, BlockPos origin, int radius, BlockState targetSoil) {
        int radiusSq = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radiusSq) {
                    continue;
                }

                BlockPos target = origin.offset(x, 0, z);
                if (!level.ensureCanWrite(target)) {
                    continue;
                }

                BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, target).below();
                if (!level.ensureCanWrite(surface)) {
                    continue;
                }

                if (!level.getFluidState(surface).isEmpty() || !level.getFluidState(surface.above()).isEmpty()) {
                    continue;
                }

                BlockState current = level.getBlockState(surface);
                if (current.is(Blocks.GRASS_BLOCK) || current.is(Blocks.DIRT)) {
                    level.setBlock(surface, targetSoil, 2);
                }
            }
        }
    }

    private static void tryPlaceBoulder(WorldGenLevel level, BlockPos boulderPos, RandomSource random) {
        if (!level.ensureCanWrite(boulderPos)) {
            return;
        }

        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, boulderPos);
        if (!level.ensureCanWrite(surface) || !level.ensureCanWrite(surface.below())) {
            return;
        }

        if (!isDryLandPerimeter(level, surface)) {
            return;
        }

        if (level.getBlockState(surface.below()).isSolidRender(level, surface.below())) {
            BlockState baseStone = random.nextBoolean()
                    ? Blocks.MOSSY_COBBLESTONE.defaultBlockState()
                    : Blocks.COBBLESTONE.defaultBlockState();
            level.setBlock(surface, baseStone, 2);
            if (random.nextBoolean() && level.ensureCanWrite(surface.above())) {
                level.setBlock(surface.above(), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
            }
        }
    }

    private static boolean isDryLandPerimeter(WorldGenLevel level, BlockPos pos) {
        if (!level.getFluidState(pos).isEmpty() || !level.getFluidState(pos.below()).isEmpty()) {
            return false;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (!level.getFluidState(pos.relative(dir)).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}