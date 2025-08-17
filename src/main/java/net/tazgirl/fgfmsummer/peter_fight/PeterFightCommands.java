package net.tazgirl.fgfmsummer.peter_fight;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;
import net.tazgirl.fgfmsummer.dirty.InventoryLoader;
import net.tazgirl.fgfmsummer.dirty.InventorySaver;
import net.tazgirl.fgfmsummer.entity.BombEntity;

import java.util.Random;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PeterFightCommands
{

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("PeterFightSetup")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    PeterFunctions.Setup(context.getSource().getServer());
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("setInventoryToArenaSet")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    if(context.getSource().getEntity() instanceof ServerPlayer serverPlayer)
                    {
                        InventoryLoader.setPlayerInventoryFromNBT("arena_set",serverPlayer);
                    }

                    return 1;
                })
        );
        dispatcher.register(Commands.literal("PeterFightStart")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    PeterFunctions.StartFight(context.getSource().getServer(), context.getSource().getEntity());

                    return 1;
                })
        );
        dispatcher.register(Commands.literal("PeterFightDiscard")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    PeterFunctions.peter.discard();

                    return 1;
                })
        );
        dispatcher.register(Commands.literal("PeterFightBombTest")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    BombEntity bomb = new BombEntity(EntityType.TNT_MINECART, context.getSource().getLevel());
                    bomb.setPos(context.getSource().getPosition());
                    context.getSource().getLevel().addFreshEntity(bomb);

                    return 1;
                })
        );
        dispatcher.register(Commands.literal("PeterFightTestItemDrop")
                .requires(source -> source.hasPermission(4))
                .executes(context ->
                {
                    PeterFunctions.DropItem();


                    return 1;
                })
        );



    }
}
