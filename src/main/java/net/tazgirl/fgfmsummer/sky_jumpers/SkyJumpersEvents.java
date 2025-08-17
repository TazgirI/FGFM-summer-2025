package net.tazgirl.fgfmsummer.sky_jumpers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.TickTimer;
import net.tazgirl.fgfmsummer.init.DataAttachments;

import java.util.Random;
import java.util.Set;

import static net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersFunctions.arrowTimer;

public class SkyJumpersEvents
{
    public static void PlayerTick(PlayerTickEvent.Pre event)
    {
        if(event.getEntity().level().isClientSide()){return;}


        ServerPlayer player = (ServerPlayer) event.getEntity();

        player.getFoodData().setFoodLevel(20);

        player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(player.isCrouching() ? 0.15 : 0.1);

        DataAttachments.targetPlayerRecord targetPlayerRecord = player.getData(DataAttachments.TARGET_PLAYER);

        if(SkyJumpersFunctions.lockInPlace && player.getData(DataAttachments.LOCK_POSTION.get()).doLock())
        {
            DataAttachments.lockedPositionRecord lockPosRecord = player.getData(DataAttachments.LOCK_POSTION.get());
            Vec3 lockPos = new Vec3(lockPosRecord.xPos(), lockPosRecord.yPos(), lockPosRecord.zPos());

            SimpleFuncs.ReallySendPlayer(player, lockPos);
            return;
        }

        String attackerName = "Nobody";

        if(targetPlayerRecord.targetUuid() != 0)
        {
            attackerName = player.level().getEntity(targetPlayerRecord.targetUuid()).getName().getString();
        }

        ChatFormatting chatColour = attackerName.equals("Nobody") ? ChatFormatting.GREEN : ChatFormatting.RED;
        SimpleFuncs.SendSubtitleToOne(Component.literal(attackerName + " will currently recieve credit for your death").withStyle(Style.EMPTY.withColor(chatColour)), player);


        if(SimpleFuncs.IsBlockBelowCenter(player, Blocks.SLIME_BLOCK) && !player.isCrouching())
        {
            Vec3 currentDeltaMovement = player.getDeltaMovement();

            player.setDeltaMovement(new Vec3(currentDeltaMovement.x, 2, currentDeltaMovement.z));
            //player.setOnGroundWithMovement(false, new Vec3(currentDeltaMovement.x, 5, currentDeltaMovement.z));
            player.setOnGround(false);
            player.connection.send(new ClientboundSetEntityMotionPacket(player));


        }

        if(player.position().y < -20)
        {
            Vec3 currentPos = player.position();
            SimpleFuncs.ReallySendPlayer(player, new Vec3(currentPos.x, 50 , currentPos.z));

            SkyJumpersFunctions.KillPlayer((ServerPlayer) event.getEntity(), false);
        }
    }

    public static void EntityAttacked(LivingDamageEvent.Pre event)
    {
        if(event.getEntity().level().isClientSide() || event.getSource().getEntity() == null || !(event.getEntity() instanceof ServerPlayer)){return;}

        if(event.getSource().getEntity() != event.getEntity())
        {
            event.getEntity().setData(DataAttachments.TARGET_PLAYER.get(), new DataAttachments.targetPlayerRecord(event.getSource().getEntity().getId(), event.getEntity().level().getGameTime()));
        }

        if(event.getSource().getDirectEntity() instanceof Arrow arrow && arrow.getOwner() != event.getEntity())
        {
            SkyJumpersFunctions.KillPlayer((ServerPlayer) event.getEntity(), true);
        }

    }

    public static void  OnLivingDamage(LivingDamageEvent.Pre event)
    {

    }

    public static void ServerTick(ServerTickEvent.Pre event)
    {

        if(SkyJumpersFunctions.tickTimer)
        {
            arrowTimer++;
            if(arrowTimer >= SkyJumpersConstants.arrowTime)
            {
                arrowTimer = 0;
                for(ServerPlayer player: event.getServer().getPlayerList().getPlayers())
                {
                    if(!player.getInventory().hasAnyOf(Set.of(Items.ARROW)))
                    {
                        if(SimpleFuncs.IsPlayerHandidog(player))
                        {
                            if(new Random().nextInt(0,2) == 1)
                            {

                            }
                        }
                        else
                        {
                            player.getInventory().setItem(1,new ItemStack(Items.ARROW, 1));
                        }

                    }
                }
            }
            if(event.getServer().overworld().getGameTime() % 20 == 0){SkyJumpersFunctions.timer = TickTimer.tickTimer(SkyJumpersFunctions.timer);}
            SkyJumpersTimerBossBar.SetName(SkyJumpersFunctions.timer);
            if(SkyJumpersFunctions.timer <= 0)
            {
                SkyJumpersFunctions.GameEnd();
            }
        }
    }

}
