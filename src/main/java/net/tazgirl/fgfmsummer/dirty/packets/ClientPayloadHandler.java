package net.tazgirl.fgfmsummer.dirty.packets;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.tazgirl.fgfmsummer.GlobalConstants;

public class ClientPayloadHandler implements IPayloadHandler
{

    public static void handleDataonMain(final GamemodePayload payload, final IPayloadContext context)
    {
        GlobalConstants.gamemode = payload.gamemode();
    }

    /**
     * @param customPacketPayload
     * @param iPayloadContext
     */
    @Override
    public void handle(CustomPacketPayload customPacketPayload, IPayloadContext iPayloadContext)
    {

    }
}
