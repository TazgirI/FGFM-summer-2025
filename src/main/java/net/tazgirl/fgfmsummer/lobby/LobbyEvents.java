package net.tazgirl.fgfmsummer.lobby;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

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

        scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR,scoreboard.getObjective("overallScores"));
        for(ServerPlayer player : event.getServer().getPlayerList().getPlayers())
        {
            scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("overallScores"));
        }
    }
}
