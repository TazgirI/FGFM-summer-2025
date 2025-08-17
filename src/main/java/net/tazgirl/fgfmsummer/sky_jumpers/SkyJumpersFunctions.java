package net.tazgirl.fgfmsummer.sky_jumpers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.packets.GamemodePayload;
import net.tazgirl.fgfmsummer.init.DataAttachments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkyJumpersFunctions
{
    public static boolean lockInPlace = false;

    public static boolean gameRunning = false;

    public static double timer = 7.30;
    public static boolean tickTimer = false;

    public static int arrowTimer = 0;

    public static void Setup(MinecraftServer server)
    {
        ClearVariables();



        timer = 7.30;



        GlobalConstants.gamemode = "SkyJumpers";

        lockInPlace = true;

        SkyJumpersTimerBossBar.ShowBossbarToAll();

        List<Vec3> spawnLocationsCopy = new ArrayList<>(SkyJumpersConstants.spawnPoints);

        server.getScoreboard().setDisplayObjective(DisplaySlot.SIDEBAR,server.getScoreboard().getObjective("skyJumpersScores"));

        SimpleFuncs.ResetObjective(server, "skyJumpersScores");

        Vec3 thisPlayersPlace = null;
        Random random = new Random();

        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.setGameMode(GameType.ADVENTURE);
            player.connection.send(new ClientboundCustomPayloadPacket(new GamemodePayload("SkyJumpers")));

            int index = random.nextInt(0, spawnLocationsCopy.size());
            thisPlayersPlace = spawnLocationsCopy.get(index);
            spawnLocationsCopy.remove(index);

            SetSkyJumpersInventory(player);

            player.setData(DataAttachments.LOCK_POSTION.get(), new DataAttachments.lockedPositionRecord(true, thisPlayersPlace.x, thisPlayersPlace.y, thisPlayersPlace.z));
        }

        GlobalConstants.gamemode = "SkyJumpers";

    }

    public static void AddSkyJumperPoints(Player player, int amount)
    {
        if(!gameRunning){return;}
        Scoreboard scoreboard = player.getServer().getScoreboard();
        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("skyJumpersScores")).add(amount);
    }

    public static void Start()
    {
        SimpleFuncs.RemoveAllLocks();

        gameRunning = true;
        tickTimer = true;

    }

    public static void ClearVariables()
    {
        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.removeData(DataAttachments.TARGET_PLAYER);
            player.removeData(DataAttachments.LOCK_POSTION);
        }

        SimpleFuncs.RemoveAllLocks();

        lockInPlace = false;
        timer = 7.30;
        gameRunning = false;
        tickTimer = false;
        arrowTimer = 0;

        SkyJumpersTimerBossBar.HideBossbarToAll();
    }

    public static void GameEnd()
    {
        gameRunning = false;
        tickTimer = false;
        lockInPlace = true;

        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            Vec3 playerPos = player.position();
            player.setData(DataAttachments.LOCK_POSTION.get(), new DataAttachments.lockedPositionRecord(true, playerPos.x, playerPos.y, playerPos.z));
        }
    }

    public static void ReturnToLobby()
    {
        SimpleFuncs.RemoveAllLocks();
        ClearVariables();
        SkyJumpersTimerBossBar.HideBossbarToAll();
    }

    static void KillPlayer(ServerPlayer player, boolean arrowKill)
    {
        if(player.level().isClientSide()){return;}

        DataAttachments.targetPlayerRecord attackerRecord = player.getData(DataAttachments.TARGET_PLAYER);
        int targetUuid = attackerRecord.targetUuid();

        ServerPlayer attacker = null;

        if(targetUuid != 0 && player.level().getGameTime() - attackerRecord.assignmentTime() < 250)
        {
            attacker = (ServerPlayer) player.level().getEntity(targetUuid);
        }

        SimpleFuncs.ReallySendPlayer(player, SkyJumpersConstants.spawnPoints.get(new Random().nextInt(0, SkyJumpersConstants.spawnPoints.size())));

        SetSkyJumpersInventory(player);

        if(attacker != null)
        {
            if(arrowKill)
            {
                AddSkyJumperPoints(attacker, 2);
                SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(attacker.getName().getString() + " shot " + player.getName().getString() + ", +2 points").withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
            }
            else
            {
                AddSkyJumperPoints(attacker, 1);
                SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(attacker.getName().getString() + " knocked off " + player.getName().getString() + ", +1 point").withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
            }
        }
        else
        {
            SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(player.getName().getString() + " was a silly billy, +0 points").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        }

        player.removeData(DataAttachments.TARGET_PLAYER);

    }

    public static void SetSkyJumpersInventory(ServerPlayer player)
    {
        player.getInventory().clearContent();


        player.getInventory().setItem(40, new ItemStack(Items.BOW, 1));

        player.getInventory().setItem(1, new ItemStack(Items.ARROW, 1));

        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 30000, 0, true, false, false));

        if(SimpleFuncs.IsPlayerUnderdog(player))
        {
            player.getInventory().setItem(40, new ItemStack(Items.CROSSBOW, 1));
        }
        else if (SimpleFuncs.IsPlayerHandidog(player))
        {
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30000, 1, true, false, false));
        }

        player.inventoryMenu.broadcastChanges();
    }
}
