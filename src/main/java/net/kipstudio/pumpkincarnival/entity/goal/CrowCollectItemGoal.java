// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.entity.goal;

import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.kipstudio.pumpkincarnival.entity.crow.CrowBehaviorConfig;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class CrowCollectItemGoal extends Goal {
    private static final double SEARCH_RANGE_XZ = 10.0D;
    private static final double SEARCH_RANGE_Y = 6.0D;
    private static final double PICKUP_REACH_SQ = 2.25D;

    private final CrowEntity crow;
    private final double speedModifier;
    private ItemEntity targetItem;
    private int scanCooldown;
    private int repathCooldown;

    public CrowCollectItemGoal(CrowEntity crow, double speedModifier) {
        this.crow = crow;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.crow.isOrderedToSit() || !this.crow.getMainHandItem().isEmpty() || this.crow.getPickupCooldown() > 0) {
            return false;
        }
        if (this.scanCooldown > 0) {
            this.scanCooldown--;
            return false;
        }
        this.scanCooldown = 10;

        List<ItemEntity> items = this.crow.level.getEntitiesOfClass(
                ItemEntity.class,
                this.crow.getBoundingBox().inflate(SEARCH_RANGE_XZ, SEARCH_RANGE_Y, SEARCH_RANGE_XZ),
                item -> item.isAlive() && !item.hasPickUpDelay() && CrowBehaviorConfig.isInterestingItem(item.getItem())
        );
        if (items.isEmpty()) {
            return false;
        }

        items.sort(Comparator.comparingDouble(this.crow::distanceToSqr));
        this.targetItem = items.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetItem != null
                && this.targetItem.isAlive()
                && !this.crow.isOrderedToSit()
                && this.crow.getMainHandItem().isEmpty()
                && this.crow.getPickupCooldown() <= 0
                && this.crow.distanceToSqr(this.targetItem) <= SEARCH_RANGE_XZ * SEARCH_RANGE_XZ * 1.44D;
    }

    @Override
    public void start() {
        this.repathCooldown = 0;
    }

    @Override
    public void stop() {
        this.targetItem = null;
        this.crow.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetItem == null) {
            return;
        }

        this.crow.getLookControl().setLookAt(this.targetItem, 30.0F, 30.0F);
        if (this.crow.distanceToSqr(this.targetItem) <= PICKUP_REACH_SQ) {
            this.crow.collectGroundItem(this.targetItem);
            return;
        }

        if (this.repathCooldown-- <= 0 || this.crow.getNavigation().isDone()) {
            this.crow.getNavigation().moveTo(this.targetItem, this.speedModifier);
            this.repathCooldown = 10;
        }
    }
}