
package net.tazgirl.fgfmsummer.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;
import net.tazgirl.fgfmsummer.init.Entities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;

public class PeterGriffin extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(PeterGriffin.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(PeterGriffin.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(PeterGriffin.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DATA_currentAnimation = SynchedEntityData.defineId(PeterGriffin.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_ascendedForm = SynchedEntityData.defineId(PeterGriffin.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_currentlyUnderManualControl = SynchedEntityData.defineId(PeterGriffin.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_currentGoal = SynchedEntityData.defineId(PeterGriffin.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_currentTarget = SynchedEntityData.defineId(PeterGriffin.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure = "empty";

    public final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.GREEN, ServerBossEvent.BossBarOverlay.PROGRESS);



    public PeterGriffin(EntityType<PeterGriffin> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "petergriffin");
        builder.define(DATA_currentAnimation, "idle");
        builder.define(DATA_ascendedForm, false);
        builder.define(DATA_currentlyUnderManualControl, true);
        builder.define(DATA_currentGoal, "");
        builder.define(DATA_currentTarget, 0);
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData)
    {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        bossInfo.setVisible(true);

        for(ServerPlayer player:this.getServer().getPlayerList().getPlayers())
        {
            bossInfo.addPlayer(player);
        }

        this.setCustomName(Component.literal("Peter Griffin"));

        PeterFunctions.ClearVariables();

        PeterFunctions.peter = this;


        return result;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.death"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putString("DatacurrentAnimation", this.entityData.get(DATA_currentAnimation));
        compound.putBoolean("DataascendedForm", this.entityData.get(DATA_ascendedForm));
        compound.putBoolean("DatacurrentlyUnderManualControl", this.entityData.get(DATA_currentlyUnderManualControl));
        compound.putString("DatacurrentGoal", this.entityData.get(DATA_currentGoal));
        compound.putInt("DatacurrentTarget", this.entityData.get(DATA_currentTarget));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
        if (compound.contains("DatacurrentAnimation"))
            this.entityData.set(DATA_currentAnimation, compound.getString("DatacurrentAnimation"));
        if (compound.contains("DataascendedForm"))
            this.entityData.set(DATA_ascendedForm, compound.getBoolean("DataascendedForm"));
        if (compound.contains("DatacurrentlyUnderManualControl"))
            this.entityData.set(DATA_currentlyUnderManualControl, compound.getBoolean("DatacurrentlyUnderManualControl"));
        if (compound.contains("DatacurrentGoal"))
            this.entityData.set(DATA_currentGoal, compound.getString("DatacurrentGoal"));
        if (compound.contains("DatacurrentTarget"))
            this.entityData.set(DATA_currentTarget, compound.getInt("DatacurrentTarget"));
    }

    @Override
    public void baseTick() {
        super.baseTick();

        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        PeterFunctions.PeterTick(this);


        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).scale(2f);
    }

    public static void init(RegisterSpawnPlacementsEvent event) {
        event.register(Entities.PETER_GRIFFIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 600);
        builder = builder.add(Attributes.ARMOR, 1);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

            ) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.petergriffin.legswalk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.petergriffin.idle"));
        }
        return PlayState.STOP;
    }

    String prevAnim = "empty";

    private PlayState procedurePredicate(AnimationState event) {
        if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
            if (!this.animationprocedure.equals(prevAnim))
                event.getController().forceAnimationReset();
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                this.animationprocedure = "empty";
                event.getController().forceAnimationReset();
            }
        } else if (animationprocedure.equals("empty")) {
            prevAnim = "empty";
            return PlayState.STOP;
        }
        prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(PeterGriffin.RemovalReason.KILLED);
            this.dropExperience(this);
        }
    }

    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
    }

    public String getPlayingAnimation()
    {
        return "";
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 2, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 2, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
