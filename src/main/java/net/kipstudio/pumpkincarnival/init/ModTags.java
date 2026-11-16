// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.init;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class ModTags {
    private ModTags() {
    }

    public static final class Blocks {
        public static final TagKey<Block> DEADWOOD_LOGS = tag("deadwood_logs");

        private Blocks() {
        }

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(PumpkinCarnival.MOD_ID, name));
        }
    }

    public static final class Items {
        public static final TagKey<Item> DEADWOOD_LOGS = tag("deadwood_logs");

        private Items() {
        }

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(PumpkinCarnival.MOD_ID, name));
        }
    }

    public static final class Structures {
        public static final TagKey<Structure> CARNIVAL_DESTINATIONS =
                TagKey.create(Registries.STRUCTURE, new ResourceLocation(PumpkinCarnival.MOD_ID, "carnival_destinations"));

        private Structures() {
        }
    }
}