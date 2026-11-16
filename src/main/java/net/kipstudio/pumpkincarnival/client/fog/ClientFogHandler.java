// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.client.fog;

import com.mojang.blaze3d.shaders.FogShape;
import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PumpkinCarnival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientFogHandler {
    private static final FogProfile DAY_PROFILE = new FogProfile(18.0F, 56.0F, 82.0F / 255.0F, 96.0F / 255.0F, 107.0F / 255.0F);
    private static final FogProfile NIGHT_PROFILE = new FogProfile(12.0F, 38.0F, 28.0F / 255.0F, 34.0F / 255.0F, 38.0F / 255.0F);
    private static final FogProfile CARNIVAL_PROFILE = new FogProfile(8.0F, 28.0F, 35.0F / 255.0F, 34.0F / 255.0F, 43.0F / 255.0F);

    private static final float WEIGHT_STEP = 0.025F;
    private static final float PROPERTY_LERP_FACTOR = 0.08F;

    private static float currentWeight = 0.0F;
    private static float currentNear = DAY_PROFILE.near();
    private static float currentFar = DAY_PROFILE.far();
    private static float currentRed = DAY_PROFILE.red();
    private static float currentGreen = DAY_PROFILE.green();
    private static float currentBlue = DAY_PROFILE.blue();

    private ClientFogHandler() {
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        resetState();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            resetState();
            return;
        }

        if (mc.isPaused()) {
            return;
        }

        Camera camera = mc.gameRenderer.getMainCamera();
        if (camera.getFluidInCamera() != FogType.NONE) {
            currentWeight = 0.0F;
            return;
        }

        BlockPos samplePos = camera.getEntity() != null ? camera.getBlockPosition() : mc.player.blockPosition();
        boolean inBiome = CarnivalConditions.isInMistyGrounds(mc.level, samplePos);
        float targetWeight = inBiome ? 1.0F : 0.0F;
        currentWeight = stepToward(currentWeight, targetWeight, WEIGHT_STEP);

        if (inBiome) {
            FogProfile target = selectProfile(mc.level);
            float maxViewDistance = mc.options.getEffectiveRenderDistance() * 16.0F;
            float desiredFar = Math.min(target.far(), maxViewDistance * 0.85F);
            float desiredNear = Math.min(target.near(), desiredFar * 0.4F);

            currentNear = Mth.lerp(PROPERTY_LERP_FACTOR, currentNear, desiredNear);
            currentFar = Mth.lerp(PROPERTY_LERP_FACTOR, currentFar, desiredFar);
            currentRed = Mth.lerp(PROPERTY_LERP_FACTOR, currentRed, target.red());
            currentGreen = Mth.lerp(PROPERTY_LERP_FACTOR, currentGreen, target.green());
            currentBlue = Mth.lerp(PROPERTY_LERP_FACTOR, currentBlue, target.blue());
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Camera camera = event.getCamera();
        if (camera.getEntity() == null || camera.getFluidInCamera() != FogType.NONE) {
            return;
        }

        if (currentWeight > 0.001F && event.getMode() == FogRenderer.FogMode.FOG_TERRAIN) {
            event.setNearPlaneDistance(Mth.lerp(currentWeight, event.getNearPlaneDistance(), currentNear));
            event.setFarPlaneDistance(Mth.lerp(currentWeight, event.getFarPlaneDistance(), currentFar));
            event.setFogShape(FogShape.CYLINDER);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        if (camera.getEntity() == null || camera.getFluidInCamera() != FogType.NONE) {
            return;
        }

        if (currentWeight > 0.001F) {
            event.setRed(Mth.lerp(currentWeight, event.getRed(), currentRed));
            event.setGreen(Mth.lerp(currentWeight, event.getGreen(), currentGreen));
            event.setBlue(Mth.lerp(currentWeight, event.getBlue(), currentBlue));
        }
    }

    private static FogProfile selectProfile(Level level) {
        if (CarnivalConditions.isCarnivalMoonNight(level)) {
            return CARNIVAL_PROFILE;
        }
        if (level.isNight()) {
            return NIGHT_PROFILE;
        }
        return DAY_PROFILE;
    }

    private static float stepToward(float current, float target, float step) {
        if (current < target) {
            return Math.min(current + step, target);
        }
        if (current > target) {
            return Math.max(current - step, target);
        }
        return current;
    }

    private static void resetState() {
        currentWeight = 0.0F;
        currentNear = DAY_PROFILE.near();
        currentFar = DAY_PROFILE.far();
        currentRed = DAY_PROFILE.red();
        currentGreen = DAY_PROFILE.green();
        currentBlue = DAY_PROFILE.blue();
    }

    private record FogProfile(float near, float far, float red, float green, float blue) {
    }
}