package net.tazgirl.fgfmsummer.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.tazgirl.fgfmsummer.FGFMSummer;

import java.util.function.Supplier;

public class DataAttachments
{
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FGFMSummer.MODID);

    public static final Codec<isDeadRecord> IS_DEAD_CODEC = RecordCodecBuilder.create(instance -> instance.group
            (Codec.BOOL.fieldOf("isDead").forGetter(isDeadRecord::isDead),
            Codec.LONG.fieldOf("timeWhenDied").forGetter(isDeadRecord::timeWhenDied),
            Codec.LONG.fieldOf("timeWhenFree").forGetter(isDeadRecord::timeWhenFree)
            ).apply(instance, isDeadRecord::new));

    public static final Codec<scoreWhenLastDiedRecord> SCORE_WHEN_LAST_DIED_CODEC = RecordCodecBuilder.create(instance -> instance.group
            (Codec.INT.fieldOf("scoreWhenLastDied").forGetter(scoreWhenLastDiedRecord::scoreWhenLastDied)
            ).apply(instance, scoreWhenLastDiedRecord::new));




    public static final Supplier<AttachmentType<isDeadRecord>> IS_DEAD = ATTACHMENT_TYPES.register("is_dead",() -> AttachmentType.builder(() -> new isDeadRecord(false, 0L, 0L)).serialize(IS_DEAD_CODEC).build());
    public static final Supplier<AttachmentType<scoreWhenLastDiedRecord>> SCORE_WHEN_LAST_DIED = ATTACHMENT_TYPES.register("score_when_last_dieed",() -> AttachmentType.builder(() -> new scoreWhenLastDiedRecord(0)).serialize(SCORE_WHEN_LAST_DIED_CODEC).build());

    public record isDeadRecord(boolean isDead, Long timeWhenDied, long timeWhenFree){}
    public record scoreWhenLastDiedRecord(int scoreWhenLastDied){}

    public static void register(IEventBus eventBus)
    {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
