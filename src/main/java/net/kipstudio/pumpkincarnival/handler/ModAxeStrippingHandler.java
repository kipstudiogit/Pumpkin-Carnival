// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.handler;

import com.google.common.base.Suppliers;
import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = PumpkinCarnival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModAxeStrippingHandler {
    private static final Supplier<Map<Block, Block>> STRIPPING_MAP = Suppliers.memoize(() -> Map.of(
            ModBlocks.DEADWOOD_LOG.get(), ModBlocks.STRIPPED_DEADWOOD_LOG.get(),
            ModBlocks.DEADWOOD_WOOD.get(), ModBlocks.STRIPPED_DEADWOOD_WOOD.get()
    ));

    private ModAxeStrippingHandler() {
    }

    @SubscribeEvent
    public static void onBlockToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getToolAction() != ToolActions.AXE_STRIP) {
            return;
        }

        BlockState state = event.getState();
        Block strippedBlock = STRIPPING_MAP.get().get(state.getBlock());
        if (strippedBlock != null) {
            event.setFinalState(strippedBlock.defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
        }
    }
}