package net.tazgirl.fgfmsummer.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Implements;

import java.lang.reflect.Field;

public class InvincibleItemFrame extends ItemFrame
{

    private boolean fixed;

    public InvincibleItemFrame(EntityType<? extends ItemFrame> entityType, Level level) {
        super(entityType, level);
    }

    public InvincibleItemFrame(Level level, BlockPos pos, Direction facingDirection)
    {
        super(level, pos, facingDirection);
    }

    public void setFixed(boolean value)
    {
        try
        {
            Field fixedField = ItemFrame.class.getDeclaredField("fixed");
            fixedField.setAccessible(true);
            fixedField.setBoolean(this, value);
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }



}
