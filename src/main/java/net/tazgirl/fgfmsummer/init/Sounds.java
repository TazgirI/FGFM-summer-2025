package net.tazgirl.fgfmsummer.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.tazgirl.fgfmsummer.FGFMSummer;

public class Sounds
{
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, FGFMSummer.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent> PETERGRIFFINSTOPIT = REGISTRY.register("petergriffinstopit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.parse("fgfmsummer:petergriffinstopit")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HIIMPETER = REGISTRY.register("hiimpeter", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.parse("fgfmsummer:hiimpeter")));

}
