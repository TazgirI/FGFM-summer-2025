package net.tazgirl.fgfmsummer.egg_toss;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.lobby.LobbyFunctions;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersTimerBossBar;

import java.util.Random;

public class EggTossFunctions
{

    public static ServerPlayer stooge = null;

    public static double chickenSpawnChance = 0.06;

    public static double timer = 4.0;

    public static boolean running = true;

    public static void Setup()
    {
        ResetEverything();

        MinecraftServer server = GlobalConstants.thisServer;

        server.getScoreboard().setDisplayObjective(DisplaySlot.SIDEBAR,server.getScoreboard().getObjective("eggTossScores"));

        SimpleFuncs.ResetObjective(server, "eggTossScores");

        GlobalConstants.gamemode = "EggToss";

        chickenSpawnChance = chickenSpawnChance * ((GlobalConstants.thisServer.getPlayerList().getPlayers().size() - 1) * 0.5);

        stooge = null;

        timer = 5.0;
        running = true;

        for(Entity entity: server.overworld().getEntities().getAll())
        {
            if(entity instanceof Chicken || entity instanceof ItemEntity)
            {
                entity.discard();
            }
        }

        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
            player.setGameMode(GameType.ADVENTURE);

            player.getInventory().clearContent();
            player.getInventory().setItem(0, new ItemStack(Items.STONE_SWORD, 1));

            SimpleFuncs.ReallySendPlayer(player, EggTossConstants.redTeamPos);

            player.setData(DataAttachments.TEAMS.get(), new DataAttachments.teamsRecord(false));

            if(SimpleFuncs.IsPlayerHandidog(player))
            {
                stooge = player;
            }
        }

        if(stooge == null)
        {
            stooge = server.getPlayerList().getPlayerByName("Tazgirl");
        }

        if(stooge == null)
        {
            stooge = server.getPlayerList().getPlayers().get(new Random().nextInt(0, server.getPlayerList().getPlayers().size()));
        }

        stooge.setData(DataAttachments.TEAMS.get(), new DataAttachments.teamsRecord(true));

        SimpleFuncs.ReallySendPlayer(stooge, EggTossConstants.stoogePos);

        SkyJumpersTimerBossBar.ShowBossbarToAll();

    }

    public static void AddEggTossPoints(ServerPlayer player, int amount)
    {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("eggTossScores")).add(amount);
    }

    public static void End()
    {
        LobbyFunctions.GoToLobby();
    }

    public static void ResetEverything()
    {
        stooge = null;
        chickenSpawnChance = 0.06;
        running = true;
    }


}
