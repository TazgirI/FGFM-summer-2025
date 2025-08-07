package net.tazgirl.fgfmsummer.peter_fight;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.DropItemsFromList;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;
import net.tazgirl.fgfmsummer.init.DataAttachments;

import java.util.EnumSet;

public class PeterFightEvents
{

    public static boolean OnPlayerDeath(LivingDamageEvent.Pre event)
    {
        return true;
    }

    public static float OnEntityHurt(LivingDamageEvent.Pre event)
    {

        if(event.getEntity() instanceof Player player && player.getHealth() - event.getNewDamage() <= 0)
        {
            if(player instanceof ServerPlayer)
            {
                PeterFightPlayerDeath(event, player);
            }

            return 0f;
        }

        if(event.getEntity().getData(DataAttachments.IS_DEAD).isDead())
        {
            return 0f;
        }

        if(event.getEntity() instanceof PeterGriffin peter)
        {
            if(peter.getHealth() - event.getNewDamage() <= 0)
            {
                if(!PeterFunctions.isAscended)
                {
                    PeterFunctions.AscendPeter();
                    return 0f;
                }
                else
                {
                    PeterFunctions.End();
                    return 0f;
                }
            }
        }


        if(PeterFunctions.punishDamage)
        {
            if(event.getSource().getEntity() == null)
            {
                return event.getNewDamage();
            }
            if(event.getSource().getEntity() instanceof Player player)
            {
                DamageSource bypassDamageSource = new DamageSource(event.getEntity().level().registryAccess().holderOrThrow(DamageTypes.ARROW));
                player.hurt(bypassDamageSource, event.getOriginalDamage());
            }
            return 0f;
        }

        if(event.getEntity() instanceof PeterGriffin && event.getSource().getEntity() instanceof Player player)
        {
            AddPeterPoints(player, Math.round(event.getNewDamage()));
        }

        return event.getNewDamage();
    }

    public static boolean OnEntityKnockback(LivingKnockBackEvent event)
    {
        if (event.getEntity() instanceof PeterGriffin)
        {
            return PeterFunctions.cancelKnockback;
        }

        return false;

    }

    public static void OnServerTick(ServerTickEvent.Pre event)
    {
        MinecraftServer server = event.getServer();

        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
            if(player.position().distanceTo(PeterFightConstants.deathCageCoords) < 10)
            {
                DataAttachments.isDeadRecord playerDeathRecord = player.getData(DataAttachments.IS_DEAD.get());

                if(playerDeathRecord.isDead() && playerDeathRecord.timeWhenFree() <= player.level().getGameTime())
                {
                    PeterFunctions.ReturnPlayerToArena(player);
                }
            }
        }
    }

    public static void AddPeterPoints(Player player, int amount)
    {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("peterFightScores")).add(amount);
    }

    static void PeterFightPlayerDeath(LivingDamageEvent.Pre event, Player player)
    {
        DropItemsFromList.DropItems((ServerPlayer) player, PeterFightConstants.itemsToDrop, PeterFightConstants.itemsToDestroy);
        player.getInventory().clearContent();
        Scoreboard scoreboard = player.getServer().getScoreboard();

        long currentTime = player.level().getGameTime();
        long timeWhenRespawn = currentTime + (((Math.min(scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("peterFightScores")).get() - player.getData(DataAttachments.SCORE_WHEN_LAST_DIED.get()).scoreWhenLastDied(), 150) / 10) + 5) * 20L);
        player.setData(DataAttachments.IS_DEAD.get(), new DataAttachments.isDeadRecord(true, currentTime,timeWhenRespawn));

        player.setData(DataAttachments.SCORE_WHEN_LAST_DIED.get(),new DataAttachments.scoreWhenLastDiedRecord(scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("peterFightScores")).get()));

        Vec3 respawnPos = PeterFightConstants.deathCageCoords;
        SimpleFuncs.ReallySendPlayer((ServerPlayer) player, PeterFightConstants.deathCageCoords);
        player.heal(20);
        player.getFoodData().setFoodLevel(20);
    }
}
