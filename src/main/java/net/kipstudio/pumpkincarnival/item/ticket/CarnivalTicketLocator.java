// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.item.ticket;

import com.mojang.datafixers.util.Pair;
import net.kipstudio.pumpkincarnival.init.ModBiomes;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.kipstudio.pumpkincarnival.init.ModTags;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class CarnivalTicketLocator {
    private static final int BIOME_SEARCH_RADIUS = 6400;
    private static final int STRUCTURE_SEARCH_RADIUS_CHUNKS = 100;
    private static final double ARRIVAL_DISTANCE_SQ = 36.0D * 36.0D;

    private CarnivalTicketLocator() {
    }

    public static void guidePlayer(ServerLevel level, Player player, ItemStack ticketStack) {
        if (!CarnivalConditions.hasNaturalSkyCycle(level)) {
            player.displayClientMessage(
                    Component.translatable("actionbar.pumpkincarnival.ticket.wrong_dimension").withStyle(ChatFormatting.GRAY),
                    true
            );
            return;
        }

        BlockPos origin = player.blockPosition();
        boolean inMistyGrounds = CarnivalConditions.isInMistyGrounds(level, origin);

        Optional<CarnivalTicketData.BoundTarget> resolved = resolveTarget(level, ticketStack, origin, inMistyGrounds);
        if (resolved.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("actionbar.pumpkincarnival.ticket.no_destination").withStyle(ChatFormatting.DARK_GRAY),
                    true
            );
            return;
        }

        CarnivalTicketData.BoundTarget target = resolved.get();
        double dx = target.x() + 0.5D - player.getX();
        double dz = target.z() + 0.5D - player.getZ();
        double distSq = dx * dx + dz * dz;

        if (distSq <= ARRIVAL_DISTANCE_SQ && target.kind() == CarnivalTicketData.TargetKind.PUMPKIN_PLANTATION) {
            emitArrivalPulse(level, player);
            player.displayClientMessage(
                    Component.translatable("actionbar.pumpkincarnival.ticket.arrived_plantation").withStyle(ChatFormatting.GOLD),
                    true
            );
            return;
        }

        emitGuidingTrail(level, player, dx, dz);
        level.playSound(null, origin, ModSounds.CARNIVAL_START.get(), SoundSource.PLAYERS, 0.45F, 1.35F);

        int approxDistance = Math.max(1, Mth.floor(Math.sqrt(distSq)));
        Component directionText = getCardinalComponent(dx, dz);
        String key = target.kind() == CarnivalTicketData.TargetKind.PUMPKIN_PLANTATION
                ? "actionbar.pumpkincarnival.ticket.guide_plantation"
                : "actionbar.pumpkincarnival.ticket.guide_biome";
        player.displayClientMessage(
                Component.translatable(key, directionText, approxDistance).withStyle(ChatFormatting.GOLD),
                true
        );
    }

    private static Optional<CarnivalTicketData.BoundTarget> resolveTarget(ServerLevel level, ItemStack stack, BlockPos origin, boolean inMistyGrounds) {
        Optional<CarnivalTicketData.BoundTarget> cached = CarnivalTicketData.getTarget(stack);
        if (cached.isPresent() && cached.get().matchesDimension(level)) {
            CarnivalTicketData.BoundTarget current = cached.get();
            boolean shouldUpgradeToPlantation = inMistyGrounds && current.kind() == CarnivalTicketData.TargetKind.MISTY_GROUNDS;
            if (!shouldUpgradeToPlantation) {
                return cached;
            }
        }

        if (inMistyGrounds) {
            BlockPos plantationPos = level.findNearestMapStructure(ModTags.Structures.CARNIVAL_DESTINATIONS, origin, STRUCTURE_SEARCH_RADIUS_CHUNKS, false);
            if (plantationPos != null) {
                CarnivalTicketData.setTarget(stack, level, plantationPos, CarnivalTicketData.TargetKind.PUMPKIN_PLANTATION);
                return CarnivalTicketData.getTarget(stack);
            }
        }

        Pair<BlockPos, Holder<Biome>> biomeMatch = level.findClosestBiome3d(
                holder -> holder.is(ModBiomes.MISTY_GROUNDS),
                origin,
                BIOME_SEARCH_RADIUS,
                32,
                64
        );
        if (biomeMatch != null) {
            CarnivalTicketData.setTarget(stack, level, biomeMatch.getFirst(), CarnivalTicketData.TargetKind.MISTY_GROUNDS);
            return CarnivalTicketData.getTarget(stack);
        }

        BlockPos fallbackStructure = level.findNearestMapStructure(ModTags.Structures.CARNIVAL_DESTINATIONS, origin, STRUCTURE_SEARCH_RADIUS_CHUNKS, false);
        if (fallbackStructure != null) {
            CarnivalTicketData.setTarget(stack, level, fallbackStructure, CarnivalTicketData.TargetKind.PUMPKIN_PLANTATION);
            return CarnivalTicketData.getTarget(stack);
        }

        return Optional.empty();
    }

    private static void emitGuidingTrail(ServerLevel level, Player player, double dx, double dz) {
        Vec3 horizontal = new Vec3(dx, 0.0D, dz).normalize();
        Vec3 start = player.position().add(0.0D, player.getEyeHeight() * 0.75D, 0.0D);

        for (int step = 1; step <= 14; step++) {
            double progress = step * 0.55D;
            double arcY = Math.sin((step / 14.0D) * Math.PI) * 0.9D;
            double px = start.x + horizontal.x * progress;
            double py = start.y + arcY;
            double pz = start.z + horizontal.z * progress;

            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 1, 0.04D, 0.04D, 0.04D, 0.005D);
            if (step % 3 == 0) {
                level.sendParticles(ParticleTypes.SOUL, px, py, pz, 1, 0.06D, 0.06D, 0.06D, 0.01D);
            }
        }
    }

    private static void emitArrivalPulse(ServerLevel level, Player player) {
        level.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY(0.6D), player.getZ(), 14, 0.45D, 0.4D, 0.45D, 0.03D);
        level.playSound(null, player.blockPosition(), ModSounds.CARNIVAL_START.get(), SoundSource.PLAYERS, 0.65F, 1.1F);
    }

    public static Component getCardinalComponent(double dx, double dz) {
        double angle = Math.toDegrees(Math.atan2(dz, dx));
        double normalized = (angle + 360.0D) % 360.0D;
        int sector = (int) Math.round(normalized / 45.0D) % 8;
        String suffix = switch (sector) {
            case 0 -> "east";
            case 1 -> "southeast";
            case 2 -> "south";
            case 3 -> "southwest";
            case 4 -> "west";
            case 5 -> "northwest";
            case 6 -> "north";
            default -> "northeast";
        };
        return Component.translatable("direction.pumpkincarnival." + suffix);
    }
}