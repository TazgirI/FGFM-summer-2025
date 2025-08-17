package net.tazgirl.fgfmsummer.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.peter_fight.FallingBombExplosionHandler;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;

public class BombEntity extends MinecartTNT
{
    public BombEntity(EntityType<? extends MinecartTNT> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source)
    {
        if(this.level().isClientSide){return false;}

        if(!PeterFunctions.bombHover)
        {
            this.level().explode(null, new DamageSource(this.level().registryAccess().holderOrThrow(DamageTypes.EXPLOSION), PeterFunctions.peter), new FallingBombExplosionHandler(), this.position(), 5, false, Level.ExplosionInteraction.TRIGGER);
            this.discard();
            return false;
        }
        else
        {
            Vec3 posNormalized = new Vec3((int) Math.ceil(this.position().x), (int) Math.ceil(this.position().y), (int) Math.ceil(this.position().z));

            Vec3 currentProcessPos = posNormalized;

            for(Direction direction: Direction.values())
            {
                currentProcessPos = posNormalized;

                currentProcessPos.relative(direction, 1);

                if(this.level().isEmptyBlock(BlockPos.containing(currentProcessPos)))
                {
                    this.setPos(currentProcessPos);
                    this.teleportTo(currentProcessPos.x, currentProcessPos.y, currentProcessPos.z);
                    return false;
                }
            }


        }

        return false;
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.level().isClientSide){return;}

        if(PeterFunctions.bombHover)
        {
            int uuidAttempt = this.getData(DataAttachments.TARGET_PLAYER.get()).targetUuid();

            if (uuidAttempt != 0)
            {
                if (this.level().getEntity(uuidAttempt) instanceof Entity target)
                {
                    Vec3 backupPos = this.position();
                    ServerLevel level = (ServerLevel) this.level();
                    Vec3 hoverPos = new Vec3(target.position().x, target.position().y ,target.position().z);
                        for (int i = (int) Math.ceil(target.position().y) + 1; i <= 110; i++)
                    {
                        hoverPos = new Vec3(hoverPos.x, i + 1, hoverPos.z);

                        if(!level.isEmptyBlock(BlockPos.containing(hoverPos)))
                        {
                            hoverPos = new Vec3(hoverPos.x, i , hoverPos.z);
                            break;
                        }

                    }



                    if(NoNearbyBlocks(hoverPos,level))
                    {
                        this.setPos(hoverPos);
                        this.teleportTo(hoverPos.x,hoverPos.y,hoverPos.z);
                    }
                    else
                    {
                        this.setPos(backupPos);
                        this.teleportTo(backupPos.x,backupPos.y,backupPos.z);
                    }

                }
            }
        }

    }

    static boolean NoNearbyBlocks(Vec3 pos, ServerLevel level)
    {
        Vec3 currentCheckPos = new Vec3(pos.x, pos.y, pos.z);

        for(Direction direction: Direction.values())
        {
            if (direction != Direction.UP)
            {
                currentCheckPos = new Vec3(pos.x, pos.y, pos.z);
                currentCheckPos.relative(direction, 1);
                if(!level.isEmptyBlock(BlockPos.containing(currentCheckPos)))
                {
                    return false;
                }
            }

        }

        return true;
    }
}
