// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.init;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.minecraft.resources.ResourceLocation;

public final class ModLootTables {
    public static final ResourceLocation CHEST_PLANTATION_MANOR =
            new ResourceLocation(PumpkinCarnival.MOD_ID, "chests/plantation_manor");
    public static final ResourceLocation CHEST_CARNIVAL_RELIC =
            new ResourceLocation(PumpkinCarnival.MOD_ID, "chests/carnival_relic");
    public static final ResourceLocation CHEST_PUMPKIN_FARM =
            new ResourceLocation(PumpkinCarnival.MOD_ID, "chests/pumpkin_farm");

    private ModLootTables() {
    }
}