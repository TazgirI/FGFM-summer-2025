package net.tazgirl.fgfmsummer.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class PeterArrow extends Arrow
{
    public PeterArrow(EntityType<? extends Arrow> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        if(result.getEntity() instanceof PeterGriffin)
        {
            return;
        }
        else
        {
            super.onHitEntity(result);
        }

    }
}
