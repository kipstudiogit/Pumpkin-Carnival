// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class CarnivalSavedData extends SavedData {
    private static final String DATA_NAME = "pumpkincarnival_carnival_data";
    private static final String KEY_CARNIVAL_ACTIVE = "CarnivalActive";
    private static final String KEY_LAST_TRIGGERED_DAY = "LastTriggeredDay";

    private boolean carnivalActive = false;
    private long lastTriggeredDay = -1L;

    public static CarnivalSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                CarnivalSavedData::load,
                CarnivalSavedData::new,
                DATA_NAME
        );
    }

    public static CarnivalSavedData load(CompoundTag tag) {
        CarnivalSavedData data = new CarnivalSavedData();
        data.carnivalActive = tag.getBoolean(KEY_CARNIVAL_ACTIVE);
        data.lastTriggeredDay = tag.contains(KEY_LAST_TRIGGERED_DAY) ? tag.getLong(KEY_LAST_TRIGGERED_DAY) : -1L;
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean(KEY_CARNIVAL_ACTIVE, carnivalActive);
        tag.putLong(KEY_LAST_TRIGGERED_DAY, lastTriggeredDay);
        return tag;
    }

    public boolean isCarnivalActive() {
        return carnivalActive;
    }

    public void setCarnivalActive(boolean active) {
        this.carnivalActive = active;
        setDirty();
    }

    public long getLastTriggeredDay() {
        return lastTriggeredDay;
    }

    public void setLastTriggeredDay(long day) {
        this.lastTriggeredDay = day;
        setDirty();
    }
}