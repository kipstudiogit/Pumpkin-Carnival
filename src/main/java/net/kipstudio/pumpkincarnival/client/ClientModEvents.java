// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.client;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.client.model.CrowModel;
import net.kipstudio.pumpkincarnival.client.renderer.CrowRenderer;
import net.kipstudio.pumpkincarnival.client.renderer.layer.CrowOnShoulderLayer;
import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModEntities;
import net.kipstudio.pumpkincarnival.init.ModItems;
import net.kipstudio.pumpkincarnival.item.ticket.CarnivalTicketData;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PumpkinCarnival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CARNIVAL_LANTERN.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.DEADWOOD_DOOR.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.DEADWOOD_TRAPDOOR.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.DEADWOOD_SAPLING.get(), RenderType.cutout());
            ItemProperties.register(
                    ModItems.CARNIVAL_TICKET.get(),
                    new ResourceLocation(PumpkinCarnival.MOD_ID, "attuned"),
                    (stack, level, entity, seed) -> CarnivalTicketData.isAttuned(stack) ? 1.0F : 0.0F
            );
        });
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CrowModel.LAYER_LOCATION, CrowModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CROW.get(), CrowRenderer::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new CrowOnShoulderLayer<>(playerRenderer, event.getEntityModels()));
            }
        }
    }
}