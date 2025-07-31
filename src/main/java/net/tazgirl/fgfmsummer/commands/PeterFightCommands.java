package net.tazgirl.fgfmsummer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;
import net.tazgirl.fgfmsummer.damage.DamageTypes;

import static net.minecraft.commands.Commands.argument;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PeterFightCommands
{

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("testPeterDamage")
                .requires(source -> source.hasPermission(0))
                .executes(context ->
                {
                    DamageSource damageSource = new DamageSource(context.getSource().getLevel().registryAccess().holderOrThrow(DamageTypes.PETER_DAMAGE));
                    context.getSource().getEntity().hurt(damageSource, 4);
                    return 1;
                })
        );
    }
}
