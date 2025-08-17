package net.tazgirl.fgfmsummer.kit_pvp;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.InventoryLoader;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.lobby.LobbyFunctions;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersTimerBossBar;

import java.util.Random;

public class KitPvpFunctions
{

    public static double timer = 8.00;

    public static void AddKitPvpPoints(ServerPlayer player, int amount)
    {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("kitPvpScores")).add(amount);
    }

    public static void Setup()
    {
        GlobalConstants.gamemode = "KitPvp";

        SkyJumpersTimerBossBar.ShowBossbarToAll();
        timer = 8.00;

        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            SimpleFuncs.ReallySendPlayer(player, KitPvpConstants.kitRoom.get(new Random().nextInt(0, KitPvpConstants.kitRoom.size())));
            player.setData(DataAttachments.TEAMS.get(), new DataAttachments.teamsRecord(true));
            player.getInventory().clearContent();
        }
    }

    public static void SendPlayerToArena(ServerPlayer player, String kitName)
    {
        player.setData(DataAttachments.TEAMS.get(), new DataAttachments.teamsRecord(false));

        switch(kitName)
        {
            case "quagmire":
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30000, 2));
                InventoryLoader.setPlayerInventoryFromNBT("quagmire", player);
                break;
        }

        SimpleFuncs.ReallySendPlayer(player, KitPvpConstants.spawnPositions.get(new Random().nextInt(0, KitPvpConstants.spawnPositions.size())));
    }

    public static void End()
    {
        LobbyFunctions.GoToLobby();
    }

}
