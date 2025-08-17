package net.tazgirl.fgfmsummer.peter_fight;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.lobby.LobbyConstants;

import java.util.*;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PeterFightConstants
{
    public static Vec3 deathCageCoords = new Vec3(988.962, 113, 1011.014);
    public static Vec3 arenaSpawnCoords = new Vec3(988.962, 77, 1011.014);

    public static Vec2 itemSpawnPos1 = new Vec2(972,994);
    public static Vec2 itemSpawnPos2 = new Vec2(1005,1027);

    public static List<String> itemsToNotDrop = List.of("minecraft:arrow", "minecraft:crossbow", "minecraft:iron_sword", "minecraft:chainmail_helmet", "minecraft:chainmail_chestplate", "minecraft:chainmail_leggings", "minecraft:chainmail_boots", "minecraft:leather_chestplate", "minecraft:diamond_chestplate");
    public static List<String> itemsToDestroy = List.of("minecraft:cooked_beef");

    public static Map<ItemStack, Integer> itemsToSpawn = new HashMap<>();

    @SubscribeEvent
    public static void OnServerStart(ServerStartingEvent event)
    {
        MinecraftServer server = GlobalConstants.thisServer;


        MobEffectInstance effect = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 0);
        ItemStack stackToAdd = new ItemStack(Items.POTION, 1);
        stackToAdd.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(effect)));
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("STRENGTH POTION"));

        itemsToSpawn.put(stackToAdd, 2);


        stackToAdd = new ItemStack(Items.GOLDEN_APPLE,1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("GOLDEN APPLE"));

        itemsToSpawn.put(stackToAdd, 2);


        stackToAdd = new ItemStack(Items.SPLASH_POTION, 1);
        effect = new MobEffectInstance(MobEffects.HEAL, 2, 2);
        stackToAdd.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(effect)));
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("HEALING POTION"));

        itemsToSpawn.put(stackToAdd, 1);


        stackToAdd = new ItemStack(Items.SPLASH_POTION, 1);
        effect = new MobEffectInstance(MobEffects.LEVITATION, 200, 1);
        stackToAdd.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(effect)));
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("BYE-BYE POTION"));

        itemsToSpawn.put(stackToAdd, 1);


        stackToAdd = new ItemStack(Items.LINGERING_POTION, 1);
        effect = new MobEffectInstance(MobEffects.POISON, 200, 1);
        stackToAdd.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(effect)));
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("POISON POTION"));

        itemsToSpawn.put(stackToAdd, 1);


        stackToAdd = new ItemStack(Items.SPLASH_POTION, 1);
        effect = new MobEffectInstance(MobEffects.WEAKNESS, 300, 3);
        MobEffectInstance effect2 = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 2);
        stackToAdd.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(effect, effect2)));
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("PLAYER DISABLE POTION"));

        itemsToSpawn.put(stackToAdd, 1);


        stackToAdd = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("TOTEM"));

        itemsToSpawn.put(stackToAdd, 1);


        stackToAdd = new ItemStack(Items.FLINT, 1);
        stackToAdd.enchant(server.registryAccess().holderOrThrow(Enchantments.SHARPNESS), 1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("SHARPNESS ENCHANT"));

        itemsToSpawn.put(stackToAdd, 3);

        stackToAdd = new ItemStack(Items.FLINT_AND_STEEL, 1);
        stackToAdd.enchant(server.registryAccess().holderOrThrow(Enchantments.FIRE_ASPECT), 1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("FIRE-ASPECT ENCHANT"));

        itemsToSpawn.put(stackToAdd, 2);

        stackToAdd = new ItemStack(Items.SLIME_BALL, 1);
        stackToAdd.enchant(server.registryAccess().holderOrThrow(Enchantments.KNOCKBACK), 1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("KNOCKBACK ENCHANT"));

        itemsToSpawn.put(stackToAdd, 2);

        stackToAdd = new ItemStack(Items.SHEARS, 1);
        stackToAdd.enchant(server.registryAccess().holderOrThrow(Enchantments.SWEEPING_EDGE), 1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("SWEEPING-EDGE ENCHANT"));

        itemsToSpawn.put(stackToAdd, 2);

        stackToAdd = new ItemStack(Items.FIREWORK_ROCKET, 3);
        FireworkExplosion explosion = new FireworkExplosion(FireworkExplosion.Shape.BURST, IntList.of(), IntList.of(), false, false);
        stackToAdd.set(DataComponents.FIREWORKS, new Fireworks(20, List.of(explosion, explosion, explosion, explosion)));
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("CROSSBOW ROCKET"));

        itemsToSpawn.put(stackToAdd, 2);

        stackToAdd = new ItemStack(Items.SUGAR_CANE, 1);
        stackToAdd.enchant(server.registryAccess().holderOrThrow(Enchantments.MULTISHOT), 1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("MULTISHOT ENCHANT"));

        itemsToSpawn.put(stackToAdd, 2);

        stackToAdd = new ItemStack(Items.SUGAR, 1);
        stackToAdd.enchant(server.registryAccess().holderOrThrow(Enchantments.QUICK_CHARGE), 1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("QUICKCHARGE ENCHANT"));

        itemsToSpawn.put(stackToAdd, 2);

        stackToAdd = new ItemStack(Items.IRON_INGOT, 1);
        stackToAdd.enchant(server.registryAccess().holderOrThrow(Enchantments.PROTECTION), 1);
        stackToAdd.set(DataComponents.CUSTOM_NAME, Component.literal("PROTECTION ENCHANT"));

        itemsToSpawn.put(stackToAdd, 2);
    }


}
