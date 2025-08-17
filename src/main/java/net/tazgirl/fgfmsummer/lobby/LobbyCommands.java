package net.tazgirl.fgfmsummer.lobby;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.types.templates.Tag;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.InventorySaver;
import net.tazgirl.fgfmsummer.entity.InvincibleItemFrame;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.peter_fight.PeterFunctions;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.createValidationContext;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class LobbyCommands
{

    @SubscribeEvent
    public static void OnRegisterCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("LobbyAddOverallScore")
                .requires(source -> source.hasPermission(4)).then(argument("playerToGainScore", EntityArgument.player()).then(argument("amountToAdd", IntegerArgumentType.integer())
                .executes(context ->
                {
                    LobbyFunctions.AddOverallPoints(EntityArgument.getPlayer(context,"playerToGainScore"),IntegerArgumentType.getInteger(context,"amountToAdd"));
                    SimpleFuncs.sendMessageToAllPlayers(context.getSource().getServer(), Component.literal("Gave " + EntityArgument.getPlayer(context,"playerToGainScore").getName().getString() + " " + IntegerArgumentType.getInteger(context,"amountToAdd") + " points"));
                    return 1;
                })
        )));
        dispatcher.register(Commands.literal("summonInvincibleItemFrame")
                .requires(source -> source.hasPermission(4)).then(argument("itemToAdd", ItemArgument.item(event.getBuildContext()))
                .executes(context ->
                {
                    Level level = context.getSource().getLevel();
                    Vec3 pos = context.getSource().getPosition();
                    InvincibleItemFrame itemFrame = new InvincibleItemFrame(level,new BlockPos((int) pos.x, (int) pos.y, (int) pos.z),context.getSource().getEntity().getDirection());
                    itemFrame.setItem(ItemArgument.getItem(context,"itemToAdd").createItemStack(1,false));
                    itemFrame.setFixed(true);
                    level.addFreshEntity(itemFrame);
                    return 1;
                })
        ));
        dispatcher.register(Commands.literal("LobbyMakePlayerUnderdog")
                .requires(source -> source.hasPermission(4)).then(argument("playerToBeUnderdog", EntityArgument.player())
                        .executes(context ->
                        {
                            ServerPlayer player = EntityArgument.getPlayer(context,"playerToBeUnderdog");

                            LobbyFunctions.MakePlayerUnderdog(player);

                            SimpleFuncs.sendMessageToAllPlayers(context.getSource().getServer(), Component.literal(player.getName().getString() + " is now an Underdog"));

                            return 1;
                        })
                ));
        dispatcher.register(Commands.literal("LobbyMakePlayerHandidog")
                .requires(source -> source.hasPermission(4)).then(argument("playerToBeHandidog", EntityArgument.player())
                        .executes(context ->
                        {
                            ServerPlayer player = EntityArgument.getPlayer(context,"playerToBeHandidog");

                            LobbyFunctions.MakePlayerHandidog(player);

                            SimpleFuncs.sendMessageToAllPlayers(context.getSource().getServer(), Component.literal(player.getName().getString() + " is now an Overdog"));

                            return 1;
                        })
                ));
        dispatcher.register(Commands.literal("LobbyClearDogs")
                .requires(source -> source.hasPermission(4))
                        .executes(context ->
                        {
                            LobbyFunctions.ClearAllDogs();

                            return 1;
                        })
                );
        dispatcher.register(Commands.literal("saveInventoryAsSet")
                .requires(source -> source.hasPermission(4)).then(argument("fileName", StringArgumentType.string())
                .executes(context ->
                {
                    if(context.getSource().getEntity() instanceof ServerPlayer serverPlayer)
                    {
                        InventorySaver.savePlayerInventory(serverPlayer, StringArgumentType.getString(context, "fileName"));
                    }

                    return 1;
                })
        ));
        dispatcher.register(Commands.literal("LobbyReturn")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    LobbyFunctions.GoToLobby();

                    return 1;
                })
        );
        dispatcher.register(Commands.literal("LobbyMakePlayerBlueTeam")
                .requires(source -> source.hasPermission(4)).then(argument("playerToBeBlueTeam", EntityArgument.player())
                .executes(context ->
                {
                    LobbyFunctions.MakePlayerBlueTeam(EntityArgument.getPlayer(context,"playerToBeBlueTeam"));

                    return 1;
                })
        ));
        dispatcher.register(Commands.literal("LobbyMakePlayerRedTeam")
                .requires(source -> source.hasPermission(4)).then(argument("playerToBeRedTeam", EntityArgument.player())
                        .executes(context ->
                        {
                            LobbyFunctions.MakePlayerRedTeam(EntityArgument.getPlayer(context,"playerToBeRedTeam"));

                            return 1;
                        })
                ));
        dispatcher.register(Commands.literal("LobbyClearTeams")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    LobbyFunctions.ClearAllTeams();

                    return 1;
                })
        );
        dispatcher.register(Commands.literal("LobbySayAllOverallScores")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    LobbyFunctions.SoundOffOverallScores();

                    return 1;
                })
        );
        dispatcher.register(Commands.literal("LobbyListScoreboard")
                .requires(source -> source.hasPermission(4)).then(argument("scoreboardName", StringArgumentType.string())
                .executes(context ->
                {
                    LobbyFunctions.ListScoreboard(StringArgumentType.getString(context, "scoreboardName"));

                    return 1;
                })
        ));
        dispatcher.register(Commands.literal("LobbyClearAll")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    LobbyFunctions.ClearEverything();

                    return 1;
                })
        );
        dispatcher.register(Commands.literal("LobbyListTeams")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    LobbyFunctions.ListTeams();

                    return 1;
                })
        );

    }
}
