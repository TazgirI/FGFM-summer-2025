package net.tazgirl.fgfmsummer.sky_jumpers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.lobby.LobbyFunctions;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;

import static net.minecraft.commands.Commands.argument;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SkyJumpersCommands
{
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("SkyJumpersSetup")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    SkyJumpersFunctions.Setup(context.getSource().getServer());
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("SkyJumpersStart")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    SkyJumpersFunctions.Start();
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("SkyJumpersUnfreezePlayer")
        .requires(source -> source.hasPermission(4)).then(argument("playerToUnfreeze", EntityArgument.player())
                .executes(context ->
                {
                    EntityArgument.getPlayer(context,"playerToUnfreeze").setData(DataAttachments.LOCK_POSTION.get(), new DataAttachments.lockedPositionRecord(false, 0, 0, 0));
                    return 1;
                })
        ));
        dispatcher.register(Commands.literal("SkyJumpersForceEnd")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    SkyJumpersFunctions.GameEnd();
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("SkyJumpersShowSpawns")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    for(Vec3 pos: SkyJumpersConstants.spawnPoints)
                    {
                        ArmorStand armorStand = new ArmorStand(context.getSource().getLevel(), pos.x, pos.y, pos.z);

                        armorStand.setGlowingTag(true);
                        armorStand.setNoGravity(true);

                        context.getSource().getLevel().addFreshEntity(armorStand);


                    }
                    return 1;
                })
        );
    }
}
