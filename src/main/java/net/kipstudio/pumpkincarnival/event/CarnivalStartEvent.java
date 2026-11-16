// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class CarnivalStartEvent extends Event {
    private final ServerLevel level;
    private final ServerPlayer triggeringPlayer;

    public CarnivalStartEvent(ServerLevel level, ServerPlayer triggeringPlayer) {
        this.level = level;
        this.triggeringPlayer = triggeringPlayer;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public ServerPlayer getTriggeringPlayer() {
        return triggeringPlayer;
    }
}