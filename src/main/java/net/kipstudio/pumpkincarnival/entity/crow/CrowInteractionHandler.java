// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.entity.crow;

import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.kipstudio.pumpkincarnival.init.ModItems;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

public final class CrowInteractionHandler {
    private static final int FORAGE_COOLDOWN_TICKS = 1200;
    private static final int GIFT_SEARCH_DURATION_TICKS = 40;

    private CrowInteractionHandler() {
    }

    public static InteractionResult handleInteract(CrowEntity crow, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        if (!crow.getMainHandItem().isEmpty() && CrowBehaviorConfig.isTameItem(heldStack)) {
            return handleBarter(crow, player, heldStack);
        }

        if (!crow.isTame()) {
            if (CrowBehaviorConfig.isTameItem(heldStack)) {
                return handleTaming(crow, player, heldStack);
            }
            return InteractionResult.PASS;
        }

        if (crow.getHealth() < crow.getMaxHealth() && CrowBehaviorConfig.isFoodItem(heldStack)) {
            return handleHealing(crow, player, heldStack);
        }

        if (crow.isOwnedBy(player)) {
            return handleOwnerCommand(crow, player, heldStack);
        }

        return InteractionResult.PASS;
    }

    private static InteractionResult handleBarter(CrowEntity crow, Player player, ItemStack heldStack) {
        if (!crow.level.isClientSide) {
            boolean usedSoulCandy = heldStack.is(ModItems.SOUL_CANDY.get());
            consumeOne(player, heldStack);
            crow.dropHeldItemToward(player);
            crow.playSound(SoundEvents.PARROT_EAT, 0.8F, 1.1F);
            crow.playSound(ModSounds.CROW_CAW.get(), 0.75F, 1.1F);
            spawnParticles(crow, ParticleTypes.HAPPY_VILLAGER, 6);

            if (!crow.isTame() && usedSoulCandy && !ForgeEventFactory.onAnimalTame(crow, player)) {
                crow.tame(player);
                crow.getNavigation().stop();
                crow.setOrderedToSit(true);
                crow.level.broadcastEntityEvent(crow, (byte) 7);
            }
        }
        return InteractionResult.sidedSuccess(crow.level.isClientSide);
    }

    private static InteractionResult handleTaming(CrowEntity crow, Player player, ItemStack heldStack) {
        if (!crow.level.isClientSide) {
            boolean guaranteed = heldStack.is(ModItems.SOUL_CANDY.get());
            consumeOne(player, heldStack);
            crow.playSound(SoundEvents.PARROT_EAT, 0.8F, 1.1F);

            if ((guaranteed || crow.getRandom().nextInt(3) == 0) && !ForgeEventFactory.onAnimalTame(crow, player)) {
                crow.tame(player);
                crow.getNavigation().stop();
                crow.setOrderedToSit(true);
                crow.level.broadcastEntityEvent(crow, (byte) 7);
                if (guaranteed) {
                    spawnParticles(crow, ParticleTypes.SOUL, 5);
                }
            } else {
                crow.level.broadcastEntityEvent(crow, (byte) 6);
            }
        }
        return InteractionResult.sidedSuccess(crow.level.isClientSide);
    }

    private static InteractionResult handleHealing(CrowEntity crow, Player player, ItemStack heldStack) {
        if (!crow.level.isClientSide) {
            float healAmount = heldStack.is(ModItems.SOUL_CANDY.get()) ? 4.0F : 2.0F;
            consumeOne(player, heldStack);
            crow.heal(healAmount);
            crow.playSound(SoundEvents.PARROT_EAT, 0.8F, 1.1F);
            spawnParticles(crow, ParticleTypes.HAPPY_VILLAGER, 5);
        }
        return InteractionResult.sidedSuccess(crow.level.isClientSide);
    }

    private static InteractionResult handleOwnerCommand(CrowEntity crow, Player player, ItemStack heldStack) {
        if (!crow.getMainHandItem().isEmpty()) {
            if (!crow.level.isClientSide) {
                crow.dropHeldItemToward(player);
                crow.playSound(SoundEvents.ITEM_PICKUP, 0.4F, 1.2F);
            }
            return InteractionResult.sidedSuccess(crow.level.isClientSide);
        }

        if (heldStack.is(ModItems.CARNIVAL_TICKET.get())) {
            return InteractionResult.PASS;
        }

        if (heldStack.is(ModItems.SOUL_CANDY.get())) {
            if (!crow.level.isClientSide) {
                if (crow.getForageCooldown() <= 0 && crow.getPendingGiftTicks() <= 0) {
                    consumeOne(player, heldStack);
                    crow.playSound(SoundEvents.PARROT_EAT, 0.8F, 1.1F);
                    crow.playSound(ModSounds.CROW_CAW.get(), 0.7F, 1.2F);
                    spawnParticles(crow, ParticleTypes.SOUL, 6);
                    crow.setOrderedToSit(false);
                    crow.startGiftForage(GIFT_SEARCH_DURATION_TICKS);
                    crow.setForageCooldown(FORAGE_COOLDOWN_TICKS);
                } else {
                    spawnParticles(crow, ParticleTypes.SMOKE, 3);
                }
            }
            return InteractionResult.sidedSuccess(crow.level.isClientSide);
        }

        if (!crow.level.isClientSide) {
            crow.toggleSitOrder();
        }
        return InteractionResult.sidedSuccess(crow.level.isClientSide);
    }

    private static void consumeOne(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private static void spawnParticles(CrowEntity crow, ParticleOptions particle, int count) {
        if (crow.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, crow.getX(), crow.getY(0.6D), crow.getZ(), count, 0.2D, 0.2D, 0.2D, 0.02D);
        }
    }
}