// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival;

import com.mojang.logging.LogUtils;
import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModCreativeModeTabs;
import net.kipstudio.pumpkincarnival.init.ModEntities;
import net.kipstudio.pumpkincarnival.init.ModFeatures;
import net.kipstudio.pumpkincarnival.init.ModItems;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.kipstudio.pumpkincarnival.init.ModStructurePieceTypes;
import net.kipstudio.pumpkincarnival.init.ModStructureTypes;
import net.kipstudio.pumpkincarnival.worldgen.biome.CarnivalOverworldRegion;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
import terrablender.api.Regions;

@Mod(PumpkinCarnival.MOD_ID)
public class PumpkinCarnival {
    public static final String MOD_ID = "pumpkincarnival";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int OVERWORLD_REGION_WEIGHT = 5;

    public PumpkinCarnival() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GeckoLib.initialize();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModStructureTypes.register(modEventBus);
        ModStructurePieceTypes.register(modEventBus);

        modEventBus.addListener(ModCreativeModeTabs::registerTabs);
        modEventBus.addListener(this::commonSetup);

        LOGGER.info("[Pumpkin Carnival] Main initialization complete.");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Regions.register(new CarnivalOverworldRegion(
                    new ResourceLocation(MOD_ID, "overworld_carnival"),
                    OVERWORLD_REGION_WEIGHT
            ));
            LOGGER.info("[Pumpkin Carnival] TerraBlender region registered.");
        });
    }
}