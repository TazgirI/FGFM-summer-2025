package net.tazgirl.fgfmsummer.kit_pvp;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class KitPvpConstants
{
    public static List<Vec3> kitRoom = new ArrayList<>();

    public static class ButtonDat
    {
        public BlockPos pos;
        public Direction facing;

        public ButtonDat(Vec3 newPos, Direction newFacing)
        {
            this.pos = BlockPos.containing(newPos);
            this.facing = newFacing;
        }
    }

    public static List<ButtonDat> buttons = new ArrayList<>();

    public static List<Vec3> spawnPositions = new ArrayList<>();

    @SubscribeEvent
    public static void OnServerStart(ServerStartingEvent event)
    {
        kitRoom.add(new Vec3(687, 79, 1171));
        kitRoom.add(new Vec3(700, 79, 1156));
        kitRoom.add(new Vec3(712, 79, 1171));
        kitRoom.add(new Vec3(700, 79, 1187));

        buttons.add(new ButtonDat(new Vec3(0, 0, 0), Direction.UP));
    }

}
