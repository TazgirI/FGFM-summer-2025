package net.tazgirl.fgfmsummer.sky_jumpers;

import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SkyJumpersConstants
{
    public static List<Vec3> spawnPoints = new ArrayList<>();
    public static int arrowTime = 200;

    @SubscribeEvent
    public static void ServerStarting(ServerStartingEvent event)
    {
        spawnPoints.add(new Vec3(874.5, 43.15, 932.5));
        spawnPoints.add(new Vec3(858.5, 49.15, 911.5));
        spawnPoints.add(new Vec3(855.5, 45.15, 941.5));
        spawnPoints.add(new Vec3(891.5, 43.15, 940.5));
        spawnPoints.add(new Vec3(880.5, 49.15, 913.5));
        spawnPoints.add(new Vec3(867.5, 46.15, 922.5));
        spawnPoints.add(new Vec3(852.5, 48.15, 929.5));
        spawnPoints.add(new Vec3(866.5, 43.15, 910.5));
        spawnPoints.add(new Vec3(889.5, 44.15, 919.5));
        spawnPoints.add(new Vec3(873.5, 36.15, 942.5));
        spawnPoints.add(new Vec3(862.5, 39.15, 934.5));
        spawnPoints.add(new Vec3(857.5, 41.15, 919.5));
        spawnPoints.add(new Vec3(882.5, 17.15, 905.5));
        spawnPoints.add(new Vec3(885.5, 45.15, 913.5));
        spawnPoints.add(new Vec3(852.5, 29.15, 936.5));
        spawnPoints.add(new Vec3(854.5, 37.15, 912.5));
        spawnPoints.add(new Vec3(883.5, 44.15, 927.5));
        spawnPoints.add(new Vec3(872.5, 32.15, 922.5));
        spawnPoints.add(new Vec3(880.5, 24.15, 927.5));
        spawnPoints.add(new Vec3(865.5, 33.15, 912.5));
    }

}
