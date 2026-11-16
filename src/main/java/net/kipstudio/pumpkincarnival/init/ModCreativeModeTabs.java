// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.init;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;

public final class ModCreativeModeTabs {
    public static CreativeModeTab CARNIVAL_TAB;

    private ModCreativeModeTabs() {
    }

    public static void registerTabs(CreativeModeTabEvent.Register event) {
        CARNIVAL_TAB = event.registerCreativeModeTab(
                new ResourceLocation(PumpkinCarnival.MOD_ID, "carnival_tab"),
                builder -> builder
                        .icon(() -> new ItemStack(ModBlocks.DEADWOOD_SAPLING.get()))
                        .title(Component.translatable("creativetab.pumpkincarnival.carnival_tab"))
                        .displayItems((parameters, output) -> {
                            output.accept(ModItems.CARNIVAL_TICKET.get());
                            output.accept(ModItems.SOUL_CANDY.get());
                            output.accept(ModItems.CROW_SPAWN_EGG.get());
                            output.accept(ModBlocks.CARNIVAL_LANTERN.get());
                            output.accept(ModBlocks.DEADWOOD_LOG.get());
                            output.accept(ModBlocks.STRIPPED_DEADWOOD_LOG.get());
                            output.accept(ModBlocks.DEADWOOD_WOOD.get());
                            output.accept(ModBlocks.STRIPPED_DEADWOOD_WOOD.get());
                            output.accept(ModBlocks.DEADWOOD_PLANKS.get());
                            output.accept(ModBlocks.DEADWOOD_STAIRS.get());
                            output.accept(ModBlocks.DEADWOOD_SLAB.get());
                            output.accept(ModBlocks.DEADWOOD_FENCE.get());
                            output.accept(ModBlocks.DEADWOOD_FENCE_GATE.get());
                            output.accept(ModBlocks.DEADWOOD_DOOR.get());
                            output.accept(ModBlocks.DEADWOOD_TRAPDOOR.get());
                            output.accept(ModBlocks.DEADWOOD_PRESSURE_PLATE.get());
                            output.accept(ModBlocks.DEADWOOD_BUTTON.get());
                            output.accept(ModBlocks.DEADWOOD_SAPLING.get());
                        })
        );
    }
}