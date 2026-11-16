// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kipstudio.pumpkincarnival.client.model.CrowModel;
import net.kipstudio.pumpkincarnival.client.renderer.CrowRenderer;
import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.kipstudio.pumpkincarnival.init.ModEntities;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class CrowOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    private final CrowModel<CrowEntity> model;

    public CrowOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new CrowModel<>(modelSet.bakeLayer(CrowModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (player.isInvisible()) {
            return;
        }
        renderShoulderCrow(poseStack, buffer, packedLight, player, limbSwing, limbSwingAmount, netHeadYaw, headPitch, true);
        renderShoulderCrow(poseStack, buffer, packedLight, player, limbSwing, limbSwingAmount, netHeadYaw, headPitch, false);
    }

    private void renderShoulderCrow(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T player,
                                    float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, boolean leftShoulder) {
        CompoundTag shoulderTag = leftShoulder ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        EntityType.byString(shoulderTag.getString("id"))
                .filter(type -> type == ModEntities.CROW.get())
                .ifPresent(type -> {
                    poseStack.pushPose();
                    poseStack.translate(leftShoulder ? 0.4F : -0.4F, player.isCrouching() ? -1.3F : -1.5F, 0.0F);
                    VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(CrowRenderer.TEXTURE));
                    this.model.renderOnShoulder(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                            limbSwing, limbSwingAmount, netHeadYaw, headPitch, player.tickCount);
                    poseStack.popPose();
                });
    }
}