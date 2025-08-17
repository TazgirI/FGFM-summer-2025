package net.tazgirl.fgfmsummer.kit_pvp;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.TickTimer;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersTimerBossBar;

import java.util.Random;

public class KitPvpEvents
{
    public static float PlayerTakesDamage(LivingDamageEvent.Pre event)
    {
        if(event.getEntity().level().isClientSide() || !(event.getEntity() instanceof ServerPlayer)){return event.getNewDamage();}

        if(SimpleFuncs.IsPlayerBlueTeam((ServerPlayer) event.getEntity()))
        {
            return 0;
        }

        if(event.getEntity().getHealth() - event.getNewDamage() <= 0)
        {
            ServerPlayer player = (ServerPlayer) event.getEntity();

            player.setData(DataAttachments.TEAMS.get(), new DataAttachments.teamsRecord(true));

            player.getInventory().clearContent();
            player.removeAllEffects();

            if(event.getSource().getEntity() instanceof ServerPlayer attacker)
            {
                KitPvpFunctions.AddKitPvpPoints(attacker, 1);
                SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(player.getName().getString() + " was killed by " + attacker.getName().getString()));
            }
            else if(player.getLastAttacker() instanceof ServerPlayer lastAttacker)
            {
                KitPvpFunctions.AddKitPvpPoints(lastAttacker, 1);
                SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(player.getName().getString() + " was killed while escaping " + lastAttacker.getName().getString()));

            }
            else
            {
                KitPvpFunctions.AddKitPvpPoints(player, -1);
                SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(player.getName().getString() + " died like an idiot, -1 point"));

            }

            player.heal(20);
            SimpleFuncs.ReallySendPlayer(player, KitPvpConstants.kitRoom.get(new Random().nextInt(0, KitPvpConstants.kitRoom.size())));

            return 0;
        }


        return event.getNewDamage();
    }

    public static void PlayerTick(PlayerTickEvent.Pre event)
    {
        if(event.getEntity().level().isClientSide()){return;}

        ServerPlayer player = (ServerPlayer) event.getEntity();

        if(!player.isCrouching())
        {
            player.setGlowingTag(true);
        }
        else
        {
            player.setGlowingTag(false);
        }

    }

    public static void ServerTick(ServerTickEvent.Pre event)
    {
        if(event.getServer().overworld().getGameTime() % 20 == 0)
        {
            KitPvpFunctions.timer = TickTimer.tickTimer(KitPvpFunctions.timer);
        }

        SkyJumpersTimerBossBar.SetName(KitPvpFunctions.timer);
    }

    public static boolean PlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getLevel().isClientSide() || event.getLevel().getBlockState(event.getPos()).getBlock() != Blocks.OAK_BUTTON){
            return false;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();

        KitPvpFunctions.AddKitPvpPoints(player, 3);

        SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(player.getName().getString() + " found a button, +3 points"));
        event.getLevel().removeBlock(event.getPos(), true);

        return true;
    }

}
