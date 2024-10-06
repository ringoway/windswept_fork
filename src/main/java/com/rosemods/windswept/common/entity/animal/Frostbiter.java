package com.rosemods.windswept.common.entity.animal;

import com.rosemods.windswept.common.entity.ai.goal.FrostbiterEatFlowersGoal;
import com.rosemods.windswept.core.registry.WindsweptEntityTypes;
import com.rosemods.windswept.core.registry.WindsweptItems;
import com.rosemods.windswept.core.registry.WindsweptPlayableEndimations;
import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class Frostbiter extends TamableAnimal implements Endimatable, NeutralMob, ItemSteerable, Saddleable {
    private static final EntityDataAccessor<Boolean> LEFT_ANTLER = SynchedEntityData.defineId(Frostbiter.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RIGHT_ANTLER = SynchedEntityData.defineId(Frostbiter.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(Frostbiter.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BOOST_TIME = SynchedEntityData.defineId(Frostbiter.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANGER_TIME = SynchedEntityData.defineId(Frostbiter.class, EntityDataSerializers.INT);
    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private final ItemBasedSteering steering = new ItemBasedSteering(this.entityData, BOOST_TIME, SADDLED);
    private int dropDelay;
    private boolean hasJustDropped;
    private UUID lastHurtBy;

    public Frostbiter(EntityType<? extends Frostbiter> type, Level level) {
        super(type, level);
        this.setTame(false);
        this.setLeftAntler(true);
        this.setRightAntler(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEFT_ANTLER, true);
        this.entityData.define(RIGHT_ANTLER, true);
        this.entityData.define(SADDLED, false);
        this.entityData.define(BOOST_TIME, 0);
        this.entityData.define(ANGER_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("LeftAntler", this.hasLeftAntler());
        compound.putBoolean("RightAntler", this.hasRightAntler());
        compound.putBoolean("Saddled", this.isSaddled());
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setLeftAntler(compound.getBoolean("LeftAntler"));
        this.setRightAntler(compound.getBoolean("RightAntler"));
        this.setSaddled(compound.getBoolean("Saddled"));
        this.readPersistentAngerSaveData(this.level(), compound);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FrostbiterEatFlowersGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FrostbiterPanicGoal());
        this.goalSelector.addGoal(2, new FrostbiterMeleeAttackGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1f));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.15f, Ingredient.of(WindsweptItems.HOLLY_BERRIES.get(), WindsweptItems.HOLLY_BERRIES_ON_A_STICK.get()), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, .7f));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected void customServerAiStep() {
        this.updatePersistentAnger((ServerLevel) this.level(), true);

        if (this.isAngry())
            this.lastHurtByPlayerTime = this.tickCount;

        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isNoAi()) {
            this.tickAntlerShaking();
        }

    }

    private void tickAntlerShaking() {
        boolean isShaking = this.isEndimationPlaying(WindsweptPlayableEndimations.FROSTBITER_SHAKE);

        if (isShaking)
            this.dropDelay++;
        else {
            this.dropDelay = 0;
            this.hasJustDropped = false;
        }

        if (this.hasAntlers() && !this.hasJustDropped && !this.isVehicle())
            if (this.dropDelay == 0 && this.random.nextInt(5000) == 0 && this.isNoEndimationPlaying()) {
                NetworkUtil.setPlayingAnimation(this, WindsweptPlayableEndimations.FROSTBITER_SHAKE);
                this.getNavigation().stop();
            } else if (this.dropDelay == 8 && isShaking)
                this.dropRandomAntler();

    }

    public void setLeftAntler(boolean has) {
        this.entityData.set(LEFT_ANTLER, has);
    }

    public void setRightAntler(boolean has) {
        this.entityData.set(RIGHT_ANTLER, has);
    }

    public boolean hasLeftAntler() {
        return this.entityData.get(LEFT_ANTLER) && !this.isBaby();
    }

    public boolean hasRightAntler() {
        return this.entityData.get(RIGHT_ANTLER) && !this.isBaby();
    }

    public boolean hasAntlers() {
        return (this.entityData.get(LEFT_ANTLER) || this.entityData.get(RIGHT_ANTLER)) && !this.isBaby();
    }

    private void dropRandomAntler() {
        if (this.hasAntlers()) {
            if (this.random.nextBoolean())
                this.setLeftAntler(false);
            else
                this.setRightAntler(false);
        } else if (this.hasRightAntler())
            this.setRightAntler(false);
        else if (this.hasLeftAntler())
            this.setLeftAntler(false);

        this.hasJustDropped = true;
        this.spawnAtLocation(WindsweptItems.FROZEN_BRANCH.get(), 1);
    }

    public void growRandomAntler() {
        if (!this.hasAntlers()) {
            if (this.random.nextBoolean())
                this.setLeftAntler(true);
            else
                this.setRightAntler(true);
        } else if (!this.hasRightAntler())
            this.setRightAntler(true);
        else if (!this.hasLeftAntler())
            this.setLeftAntler(true);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity) && entity instanceof LivingEntity livingEntity) {
            livingEntity.setTicksFrozen(livingEntity.getTicksFrozen() + 100);
            return true;
        }

        return false;
    }

    @Override
    protected void onChangedBlock(BlockPos pos) {
        FrostWalkerEnchantment.onEntityMoved(this, this.level(), pos, 0);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide)
                player.startRiding(this);

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (stack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1f, 1f);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, Items.MILK_BUCKET.getDefaultInstance()));

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (!this.isTame() && stack.is(WindsweptItems.HOLLY_BERRIES.get())) {
            if (this.random.nextInt(4) == 0) {
                this.setTame(true);
                this.setOwnerUUID(player.getUUID());
                this.usePlayerItem(player, hand, stack);
                this.navigation.stop();
                this.level().broadcastEntityEvent(this, (byte) 7);

                //this.setOrderedToSit(true);
            } else {
                this.usePlayerItem(player, hand, stack);
                this.level().broadcastEntityEvent(this, (byte) 6);
            }

            return InteractionResult.SUCCESS;
        } else {
            InteractionResult interactionresult = super.mobInteract(player, hand);

            return !interactionresult.consumesAction() ? (stack.is(Items.SADDLE) ?
                    stack.interactLivingEntity(player, this, hand) : InteractionResult.PASS) : interactionresult;
        }

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? SoundEvents.POLAR_BEAR_AMBIENT_BABY : SoundEvents.POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, .15f, 1f);
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby() ? 1.4f : .4f;
    }

    @Override
    protected float getSoundVolume() {
        return this.isBaby() ? .8f : 1f;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return this.isTame();
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        Frostbiter biter = WindsweptEntityTypes.FROSTBITER.get().create(level);

        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            biter.setOwnerUUID(uuid);
            biter.setTame(true);
        }

        return biter;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(WindsweptItems.HOLLY_BERRIES.get());
    }

    @Override
    protected float getStandingEyeHeight(Pose pos, EntityDimensions dimensions) {
        return this.isBaby() ? dimensions.height * .95f : 1.3f;
    }

    public static AttributeSupplier.Builder createFrostbiterAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ARMOR, 4f)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.MAX_HEALTH, 40f)
                .add(Attributes.MOVEMENT_SPEED, .22f)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2f);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(ANGER_TIME, time);
    }

    @Override
    public UUID getPersistentAngerTarget() {
        return this.lastHurtBy;
    }

    @Override
    public void setPersistentAngerTarget(UUID target) {
        this.lastHurtBy = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_RANGE.sample(this.random));
    }

    @Override
    public Entity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();

        return entity instanceof Player player && this.canBeControlledBy(player) ? entity : null;
    }

    private boolean canBeControlledBy(Player player) {
        return this.isSaddled() && (player.getMainHandItem().is(WindsweptItems.HOLLY_BERRIES_ON_A_STICK.get()) || player.getOffhandItem().is(WindsweptItems.HOLLY_BERRIES_ON_A_STICK.get()));
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    public void travel(Vec3 travel) {
        this.travel(this, this.steering, travel);
    }

    @Override
    public void travelWithInput(Vec3 travel) {
        super.travel(travel);
    }

    @Override
    public float getSteeringSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * .6f;
    }

    @Override
    public boolean isSaddleable() {
        return this.isTame() && !this.isBaby();
    }

    @Override
    public void equipSaddle(SoundSource sound) {
        this.setSaddled(true);

        if (sound != null)
            this.level().playSound(null, this, SoundEvents.HORSE_SADDLE, sound, .5f, 1f);
    }

    private void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    @Override
    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() * 1.25d;
    }

    public class FrostbiterPanicGoal extends PanicGoal {
        public FrostbiterPanicGoal() {
            super(Frostbiter.this, 2.2d);
        }

        @Override
        protected boolean shouldPanic() {
            return this.mob.isBaby() && super.shouldPanic();
        }

    }

    public class FrostbiterMeleeAttackGoal extends MeleeAttackGoal {

        public FrostbiterMeleeAttackGoal() {
            super(Frostbiter.this, 1.2f, false);
        }

        @Override
        public boolean canUse() {
            return !this.mob.isBaby() && super.canUse();
        }

    }

}
