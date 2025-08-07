package net.tazgirl.fgfmsummer.dirty;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class DropItemsFromList
{
    public static void DropItems(ServerPlayer player, List<String> itemIdsToDrop, List<String> itemIdsToDestroy)
    {
        Level level = player.level();
        List<ItemStack> inventory = player.getInventory().items;
        Random random = new Random();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                ResourceLocation stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());

                // Check if this item matches any ID in the list
                if (itemIdsToDrop.contains(stackId.toString())) {
                    // Copy stack for drop
                    ItemStack dropStack = stack.copy();

                    // Remove from inventory
                    inventory.set(i, ItemStack.EMPTY);

                    // Spawn dropped item entity
                    ItemEntity droppedItem = new ItemEntity(
                            level,
                            player.getX(), player.getY(), player.getZ(),
                            dropStack
                    );
                    droppedItem.setPickUpDelay(20);
                    droppedItem.setGlowingTag(true);

                    droppedItem.setDeltaMovement(new Vec3(random.nextDouble(-1,1),random.nextDouble(1),random.nextDouble(-1,1)));
                    level.addFreshEntity(droppedItem);
                }
                else if(itemIdsToDestroy.contains(stackId.toString()))
                {
                    inventory.set(i, ItemStack.EMPTY);
                }
            }
        }

        // Update inventory to reflect changes
        player.inventoryMenu.broadcastChanges();
    }
}

