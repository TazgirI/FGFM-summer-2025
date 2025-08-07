package net.tazgirl.fgfmsummer.dirty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InventorySaver {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void savePlayerInventory(ServerPlayer player, String fileName) {
        // Save inventory to a ListTag
        ListTag inventoryList = new ListTag();
        player.getInventory().save(inventoryList);

        // Wrap in a CompoundTag for structure
        CompoundTag inventoryTag = new CompoundTag();
        inventoryTag.put("Inventory", inventoryList);

        // Convert CompoundTag to JSON string using Mojang NBT-to-String (optional, more precise)
        String json = inventoryTag.toString();  // Compact NBT format as string

        // OPTIONAL: To use readable JSON format via Gson, uncomment below:
        // String json = GSON.toJson(NbtToJson.convert(inventoryTag)); // You'd need to write a converter for this

        File directory = new File("data/fgfmsummer/inventories");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName + ".nbt"); // You can use .json if using Gson
        try {
            NbtIo.writeCompressed(inventoryTag, file.toPath());  // Write as .nbt (binary compressed)
            System.out.println("Saved inventory to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
