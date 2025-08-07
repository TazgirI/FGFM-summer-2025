//package net.tazgirl.fgfmsummer.dirty.packets;
//
//import net.minecraft.network.FriendlyByteBuf;
//import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
//
//import java.util.function.Supplier;
//
//public class GamemodePacket {
//    private final String gamemode;
//
//    public GamemodePacket(String gamemode) {
//        this.gamemode = gamemode;
//    }
//
//    public GamemodePacket(FriendlyByteBuf buf) {
//        this.gamemode = buf.readUtf(); // Read String from buffer
//    }
//
//    public void toBytes(FriendlyByteBuf buf) {
//        buf.writeUtf(gamemode); // Write String to buffer
//    }
//
//    public void handle(Supplier<ClientPlayerNetworkEvent> ctx) {
//        ctx.get().enqueueWork(() -> {
//            // Update the client-side field
//            PeterConstants.currentGamemode = this.gamemode;
//        });
//        ctx.get().setPacketHandled(true);
//    }
//}