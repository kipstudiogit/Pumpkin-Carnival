// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.item.ticket;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class CarnivalTicketData {
    public static final int MAX_STAMPS = 3;

    private static final String KEY_ROOT = "CarnivalTicket";
    private static final String KEY_STAMPS = "Stamps";
    private static final String KEY_TARGET_X = "TargetX";
    private static final String KEY_TARGET_Z = "TargetZ";
    private static final String KEY_TARGET_DIM = "TargetDim";
    private static final String KEY_TARGET_KIND = "TargetKind";

    private CarnivalTicketData() {
    }

    public static boolean isAttuned(ItemStack stack) {
        CompoundTag root = stack.getTagElement(KEY_ROOT);
        return root != null && root.getInt(KEY_STAMPS) > 0;
    }

    public static int getStamps(ItemStack stack) {
        CompoundTag root = stack.getTagElement(KEY_ROOT);
        return root != null ? Math.max(0, Math.min(MAX_STAMPS, root.getInt(KEY_STAMPS))) : 0;
    }

    public static void attune(ItemStack stack) {
        CompoundTag root = stack.getOrCreateTagElement(KEY_ROOT);
        if (root.getInt(KEY_STAMPS) <= 0) {
            root.putInt(KEY_STAMPS, MAX_STAMPS);
        }
    }

    public static boolean consumeStamp(ItemStack stack, boolean bypassShrink) {
        int current = getStamps(stack);
        if (current <= 0) {
            return false;
        }
        int next = current - 1;
        if (next <= 0) {
            if (!bypassShrink) {
                stack.shrink(1);
            } else {
                stack.getOrCreateTagElement(KEY_ROOT).putInt(KEY_STAMPS, MAX_STAMPS);
            }
        } else {
            stack.getOrCreateTagElement(KEY_ROOT).putInt(KEY_STAMPS, next);
        }
        return true;
    }

    public static void clearAllStamps(ItemStack stack, boolean bypassShrink) {
        if (!bypassShrink) {
            stack.shrink(1);
        }
    }

    public static void setTarget(ItemStack stack, Level level, BlockPos pos, TargetKind kind) {
        CompoundTag root = stack.getOrCreateTagElement(KEY_ROOT);
        root.putInt(KEY_TARGET_X, pos.getX());
        root.putInt(KEY_TARGET_Z, pos.getZ());
        root.putString(KEY_TARGET_DIM, level.dimension().location().toString());
        root.putString(KEY_TARGET_KIND, kind.id);
    }

    public static Optional<BoundTarget> getTarget(ItemStack stack) {
        CompoundTag root = stack.getTagElement(KEY_ROOT);
        if (root == null
                || !root.contains(KEY_TARGET_X, Tag.TAG_INT)
                || !root.contains(KEY_TARGET_Z, Tag.TAG_INT)
                || !root.contains(KEY_TARGET_DIM, Tag.TAG_STRING)) {
            return Optional.empty();
        }
        ResourceLocation dimId = ResourceLocation.tryParse(root.getString(KEY_TARGET_DIM));
        if (dimId == null) {
            return Optional.empty();
        }
        TargetKind kind = TargetKind.byId(root.getString(KEY_TARGET_KIND));
        return Optional.of(new BoundTarget(root.getInt(KEY_TARGET_X), root.getInt(KEY_TARGET_Z), dimId, kind));
    }

    public enum TargetKind {
        MISTY_GROUNDS("misty_grounds"),
        PUMPKIN_PLANTATION("pumpkin_plantation");

        private final String id;

        TargetKind(String id) {
            this.id = id;
        }

        public String translationSuffix() {
            return this.id;
        }

        public static TargetKind byId(String id) {
            for (TargetKind value : values()) {
                if (value.id.equals(id)) {
                    return value;
                }
            }
            return MISTY_GROUNDS;
        }
    }

    public record BoundTarget(int x, int z, ResourceLocation dimensionId, TargetKind kind) {
        public boolean matchesDimension(Level level) {
            return level.dimension().location().equals(this.dimensionId);
        }
    }
}