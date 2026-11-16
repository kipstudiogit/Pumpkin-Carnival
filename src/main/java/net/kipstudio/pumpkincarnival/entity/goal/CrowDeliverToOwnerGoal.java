// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.entity.goal;

import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CrowDeliverToOwnerGoal extends Goal {
    private static final double MIN_DELIVERY_DISTANCE_SQ = 4.5D;
    private static final double MAX_DELIVERY_DISTANCE_SQ = 400.0D;

    private final CrowEntity crow;
    private final double speedModifier;
    private LivingEntity owner;
    private int repathCooldown;
    private boolean announcedArrival;

    public CrowDeliverToOwnerGoal(CrowEntity crow, double speedModifier) {
        this.crow = crow;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.crow.isTame() || this.crow.isOrderedToSit() || this.crow.getMainHandItem().isEmpty()) {
            return false;
        }
        LivingEntity candidate = this.crow.getOwner();
        if (candidate == null || !candidate.isAlive() || candidate.isSpectator()) {
            return false;
        }
        double distSq = this.crow.distanceToSqr(candidate);
        if (distSq <= MIN_DELIVERY_DISTANCE_SQ || distSq > MAX_DELIVERY_DISTANCE_SQ) {
            return false;
        }
        this.owner = candidate;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.owner == null || !this.owner.isAlive() || this.crow.isOrderedToSit() || this.crow.getMainHandItem().isEmpty()) {
            return false;
        }
        double distSq = this.crow.distanceToSqr(this.owner);
        return distSq > MIN_DELIVERY_DISTANCE_SQ && distSq <= MAX_DELIVERY_DISTANCE_SQ;
    }

    @Override
    public void start() {
        this.repathCooldown = 0;
        this.announcedArrival = false;
    }

    @Override
    public void stop() {
        if (this.owner != null && !this.announcedArrival && this.crow.distanceToSqr(this.owner) <= 6.25D) {
            this.crow.playSound(ModSounds.CROW_CAW.get(), 0.7F, 1.15F);
            this.announcedArrival = true;
        }
        this.owner = null;
        this.crow.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.owner == null) {
            return;
        }
        this.crow.getLookControl().setLookAt(this.owner, 25.0F, 25.0F);
        if (this.repathCooldown-- <= 0 || this.crow.getNavigation().isDone()) {
            this.crow.getNavigation().moveTo(this.owner, this.speedModifier);
            this.repathCooldown = 8;
        }
    }
}