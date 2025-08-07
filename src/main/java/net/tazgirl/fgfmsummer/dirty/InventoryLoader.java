package net.tazgirl.fgfmsummer.dirty;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class InventoryLoader {

    public static void setPlayerInventoryFromNBT(String inventoryName, ServerPlayer player)
    {
        ResourceManager resourceManager = player.getServer().getResourceManager();

        ResourceLocation nbtLocation = ResourceLocation.parse("fgfmsummer:inventories/" + inventoryName + ".nbt");

        Optional<Resource> resourceOpt = resourceManager.getResource(nbtLocation);

        if (resourceOpt.isEmpty()) {
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("Inventory NBT file not found: " + nbtLocation));
            return;
        }

        Resource resource = resourceOpt.get();
        InputStream inputStream = null;

        try {
            inputStream = resource.open();

            // Use maxDepth = 512, maxBytes = 2097152 (2MB) or adjust as needed
            NbtAccounter accounter = new NbtAccounter( 2097152L, 512);

            CompoundTag inventoryTag = NbtIo.readCompressed(inputStream, accounter);
            if (inventoryTag == null || !inventoryTag.contains("Inventory", 9)) {
                player.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal("Inventory NBT is missing or invalid."));
                return;
            }

            ListTag inventoryList = inventoryTag.getList("Inventory", 10);

            player.getInventory().clearContent();
            player.getInventory().load(inventoryList);
            player.inventoryMenu.broadcastChanges();

        } catch (IOException e) {
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("Failed to load inventory from " + nbtLocation));
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
