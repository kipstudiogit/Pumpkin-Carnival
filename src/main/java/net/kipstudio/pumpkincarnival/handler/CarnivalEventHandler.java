// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.handler;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.data.CarnivalSavedData;
import net.kipstudio.pumpkincarnival.event.CarnivalStartEvent;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PumpkinCarnival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CarnivalEventHandler {
    private static final long EVALUATION_INTERVAL_TICKS = 20L;
    private static final long TICKS_PER_DAY = 24000L;

    private CarnivalEventHandler() {
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()) {
            return;
        }

        if (!(event.level instanceof ServerLevel level) || !CarnivalConditions.hasNaturalSkyCycle(level)) {
            return;
        }

        if (level.getGameTime() % EVALUATION_INTERVAL_TICKS != 0L) {
            return;
        }

        evaluateCarnivalCycle(level);
    }

    private static void evaluateCarnivalCycle(ServerLevel level) {
        CarnivalSavedData savedData = CarnivalSavedData.get(level);
        long currentDay = level.getDayTime() / TICKS_PER_DAY;

        if (CarnivalConditions.isCarnivalMoonNight(level)) {
            if (!savedData.isCarnivalActive() && savedData.getLastTriggeredDay() != currentDay) {
                tryTriggerCarnival(level, savedData, currentDay);
            }
        } else if (savedData.isCarnivalActive()) {
            endCarnival(savedData);
        }
    }

    private static void tryTriggerCarnival(ServerLevel level, CarnivalSavedData savedData, long currentDay) {
        for (ServerPlayer player : level.players()) {
            if (CarnivalConditions.isInMistyGrounds(level, player.blockPosition())) {
                startCarnival(level, player, savedData, currentDay);
                return;
            }
        }
    }

    private static void startCarnival(ServerLevel level, ServerPlayer triggeringPlayer, CarnivalSavedData savedData, long currentDay) {
        savedData.setCarnivalActive(true);
        savedData.setLastTriggeredDay(currentDay);

        MinecraftForge.EVENT_BUS.post(new CarnivalStartEvent(level, triggeringPlayer));
        PumpkinCarnival.LOGGER.info("[Pumpkin Carnival] Carnival triggered by {} in Misty Grounds.", triggeringPlayer.getScoreboardName());

        notifyPlayersInMistyGrounds(level);
    }

    private static void endCarnival(CarnivalSavedData savedData) {
        savedData.setCarnivalActive(false);
        PumpkinCarnival.LOGGER.info("[Pumpkin Carnival] The carnival night has ended.");
    }

    private static void notifyPlayersInMistyGrounds(ServerLevel level) {
        Component title = Component.translatable("event.pumpkincarnival.carnival_awakens.title");
        Component subtitle = Component.translatable("event.pumpkincarnival.carnival_awakens.subtitle");

        for (ServerPlayer target : level.players()) {
            if (CarnivalConditions.isInMistyGrounds(level, target.blockPosition())) {
                target.connection.send(new ClientboundSetTitleTextPacket(title));
                target.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
                target.playNotifySound(ModSounds.CARNIVAL_START.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
            }
        }
    }
}