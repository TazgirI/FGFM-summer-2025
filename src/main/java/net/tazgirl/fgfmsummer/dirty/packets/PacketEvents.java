package net.tazgirl.fgfmsummer.dirty.packets;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handlers.ServerPayloadHandler;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.tazgirl.fgfmsummer.FGFMSummer;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PacketEvents
{

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                GamemodePayload.TYPE,
                GamemodePayload.STREAM_CODEC,
                ClientPayloadHandler::handleDataonMain
        );
    }


}
