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

    private static final Codec<targetPlayerRecord> TARGET_PLAYER_CODEC = RecordCodecBuilder.create(instance -> instance.group
            (Codec.INT.fieldOf("targetPlayer").forGetter(targetPlayerRecord::targetUuid),
             Codec.LONG.fieldOf("assignmentTime").forGetter(targetPlayerRecord::assignmentTime)
            ).apply(instance, targetPlayerRecord::new));

    private static final Codec<dogsRecord> DOGS_CODEC = RecordCodecBuilder.create(instance -> instance.group
            (Codec.BOOL.fieldOf("underdog").forGetter(dogsRecord::underdog),
            Codec.BOOL.fieldOf("handidog").forGetter(dogsRecord::handidog)
            ).apply(instance, dogsRecord::new));

    private static final Codec<lockedPositionRecord> LOCKED_POSITION_CODEC = RecordCodecBuilder.create(instance -> instance.group
            (Codec.BOOL.fieldOf("doLock").forGetter(lockedPositionRecord::doLock),
            Codec.DOUBLE.fieldOf("xPos").forGetter(lockedPositionRecord::xPos),
            Codec.DOUBLE.fieldOf("yPos").forGetter(lockedPositionRecord::yPos),
            Codec.DOUBLE.fieldOf("zPos").forGetter(lockedPositionRecord::zPos)
            ).apply(instance, lockedPositionRecord::new));

    private static final Codec<teamsRecord> TEAMS_CODEC = RecordCodecBuilder.create(instance -> instance.group
            (Codec.BOOL.fieldOf("blueTeam").forGetter(teamsRecord::blueTeam)
            ).apply(instance, teamsRecord::new));




    public static final Supplier<AttachmentType<isDeadRecord>> IS_DEAD = ATTACHMENT_TYPES.register("is_dead",() -> AttachmentType.builder(() -> new isDeadRecord(false, 0L, 0L)).serialize(IS_DEAD_CODEC).build());
    public static final Supplier<AttachmentType<scoreWhenLastDiedRecord>> SCORE_WHEN_LAST_DIED = ATTACHMENT_TYPES.register("score_when_last_died",() -> AttachmentType.builder(() -> new scoreWhenLastDiedRecord(0)).serialize(SCORE_WHEN_LAST_DIED_CODEC).build());

    public static final Supplier<AttachmentType<targetPlayerRecord>> TARGET_PLAYER = ATTACHMENT_TYPES.register("target_player",() -> AttachmentType.builder(() -> new targetPlayerRecord(0,0)).serialize(TARGET_PLAYER_CODEC).build());
    public static final Supplier<AttachmentType<dogsRecord>> DOGS = ATTACHMENT_TYPES.register("dogs",() -> AttachmentType.builder(() -> new dogsRecord(false, false)).serialize(DOGS_CODEC).build());
    public static final Supplier<AttachmentType<lockedPositionRecord>> LOCK_POSTION = ATTACHMENT_TYPES.register("lock_position",() -> AttachmentType.builder(() -> new lockedPositionRecord(false, 0,0,0)).serialize(LOCKED_POSITION_CODEC).build());
    public static final Supplier<AttachmentType<teamsRecord>> TEAMS = ATTACHMENT_TYPES.register("teams",() -> AttachmentType.builder(() -> new teamsRecord(false)).serialize(TEAMS_CODEC).build());



    public record isDeadRecord(boolean isDead, Long timeWhenDied, long timeWhenFree){}
    public record scoreWhenLastDiedRecord(int scoreWhenLastDied){}
    public record targetPlayerRecord(int targetUuid, long assignmentTime){}
    public record dogsRecord(boolean underdog, boolean handidog){}
    public record lockedPositionRecord(boolean doLock, double xPos, double yPos, double zPos){}
    public record teamsRecord(boolean blueTeam){}
    public record lastApplierRecord(int fireUuid, int poisonUuid, int witherUuid, int KnockbackUuid){}

    public static void register(IEventBus eventBus)
    {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
