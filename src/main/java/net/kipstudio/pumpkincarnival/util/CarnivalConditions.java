// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.util;

import net.kipstudio.pumpkincarnival.init.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public final class CarnivalConditions {
    private static final int FULL_MOON_PHASE = 0;

    private CarnivalConditions() {
    }

    public static boolean isInMistyGrounds(LevelReader level, BlockPos pos) {
        return level.getBiome(pos).is(ModBiomes.MISTY_GROUNDS);
    }

    public static boolean hasNaturalSkyCycle(Level level) {
        return level.dimensionType().natural() && level.dimensionType().hasSkyLight();
    }

    public static boolean isFullMoon(Level level) {
        return level.getMoonPhase() == FULL_MOON_PHASE;
    }

    public static boolean isCarnivalMoonNight(Level level) {
        return hasNaturalSkyCycle(level) && level.isNight() && isFullMoon(level);
    }
}