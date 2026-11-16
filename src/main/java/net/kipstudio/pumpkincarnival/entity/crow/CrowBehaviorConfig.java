// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.entity.crow;

import net.kipstudio.pumpkincarnival.init.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class CrowBehaviorConfig {
    private CrowBehaviorConfig() {
    }

    public static boolean isTameItem(ItemStack stack) {
        return stack.is(Items.PUMPKIN_SEEDS) || stack.is(ModItems.SOUL_CANDY.get());
    }

    public static boolean isFoodItem(ItemStack stack) {
        return stack.is(Items.PUMPKIN_SEEDS)
                || stack.is(Items.MELON_SEEDS)
                || stack.is(ModItems.SOUL_CANDY.get());
    }

    public static boolean isInterestingItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.GOLD_NUGGET)
                || stack.is(Items.IRON_NUGGET)
                || stack.is(Items.EMERALD)
                || stack.is(Items.DIAMOND)
                || stack.is(Items.AMETHYST_SHARD)
                || stack.is(Items.GOLD_INGOT)
                || stack.is(Items.PUMPKIN_SEEDS)
                || stack.is(ModItems.CARNIVAL_TICKET.get())
                || stack.is(ModItems.SOUL_CANDY.get());
    }

    public static ItemStack rollForagedGift(RandomSource random, boolean carnivalNight) {
        int roll = random.nextInt(100);
        if (carnivalNight) {
            if (roll < 15) {
                return new ItemStack(ModItems.CARNIVAL_TICKET.get());
            }
            if (roll < 35) {
                return new ItemStack(Items.EMERALD);
            }
            if (roll < 60) {
                return new ItemStack(Items.GOLD_NUGGET, 1 + random.nextInt(3));
            }
            if (roll < 80) {
                return new ItemStack(Items.AMETHYST_SHARD);
            }
            return new ItemStack(Items.PUMPKIN_SEEDS, 2 + random.nextInt(3));
        }

        if (roll < 5) {
            return new ItemStack(ModItems.CARNIVAL_TICKET.get());
        }
        if (roll < 18) {
            return new ItemStack(Items.EMERALD);
        }
        if (roll < 45) {
            return new ItemStack(Items.GOLD_NUGGET, 1 + random.nextInt(2));
        }
        if (roll < 72) {
            return new ItemStack(Items.IRON_NUGGET, 1 + random.nextInt(3));
        }
        return new ItemStack(Items.PUMPKIN_SEEDS, 1 + random.nextInt(3));
    }
}