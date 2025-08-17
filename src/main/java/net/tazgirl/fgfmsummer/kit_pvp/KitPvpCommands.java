package net.tazgirl.fgfmsummer.kit_pvp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.InventoryLoader;
import net.tazgirl.fgfmsummer.entity.BombEntity;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;

import java.util.Random;

import static net.minecraft.commands.Commands.argument;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class KitPvpCommands
{
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("SetInventoryToKit")
                .requires(source -> source.hasPermission(4)).then(argument("playerToSet", EntityArgument.player()).then(argument("kitName", StringArgumentType.string())
                .executes(context ->
                {
                    KitPvpFunctions.SendPlayerToArena(EntityArgument.getPlayer(context, "playerToSet"), StringArgumentType.getString(context, "kitName"));


                    return 1;
                })
        )));
        dispatcher.register(Commands.literal("KitPvpSetup")
                .requires(source -> source.hasPermission(4))
                        .executes(context ->
                        {

                            KitPvpFunctions.Setup();

                            return 1;
                        })
                );
        dispatcher.register(Commands.literal("KitPvpLogLocations")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    String messageText = "";

                    for(Entity entity: context.getSource().getLevel().getAllEntities())
                    {
                        if(entity instanceof GlowItemFrame glowFrame && glowFrame.distanceToSqr(context.getSource().getPosition()) <= 150 * 150)
                        {
                            Vec3 glowPos = glowFrame.position();

                            Direction direction = glowFrame.getDirection();

                            double x = glowPos.x;
                            double y = glowPos.y;
                            double z = glowPos.z;

                            messageText = messageText.concat("buttons.add(new ButtonDat(new Vec3(" + x +"," + y + "," + z + "), Direction." + direction.toString().toUpperCase() + ")) \n");
                        }

                    }

                    System.out.print(messageText);
                    SimpleFuncs.sendMessageToAllPlayers(GlobalConstants.thisServer, Component.literal(messageText));


                    return 1;
                })
        );






    }

}
