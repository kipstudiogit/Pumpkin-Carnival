// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.item.ticket;

import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModEntities;
import net.kipstudio.pumpkincarnival.init.ModItems;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

public final class CarnivalTicketRites {
    private static final double LANTERN_WARD_RADIUS = 32.0D;
    private static final double CROW_SIGHT_RADIUS = 24.0D;
    private static final int MAX_LOCAL_CROWS_FOR_SUMMON = 3;

    private CarnivalTicketRites() {
    }

    public static boolean invokeLanternWard(ServerLevel level, Player player, BlockPos lanternPos, ItemStack ticketStack) {
        if (!CarnivalTicketData.consumeStamp(ticketStack, player.getAbilities().instabuild)) {
            return false;
        }

        revealMonsters(level, lanternPos, LANTERN_WARD_RADIUS, 240);

        AABB playerBox = new AABB(lanternPos).inflate(8.0D);
        for (Player nearby : level.getEntitiesOfClass(Player.class, playerBox)) {
            nearby.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2400, 0));
            nearby.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 900, 0));
            nearby.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 0));
        }

        emitLanternRing(level, lanternPos, 18);
        level.playSound(null, lanternPos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 0.9F);
        level.playSound(null, lanternPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.55F, 1.25F);
        player.displayClientMessage(
                Component.translatable("actionbar.pumpkincarnival.ticket.lantern_ward").withStyle(ChatFormatting.GOLD),
                true
        );
        return true;
    }

    public static boolean invokeGrandLanternBoon(ServerLevel level, Player player, BlockPos lanternPos, ItemStack ticketStack) {
        if (CarnivalTicketData.getStamps(ticketStack) < CarnivalTicketData.MAX_STAMPS) {
            return false;
        }
        CarnivalTicketData.clearAllStamps(ticketStack, player.getAbilities().instabuild);

        revealMonsters(level, lanternPos, LANTERN_WARD_RADIUS, 360);

        AABB playerBox = new AABB(lanternPos).inflate(10.0D);
        for (Player nearby : level.getEntitiesOfClass(Player.class, playerBox)) {
            nearby.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
            nearby.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3600, 0));
            nearby.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1800, 0));
            nearby.addEffect(new MobEffectInstance(MobEffects.LUCK, 2400, 0));
        }

        int candyCount = 2 + level.random.nextInt(2);
        ItemStack candyStack = new ItemStack(ModItems.SOUL_CANDY.get(), candyCount);
        ItemEntity drop = new ItemEntity(level, lanternPos.getX() + 0.5D, lanternPos.getY() + 0.9D, lanternPos.getZ() + 0.5D, candyStack);
        drop.setDeltaMovement(0.0D, 0.2D, 0.0D);
        level.addFreshEntity(drop);

        emitLanternRing(level, lanternPos, 30);
        level.sendParticles(ParticleTypes.SOUL, lanternPos.getX() + 0.5D, lanternPos.getY() + 0.8D, lanternPos.getZ() + 0.5D, 16, 0.5D, 0.5D, 0.5D, 0.04D);
        level.playSound(null, lanternPos, ModSounds.CARNIVAL_START.get(), SoundSource.BLOCKS, 0.9F, 1.0F);

        player.displayClientMessage(
                Component.translatable("actionbar.pumpkincarnival.ticket.grand_boon").withStyle(ChatFormatting.LIGHT_PURPLE),
                true
        );
        return true;
    }

    public static boolean invokeCrowSight(ServerLevel level, Player player, ItemStack ticketStack) {
        if (!CarnivalTicketData.consumeStamp(ticketStack, player.getAbilities().instabuild)) {
            return false;
        }

        BlockPos origin = player.blockPosition();
        revealMonsters(level, origin, CROW_SIGHT_RADIUS, 160);
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0));

        List<CrowEntity> nearbyCrows = level.getEntitiesOfClass(
                CrowEntity.class,
                player.getBoundingBox().inflate(CROW_SIGHT_RADIUS)
        );
        for (CrowEntity crow : nearbyCrows) {
            if (!crow.isOrderedToSit()) {
                crow.getNavigation().moveTo(player, 1.35D);
            }
            crow.playSound(ModSounds.CROW_CAW.get(), 0.7F, 1.15F);
        }

        if (CarnivalConditions.isInMistyGrounds(level, origin) && nearbyCrows.size() < MAX_LOCAL_CROWS_FOR_SUMMON) {
            summonMistCrow(level, player);
        }

        level.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY(0.6D), player.getZ(), 14, 0.6D, 0.4D, 0.6D, 0.03D);
        level.playSound(null, origin, ModSounds.CROW_CAW.get(), SoundSource.PLAYERS, 0.8F, 0.95F);
        player.displayClientMessage(
                Component.translatable("actionbar.pumpkincarnival.ticket.crow_sight").withStyle(ChatFormatting.GOLD),
                true
        );
        return true;
    }

    public static boolean invokeCrowDispatch(ServerLevel level, Player player, CrowEntity crow, ItemStack ticketStack) {
        if (crow.isTame() && !crow.isOwnedBy(player)) {
            return false;
        }
        if (!crow.getMainHandItem().isEmpty()) {
            return false;
        }
        if (!CarnivalTicketData.consumeStamp(ticketStack, player.getAbilities().instabuild)) {
            return false;
        }

        if (!crow.isTame() && !ForgeEventFactory.onAnimalTame(crow, player)) {
            crow.tame(player);
            crow.setOrderedToSit(false);
            level.broadcastEntityEvent(crow, (byte) 7);
        }

        ItemStack gift = rollRoyalCrowGift(level.random, CarnivalConditions.isCarnivalMoonNight(level));
        crow.setForageCooldown(0);
        crow.setItemSlot(EquipmentSlot.MAINHAND, gift);
        crow.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        crow.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 0));
        crow.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 0));

        crow.playSound(ModSounds.CROW_CAW.get(), 0.9F, 1.2F);
        level.playSound(null, crow.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.85F, 1.2F);
        level.sendParticles(ParticleTypes.SOUL, crow.getX(), crow.getY(0.6D), crow.getZ(), 12, 0.25D, 0.25D, 0.25D, 0.03D);
        player.displayClientMessage(
                Component.translatable("actionbar.pumpkincarnival.ticket.crow_dispatch").withStyle(ChatFormatting.GOLD),
                true
        );
        return true;
    }

    private static void summonMistCrow(ServerLevel level, Player player) {
        CrowEntity crow = ModEntities.CROW.get().create(level);
        if (crow == null) {
            return;
        }

        double angle = level.random.nextDouble() * Math.PI * 2.0D;
        double dist = 5.0D + level.random.nextDouble() * 4.0D;
        int spawnX = (int) Math.round(player.getX() + Math.cos(angle) * dist);
        int spawnZ = (int) Math.round(player.getZ() + Math.sin(angle) * dist);
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(spawnX, 0, spawnZ));

        crow.moveTo(surface.getX() + 0.5D, surface.getY() + 2.5D, surface.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        crow.finalizeSpawn(level, level.getCurrentDifficultyAt(surface), MobSpawnType.MOB_SUMMONED, null, null);
        level.addFreshEntity(crow);
        crow.getNavigation().moveTo(player, 1.35D);
        level.sendParticles(ParticleTypes.SMOKE, crow.getX(), crow.getY(), crow.getZ(), 10, 0.3D, 0.3D, 0.3D, 0.02D);
    }

    private static ItemStack rollRoyalCrowGift(RandomSource random, boolean carnivalNight) {
        int roll = random.nextInt(100);
        if (carnivalNight && roll < 12) {
            return new ItemStack(ModBlocks.CARNIVAL_LANTERN.get());
        }
        if (roll < 15) {
            return new ItemStack(Items.DIAMOND);
        }
        if (roll < 40) {
            return new ItemStack(Items.EMERALD, 1 + random.nextInt(2));
        }
        if (roll < 65) {
            return new ItemStack(ModItems.SOUL_CANDY.get(), 1 + random.nextInt(2));
        }
        if (roll < 85) {
            return new ItemStack(Items.GOLD_INGOT, 1 + random.nextInt(2));
        }
        return new ItemStack(Items.AMETHYST_SHARD, 2 + random.nextInt(2));
    }

    private static void revealMonsters(ServerLevel level, BlockPos center, double radius, int durationTicks) {
        AABB area = new AABB(center).inflate(radius);
        for (Monster monster : level.getEntitiesOfClass(Monster.class, area, Monster::isAlive)) {
            monster.addEffect(new MobEffectInstance(MobEffects.GLOWING, durationTicks, 0, false, false));
        }
    }

    private static void emitLanternRing(ServerLevel level, BlockPos center, int points) {
        double cx = center.getX() + 0.5D;
        double cy = center.getY() + 0.6D;
        double cz = center.getZ() + 0.5D;

        for (int i = 0; i < points; i++) {
            double angle = (2.0D * Math.PI * i) / points;
            double rx = cx + Math.cos(angle) * 1.4D;
            double rz = cz + Math.sin(angle) * 1.4D;
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, rx, cy, rz, 1, 0.05D, 0.15D, 0.05D, 0.02D);
        }
    }
}