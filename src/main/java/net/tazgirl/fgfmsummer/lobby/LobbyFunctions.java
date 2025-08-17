package net.tazgirl.fgfmsummer.lobby;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.egg_toss.EggTossFunctions;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersFunctions;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersTimerBossBar;

public class LobbyFunctions
{
    public static void AddOverallPoints(Player player, int amount)
    {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("overallScores")).add(amount);
    }

    public static void ClearEverything()
    {
        PeterFunctions.ClearVariables();

        SimpleFuncs.RemoveAllLocks();
        SkyJumpersFunctions.ClearVariables();
        SkyJumpersTimerBossBar.HideBossbarToAll();
        EggTossFunctions.ResetEverything();


        SimpleFuncs.ResetObjective(GlobalConstants.thisServer, "peterFightScores");
        SimpleFuncs.ResetObjective(GlobalConstants.thisServer, "skyJumpersScores");
        SimpleFuncs.ResetObjective(GlobalConstants.thisServer, "eggTossScores");

        ClearAllTeams();
        ClearAllDogs();

        Scoreboard scoreboard = GlobalConstants.thisServer.getScoreboard();

        scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR,scoreboard.getObjective("overallScores"));


        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.setData(DataAttachments.DOGS.get(), new DataAttachments.dogsRecord(false, false));
            player.getInventory().clearContent();
            player.removeAllEffects();
        }
    }

    public static void MakePlayerUnderdog(ServerPlayer player)
    {
        player.setData(DataAttachments.DOGS.get(), new DataAttachments.dogsRecord(true, false));
    }

    public static void MakePlayerHandidog(ServerPlayer player)
    {
        player.setData(DataAttachments.DOGS.get(), new DataAttachments.dogsRecord(false, true));
    }

    public static void GoToLobby()
    {
        GlobalConstants.gamemode = "Lobby";

        SimpleFuncs.ClearAllInventories();

        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.setGameMode(GameType.ADVENTURE);
            SimpleFuncs.ReallySendPlayer(player, LobbyConstants.lobbyPos);
        }

    }

    public static void MakePlayerBlueTeam(ServerPlayer player)
    {
        player.setData(DataAttachments.TEAMS.get(), new DataAttachments.teamsRecord(true));
    }

    public static void MakePlayerRedTeam(ServerPlayer player)
    {
        player.setData(DataAttachments.TEAMS.get(), new DataAttachments.teamsRecord(false));
    }

    public static void ClearAllTeams()
    {
        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.setData(DataAttachments.TEAMS.get(), new DataAttachments.teamsRecord(false));
        }
    }

    public static void SoundOffOverallScores()
    {
        MinecraftServer server = GlobalConstants.thisServer;

        String messageToSend = "";

        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
            messageToSend = messageToSend.concat(player.getName().getString() + ": " + player.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),player.getScoreboard().getObjective("overallScores")).get() + "\n");
        }

        SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(messageToSend));
    }

    public static void ListScoreboard(String scoreboardName)
    {
        MinecraftServer server = GlobalConstants.thisServer;

        String messageToSend = "";

        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
            messageToSend = messageToSend.concat(player.getName().getString() + ": " + player.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),player.getScoreboard().getObjective(scoreboardName)).get() + "\n");
        }

        SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(messageToSend));
    }

    public static void ListTeams()
    {
        MinecraftServer server = GlobalConstants.thisServer;

        String messageToSend = "";

        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
            messageToSend = messageToSend.concat(player.getName().getString() + " is on " + (SimpleFuncs.IsPlayerBlueTeam(player) ? "blue " : "red ") + " team \n");
        }

        SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(messageToSend));
    }

    public static void ClearAllDogs()
    {
        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.setData(DataAttachments.DOGS.get(), new DataAttachments.dogsRecord(false, false));
        }
    }
}
