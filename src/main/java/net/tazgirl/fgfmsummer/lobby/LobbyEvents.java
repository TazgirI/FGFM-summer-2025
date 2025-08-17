package net.tazgirl.fgfmsummer.lobby;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.tazgirl.fgfmsummer.SimpleFuncs;

public class LobbyEvents
{

    public static void OnServerStart(ServerStartingEvent event)
    {


        Scoreboard scoreboard = event.getServer().getScoreboard();
        Objective objective = scoreboard.getObjective("overallScores");

        if (objective == null) {
            scoreboard.addObjective("overallScores", ObjectiveCriteria.DUMMY, Component.literal("Overall scores"), ObjectiveCriteria.DUMMY.getDefaultRenderType(), true, null);
        }

        if(scoreboard.getObjective("peterFightScores") == null){scoreboard.addObjective("peterFightScores",ObjectiveCriteria.DUMMY, Component.literal("Peter Slaying"), ObjectiveCriteria.DUMMY.getDefaultRenderType(), true, null);}
        if(scoreboard.getObjective("skyJumpersScores") == null){scoreboard.addObjective("skyJumpersScores",ObjectiveCriteria.DUMMY, Component.literal("Sky Jumper Kills"), ObjectiveCriteria.DUMMY.getDefaultRenderType(), true, null);}
        if(scoreboard.getObjective("eggTossScores") == null){scoreboard.addObjective("eggTossScores",ObjectiveCriteria.DUMMY, Component.literal("Egg Toss Points"), ObjectiveCriteria.DUMMY.getDefaultRenderType(), true, null);}
        if(scoreboard.getObjective("kitPvpScores") == null){scoreboard.addObjective("kitPvpScores",ObjectiveCriteria.DUMMY, Component.literal("Kit PvP Kills"), ObjectiveCriteria.DUMMY.getDefaultRenderType(), true, null);}


        LobbyFunctions.ClearEverything();

        scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR,scoreboard.getObjective("overallScores"));
        for(ServerPlayer player : event.getServer().getPlayerList().getPlayers())
        {
            scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("overallScores"));
        }
    }

    public static void OnPlayerJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getEntity().level().isClientSide()){return;}

        SimpleFuncs.ReallySendPlayer((ServerPlayer) event.getEntity(), LobbyConstants.lobbyPos);

        ((ServerPlayer) event.getEntity()).setGameMode(GameType.ADVENTURE);
    }

    public static void OnPlayerTick(PlayerTickEvent.Pre event)
    {
        event.getEntity().heal(20);
        event.getEntity().getFoodData().setFoodLevel(20);

    }
}
