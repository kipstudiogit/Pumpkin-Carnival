// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.entity;

import net.kipstudio.pumpkincarnival.entity.crow.CrowBehaviorConfig;
import net.kipstudio.pumpkincarnival.entity.crow.CrowInteractionHandler;
import net.kipstudio.pumpkincarnival.entity.goal.CrowCollectItemGoal;
import net.kipstudio.pumpkincarnival.entity.goal.CrowDeliverToOwnerGoal;
import net.kipstudio.pumpkincarnival.entity.goal.CrowPerchWanderGoal;
import net.kipstudio.pumpkincarnival.entity.goal.CrowScarecrowAlertGoal;
import net.kipstudio.pumpkincarnival.init.ModSounds;
import net.kipstudio.pumpkincarnival.init.ModTags;
import net.kipstudio.pumpkincarnival.util.CarnivalConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CrowEntity extends ShoulderRidingEntity implements FlyingAnimal {
    private static final EntityDataAccessor<Boolean> DATA_ALERTED =
            SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Ingredient TEMPT_ITEMS = Ingredient.of(Items.PUMPKIN_SEEDS);
    private static final String KEY_PICKUP_COOLDOWN = "PickupCooldown";
    private static final String KEY_FORAGE_COOLDOWN = "ForageCooldown";
    private static final String KEY_PENDING_GIFT = "PendingGiftTicks";

    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;

    private int pickupCooldownTicks;
    private int forageCooldownTicks;
    private int pendingGiftTicks;

    public CrowEntity(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setCanPickUpLoot(true);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.FLYING_SPEED, 0.45D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    public static boolean checkCrowSpawnRules(EntityType<CrowEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getFluidState(pos).isEmpty()) {
            return false;
        }
        BlockState ground = level.getBlockState(pos.below());
        return ground.is(BlockTags.ANIMALS_SPAWNABLE_ON)
                || ground.is(Blocks.PODZOL)
                || ground.is(Blocks.COARSE_DIRT)
                || ground.is(Blocks.ROOTED_DIRT)
                || ground.is(ModTags.Blocks.DEADWOOD_LOGS);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ALERTED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.35D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new CrowScarecrowAlertGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, TEMPT_ITEMS, false));
        this.goalSelector.addGoal(5, new AvoidPlayerWhenWildGoal(this, 4.5F, 1.15D, 1.45D));
        this.goalSelector.addGoal(6, new CrowDeliverToOwnerGoal(this, 1.25D));
        this.goalSelector.addGoal(7, new CrowCollectItemGoal(this, 1.25D));
        this.goalSelector.addGoal(8, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.5F, true));
        this.goalSelector.addGoal(9, new LandOnOwnersShoulderGoal(this));
        this.goalSelector.addGoal(10, new CrowPerchWanderGoal(this, 1.0D));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(13, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
    }

    private static boolean shouldAvoidPlayer(LivingEntity entity) {
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) || entity.isShiftKeyDown()) {
            return false;
        }
        return !CrowBehaviorConfig.isTameItem(entity.getMainHandItem())
                && !CrowBehaviorConfig.isTameItem(entity.getOffhandItem());
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if ((reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION) && this.random.nextInt(8) == 0) {
            ItemStack initialLoot = switch (this.random.nextInt(3)) {
                case 0 -> new ItemStack(Items.IRON_NUGGET);
                case 1 -> new ItemStack(Items.GOLD_NUGGET);
                default -> new ItemStack(Items.PUMPKIN_SEEDS);
            };
            this.setItemSlot(EquipmentSlot.MAINHAND, initialLoot);
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult result = CrowInteractionHandler.handleInteract(this, player, hand);
        if (result.consumesAction()) {
            return result;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.calculateFlapping();
        if (!this.level.isClientSide) {
            tickServerTimers();
        }
    }

    private void tickServerTimers() {
        if (this.pickupCooldownTicks > 0) {
            this.pickupCooldownTicks--;
        }
        if (this.forageCooldownTicks > 0) {
            this.forageCooldownTicks--;
        }
        if (this.pendingGiftTicks > 0) {
            this.pendingGiftTicks--;
            if (this.pendingGiftTicks == 0 && this.getMainHandItem().isEmpty()) {
                completeGiftForage();
            }
        }
    }

    private void completeGiftForage() {
        ItemStack gift = CrowBehaviorConfig.rollForagedGift(this.random, CarnivalConditions.isCarnivalMoonNight(this.level));
        this.setItemSlot(EquipmentSlot.MAINHAND, gift);
        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        this.playSound(ModSounds.CROW_CAW.get(), 0.8F, 1.15F);
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY(0.6D), this.getZ(), 4, 0.2D, 0.2D, 0.2D, 0.02D);
        }
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return this.getMainHandItem().isEmpty() && this.pickupCooldownTicks <= 0 && CrowBehaviorConfig.isInterestingItem(stack);
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return this.canHoldItem(stack);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        collectGroundItem(itemEntity);
    }

    public void collectGroundItem(ItemEntity itemEntity) {
        if (this.level.isClientSide || !itemEntity.isAlive() || this.pickupCooldownTicks > 0) {
            return;
        }
        ItemStack stack = itemEntity.getItem();
        if (!this.wantsToPickUp(stack)) {
            return;
        }

        this.onItemPickup(itemEntity);
        ItemStack picked = stack.split(1);
        this.setItemSlot(EquipmentSlot.MAINHAND, picked);
        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        this.take(itemEntity, 1);
        if (stack.isEmpty()) {
            itemEntity.discard();
        }
        this.playSound(SoundEvents.ITEM_PICKUP, 0.35F, 1.3F);
    }

    public void dropHeldItemToward(Player player) {
        ItemStack held = this.getMainHandItem();
        if (held.isEmpty() || this.level.isClientSide) {
            return;
        }
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.pickupCooldownTicks = 100;

        Vec3 mouthPos = this.getEyePosition();
        Vec3 direction = player.getEyePosition().subtract(mouthPos).normalize();
        ItemEntity dropped = new ItemEntity(this.level, mouthPos.x, mouthPos.y, mouthPos.z, held.copy());
        dropped.setDeltaMovement(direction.x * 0.22D, 0.16D, direction.z * 0.22D);
        dropped.setPickUpDelay(5);
        this.level.addFreshEntity(dropped);
    }

    public void toggleSitOrder() {
        this.setOrderedToSit(!this.isOrderedToSit());
        this.jumping = false;
        this.navigation.stop();
    }

    @Override
    public boolean canSitOnShoulder() {
        return super.canSitOnShoulder() && this.getMainHandItem().isEmpty() && this.pendingGiftTicks <= 0;
    }

    public boolean isAlerted() {
        return this.entityData.get(DATA_ALERTED);
    }

    public void setAlerted(boolean alerted) {
        this.entityData.set(DATA_ALERTED, alerted);
    }

    public int getPickupCooldown() {
        return this.pickupCooldownTicks;
    }

    public void setPickupCooldown(int ticks) {
        this.pickupCooldownTicks = ticks;
    }

    public int getForageCooldown() {
        return this.forageCooldownTicks;
    }

    public void setForageCooldown(int ticks) {
        this.forageCooldownTicks = ticks;
    }

    public int getPendingGiftTicks() {
        return this.pendingGiftTicks;
    }

    public void startGiftForage(int ticks) {
        this.pendingGiftTicks = ticks;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(KEY_PICKUP_COOLDOWN, this.pickupCooldownTicks);
        tag.putInt(KEY_FORAGE_COOLDOWN, this.forageCooldownTicks);
        tag.putInt(KEY_PENDING_GIFT, this.pendingGiftTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.pickupCooldownTicks = tag.getInt(KEY_PICKUP_COOLDOWN);
        this.forageCooldownTicks = tag.getInt(KEY_FORAGE_COOLDOWN);
        this.pendingGiftTicks = tag.getInt(KEY_PENDING_GIFT);
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (!this.isOnGround() && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.isOnGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }
        this.flapping *= 0.9F;
        Vec3 movement = this.getDeltaMovement();
        if (!this.isOnGround() && movement.y < 0.0D) {
            this.setDeltaMovement(movement.multiply(1.0D, 0.6D, 1.0D));
        }
        this.flap += this.flapping * 2.0F;
    }

    @Override
    public boolean isFlying() {
        return !this.isOnGround();
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.playSound(SoundEvents.PARROT_FLY, 0.15F, 0.9F);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.72F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound() {
        return ModSounds.CROW_CAW.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    private static class AvoidPlayerWhenWildGoal extends AvoidEntityGoal<Player> {
        private final CrowEntity crow;

        AvoidPlayerWhenWildGoal(CrowEntity crow, float maxDistance, double walkSpeed, double sprintSpeed) {
            super(crow, Player.class, maxDistance, walkSpeed, sprintSpeed, CrowEntity::shouldAvoidPlayer);
            this.crow = crow;
        }

        @Override
        public boolean canUse() {
            return !this.crow.isTame() && super.canUse();
        }
    }
}