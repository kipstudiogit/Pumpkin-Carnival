// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.init;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomes {
    public static final ResourceKey<Biome> MISTY_GROUNDS = ResourceKey.create(
            Registries.BIOME,
            new ResourceLocation(PumpkinCarnival.MOD_ID, "misty_grounds")
    );

    private ModBiomes() {
    }
}