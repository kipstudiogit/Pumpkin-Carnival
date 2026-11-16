// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.entity.goal;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModTags;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CrowPerchWanderGoal extends WaterAvoidingRandomFlyingGoal {
    private static final int HORIZONTAL_SEARCH_RANGE = 6;
    private static final int VERTICAL_SEARCH_RANGE = 4;
    private static final float PERCH_SEEK_CHANCE = 0.72F;

    public CrowPerchWanderGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    public boolean canUse() {
        if (this.mob.isOnGround() && isRestingOnPerch() && this.mob.getRandom().nextInt(3) != 0) {
            return false;
        }
        return super.canUse();
    }

    private boolean isRestingOnPerch() {
        BlockPos below = this.mob.blockPosition().below();
        return this.mob.level.isLoaded(below) && isPreferredPerch(this.mob.level.getBlockState(below));
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if (!this.mob.isInWater() && this.mob.getRandom().nextFloat() < PERCH_SEEK_CHANCE) {
            Vec3 perchPos = findPerchPosition();
            if (perchPos != null) {
                return perchPos;
            }
        }
        return super.getPosition();
    }

    @Nullable
    private Vec3 findPerchPosition() {
        BlockPos origin = this.mob.blockPosition();
        RandomSource random = this.mob.getRandom();
        boolean carnivalNight = CarnivalConditions.isCarnivalMoonNight(this.mob.level);

        BlockPos.MutableBlockPos chosen = null;
        int totalWeight = 0;

        Iterable<BlockPos> candidates = BlockPos.betweenClosed(
                Mth.floor(this.mob.getX() - HORIZONTAL_SEARCH_RANGE),
                Mth.floor(this.mob.getY() - VERTICAL_SEARCH_RANGE),
                Mth.floor(this.mob.getZ() - HORIZONTAL_SEARCH_RANGE),
                Mth.floor(this.mob.getX() + HORIZONTAL_SEARCH_RANGE),
                Mth.floor(this.mob.getY() + VERTICAL_SEARCH_RANGE),
                Mth.floor(this.mob.getZ() + HORIZONTAL_SEARCH_RANGE)
        );

        for (BlockPos candidate : candidates) {
            if (!this.mob.level.isLoaded(candidate)) {
                continue;
            }

            BlockPos above = candidate.above();
            if (above.closerThan(origin, 2.0D)) {
                continue;
            }

            BlockState perchState = this.mob.level.getBlockState(candidate);
            int weight = getPerchWeight(perchState, carnivalNight);
            if (weight <= 0) {
                continue;
            }

            if (this.mob.level.isEmptyBlock(above) && this.mob.level.isEmptyBlock(above.above())) {
                totalWeight += weight;
                if (random.nextInt(totalWeight) < weight) {
                    if (chosen == null) {
                        chosen = new BlockPos.MutableBlockPos();
                    }
                    chosen.set(above);
                }
            }
        }

        return chosen != null ? Vec3.atBottomCenterOf(chosen) : null;
    }

    private static int getPerchWeight(BlockState state, boolean carnivalNight) {
        if (state.is(ModBlocks.CARNIVAL_LANTERN.get()) || state.is(Blocks.JACK_O_LANTERN)) {
            return carnivalNight ? 5 : 2;
        }
        if (state.is(ModTags.Blocks.DEADWOOD_LOGS) || state.is(BlockTags.FENCES)) {
            return 2;
        }
        if (state.is(Blocks.PUMPKIN) || state.is(Blocks.CARVED_PUMPKIN) || state.is(BlockTags.LEAVES)) {
            return 1;
        }
        return 0;
    }

    private static boolean isPreferredPerch(BlockState state) {
        return getPerchWeight(state, false) > 0;
    }
}