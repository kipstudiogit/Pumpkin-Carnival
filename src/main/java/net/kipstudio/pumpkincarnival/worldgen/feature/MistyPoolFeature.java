// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class MistyPoolFeature extends Feature<NoneFeatureConfiguration> {
    public MistyPoolFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int radius = 3 + random.nextInt(3);
        int minHeight = Integer.MAX_VALUE;
        int maxHeight = Integer.MIN_VALUE;

        for (int dx = -radius - 2; dx <= radius + 2; dx++) {
            for (int dz = -radius - 2; dz <= radius + 2; dz++) {
                BlockPos probe = origin.offset(dx, 0, dz);
                if (!level.ensureCanWrite(probe)) {
                    return false;
                }
                BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, probe);
                if (!level.getFluidState(surface).isEmpty() || !level.getFluidState(surface.below()).isEmpty()) {
                    return false;
                }
                minHeight = Math.min(minHeight, surface.getY());
                maxHeight = Math.max(maxHeight, surface.getY());
            }
        }

        if (maxHeight - minHeight > 1) {
            return false;
        }

        carveBasin(level, origin, radius, minHeight - 1, random);
        return true;
    }

    private static void carveBasin(WorldGenLevel level, BlockPos origin, int radius, int waterY, RandomSource random) {
        int innerRadiusSq = (radius - 1) * (radius - 1);
        int outerRadiusSq = radius * radius;

        for (int dx = -radius - 1; dx <= radius + 1; dx++) {
            for (int dz = -radius - 1; dz <= radius + 1; dz++) {
                int distSq = dx * dx + dz * dz;
                BlockPos waterPos = new BlockPos(origin.getX() + dx, waterY, origin.getZ() + dz);
                if (!level.ensureCanWrite(waterPos) || !level.ensureCanWrite(waterPos.below())) {
                    continue;
                }

                if (distSq <= innerRadiusSq) {
                    level.setBlock(waterPos, Blocks.WATER.defaultBlockState(), 2);
                    if (level.ensureCanWrite(waterPos.above())) {
                        level.setBlock(waterPos.above(), Blocks.AIR.defaultBlockState(), 2);
                        if (random.nextInt(6) == 0) {
                            level.setBlock(waterPos.above(), Blocks.LILY_PAD.defaultBlockState(), 2);
                        }
                    }
                    BlockState bed = random.nextBoolean() ? Blocks.MUD.defaultBlockState() : Blocks.CLAY.defaultBlockState();
                    level.setBlock(waterPos.below(), bed, 2);
                } else if (distSq <= outerRadiusSq) {
                    BlockState shore = random.nextBoolean() ? Blocks.MUD.defaultBlockState() : Blocks.COARSE_DIRT.defaultBlockState();
                    level.setBlock(waterPos, shore, 2);
                    level.setBlock(waterPos.below(), Blocks.MUD.defaultBlockState(), 2);

                    BlockPos aboveShore = waterPos.above();
                    if (level.ensureCanWrite(aboveShore) && level.isEmptyBlock(aboveShore) && random.nextInt(4) == 0) {
                        level.setBlock(aboveShore, Blocks.FERN.defaultBlockState(), 2);
                    }
                }
            }
        }
    }
}