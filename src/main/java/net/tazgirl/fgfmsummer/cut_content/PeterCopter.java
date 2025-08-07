package net.tazgirl.fgfmsummer.cut_content;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.List;

public class PeterCopter extends Entity implements GeoEntity
{

    public PeterCopter(EntityType<PeterCopter> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {

    }

    @Override
    public void tick() {
        super.tick(); // Let it fall naturally via gravity
    }



    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // No save data
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // No save data
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        boolean result = super.causeFallDamage(fallDistance, damageMultiplier, source);

        if (!this.level().isClientSide && fallDistance > 0) {
            AABB bounds = this.getBoundingBox();
            List<Player> players = this.level().getEntitiesOfClass(Player.class, bounds);
            for (Player player : players) {

            }
            SimpleFuncs.queueServerWork(20, this::discard);
        }

        return result;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return null;
    }
}
