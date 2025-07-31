package net.tazgirl.fgfmsummer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.damage.DamageTypes;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SimpleFuncs
{
    static Map<Long, Runnable> actionDictionary = new HashMap<>();
    static Long currentTick = 0L;

    public static double DistanceBetween(Vec3 pos1, Vec3 pos2)
    {
        return Math.sqrt(Math.pow(pos1.x - pos2.x,2) + Math.pow(pos1.y - pos2.y,2) + Math.pow(pos1.z - pos2.z,2));
    }

    // Does not support multiple events on the same tick
    public static void CreateDelayedTask(int ticks, Runnable action)
    {
        actionDictionary.put(currentTick + ticks, action);
    }

    @SubscribeEvent
    public static void OnServerTick(ServerTickEvent.Pre event)
    {
        currentTick++;

        Runnable action = actionDictionary.get(currentTick);

        if(action != null)
        {
            action.run();
        }

    }

//    @SubscribeEvent
//    public static void OnBlockBreak(BlockEvent.BreakEvent event)
//    {
//        DamageSource damageSource = new DamageSource(event.getLevel().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.PETER_DAMAGE));
//
//        event.getPlayer().hurt(damageSource, 10f);
//    }

}
