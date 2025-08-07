package net.tazgirl.fgfmsummer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.Input;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.dirty.DropItemsFromList;
import net.tazgirl.fgfmsummer.dirty.packets.GamemodePayload;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;
import net.tazgirl.fgfmsummer.lobby.LobbyEvents;
import net.tazgirl.fgfmsummer.lobby.LobbyFunctions;
import net.tazgirl.fgfmsummer.peter_fight.PeterFightConstants;
import net.tazgirl.fgfmsummer.peter_fight.PeterFightEvents;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;

import java.awt.event.InputEvent;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GlobalEventCatcher
{

    @SubscribeEvent
    public static void OnPlayerDeath(LivingDeathEvent event)
    {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof Player)
        {
            switch (GlobalConstants.gamemode)
            {
                case "PeterFight":
                    event.setCanceled(true);
                    break;

                default:
                    break;
            }
        }
        else
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void OnEntityDeath(LivingDeathEvent event)
    {
        if(!event.getEntity().level().isClientSide() && !(event.getEntity() instanceof Player))
        {
            switch (GlobalConstants.gamemode)
            {
                case "PeterFight":
                    event.setCanceled(true);
                    break;

                default:
                    break;
            }
        }
    }

    @SubscribeEvent
    public static void OnEntityHurtPre(LivingDamageEvent.Pre event)
    {
        if (!event.getEntity().level().isClientSide())
        {
            switch (GlobalConstants.gamemode)
            {
                case "PeterFight":
                    event.setNewDamage(PeterFightEvents.OnEntityHurt(event));
                    if(event.getEntity() instanceof PeterGriffin && event.getEntity().getHealth() - event.getNewDamage() <= 0)
                    {

                    }
                    break;

                default:
                    break;
            }
        }
    }

    @SubscribeEvent
    public static void OnEntityHurtPost(LivingDamageEvent.Post event)
    {
        if (!event.getEntity().level().isClientSide())
        {
            switch (GlobalConstants.gamemode)
            {
                case "PeterFight":
                    if(event.getEntity() instanceof PeterGriffin && PeterFunctions.removeInvulnFrames)
                    {
                        event.getEntity().invulnerableTime = 0;
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @SubscribeEvent
    public static void LivingEntityKnockback(LivingKnockBackEvent event)
    {
        if (!event.getEntity().level().isClientSide())
        {
            switch (GlobalConstants.gamemode)
            {
                case "PeterFight":
                    event.setCanceled(PeterFightEvents.OnEntityKnockback(event));
                    break;

                default:
                    break;
            }
        }
    }

    @SubscribeEvent
    public static void OnServerStart(ServerStartingEvent event)
    {
        LobbyEvents.OnServerStart(event);
    }

    @SubscribeEvent
    public static void OnServerTick(ServerTickEvent.Pre event)
    {
        if(!event.getServer().getPlayerList().getPlayers().isEmpty())
        {
            event.getServer().getPlayerList().getPlayers().getFirst().connection.send(new GamemodePayload("PeterFight"));
        }

        switch (GlobalConstants.gamemode)
        {
            case "PeterFight":
                PeterFightEvents.OnServerTick(event);
                break;

            default:
                break;
        }
    }

    @SubscribeEvent
    public static void OnServerPlayerTick(PlayerTickEvent.Pre event)
    {
        if(event.getEntity().level().isClientSide){return;}

        ((ServerPlayer) event.getEntity()).connection.send(new GamemodePayload(GlobalConstants.gamemode));


    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void OnOpenScreen(ScreenEvent.Opening event) {
        switch (GlobalConstants.gamemode)
        {


            case "PeterFight":
                if(event.getScreen() instanceof InventoryScreen && Minecraft.getInstance().gameMode.getPlayerMode() != GameType.CREATIVE)
                {
                    event.setCanceled(true);
                }

            default:
                break;
        }


    }

    @SubscribeEvent
    public static void OnClientTick(ClientTickEvent.Post event)
    {
        if(Minecraft.getInstance().player != null)
        {

        }

    }

}
