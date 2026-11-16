// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.kipstudio.pumpkincarnival.client.model.CrowModel;
import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CrowHeldItemLayer extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {
    private final ItemInHandRenderer itemInHandRenderer;

    public CrowHeldItemLayer(RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> parent, ItemInHandRenderer itemInHandRenderer) {
        super(parent);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, CrowEntity crow,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack heldStack = crow.getMainHandItem();
        if (heldStack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        this.getParentModel().translateToBeak(poseStack);
        poseStack.translate(0.0F, -0.05F, -0.20F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        poseStack.scale(0.55F, 0.55F, 0.55F);
        this.itemInHandRenderer.renderItem(crow, heldStack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}