// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.entity.CrowEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CrowModel<T extends CrowEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(PumpkinCarnival.MOD_ID, "crow"), "main");

    private static final float BODY_BASE_Y = 20.0F;
    private static final float BODY_BASE_X_ROT = 0.5236F;
    private static final float WING_BASE_X_ROT = 0.3927F;
    private static final float TAIL_BASE_X_ROT = 0.5672F;
    private static final float HEAD_BASE_X_ROT = -0.3927F;
    private static final float LEG_BASE_Y = 21.5F;

    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    private float flapProgress;
    private float flapIntensity;

    public CrowModel(ModelPart root) {
        this.body = root.getChild("body");
        this.leftWing = this.body.getChild("left_wing");
        this.rightWing = this.body.getChild("right_wing");
        this.tail = this.body.getChild("tail");
        this.head = this.body.getChild("head");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-0.5F, BODY_BASE_Y, 0.0F, BODY_BASE_X_ROT, 0.0F, 0.0F));

        body.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.0F, -4.0F, -1.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.05F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

        body.addOrReplaceChild("left_wing",
                CubeListBuilder.create()
                        .texOffs(12, 10)
                        .addBox(0.0F, -0.9804F, -2.1585F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 16)
                        .addBox(0.0F, -0.9804F, -0.1585F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, -2.25F, 0.0F, WING_BASE_X_ROT, 0.0F, 0.0F));

        body.addOrReplaceChild("right_wing",
                CubeListBuilder.create()
                        .texOffs(14, 0)
                        .addBox(-1.0F, -0.9804F, -2.1585F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(6, 16)
                        .addBox(-1.0F, -0.9804F, -0.1585F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, -2.25F, 0.0F, WING_BASE_X_ROT, 0.0F, 0.0F));

        body.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(18, 10)
                        .addBox(-1.5F, 0.4088F, -0.3767F, 3.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.5F, 0.0F, 2.5F, TAIL_BASE_X_ROT, 0.0F, 0.0F));

        body.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(-1.5F, -2.75F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(18, 17)
                        .addBox(-0.5F, -1.75F, -4.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.5F, -4.0F, -2.0F, HEAD_BASE_X_ROT, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(20, 5)
                        .addBox(-0.5F, -0.5F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.01F)),
                PartPose.offset(1.0F, LEG_BASE_Y, 1.0F));

        partdefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(20, 0)
                        .addBox(-0.5F, -0.5F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.01F)),
                PartPose.offset(-1.0F, LEG_BASE_Y, 1.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.flapProgress = Mth.lerp(partialTick, entity.oFlap, entity.flap);
        this.flapIntensity = Mth.lerp(partialTick, entity.oFlapSpeed, entity.flapSpeed);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean sitting = entity.isInSittingPose();
        boolean alerted = entity.isAlerted();

        float flyWeight = this.flapIntensity;
        float groundWeight = 1.0F - flyWeight;
        float wingBeat = Mth.cos(this.flapProgress);

        float sitOffset = sitting ? 0.85F * groundWeight : 0.0F;
        float alertHeadLift = alerted ? -0.14F : 0.0F;
        float alertHeadTilt = alerted ? 0.26F * groundWeight : 0.0F;

        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = HEAD_BASE_X_ROT + headPitch * Mth.DEG_TO_RAD - flyWeight * 0.42F + alertHeadLift;
        this.head.zRot = Mth.sin(ageInTicks * 0.08F + entity.getId()) * 0.06F * groundWeight + alertHeadTilt;

        float walkHop = sitting ? 0.0F : Math.abs(Mth.sin(limbSwing * 1.2F)) * 0.6F * limbSwingAmount * groundWeight;
        float flightBob = wingBeat * 0.7F * flyWeight;
        this.body.y = BODY_BASE_Y + sitOffset - walkHop - flightBob;
        this.body.xRot = BODY_BASE_X_ROT + flyWeight * 0.48F - (sitting ? 0.1F * groundWeight : 0.0F);

        float idleWingBreath = Mth.cos(ageInTicks * 0.1F) * 0.03F * groundWeight;
        float alertWingPuff = alerted ? 0.14F * groundWeight : 0.0F;
        float spreadAngle = 1.35F + wingBeat * 0.9F;
        this.leftWing.xRot = Mth.lerp(flyWeight, WING_BASE_X_ROT + idleWingBreath, 0.0F);
        this.rightWing.xRot = Mth.lerp(flyWeight, WING_BASE_X_ROT + idleWingBreath, 0.0F);
        this.leftWing.zRot = -spreadAngle * flyWeight - alertWingPuff;
        this.rightWing.zRot = spreadAngle * flyWeight + alertWingPuff;

        float alertTailFlare = alerted ? -0.25F * groundWeight : 0.0F;
        float sitTailOffset = sitting ? -0.12F * groundWeight : 0.0F;
        this.tail.xRot = TAIL_BASE_X_ROT
                + Mth.cos(ageInTicks * 0.1F) * 0.05F * groundWeight
                + limbSwingAmount * 0.25F * groundWeight
                + (0.35F + wingBeat * 0.12F) * flyWeight
                + alertTailFlare
                + sitTailOffset;

        float leftWalkRot = sitting ? -0.35F : Mth.cos(limbSwing * 1.2F) * 1.2F * limbSwingAmount;
        float rightWalkRot = sitting ? -0.35F : Mth.cos(limbSwing * 1.2F + Mth.PI) * 1.2F * limbSwingAmount;
        this.leftLeg.xRot = Mth.lerp(flyWeight, leftWalkRot, 0.85F);
        this.rightLeg.xRot = Mth.lerp(flyWeight, rightWalkRot, 0.85F);
        float legSitDrop = sitting ? 0.4F * groundWeight : 0.0F;
        this.leftLeg.y = LEG_BASE_Y + legSitDrop - flyWeight * 0.6F;
        this.rightLeg.y = LEG_BASE_Y + legSitDrop - flyWeight * 0.6F;
    }

    public void translateToBeak(PoseStack poseStack) {
        this.body.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);
    }

    public void renderOnShoulder(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                                 float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, int tickCount) {
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = HEAD_BASE_X_ROT + headPitch * Mth.DEG_TO_RAD;
        this.head.zRot = Mth.sin(tickCount * 0.07F) * 0.05F;

        this.body.y = BODY_BASE_Y + 0.6F;
        this.body.xRot = BODY_BASE_X_ROT - 0.08F;

        float idleBreath = Mth.cos(tickCount * 0.1F) * 0.03F;
        this.leftWing.xRot = WING_BASE_X_ROT + idleBreath;
        this.rightWing.xRot = WING_BASE_X_ROT + idleBreath;
        this.leftWing.zRot = 0.0F;
        this.rightWing.zRot = 0.0F;

        this.tail.xRot = TAIL_BASE_X_ROT - 0.1F + Mth.cos(tickCount * 0.1F) * 0.05F + limbSwingAmount * 0.15F;

        this.leftLeg.xRot = -0.3F;
        this.rightLeg.xRot = -0.3F;
        this.leftLeg.y = LEG_BASE_Y + 0.35F;
        this.rightLeg.y = LEG_BASE_Y + 0.35F;

        this.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}