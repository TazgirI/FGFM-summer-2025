package net.tazgirl.fgfmsummer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersConstants;
import net.tazgirl.fgfmsummer.sky_jumpers.SkyJumpersFunctions;

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

    public static void sendMessageToOnePlayer(ServerPlayer player, Component message)
    {
        player.sendSystemMessage(message);
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
        newPos = new Vec3(newPos.x + 1e-6, newPos.y, newPos.z);
        EnumSet<RelativeMovement> relative = EnumSet.noneOf(RelativeMovement.class);


        player.setPos(newPos);
        player.teleportTo(newPos.x, newPos.y, newPos.z);
        player.connection.send(new ClientboundPlayerPositionPacket(newPos.x, newPos.y, newPos.z, 0, 0, RelativeMovement.ROTATION, 0));
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

    public static void PlaySound(String soundName, ServerLevel level, Entity soundSource)
    {
        level.playSound(null, soundSource.position().x, soundSource.position().y, soundSource.position().z, SoundEvent.createFixedRangeEvent(ResourceLocation.parse(soundName),200), SoundSource.RECORDS, 1.0f, 1.0f);
    }

    public static void PlaySound(String soundName, ServerLevel level, Entity soundSource, float volume)
    {
        level.playSound(null, soundSource.position().x, soundSource.position().y, soundSource.position().z, SoundEvent.createFixedRangeEvent(ResourceLocation.parse(soundName),200), SoundSource.RECORDS, volume, 1.0f);
    }

    public static void SendSubtitleToAll(Component message, MinecraftServer server)
    {
        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
//            player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 60, 10));
//            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal(" ")));
            player.connection.send(new ClientboundSetActionBarTextPacket(message));
        }
    }

    public static void SendSubtitleToOne(Component message, ServerPlayer player)
    {
            player.connection.send(new ClientboundSetActionBarTextPacket(message));
    }

    public static boolean IsPlayerUnderdog(ServerPlayer player)
    {
        return player.getData(DataAttachments.DOGS.get()).underdog();
    }

    public static boolean IsPlayerHandidog(ServerPlayer player)
    {
        return player.getData(DataAttachments.DOGS.get()).handidog();
    }

    public static void RemoveAllLocks()
    {
        SkyJumpersFunctions.lockInPlace = false;
        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.removeData(DataAttachments.LOCK_POSTION.get());
        }
    }

    public static boolean IsBlockBelowCenter(ServerPlayer player, Block blockToCompare)
    {
        Vec3 checkPos = new Vec3(player.position().x, player.getBoundingBox().minY - 0.125, player.position().z);
        Vec3 checkPos2 = new Vec3(player.position().x + 0.15, player.getBoundingBox().minY - 0.125, player.position().z + 0.15);
        Vec3 checkPos3 = new Vec3(player.position().x - 0.15, player.getBoundingBox().minY - 0.125, player.position().z + 0.15);
        Vec3 checkPos4 = new Vec3(player.position().x + 0.15, player.getBoundingBox().minY - 0.125, player.position().z - 0.15);
        Vec3 checkPos5 = new Vec3(player.position().x - 0.15, player.getBoundingBox().minY - 0.125, player.position().z - 0.15);
        
        
        if ( player.level().getBlockState(BlockPos.containing(checkPos)).getBlock() == blockToCompare){return true;}

        if ( player.level().getBlockState(BlockPos.containing(checkPos2)).getBlock() == blockToCompare){return true;}

        if ( player.level().getBlockState(BlockPos.containing(checkPos3)).getBlock() == blockToCompare){return true;}

        if ( player.level().getBlockState(BlockPos.containing(checkPos4)).getBlock() == blockToCompare){return true;}

        if ( player.level().getBlockState(BlockPos.containing(checkPos5)).getBlock() == blockToCompare){return true;}
        
        
        return false;
    }

    public static boolean IsAirDirectlyBelow(ServerPlayer player)
    {
        Vec3 checkPos = new Vec3(player.position().x, player.getBoundingBox().minY - 0.025, player.position().z);

        return player.level().isEmptyBlock(BlockPos.containing(checkPos));
    }

    public static boolean IsPlayerBlueTeam(ServerPlayer player)
    {
        return player.getData(DataAttachments.TEAMS.get()).blueTeam();
    }

    public static void ClearAllInventories()
    {
        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.getInventory().clearContent();
        }
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
