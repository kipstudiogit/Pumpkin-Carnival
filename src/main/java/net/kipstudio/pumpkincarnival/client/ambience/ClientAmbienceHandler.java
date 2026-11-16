// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.client.ambience;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PumpkinCarnival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientAmbienceHandler {
    private static final int PROBE_RADIUS = 16;
    private static final int MAX_VERTICAL_DELTA = 6;

    private ClientAmbienceHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) {
            return;
        }

        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null || !CarnivalConditions.isInMistyGrounds(level, player.blockPosition())) {
            return;
        }

        RandomSource random = level.random;
        if (random.nextInt(3) != 0) {
            return;
        }

        int offsetX = random.nextInt(PROBE_RADIUS) - random.nextInt(PROBE_RADIUS);
        int offsetZ = random.nextInt(PROBE_RADIUS) - random.nextInt(PROBE_RADIUS);
        BlockPos probePos = player.blockPosition().offset(offsetX, 0, offsetZ);
        if (!level.isLoaded(probePos)) {
            return;
        }

        BlockPos ground = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, probePos);
        if (Math.abs(ground.getY() - player.getY()) <= MAX_VERTICAL_DELTA && random.nextInt(6) == 0) {
            double px = ground.getX() + random.nextDouble();
            double py = ground.getY() + 0.2D + random.nextDouble() * 0.6D;
            double pz = ground.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.SOUL, px, py, pz,
                    (random.nextDouble() - 0.5D) * 0.01D,
                    0.015D,
                    (random.nextDouble() - 0.5D) * 0.01D);
        }
    }
}