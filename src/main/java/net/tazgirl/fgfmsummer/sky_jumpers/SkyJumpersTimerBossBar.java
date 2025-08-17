package net.tazgirl.fgfmsummer.sky_jumpers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.tazgirl.fgfmsummer.GlobalConstants;

public class SkyJumpersTimerBossBar
{
    private static final ServerBossEvent bossBar = new ServerBossEvent(
            Component.literal(String.valueOf(SkyJumpersFunctions.timer)), // initial name
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.NOTCHED_6
    );

    public static void ShowBossbarToAll()
    {
        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            bossBar.addPlayer(player);
        }
    }

    public static void HideBossbarToAll()
    {
        bossBar.removeAllPlayers();
    }

    public static void SetName(Double timer)
    {
        bossBar.setName(Component.literal(timer + ((timer * 100) % 10 == 0 ? "0" : "")));
    }

}
