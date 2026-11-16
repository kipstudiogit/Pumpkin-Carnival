// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature;

import com.mojang.serialization.Codec;
import net.kipstudio.pumpkincarnival.worldgen.feature.farm.FarmBarnLayout;
import net.kipstudio.pumpkincarnival.worldgen.feature.farm.FarmBuildContext;
import net.kipstudio.pumpkincarnival.worldgen.feature.farm.FarmLayout;
import net.kipstudio.pumpkincarnival.worldgen.feature.farm.FarmManorLayout;
import net.kipstudio.pumpkincarnival.worldgen.feature.farm.FarmWindmillLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class PumpkinFarmFeature extends Feature<NoneFeatureConfiguration> {
    private static final int MAX_STRUCTURE_RADIUS = 14;
    private static final List<FarmLayout> LAYOUTS = List.of(
            new FarmManorLayout(),
            new FarmWindmillLayout(),
            new FarmBarnLayout()
    );

    public PumpkinFarmFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkPos chunkPos = new ChunkPos(context.origin());
        int centerX = chunkPos.getMiddleBlockX();
        int centerZ = chunkPos.getMiddleBlockZ();

        BlockPos centerProbe = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(centerX, 0, centerZ));
        if (!level.ensureCanWrite(centerProbe)) {
            return false;
        }

        int groundY = sampleFlatDryGroundY(level, centerX, centerZ);
        if (groundY == Integer.MIN_VALUE) {
            return false;
        }

        long deterministicSeed = level.getSeed() ^ ((long) chunkPos.x * 341873128712L + (long) chunkPos.z * 132897987541L);
        RandomSource random = RandomSource.create(deterministicSeed);

        FarmBuildContext buildContext = new FarmBuildContext(level, chunkPos, random);
        FarmLayout layout = LAYOUTS.get(random.nextInt(LAYOUTS.size()));
        layout.build(buildContext, new BlockPos(centerX, groundY, centerZ));
        buildContext.connectAllFences();

        return true;
    }

    private static int sampleFlatDryGroundY(WorldGenLevel level, int centerX, int centerZ) {
        int minHeight = Integer.MAX_VALUE;
        int maxHeight = Integer.MIN_VALUE;

        for (int dx = -MAX_STRUCTURE_RADIUS; dx <= MAX_STRUCTURE_RADIUS; dx += 5) {
            for (int dz = -MAX_STRUCTURE_RADIUS; dz <= MAX_STRUCTURE_RADIUS; dz += 5) {
                BlockPos probe = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(centerX + dx, 0, centerZ + dz));
                if (!level.ensureCanWrite(probe) || !level.getFluidState(probe.below()).isEmpty() || !level.getFluidState(probe).isEmpty()) {
                    return Integer.MIN_VALUE;
                }
                minHeight = Math.min(minHeight, probe.getY());
                maxHeight = Math.max(maxHeight, probe.getY());
            }
        }

        return (maxHeight - minHeight <= 3) ? minHeight : Integer.MIN_VALUE;
    }
}