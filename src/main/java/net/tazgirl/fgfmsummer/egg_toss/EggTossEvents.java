package net.tazgirl.fgfmsummer.egg_toss;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.DropItemsFromList;
import net.tazgirl.fgfmsummer.dirty.TickTimer;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.init.Entities;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersTimerBossBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EggTossEvents
{

    public static void PlayerTick(PlayerTickEvent.Pre event)
    {
        if(event.getEntity().level().isClientSide()){return;}

        ServerPlayer player = (ServerPlayer) event.getEntity();

        player.getFoodData().setFoodLevel(20);

        boolean playerIsBlueTeam = player.getData(DataAttachments.TEAMS.get()).blueTeam();



        if(playerIsBlueTeam)
        {
            player.heal(20);
            if(player.position().z > 1215.4)
            {
                SimpleFuncs.ReallySendPlayer(player, new Vec3(player.position().x, player.position().y, 1215.4));
            }
        }
        else
        {
            if(player.position().z < 1215.4)
            {
                SimpleFuncs.ReallySendPlayer(player, new Vec3(player.position().x, player.position().y, 1215.4));
            }
        }
    }

    public static void ServerTick(ServerTickEvent.Pre event)
    {
        if(new Random().nextDouble(0, 1) < EggTossFunctions.chickenSpawnChance)
        {
            Chicken chicken = new Chicken(EntityType.CHICKEN, event.getServer().overworld());

            Random random = new Random();

            Vec3 chickenCorner1 = EggTossConstants.chickenAreaCorner1;
            Vec3 chickenCorner2 = EggTossConstants.chickenAreaCorner2;

            Vec3 spawnPos = new Vec3(random.nextDouble(chickenCorner1.x, chickenCorner2.x), 76, random.nextDouble(chickenCorner1.z, chickenCorner2.z));
            chicken.setPos(spawnPos);

            event.getServer().overworld().addFreshEntity(chicken);
        }

        if(event.getServer().overworld().getGameTime() % 20 == 0){EggTossFunctions.timer = TickTimer.tickTimer(EggTossFunctions.timer);}

        SkyJumpersTimerBossBar.SetName(EggTossFunctions.timer);

        if(EggTossFunctions.timer <= 0)
        {
            EggTossFunctions.End();
        }
    }

    public static void EntityDeath(LivingDeathEvent event)
    {
        if(event.getEntity().level().isClientSide()){return;}

        if(event.getEntity() instanceof Chicken chicken)
        {
            ItemEntity item = new ItemEntity(chicken.level(), chicken.position().x, chicken.position().y, chicken.position().z, new ItemStack(Items.EGG, new Random().nextInt(1, 4) * (chicken.position().z <= 1215.4 ? 4 : 1)));
            chicken.level().addFreshEntity(item);
        }
    }



    public static void ProjectileImpact(ProjectileImpactEvent event)
    {
        if(event.getEntity().level().isClientSide() || !(event.getProjectile().getOwner() instanceof ServerPlayer) || event.getRayTraceResult().getType() != HitResult.Type.ENTITY || !(((EntityHitResult) event.getRayTraceResult()).getEntity() instanceof Player)){return;}

        if(SimpleFuncs.IsPlayerBlueTeam((ServerPlayer) ((EntityHitResult) event.getRayTraceResult()).getEntity()) != SimpleFuncs.IsPlayerBlueTeam((ServerPlayer) event.getProjectile().getOwner()))
        {
            EggTossFunctions.AddEggTossPoints((ServerPlayer) event.getProjectile().getOwner(), 1);
        }
    }

    public static float EntityTakesDamage(LivingDamageEvent.Pre event)
    {
        if(!(event.getEntity() instanceof ServerPlayer player)){return event.getNewDamage();}

        if(player.getHealth() - event.getNewDamage() <= 0)
        {
            player.heal(20);
            DropItemsFromList.DropItems(player, List.of("minecraft:stone_sword"), new ArrayList<>(), 0.4);
            SimpleFuncs.ReallySendPlayer(player, EggTossConstants.redTeamPos);
            player.getInventory().setItem(0, new ItemStack(Items.STONE_SWORD, 1));

            if(event.getSource().getEntity() instanceof ServerPlayer attacker)
            {
                SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(player.getName().getString() + " was killed by " + attacker.getName().getString()));
            }
            else
            {
                SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(player.getName().getString() + " burnt up"));
            }

            return 0;
        }

        return event.getNewDamage();
    }

    public static void EntityTick(EntityTickEvent.Pre event)
    {
        if(event.getEntity().level().isClientSide() || !(event.getEntity() instanceof ThrownEgg egg)){return;}

        if(egg.getOwner() instanceof ServerPlayer)
        {
            if(!SimpleFuncs.IsPlayerUnderdog((ServerPlayer) egg.getOwner()))
            {
                egg.setGlowingTag(true);
            }
        }

    }

}
