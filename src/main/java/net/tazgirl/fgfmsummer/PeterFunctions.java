package net.tazgirl.fgfmsummer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.tazgirl.fgfmsummer.damage.DamageTypes;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PeterFunctions
{
    static List<Player> players = new ArrayList<>();
    static String currentGoal = "AttemptSlap";
    static Player currentTarget = null;

    static String currentAnimation;

    public static void AttemptSlapTarget(PeterGriffin peter)
    {
        if(currentTarget != null)
        {
            peter.getNavigation().moveTo(currentTarget, 1);

            if(SimpleFuncs.DistanceBetween(peter.position(), currentTarget.position()) <= 2)
            {
                peter.setAnimation("animation.petergriffin.slap");

                SimpleFuncs.CreateDelayedTask(3, () ->
                {
                    DamageSource damageSource = new DamageSource(peter.level().registryAccess().holderOrThrow(DamageTypes.PETER_DAMAGE), peter);
                    currentTarget.hurt(damageSource, 4);
                    currentGoal = "";
                });


            }


        }






    }

    public static void PeterTick(PeterGriffin peter)
    {
        currentTarget = peter.level().getNearestPlayer(peter, 100);

        currentAnimation = peter.getSyncedAnimation();

        if(Objects.equals(currentGoal, "AttemptSlap"))
        {
            AttemptSlapTarget(peter);
        }

        if(Objects.equals(currentGoal, ""))
        {
            currentGoal = "AttemptSlap";
        }

        if(currentTarget != null)
        {
            System.out.println(currentTarget.getName());
        }
        System.out.println(currentAnimation);
        System.out.println(currentGoal);

    }

    public static void AscendPeterTexture(PeterGriffin peter)
    {
        peter.setTexture("amogusgriffin");
    }

}
