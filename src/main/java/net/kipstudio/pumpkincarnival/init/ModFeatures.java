// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.init;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.worldgen.feature.CarnivalGroundFeature;
import net.kipstudio.pumpkincarnival.worldgen.feature.CarnivalRelicFeature;
import net.kipstudio.pumpkincarnival.worldgen.feature.CarnivalVegetationFeature;
import net.kipstudio.pumpkincarnival.worldgen.feature.DeadTreeFeature;
import net.kipstudio.pumpkincarnival.worldgen.feature.FallenDeadwoodFeature;
import net.kipstudio.pumpkincarnival.worldgen.feature.MistyPoolFeature;
import net.kipstudio.pumpkincarnival.worldgen.feature.PumpkinEffigyFeature;
import net.kipstudio.pumpkincarnival.worldgen.feature.PumpkinFarmFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, PumpkinCarnival.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> DEAD_TREE =
            FEATURES.register("dead_tree", () -> new DeadTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FALLEN_DEADWOOD =
            FEATURES.register("fallen_deadwood", () -> new FallenDeadwoodFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PUMPKIN_EFFIGY =
            FEATURES.register("pumpkin_effigy", () -> new PumpkinEffigyFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CARNIVAL_GROUND =
            FEATURES.register("carnival_ground", () -> new CarnivalGroundFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CARNIVAL_VEGETATION =
            FEATURES.register("carnival_vegetation", () -> new CarnivalVegetationFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MISTY_POOL =
            FEATURES.register("misty_pool", () -> new MistyPoolFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CARNIVAL_RELIC =
            FEATURES.register("carnival_relic", () -> new CarnivalRelicFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PUMPKIN_FARM =
            FEATURES.register("pumpkin_farm", () -> new PumpkinFarmFeature(NoneFeatureConfiguration.CODEC));

    private ModFeatures() {
    }

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}