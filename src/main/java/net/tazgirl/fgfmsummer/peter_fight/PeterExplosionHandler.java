package net.tazgirl.fgfmsummer.peter_fight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;

public class PeterExplosionHandler extends ExplosionDamageCalculator
{


    public PeterExplosionHandler()
    {

    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power)
    {
        return false;
    }

    @Override
    public boolean shouldDamageEntity(Explosion explosion, Entity entity)
    {
        return !(entity instanceof PeterGriffin);
    }

    @Override
    public float getKnockbackMultiplier(Entity entity)
    {
        return entity instanceof PeterGriffin ? 0 : 2;

    }

    @Override
    public float getEntityDamageAmount(Explosion explosion, Entity entity)
    {
        return 10;

    }
}

