// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature.farm;

import net.minecraft.core.BlockPos;

@FunctionalInterface
public interface FarmLayout {
    void build(FarmBuildContext ctx, BlockPos center);
}