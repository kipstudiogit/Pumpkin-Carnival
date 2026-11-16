// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.item;

import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.item.ticket.CarnivalTicketData;
import net.kipstudio.pumpkincarnival.item.ticket.CarnivalTicketLocator;
import net.kipstudio.pumpkincarnival.item.ticket.CarnivalTicketRites;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class CarnivalTicketItem extends Item {
    private static final int GUIDE_COOLDOWN_TICKS = 100;
    private static final int RITE_COOLDOWN_TICKS = 40;
    private static final int CROW_SIGHT_COOLDOWN_TICKS = 600;
    private static final int STAMP_BAR_COLOR = 0xFF9E2C;

    public CarnivalTicketItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return CarnivalTicketData.isAttuned(stack) ? 1 : super.getMaxStackSize(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return CarnivalTicketData.isAttuned(stack) || super.isFoil(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return CarnivalTicketData.isAttuned(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int stamps = CarnivalTicketData.getStamps(stack);
        return Math.round(stamps * 13.0F / CarnivalTicketData.MAX_STAMPS);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return STAMP_BAR_COLOR;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return CarnivalTicketData.isAttuned(stack) ? this.getDescriptionId() + ".attuned" : super.getDescriptionId(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(heldStack);
        }

        ServerLevel serverLevel = (ServerLevel) level;
        if (player.isShiftKeyDown()) {
            boolean triggered = applyTicketAction(player, heldStack,
                    ticket -> CarnivalTicketRites.invokeCrowSight(serverLevel, player, ticket));
            if (triggered) {
                player.getCooldowns().addCooldown(this, CROW_SIGHT_COOLDOWN_TICKS);
                return InteractionResultHolder.consume(heldStack);
            }
            return InteractionResultHolder.pass(heldStack);
        }

        applyTicketAction(player, heldStack, ticket -> {
            CarnivalTicketLocator.guidePlayer(serverLevel, player, ticket);
            return true;
        });
        player.getCooldowns().addCooldown(this, GUIDE_COOLDOWN_TICKS);
        return InteractionResultHolder.consume(heldStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        boolean isCarnivalLantern = state.is(ModBlocks.CARNIVAL_LANTERN.get());
        boolean isJackOLantern = state.is(Blocks.JACK_O_LANTERN);
        if (!isCarnivalLantern && !isJackOLantern) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        ItemStack heldStack = context.getItemInHand();

        boolean useGrandBoon = isCarnivalLantern
                && player.isShiftKeyDown()
                && CarnivalConditions.isInMistyGrounds(serverLevel, pos)
                && (!CarnivalTicketData.isAttuned(heldStack) || CarnivalTicketData.getStamps(heldStack) == CarnivalTicketData.MAX_STAMPS);

        boolean triggered = applyTicketAction(player, heldStack, ticket -> useGrandBoon
                ? CarnivalTicketRites.invokeGrandLanternBoon(serverLevel, player, pos, ticket)
                : CarnivalTicketRites.invokeLanternWard(serverLevel, player, pos, ticket));

        if (triggered) {
            player.getCooldowns().addCooldown(this, RITE_COOLDOWN_TICKS);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof CrowEntity crow)) {
            return InteractionResult.PASS;
        }
        if (!crow.getMainHandItem().isEmpty() || (crow.isTame() && !crow.isOwnedBy(player))) {
            return InteractionResult.PASS;
        }
        if (player.level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        boolean triggered = applyTicketAction(player, stack,
                ticket -> CarnivalTicketRites.invokeCrowDispatch((ServerLevel) player.level, player, crow, ticket));
        if (triggered) {
            player.setItemInHand(hand, stack);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (CarnivalTicketData.isAttuned(stack)) {
            int stamps = CarnivalTicketData.getStamps(stack);
            tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.stamps", stamps, CarnivalTicketData.MAX_STAMPS)
                    .withStyle(ChatFormatting.GOLD));
            CarnivalTicketData.getTarget(stack).ifPresent(target ->
                    tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.bound." + target.kind().translationSuffix())
                            .withStyle(ChatFormatting.AQUA))
            );
        } else {
            tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.unstamped")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.attune_hint")
                    .withStyle(ChatFormatting.GOLD));
        }

        boolean showDetails = Boolean.TRUE.equals(DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> Screen::hasShiftDown));
        if (showDetails) {
            tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.detail_guide").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.detail_lantern").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.detail_crow").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.detail_ward").withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.translatable("tooltip.pumpkincarnival.carnival_ticket.shift_hint").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static boolean applyTicketAction(Player player, ItemStack sourceStack, Function<ItemStack, Boolean> action) {
        if (CarnivalTicketData.isAttuned(sourceStack)) {
            return action.apply(sourceStack);
        }

        if (sourceStack.getCount() == 1) {
            CarnivalTicketData.attune(sourceStack);
            return action.apply(sourceStack);
        }

        ItemStack splitTicket = sourceStack.copyWithCount(1);
        CarnivalTicketData.attune(splitTicket);
        boolean success = action.apply(splitTicket);
        if (!success) {
            return false;
        }

        if (!player.getAbilities().instabuild) {
            sourceStack.shrink(1);
        }
        if (!splitTicket.isEmpty() && !player.getInventory().add(splitTicket)) {
            player.drop(splitTicket, false);
        }
        return true;
    }
}