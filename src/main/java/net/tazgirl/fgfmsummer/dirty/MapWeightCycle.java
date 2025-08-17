package net.tazgirl.fgfmsummer.dirty;

import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Random;

public class MapWeightCycle
{

    public static ItemStack getWeightedRandomItem(Map<ItemStack, Integer> weightedItems) {

        Random random = new Random();

        int totalWeight = 0;
        for (int weight : weightedItems.values()) {
            totalWeight += weight;
        }

        if (totalWeight <= 0) {
            return ItemStack.EMPTY; // No valid items
        }

        // 2. Pick a random number from 0 (inclusive) to totalWeight (exclusive)
        int roll = random.nextInt(totalWeight);

        int tempRoll = 0;

        // 3. Loop through the entries and subtract weights until we find our item
        for (Map.Entry<ItemStack, Integer> entry : weightedItems.entrySet()) {
            tempRoll += entry.getValue();
            if (tempRoll >= roll) {
                return entry.getKey();
            }
        }

        // Should never get here
        return ItemStack.EMPTY;
    }
}
