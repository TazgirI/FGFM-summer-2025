package net.tazgirl.fgfmsummer;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GlobalConstants
{
    public static String gamemode = "Lobby";

    public static MinecraftServer thisServer = null;

    @SubscribeEvent
    public static void OnServerStart(ServerAboutToStartEvent event)
    {
        thisServer = event.getServer();

    }

}
