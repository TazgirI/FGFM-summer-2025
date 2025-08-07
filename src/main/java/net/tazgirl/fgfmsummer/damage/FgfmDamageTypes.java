package net.tazgirl.fgfmsummer.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class FgfmDamageTypes
{
    public static final ResourceKey<DamageType> PETER_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("fgfmsummer:petered"));

}
