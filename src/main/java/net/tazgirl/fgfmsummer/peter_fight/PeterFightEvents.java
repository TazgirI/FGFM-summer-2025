package net.tazgirl.fgfmsummer.peter_fight;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.dirty.DropItemsFromList;
import net.tazgirl.fgfmsummer.entity.PeterArrow;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;
import net.tazgirl.fgfmsummer.init.DataAttachments;

import java.util.Random;

public class PeterFightEvents
{

    public static boolean OnPlayerDeath(LivingDamageEvent.Pre event)
    {
        return true;
    }

    public static float OnEntityHurt(LivingDamageEvent.Pre event)
    {

        if(event.getSource().getEntity() instanceof PeterArrow)
        {
            event.setNewDamage(10);
        }

        if(event.getEntity() instanceof Player player && player.getHealth() - event.getNewDamage() <= 0)
        {
            if(player instanceof ServerPlayer)
            {
                PeterFightPlayerDeath(event, player);
            }

            return 0f;
        }

        if(event.getEntity().getData(DataAttachments.IS_DEAD).isDead())
        {
            return 0f;
        }

        if(event.getEntity() instanceof PeterGriffin peter)
        {
            if(peter.getHealth() - event.getNewDamage() <= 0)
            {
                if(!PeterFunctions.isAscended)
                {
                    PeterFunctions.ascendSoon = true;
                    return 0f;
                }
                else
                {
                    PeterFunctions.End();
                    return 0f;
                }
            }
        }


        if(PeterFunctions.punishDamage)
        {
            if(event.getSource().getEntity() == null)
            {
                return event.getNewDamage();
            }
            if(event.getSource().getEntity() instanceof Player player)
            {
                DamageSource bypassDamageSource = new DamageSource(event.getEntity().level().registryAccess().holderOrThrow(DamageTypes.ARROW));
                player.hurt(bypassDamageSource, event.getOriginalDamage());
            }
            return 0f;
        }

        if(event.getEntity() instanceof PeterGriffin && event.getSource().getEntity() instanceof Player player)
        {
            AddPeterPoints(player, Math.round(event.getNewDamage()));
        }

        return event.getNewDamage();
    }

    public static boolean OnEntityKnockback(LivingKnockBackEvent event)
    {
        if (event.getEntity() instanceof PeterGriffin)
        {
            return PeterFunctions.cancelKnockback;
        }

        return false;

    }

