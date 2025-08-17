package net.tazgirl.fgfmsummer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.Input;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.dirty.DropItemsFromList;
import net.tazgirl.fgfmsummer.dirty.packets.GamemodePayload;
import net.tazgirl.fgfmsummer.egg_toss.EggTossEvents;
import net.tazgirl.fgfmsummer.egg_toss.EggTossFunctions;
import net.tazgirl.fgfmsummer.entity.PeterArrow;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.init.Entities;
import net.tazgirl.fgfmsummer.kit_pvp.KitPvpEvents;
import net.tazgirl.fgfmsummer.kit_pvp.KitPvpFunctions;
import net.tazgirl.fgfmsummer.lobby.LobbyEvents;
import net.tazgirl.fgfmsummer.lobby.LobbyFunctions;
import net.tazgirl.fgfmsummer.peter_fight.PeterFightConstants;
import net.tazgirl.fgfmsummer.peter_fight.PeterFightEvents;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersEvents;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersFunctions;

import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;

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
                case "EggToss":
                    EggTossEvents.EntityDeath(event);
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
                    break;
                case "SkyJumpers":
                    event.setNewDamage(0);
                    SkyJumpersEvents.EntityAttacked(event);
                    break;
                case "EggToss":
                    event.setNewDamage(EggTossEvents.EntityTakesDamage(event));
                    break;
                case "KitPvp":
                    event.setNewDamage(KitPvpEvents.PlayerTakesDamage(event));
                default:
                    break;
            }
        }
    }

    @SubscribeEvent
    public static void OnExplosion(ExplosionEvent.Detonate event)
    {
        if (!event.getLevel().isClientSide())
        {
            switch (GlobalConstants.gamemode)
            {
                case "PeterFight":
                    event.getExplosion().clearToBlow();
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
                case "SkyJumpers":
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
                case "SkyJumpers":
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
        switch (GlobalConstants.gamemode)
        {
            case "PeterFight":
                PeterFightEvents.OnServerTick(event);
                break;
            case "SkyJumpers":
                SkyJumpersEvents.ServerTick(event);
                break;
            case "EggToss":
                EggTossEvents.ServerTick(event);
                break;
            case "KitPvp":
                KitPvpEvents.ServerTick(event);
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

        switch(GlobalConstants.gamemode)
        {
            case "SkyJumpers":
                SkyJumpersEvents.PlayerTick(event);
                break;
            case "Lobby":
                LobbyEvents.OnPlayerTick(event);
                break;
            case "EggToss":
                EggTossEvents.PlayerTick(event);
                break;
            case "KitPvp":
                KitPvpEvents.PlayerTick(event);
            default:
                break;
        }


    }
    @SubscribeEvent
    public static void ProjectileImpact(ProjectileImpactEvent event)
    {
        if(event.getEntity().level().isClientSide()){return;}

        switch(GlobalConstants.gamemode)
        {
            case "SkyJumpers":
                if(event.getRayTraceResult().getType() == HitResult.Type.ENTITY && ((EntityHitResult) event.getRayTraceResult()).getEntity() instanceof ServerPlayer player)
                {

                    DataAttachments.targetPlayerRecord targetRecord = player.getData(DataAttachments.TARGET_PLAYER.get());


                    if(player.onGround() || (targetRecord.targetUuid() != 0  && (player.level().getGameTime() - targetRecord.assignmentTime() <= 20 && player.level().getEntity(targetRecord.targetUuid()) == event.getProjectile().getOwner())))
                    {
                        event.setCanceled(true);
                        event.getProjectile().discard();

                        if(player.onGround())
                        {
                            SimpleFuncs.sendMessageToOnePlayer((ServerPlayer) event.getProjectile().getOwner(), Component.literal("That player is grounded").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
                        }
                        else
                        {
                            SimpleFuncs.sendMessageToOnePlayer((ServerPlayer) event.getProjectile().getOwner(), Component.literal("You just knocked them up, give them a chance").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
                        }
                    }
                }
                break;
            case "EggToss":
                EggTossEvents.ProjectileImpact(event);
                break;
        }

    }



    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void OnOpenScreen(ScreenEvent.Opening event) {
        switch (GlobalConstants.gamemode)
        {
            case "PeterFight":
                if(event.getScreen() instanceof InventoryScreen && Minecraft.getInstance().gameMode.getPlayerMode() != GameType.CREATIVE)
                {
                    //event.setCanceled(true);
                }

            default:
                break;
        }
    }

    @SubscribeEvent
    public static void PlayerPickUpItem(ItemEntityPickupEvent.Pre event)
    {
        if(!event.getPlayer().level().isClientSide)
        {
            switch (GlobalConstants.gamemode)
            {
                case "PeterFight":
                    if(!PeterFightEvents.ItemPickup(event))
                    {
                        event.setCanPickup(TriState.FALSE);
                        event.getItemEntity().getItem().shrink(1);
                        event.getItemEntity().discard();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @SubscribeEvent
    public static void OnClientTick(ClientTickEvent.Post event)
    {
        if(Minecraft.getInstance().player != null)
        {

        }

    }

    @SubscribeEvent
    public static void OnPlayerConnect(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getEntity().level().isClientSide()){return;}

        switch (GlobalConstants.gamemode)
        {
            case "Lobby":
                LobbyEvents.OnPlayerJoin(event);
                break;

            default:
                break;
        }

    }

    @SubscribeEvent
    public static void EntityTick(EntityTickEvent.Pre event)
    {
        if(event.getEntity().level().isClientSide()){return;}

        switch (GlobalConstants.gamemode)
        {
            case "EggToss":
                EggTossEvents.EntityTick(event);
        }

    }

    @SubscribeEvent
    public static void BlockRightClicked(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getLevel().isClientSide()){return;}

        switch(GlobalConstants.gamemode)
        {
            case "KitPvp":
                event.setCanceled(KitPvpEvents.PlayerRightClickBlock(event));
                break;
            default:
                break;
        }

    }

}
