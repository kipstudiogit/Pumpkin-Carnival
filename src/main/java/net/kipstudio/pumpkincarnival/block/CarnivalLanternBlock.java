// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CarnivalLanternBlock extends LanternBlock {
    public CarnivalLanternBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double centerY = state.getValue(HANGING) ? 0.35D : 0.28D;
        double ventY = state.getValue(HANGING) ? 0.62D : 0.56D;

        double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.18D;
        double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.18D;

        if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.SMOKE, x, pos.getY() + ventY, z, 0.0D, 0.015D, 0.0D);
        }

        if (random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.FLAME, x, pos.getY() + centerY, z, 0.0D, 0.005D, 0.0D);
        }
    }
}