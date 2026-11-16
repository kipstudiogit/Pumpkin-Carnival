// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.entity.goal;

import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class CrowScarecrowAlertGoal extends Goal {
    private static final double ALERT_RANGE_XZ = 12.0D;
    private static final double ALERT_RANGE_Y = 6.0D;
    private static final double STARTLE_DISTANCE_SQ = 25.0D;
    private static final int SOUND_COOLDOWN_TICKS = 160;

    private final CrowEntity crow;
    private Monster threatTarget;
    private int scanCooldown;
    private int alertDurationTicks;
    private int lastSoundTick = -SOUND_COOLDOWN_TICKS;

    public CrowScarecrowAlertGoal(CrowEntity crow) {
        this.crow = crow;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.scanCooldown > 0) {
            this.scanCooldown--;
            return false;
        }
        this.scanCooldown = 15;

        List<Monster> threats = this.crow.level.getEntitiesOfClass(
                Monster.class,
                this.crow.getBoundingBox().inflate(ALERT_RANGE_XZ, ALERT_RANGE_Y, ALERT_RANGE_XZ),
                monster -> monster.isAlive() && this.crow.hasLineOfSight(monster)
        );
        if (threats.isEmpty()) {
            return false;
        }

        threats.sort(Comparator.comparingDouble(this.crow::distanceToSqr));
        this.threatTarget = threats.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.alertDurationTicks > 0
                && this.threatTarget != null
                && this.threatTarget.isAlive()
                && this.crow.distanceToSqr(this.threatTarget) <= ALERT_RANGE_XZ * ALERT_RANGE_XZ * 1.44D;
    }

    @Override
    public void start() {
        this.alertDurationTicks = 50;
        this.crow.setAlerted(true);

        if (this.crow.tickCount - this.lastSoundTick >= SOUND_COOLDOWN_TICKS) {
            this.crow.playSound(ModSounds.CROW_CAW.get(), 0.85F, 1.25F);
            this.lastSoundTick = this.crow.tickCount;
        }

        if (!this.crow.isTame()
                && !this.crow.isOrderedToSit()
                && this.crow.isOnGround()
                && this.threatTarget != null
                && this.crow.distanceToSqr(this.threatTarget) < STARTLE_DISTANCE_SQ) {
            this.crow.getJumpControl().jump();
        }
    }

    @Override
    public void stop() {
        this.crow.setAlerted(false);
        this.threatTarget = null;
    }

    @Override
    public void tick() {
        if (this.threatTarget != null) {
            this.crow.getLookControl().setLookAt(this.threatTarget, 35.0F, 35.0F);
        }
        this.alertDurationTicks--;
    }
}