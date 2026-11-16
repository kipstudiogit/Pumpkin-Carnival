// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = PumpkinCarnival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientMoonHandler {
    private static final ResourceLocation JACK_O_LANTERN_MOON =
            new ResourceLocation(PumpkinCarnival.MOD_ID, "textures/environment/jack_o_lantern_moon.png");
    private static final float MOON_QUAD_SIZE = 26.0F;
    private static final float MOON_DISTANCE = -99.8F;

    private ClientMoonHandler() {
    }

    @SubscribeEvent
    public static void onRenderSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null) {
            return;
        }

        if (!CarnivalConditions.isInMistyGrounds(level, player.blockPosition())
                || !CarnivalConditions.isCarnivalMoonNight(level)) {
            return;
        }

        float partialTick = event.getPartialTick();
        float alpha = 1.0F - level.getRainLevel(partialTick);
        if (alpha <= 0.05F) {
            return;
        }

        renderMoonOverlay(event.getPoseStack(), level.getTimeOfDay(partialTick), alpha);
    }

    private static void renderMoonOverlay(PoseStack poseStack, float celestialAngle, float alpha) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(celestialAngle * 360.0F));

        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, JACK_O_LANTERN_MOON);
        RenderSystem.setShaderColor(1.0F, 0.72F, 0.25F, alpha);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, -MOON_QUAD_SIZE, MOON_DISTANCE, MOON_QUAD_SIZE).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix, MOON_QUAD_SIZE, MOON_DISTANCE, MOON_QUAD_SIZE).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(matrix, MOON_QUAD_SIZE, MOON_DISTANCE, -MOON_QUAD_SIZE).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(matrix, -MOON_QUAD_SIZE, MOON_DISTANCE, -MOON_QUAD_SIZE).uv(1.0F, 0.0F).endVertex();
        tesselator.end();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }
}