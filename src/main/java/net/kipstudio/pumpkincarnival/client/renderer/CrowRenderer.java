// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.client.renderer;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.client.model.CrowModel;
import net.kipstudio.pumpkincarnival.client.renderer.layer.CrowHeldItemLayer;
import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CrowRenderer extends MobRenderer<CrowEntity, CrowModel<CrowEntity>> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(PumpkinCarnival.MOD_ID, "textures/entity/crow.png");

    public CrowRenderer(EntityRendererProvider.Context context) {
        super(context, new CrowModel<>(context.bakeLayer(CrowModel.LAYER_LOCATION)), 0.3F);
        this.addLayer(new CrowHeldItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(CrowEntity entity) {
        return TEXTURE;
    }
}