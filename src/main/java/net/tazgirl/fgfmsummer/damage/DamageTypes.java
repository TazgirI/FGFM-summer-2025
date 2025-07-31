package net.tazgirl.fgfmsummer.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;

public class DamageTypes
{
    public static final ResourceKey<DamageType> PETER_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("fgfmsummer:petered"));

}
