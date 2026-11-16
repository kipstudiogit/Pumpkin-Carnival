// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature;

import com.mojang.serialization.Codec;
import net.kipstudio.pumpkincarnival.worldgen.feature.tree.AncientOakArchetype;
import net.kipstudio.pumpkincarnival.worldgen.feature.tree.DeadTreeArchetype;
import net.kipstudio.pumpkincarnival.worldgen.feature.tree.LightningSnagArchetype;
import net.kipstudio.pumpkincarnival.worldgen.feature.tree.WeepingCypressArchetype;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class DeadTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final List<DeadTreeArchetype> ARCHETYPES = List.of(
            new AncientOakArchetype(),
            new LightningSnagArchetype(),
            new WeepingCypressArchetype()
    );

    public DeadTreeFeature(Codec<NoneFeatureConfiguration> codec) {
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

        if (!WorldGenHelper.isNaturalSoil(level.getBlockState(origin.below()))) {
            return false;
        }

        DeadTreeArchetype archetype = ARCHETYPES.get(random.nextInt(ARCHETYPES.size()));
        archetype.generate(level, origin, random);
        return true;
    }
}