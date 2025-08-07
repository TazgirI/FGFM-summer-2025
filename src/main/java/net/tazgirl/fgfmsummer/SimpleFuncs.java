package net.tazgirl.fgfmsummer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SimpleFuncs
{
    static Map<Long, Runnable> actionDictionary = new HashMap<>();
    static Long currentTick = 0L;

    public static double DistanceBetween(Vec3 pos1, Vec3 pos2)
    {
        return Math.sqrt(Math.pow(pos1.x - pos2.x,2) + Math.pow(pos1.y - pos2.y,2) + Math.pow(pos1.z - pos2.z,2));
    }


    public static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void sendMessageToAllPlayers(MinecraftServer server, Component message) {
        if (server != null && !server.getPlayerList().getPlayers().isEmpty())
        {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(message);
            }
        }
    }

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            workQueue.add(new Tuple<>(action, tick));
    }


    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
        workQueue.forEach(work -> {
            work.setB(work.getB() - 1);
            if (work.getB() == 0)
                actions.add(work);
        });
        actions.forEach(e -> e.getA().run());
        workQueue.removeAll(actions);
    }

    public static void ReallySendPlayer(ServerPlayer player,Vec3 newPos)
    {
        player.setPos(newPos);
        player.connection.send(new ClientboundPlayerPositionPacket(newPos.x, newPos.y, newPos.z, player.getYRot(), player.getXRot(), EnumSet.noneOf(RelativeMovement.class), 0));
    }

    public static void ResetObjective(MinecraftServer server, String objectiveName)
    {
        Scoreboard scoreboard = server.getScoreboard();

        ScoreAccess scoreAccessor;

        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
            player.setGameMode(GameType.ADVENTURE);

            scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective(objectiveName)).reset();

        }

    }

    public static <T> List<T> ShuffleList(List<T> providedList)
    {
        List<T> providedListCopy = new ArrayList<>(providedList);
        List<T> listToReturn = new ArrayList<>();

        for(int i = 0; i < providedList.size(); i++)
        {
            int position = new Random().nextInt(0, providedListCopy.size());

            listToReturn.add(providedListCopy.get(position));
            providedListCopy.remove(position);

        }

        return listToReturn;

    }


//    // Does not support multiple events on the same tick
//    public static void CreateDelayedTask(int ticks, Runnable action)
//    {
//        actionDictionary.put(currentTick + ticks, action);
//    }
//
//    @SubscribeEvent
//    public static void OnServerTick(ServerTickEvent.Pre event)
//    {
//        currentTick++;
//
//        Runnable action = actionDictionary.get(currentTick);
//
//        if(action != null)
//        {
//            action.run();
//            actionDictionary.remove(currentTick);
//        }
//
//    }



//    @SubscribeEvent
//    public static void OnBlockBreak(BlockEvent.BreakEvent event)
//    {
//        DamageSource damageSource = new DamageSource(event.getLevel().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.PETER_DAMAGE));
//
//        event.getPlayer().hurt(damageSource, 10f);
//    }

}
