// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.handler;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.data.CarnivalSavedData;
import net.kipstudio.pumpkincarnival.init.ModItems;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.kipstudio.pumpkincarnival.item.ticket.CarnivalTicketData;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PumpkinCarnival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CarnivalTicketEventHandler {
    private CarnivalTicketEventHandler() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level.isClientSide()) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level;
        boolean insideMist = CarnivalConditions.isInMistyGrounds(level, player.blockPosition());
        boolean carnivalActive = CarnivalSavedData.get(level).isCarnivalActive() || CarnivalConditions.isCarnivalMoonNight(level);
        if (!insideMist && !carnivalActive) {
            return;
        }

        ItemStack ticketStack = findHeldAttunedTicket(player);
        if (ticketStack.isEmpty()) {
            return;
        }

        event.setCanceled(true);
        CarnivalTicketData.clearAllStamps(ticketStack, false);

        player.setHealth(6.0F);
        player.clearFire();
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1));
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));

        level.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY(0.8D), player.getZ(), 20, 0.5D, 0.6D, 0.5D, 0.04D);
        level.sendParticles(ParticleTypes.POOF, player.getX(), player.getY(0.5D), player.getZ(), 14, 0.4D, 0.4D, 0.4D, 0.03D);
        level.playSound(null, player.blockPosition(), ModSounds.CARNIVAL_START.get(), SoundSource.PLAYERS, 1.0F, 0.85F);

        player.displayClientMessage(
                Component.translatable("actionbar.pumpkincarnival.ticket.death_saved").withStyle(ChatFormatting.LIGHT_PURPLE),
                true
        );
    }

    private static ItemStack findHeldAttunedTicket(ServerPlayer player) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(ModItems.CARNIVAL_TICKET.get()) && CarnivalTicketData.isAttuned(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}