    public static void OnServerTick(ServerTickEvent.Pre event)
    {
        MinecraftServer server = event.getServer();

        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
            if(player.position().distanceTo(PeterFightConstants.deathCageCoords) < 10)
            {
                DataAttachments.isDeadRecord playerDeathRecord = player.getData(DataAttachments.IS_DEAD.get());

                if(playerDeathRecord.isDead() && playerDeathRecord.timeWhenFree() <= player.level().getGameTime())
                {
                    PeterFunctions.ReturnPlayerToArena(player);
                }
            }
        }
    }

    public static void AddPeterPoints(Player player, int amount)
    {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("peterFightScores")).add(amount);
    }

    static void PeterFightPlayerDeath(LivingDamageEvent.Pre event, Player player)
    {
        if(player.level().isClientSide){return;}

        if (player.getMainHandItem().is(Items.TOTEM_OF_UNDYING))
        {
            ItemStack newStack = player.getMainHandItem();
            newStack.shrink(1);
            player.setItemInHand(InteractionHand.MAIN_HAND, newStack);
            player.inventoryMenu.broadcastChanges();
            TotemStuff(player);
        }
        else if(player.getOffhandItem().is(Items.TOTEM_OF_UNDYING))
        {
            ItemStack newStack = player.getOffhandItem();
            newStack.shrink(1);
            player.setItemInHand(InteractionHand.OFF_HAND, newStack);
            player.inventoryMenu.broadcastChanges();
            TotemStuff(player);

        }
        else
        {
            if(player == PeterFunctions.currentTarget){PeterFunctions.JustSetGoal(PeterFunctions.currentGoal);}

            DropItemsFromList.DropItems((ServerPlayer) player, PeterFightConstants.itemsToNotDrop, PeterFightConstants.itemsToDestroy, 1.0);
            player.getInventory().clearContent();
            Scoreboard scoreboard = player.getServer().getScoreboard();

            long currentTime = player.level().getGameTime();
            long timeWhenRespawn = currentTime + (((Math.min(scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("peterFightScores")).get() - player.getData(DataAttachments.SCORE_WHEN_LAST_DIED.get()).scoreWhenLastDied(), 150) / 10) + 5) * 20L);
            player.setData(DataAttachments.IS_DEAD.get(), new DataAttachments.isDeadRecord(true, currentTime,timeWhenRespawn));

            player.setData(DataAttachments.SCORE_WHEN_LAST_DIED.get(),new DataAttachments.scoreWhenLastDiedRecord(scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("peterFightScores")).get()));

            SimpleFuncs.ReallySendPlayer((ServerPlayer) player, PeterFightConstants.deathCageCoords);
            player.heal(20);
            player.getFoodData().setFoodLevel(20);

            Entity sourceEntity = event.getSource().getDirectEntity();

            if(sourceEntity instanceof Player)
            {
                SimpleFuncs.sendMessageToAllPlayers(player.getServer(), Component.literal("" + player.getDisplayName().getString() + " was murdered by " + sourceEntity.getDisplayName().getString()).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)));
            }
            else if(sourceEntity instanceof PeterGriffin)
            {
                SimpleFuncs.sendMessageToAllPlayers(player.getServer(),Component.literal("" + player.getDisplayName().getString() + " got petered").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)));
            }
            else if(sourceEntity instanceof PeterArrow)
            {
                SimpleFuncs.sendMessageToAllPlayers(player.getServer(),Component.literal("" + player.getDisplayName().getString() + " was fucking shot").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)));
            }
            else
            {
                SimpleFuncs.sendMessageToAllPlayers(player.getServer(),Component.literal("" + player.getDisplayName().getString() + " got too silly").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)));
            }
        }
    }

    static void TotemStuff(Player player)
    {
        if(player.level().isClientSide){return;}

        Random random = new Random();
        ((ServerLevel) player.level()).sendParticles(ParticleTypes.TOTEM_OF_UNDYING,player.position().x,player.position().y,player.position().z, 50, random.nextGaussian(), random.nextGaussian(), random.nextGaussian(), 1);
        SimpleFuncs.PlaySound("minecraft:item.totem.use", (ServerLevel) player.level(),player);

        SimpleFuncs.sendMessageToAllPlayers(player.getServer(),Component.literal("" + player.getName().getString() + " was saved by their totem").withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
        player.heal(10);
        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 7);
    }

    public static boolean ItemPickup(ItemEntityPickupEvent.Pre event)
    {
        if(event.getPlayer().level().isClientSide()){return true;}

        ServerPlayer player = (ServerPlayer) event.getPlayer();
        MinecraftServer server = player.getServer();
        ItemEntity itemEntity = event.getItemEntity();
        Inventory inventory = player.getInventory();

        if(itemEntity.getItem().is(Items.FLINT))
        {
            ItemStack itemInSlot0 = inventory.getItem(0);
            int currentEnchantLevel = itemInSlot0.getEnchantmentLevel(server.reloadableRegistries().get().holderOrThrow(Enchantments.SHARPNESS));
            itemInSlot0.enchant(server.registryAccess().holderOrThrow(Enchantments.SHARPNESS),Math.min(currentEnchantLevel + 1, 5));
            player.getInventory().setItem(0,itemInSlot0);
            return false;
        }

        if(itemEntity.getItem().is(Items.TOTEM_OF_UNDYING))
        {
            ItemStack offhandItem = player.getOffhandItem();

            if(offhandItem.is(Items.AIR))
            {
                itemEntity.getItem().setCount(1);
                player.getInventory().setItem(40, itemEntity.getItem().copy());
                player.inventoryMenu.broadcastChanges();
                return false;
            }

            return true;
        }

        if(itemEntity.getItem().is(Items.FIREWORK_ROCKET))
        {
            ItemStack offhandItem = player.getOffhandItem();

            if(offhandItem.is(Items.AIR))
            {
                player.getInventory().setItem(40, itemEntity.getItem().copy());
                player.inventoryMenu.broadcastChanges();
                return false;
            }

            return true;
        }

        if(itemEntity.getItem().is(Items.FLINT_AND_STEEL))
        {
            ItemStack itemInSlot0 = inventory.getItem(0);
            int currentEnchantLevel = itemInSlot0.getEnchantmentLevel(server.reloadableRegistries().get().holderOrThrow(Enchantments.FIRE_ASPECT));
            itemInSlot0.enchant(server.registryAccess().holderOrThrow(Enchantments.FIRE_ASPECT),Math.min(currentEnchantLevel + 1, 2));
            player.getInventory().setItem(0,itemInSlot0);
            return false;
        }

        if(itemEntity.getItem().is(Items.SLIME_BALL))
        {
            ItemStack itemInSlot0 = inventory.getItem(0);
            int currentEnchantLevel = itemInSlot0.getEnchantmentLevel(server.reloadableRegistries().get().holderOrThrow(Enchantments.KNOCKBACK));
            itemInSlot0.enchant(server.registryAccess().holderOrThrow(Enchantments.KNOCKBACK),Math.min(currentEnchantLevel + 1, 3));
            player.getInventory().setItem(0,itemInSlot0);
            return false;
        }

        if(itemEntity.getItem().is(Items.SHEARS))
        {
            ItemStack itemInSlot0 = inventory.getItem(0);
            int currentEnchantLevel = itemInSlot0.getEnchantmentLevel(server.reloadableRegistries().get().holderOrThrow(Enchantments.SWEEPING_EDGE));
            itemInSlot0.enchant(server.registryAccess().holderOrThrow(Enchantments.SWEEPING_EDGE),Math.min(currentEnchantLevel + 2, 4));
            player.getInventory().setItem(0,itemInSlot0);
            return false;
        }

        if(itemEntity.getItem().is(Items.SUGAR_CANE))
        {
            ItemStack itemInSlot1 = inventory.getItem(1);
            int currentEnchantLevel = itemInSlot1.getEnchantmentLevel(server.reloadableRegistries().get().holderOrThrow(Enchantments.MULTISHOT));
            itemInSlot1.enchant(server.registryAccess().holderOrThrow(Enchantments.MULTISHOT),Math.min(currentEnchantLevel + 1, 2));
            player.getInventory().setItem(1,itemInSlot1);
            return false;
        }

        if(itemEntity.getItem().is(Items.SUGAR))
        {
            ItemStack itemInSlot1 = inventory.getItem(1);
            int currentEnchantLevel = itemInSlot1.getEnchantmentLevel(server.reloadableRegistries().get().holderOrThrow(Enchantments.QUICK_CHARGE));
            itemInSlot1.enchant(server.registryAccess().holderOrThrow(Enchantments.QUICK_CHARGE),Math.min(currentEnchantLevel + 1, 4));
            player.getInventory().setItem(1,itemInSlot1);
            return false;
        }

        if(itemEntity.getItem().is(Items.IRON_INGOT))
        {
            int slotNumber = new Random().nextInt(0,4);
            ItemStack returnStack = inventory.getArmor(slotNumber);;

            int currentEnchantLevel = returnStack.getEnchantmentLevel(server.reloadableRegistries().get().holderOrThrow(Enchantments.PROTECTION));
            returnStack.enchant(server.registryAccess().holderOrThrow(Enchantments.PROTECTION),Math.min(currentEnchantLevel + 2, 10));
            player.getInventory().armor.set(slotNumber,returnStack);
            return false;
        }


        return true;
    }
}
