package net.tazgirl.fgfmsummer.dirty.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jetbrains.annotations.NotNull;

public record GamemodePayload(String gamemode) implements CustomPacketPayload
{

    public static final CustomPacketPayload.Type<GamemodePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.parse("fgfmsummer:gamemode_payload"));

    public static final StreamCodec<ByteBuf, GamemodePayload> STREAM_CODEC = StreamCodec.composite
            (
                    ByteBufCodecs.STRING_UTF8,
                    GamemodePayload::gamemode,
                    GamemodePayload::new
            );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }


}
