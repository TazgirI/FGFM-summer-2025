package net.tazgirl.fgfmsummer.peter_fight;

import com.sun.jna.platform.win32.WinDef;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.tazgirl.fgfmsummer.FGFMSummer;
import net.tazgirl.fgfmsummer.GlobalConstants;
import net.tazgirl.fgfmsummer.SimpleFuncs;
import net.tazgirl.fgfmsummer.damage.FgfmDamageTypes;
import net.tazgirl.fgfmsummer.dirty.InventoryLoader;
import net.tazgirl.fgfmsummer.dirty.MapWeightCycle;
import net.tazgirl.fgfmsummer.dirty.packets.GamemodePayload;
import net.tazgirl.fgfmsummer.entity.BombEntity;
import net.tazgirl.fgfmsummer.entity.PeterArrow;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.init.Entities;
import net.minecraft.world.damagesource.DamageTypes;
import net.tazgirl.fgfmsummer.lobby.LobbyConstants;

import java.util.*;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PeterFunctions
{
    public static boolean ascendSoon = false;
    static List<ServerPlayer> players = new ArrayList<>();
    static Map<String, String> animationBusyPairs = new HashMap<>(); // Key is goal while performing action, value is animation while performing action
    static String currentGoal = "";
    static ServerPlayer currentTarget = null;
    public static PeterGriffin peter = null;
    public static ServerLevel serverLevel = null;

    public static boolean canwalk = true;
    public static double mySpeed = 1.0F;

    static Vec3 targetPos = new Vec3(0,0,0);

    public static double speacialMoveChance = 0.33;
    public static double priorityChance = 0.25;
    public static int ticksPerItem = 200;
    public static int ticksSinceItem = 0;

    public static List<String> availableMoves = new ArrayList<>();
    public static List<String> possibleMoves = List.of("Slam","StopIt", "Combo", "Gun","Roadhouse", "Summertime");
    //public static List<String> possibleMoves = List.of("Roadhouse");
    public static List<String> possibleAscendedMoves = List.of("Slam", "Combo", "Gun", "Roadhouse", "Summertime","Summon");

    public static boolean isAscended = false;
    public static boolean isDead = false;

    public static boolean punishDamage = false;

    public static boolean removeInvulnFrames = false;

    public static boolean cancelKnockback = false;

    public static boolean bombHover = false;

    public static int currentComboCycles = 0;

    static String currentAnimation;

    @SubscribeEvent
    public static void OnServerStart(ServerStartingEvent event)
    {
        for(ServerLevel level: event.getServer().getAllLevels())
        {
            for(PeterGriffin currentPeter: level.getEntities(Entities.PETER_GRIFFIN.get(), peterEntity -> peterEntity.isAlive()))
            {
                currentPeter.discard();
            }
        }

        animationBusyPairs.put("Slapping", "animation.petergriffin.slap");
        animationBusyPairs.put("StoppingIt", "animation.petergriffin.stopit");
        animationBusyPairs.put("StartingUp","animation.petergriffin.spawnin");
        animationBusyPairs.put("Dead","animation.petergriffin.deathpose");
        animationBusyPairs.put("Ascending","animation.petergriffin.rise");
        animationBusyPairs.put("Slamming","animation.petergriffin.slam");
        animationBusyPairs.put("Gunning", "animation.petergriffin.gun");
        animationBusyPairs.put("Roadhousing","animation.petergriffin.roadhouse");
        animationBusyPairs.put("Summertiming", "animation.petergriffin.tinyarms");
        animationBusyPairs.put("Summoning", "animation.petergriffin.summon");
        animationBusyPairs.put("Comboing", "animation.petergriffin.slap");
    }

    public static void Slap()
    {
        if(peter.level().isClientSide()){return;}

        canwalk = true;

        if(currentTarget != null)
        {
            mySpeed = 1.5;

            if(SimpleFuncs.DistanceBetween(peter.position(), currentTarget.position()) <= 2)
            {
                peter.setAnimation("animation.petergriffin.slap");
                mySpeed = 1.0;
                currentGoal = "Slapping";

                if(new Random().nextInt(0,6) == 1)
                {
                    SimpleFuncs.PlaySound("fgfmsummer:laugh",serverLevel, peter);
                }

                SimpleFuncs.queueServerWork(4, () ->
                {
                    DamageSource damageSource = new DamageSource(peter.level().registryAccess().holderOrThrow(FgfmDamageTypes.PETER_DAMAGE), peter);
                    if(currentTarget != null) {currentTarget.hurt(damageSource, !isAscended ? 3 : 4.5F);}
                });
                SimpleFuncs.queueServerWork(9, () ->
                {
                    currentGoal = "";
                });


            }


        }
    }

    public static void Combo()
    {
        if(peter.level().isClientSide()){return;}

        if(currentTarget != null)
        {
            mySpeed = 1.5;

            if(SimpleFuncs.DistanceBetween(peter.position(), currentTarget.position()) <= 2)
            {
                peter.setAnimation("animation.petergriffin.slap");
                currentGoal = "Comboing";

                SimpleFuncs.queueServerWork(2, () ->
                {
                    DamageSource damageSource = new DamageSource(peter.level().registryAccess().holderOrThrow(FgfmDamageTypes.PETER_DAMAGE), peter);
                    if(currentTarget != null) {currentTarget.hurt(damageSource, !isAscended ? 3 : 4.5F);}
                });
                SimpleFuncs.queueServerWork(9, () ->
                {

                    if (currentTarget != null)
                    {
                        if(!(new Random().nextInt(1,10) - currentComboCycles < 0))
                        {
                            currentGoal = "Combo";
                            currentComboCycles++;
                        }
                        else
                        {
                            mySpeed = 1.0;
                            currentGoal = "";
                            currentTarget = null;

                        }
                    }
                    else
                    {
                        currentGoal = "";
                        currentTarget = null;
                        mySpeed = 1.0;
                        currentComboCycles = 0;
                    }

                });


            }


        }
    }

    public static void StopIt()
    {
        if(peter.level().isClientSide()){return;}

        SimpleFuncs.PlaySound("fgfmsummer:petergriffinstopit",serverLevel,peter);

        peter.setAnimation("animation.petergriffin.stopit");
        currentGoal = "StoppingIt";

        mySpeed = 0.8;

        cancelKnockback = true;

        SimpleFuncs.queueServerWork(41,() ->
        {
            canwalk = false;
            punishDamage = true;
            removeInvulnFrames = true;

        });
        SimpleFuncs.queueServerWork(161,() ->
        {
            punishDamage = false;
            currentGoal = "";
            cancelKnockback = false;
            removeInvulnFrames = false;
            canwalk = true;
            mySpeed = 1.0;
        });
    }

    public static void Summertime()
    {
        if(peter.level().isClientSide()){return;}

        int nearbyPlayers = 0;

        for(ServerPlayer player: players)
        {
            if(peter.distanceTo(player) <= 2.5){nearbyPlayers++;}
        }

        if (nearbyPlayers > players.size() / 3)
        {
            currentGoal = "Summertiming";
            peter.setAnimation("animation.petergriffin.tinyarm");
            canwalk = false;

            SimpleFuncs.queueServerWork(16,() ->
            {
                SimpleFuncs.PlaySound("fgfmsummer:summertime",serverLevel, peter);
                currentGoal = "SummertimingAgro";
                canwalk = true;
                mySpeed = 1.8;
            });
            SimpleFuncs.queueServerWork(112,() ->
            {
                currentGoal = "";
                mySpeed = 1.0;
            });
        }

    }

    public static void SummertimingAgro()
    {
        ClosestTarget();

        int playersNearby = 0;

        for(ServerPlayer player: players)
        {
            if(peter.distanceTo(player) <= 2.5)
            {
                playersNearby++;

                player.hurt(new DamageSource(peter.level().registryAccess().holderOrThrow(FgfmDamageTypes.PETER_DAMAGE), peter), !isAscended ? 2 : 3.5F);


                Vec3 direction = player.position().subtract(peter.position()).normalize();
                double knockX = direction.x;
                double knockZ = direction.z;

                player.knockback(5, knockX, knockZ);
            }
        }

        if(playersNearby < 1)
        {
            mySpeed += 0.025;
        }
    }

    public static void Gun()
    {
        if(peter.level().isClientSide()){return;}

        currentGoal = "Gunning";

        mySpeed = 0.5;

        peter.setAnimation("animation.petergriffin.gun");
        SimpleFuncs.PlaySound("fgfmsummer:howthegunworks",serverLevel,peter);

        SimpleFuncs.queueServerWork(22, PeterFunctions::GunShoot);
        SimpleFuncs.queueServerWork(32,() -> {currentGoal = ""; mySpeed = 1.0;});
    }

    public static void GunShoot()
    {
        if(peter.level().isClientSide()){return;}
        if(!players.isEmpty())
        {
            for(ServerPlayer player: players)
            {
                PeterArrow arrow = new PeterArrow(Entities.PETER_ARROW.get(),peter.level());
                arrow.setPos(peter.position().x, peter.position().y + 2, peter.position().z);
                peter.level().addFreshEntity(arrow);
                Vec3 targetPos = new Vec3(player.position().x - arrow.getX(), player.getEyeY() - arrow.getY(),player.getZ() - arrow.getZ());
                if(player == currentTarget)
                {
                    arrow.shoot(targetPos.x, targetPos.y, targetPos.z, 3f, 0);
                }
                else
                {
                    arrow.shoot(targetPos.x, targetPos.y, targetPos.z, 2f, 0.1f);
                }

            }
        }
    }

    public static void Roadhouse()
    {
        if(peter.level().isClientSide()){return;}

        mySpeed = 1.0;

        cancelKnockback = true;

        List<ServerPlayer> playersInFrontOfPeter = PlayersInFrontOfPeter();

        if ((!playersInFrontOfPeter.isEmpty() && playersInFrontOfPeter.contains(currentTarget)) ||(!playersInFrontOfPeter.isEmpty() && playersInFrontOfPeter.size() > 2))
        {
            currentGoal = "Roadhousing";
            peter.setAnimation("animation.petergriffin.roadhouse");

            mySpeed = 0.8;

            SimpleFuncs.queueServerWork(8,() ->
            {

                SimpleFuncs.PlaySound("fgfmsummer:kicksmash",serverLevel,peter);
                peter.lookAt(EntityAnchorArgument.Anchor.EYES,currentTarget.position());
            });
            SimpleFuncs.queueServerWork(9,() -> peter.lookAt(EntityAnchorArgument.Anchor.EYES,currentTarget.position()));
            SimpleFuncs.queueServerWork(10,() ->
            {
                List<ServerPlayer> currentPlayersInFrontOfPeter = PlayersInFrontOfPeter();
                for(ServerPlayer player: currentPlayersInFrontOfPeter)
                {
                    player.hurt(new DamageSource(peter.level().registryAccess().holderOrThrow(DamageTypes.GENERIC),peter),!isAscended ? 7 : 11);

                    Vec3 direction = player.position().subtract(peter.position()).normalize();
                    double knockX = direction.x;
                    double knockZ = direction.z;

                    player.knockback(10, knockX, knockZ);
                }
            });
            SimpleFuncs.queueServerWork(28,() ->
            {
                SimpleFuncs.PlaySound("fgfmsummer:roadhouse",serverLevel,peter);

            });
            SimpleFuncs.queueServerWork(50,() ->
            {
                cancelKnockback = false;
                mySpeed = 1.0;
                currentGoal = "";
            });
        }
    }

    public static List<ServerPlayer> PlayersInFrontOfPeter() {
        if (peter.level().isClientSide()) {
            return List.of();
        }

        // Direction Peter is facing
        Vec3 forward = Vec3.directionFromRotation(0, peter.getYRot()).normalize();

        // Start with Peter's hitbox
        double width = 3;   // total width
        double height = 2;  // total height
        double length = 2;  // forward reach

        AABB box = peter.getBoundingBox()
                .inflate(width / 2.0, height / 2.0, width / 2.0) // make it wider/taller
                .move(forward.scale(length)); // move it forward


        return peter.level().getEntitiesOfClass(ServerPlayer.class, box);
    }

    private static void Slam()
    {
        if(peter.level().isClientSide()){return;}

        ClosestTarget();



        mySpeed = 1.5;

        int nearbyPlayers = 0;

        for (Player player:players)
        {
            if(player.distanceTo(peter) <= 3)
            {
                nearbyPlayers++;
            }
        }

        if (nearbyPlayers >= Math.round(players.size() / 2f))
        {
            SimpleFuncs.PlaySound("fgfmsummer:whynohole",serverLevel,peter);
            peter.setAnimation("animation.petergriffin.slam");
            mySpeed = 0.4;
            currentGoal = "Slamming";


            SimpleFuncs.queueServerWork(49,() ->
            {
                canwalk = false;
                peter.level().explode(peter, new DamageSource(peter.level().registryAccess().holderOrThrow(DamageTypes.EXPLOSION), peter), new PeterExplosionHandler(), peter.position(), 5f, false, Level.ExplosionInteraction.TRIGGER);
            });
            SimpleFuncs.queueServerWork(61, () ->
            {
                mySpeed = 1.0;
                currentGoal = "";
                canwalk = true;
            });
        }
    }

    public static void Summon()
    {
        if(peter.level().isClientSide){return;}

        currentGoal = "Summoning";

        mySpeed = 0.1;

        peter.setAnimation("animation.petergriffin.summon");
        SimpleFuncs.PlaySound("fgfmsummer:thanos",serverLevel,peter);
        bombHover = true;

        SimpleFuncs.queueServerWork(85,() ->
        {
            SimpleFuncs.PlaySound("fgfmsummer:ontheceiling", serverLevel, peter);

            for(ServerPlayer player: players)
            {
                BombEntity bomb = new BombEntity(EntityType.TNT_MINECART, serverLevel);
                bomb.setPos(player.position().x,110,player.position().z);
                serverLevel.addFreshEntity(bomb);
                bomb.setData(DataAttachments.TARGET_PLAYER.get(), new DataAttachments.targetPlayerRecord(player.getId(), 0));
            }

        });
        SimpleFuncs.queueServerWork(121,() ->
        {
            bombHover = false;

        });
        SimpleFuncs.queueServerWork(145,() ->
        {
            currentGoal = "";
            mySpeed = 1.0;
        });

    }

    public static void PeterTick(PeterGriffin peterEntity)
    {
        if (peterEntity.level().isClientSide || isDead){return;}


        //SimpleFuncs.sendMessageToAllPlayers(peterEntity.getServer(), Component.literal(currentGoal + " " + currentAnimation));

        peter = peterEntity;

        if(peter.level() instanceof ServerLevel serverSideLevel)
        {
            serverLevel = serverSideLevel;
        }

        targetPos = currentTarget != null ? currentTarget.position().scale(2).subtract(peter.position()) : targetPos;

        //SimpleFuncs.sendMessageToAllPlayers(peter.getServer(), Component.literal(String.valueOf(targetPos)));

        if(availableMoves.isEmpty()){RecalculateMoves();}

        if (serverLevel != null)
        {
            players.clear();
            for(ServerPlayer player: serverLevel.players())
            {
                if(SimpleFuncs.DistanceBetween(player.position(), peter.position()) <= 100 && player.position().y < 110)
                {
                    players.add(player);
                }

            }

            if(Objects.equals(currentGoal, ""))
            {
                Random random = new Random();
                if(random.nextDouble(0,1) < speacialMoveChance)
                {
                    int position = random.nextInt(0, availableMoves.size());
                    currentGoal = availableMoves.get(position);
                    availableMoves.remove(position);
                }
                else
                {
                    currentGoal = "Slap";
                }

                if(ascendSoon)
                {
                    currentGoal = "Ascend";
                }

                JustSetGoal(currentGoal);

            }
        }

        if(canwalk)
        {
            peter.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, mySpeed);
        }
        else
        {
            peter.getNavigation().stop();
        }

        ticksSinceItem++;
        if(ticksSinceItem >= ticksPerItem)
        {
            DropItem();
            ticksSinceItem = 0;
        }


        currentAnimation = peter.animationprocedure;

        //SimpleFuncs.sendMessageToAllPlayers(serverLevel.getServer(), Component.literal(currentGoal + " " + currentAnimation));

        if(IsCurrentActionABusy() || players.isEmpty()) {return;}







        if(currentTarget == null){GenericTarget();}


        if(Objects.equals(currentGoal, "Ascend")) {AscendPeter();}

        if(Objects.equals(currentGoal, "Slap")) {Slap();}

        if(Objects.equals(currentGoal, "StopIt")) {StopIt();}

        if(Objects.equals(currentGoal, "Slam")) {Slam();}

        if(Objects.equals(currentGoal, "Combo")) {Combo();}

        if(Objects.equals(currentGoal, "Gun")){Gun();}

        if(Objects.equals(currentGoal, "Roadhouse")){Roadhouse();}

        if(Objects.equals(currentGoal, "Summertime")){Summertime();}

        if(Objects.equals(currentGoal, "SummertimingAgro")){SummertimingAgro();}

        if(Objects.equals(currentGoal, "Summon")){Summon();}



    }

    private static void SpawnItem()
    {


    }

    public static void JustSetGoal(String currentGoal)
    {
        switch (currentGoal)
        {
            case "Slap":
                GenericTarget();
                break;
            case "StopIt":
                GenericTarget();
                break;
            case "Gun":
                FurthestTarget();
                break;
            case "Slam":
                ClosestTarget();
                break;
            case "Combo":
                GenericTarget();
                break;
            case "Roadhouse":
                RandomTarget();
                break;
            case "Summertime":
                ClosestTarget();
                break;
            case "Summon":
                GenericTarget();


            default:
                break;
        }

    }

    public static void GenericTarget()
    {

        if(new Random().nextDouble(0,1) < priorityChance)
        {
            PriorityTarget();
        }

        if(new Random().nextInt(0,2) == 1)
        {
            RandomTarget();
        }
        else
        {
            ClosestTarget();
        }



    }


    private static boolean IsCurrentActionABusy()
    {
        return animationBusyPairs.containsKey(currentGoal);
    }


    public static void AscendPeter()
    {
        if(peter.level().isClientSide()){return;}

        ascendSoon = false;
        SimpleFuncs.workQueue.clear();
        peter.setHealth(1);
        peter.setAnimation("animation.petergriffin.rise");
        canwalk = false;
        cancelKnockback = true;
        peter.setInvulnerable(true);
        currentGoal = "Ascending";

        ticksPerItem = 150;

        peter.getAttributes().getInstance(Attributes.ARMOR).setBaseValue(2.5);

        speacialMoveChance = 0.5;

        SimpleFuncs.queueServerWork(39, () ->
        {
            SimpleFuncs.PlaySound("fgfmsummer:redstool",serverLevel, peter, 2.5f);
        });

        SimpleFuncs.queueServerWork(43, () ->
        {
            peter.setHealth(peter.getMaxHealth());
            isAscended = true;
            peter.setTexture("amogusgriffin");
            peter.setCustomName(Component.literal("Ascended Peter"));
            peter.bossInfo.setName(Component.literal("Ascended Peter"));
        });
        SimpleFuncs.queueServerWork(80, () ->
        {
            peter.setInvulnerable(false);
            cancelKnockback = false;
            currentGoal = "";
            canwalk = true;
        });

    }

    public static void ClearVariables()
    {
        for(ServerPlayer player: GlobalConstants.thisServer.getPlayerList().getPlayers())
        {
            player.removeData(DataAttachments.IS_DEAD);
            player.removeData(DataAttachments.TARGET_PLAYER);
            player.removeData(DataAttachments.SCORE_WHEN_LAST_DIED);
        }

        ascendSoon = false;


        players = new ArrayList<>();

        currentGoal = "";
        currentTarget = null;
        peter = null;
        serverLevel = null;

        canwalk = true;
        mySpeed = 1.0F;

        isAscended = false;
        isDead = false;

        speacialMoveChance = 0.33;
        priorityChance = 0.25;
        ticksPerItem = 200;
        ticksSinceItem = 0;

        punishDamage = false;

        availableMoves = new ArrayList<>();

        cancelKnockback = false;

        currentComboCycles = 0;

        currentAnimation = "empty";


    }

    public static void Setup(MinecraftServer server)
    {

        ClearVariables();

        Vec3 arenaPos = PeterFightConstants.arenaSpawnCoords;

       GlobalConstants.gamemode = "PeterFight";

        server.getScoreboard().setDisplayObjective(DisplaySlot.SIDEBAR,server.getScoreboard().getObjective("peterFightScores"));

       SimpleFuncs.ResetObjective(server, "peterFightScores");

        for(ServerPlayer player: server.getPlayerList().getPlayers())
        {
            InventoryLoader.setPlayerInventoryFromNBT("arena_set",player);

            player.setData(DataAttachments.SCORE_WHEN_LAST_DIED.get(),new DataAttachments.scoreWhenLastDiedRecord(0));
            player.setData(DataAttachments.IS_DEAD.get(),new DataAttachments.isDeadRecord(false,0L,0L));

            player.connection.send(new ClientboundCustomPayloadPacket(new GamemodePayload("PeterFight")));

            SimpleFuncs.ReallySendPlayer(player,arenaPos);
        }

    }

    public static void ReturnPlayerToArena(ServerPlayer player)
    {
        if(player.level().isClientSide()){return;}


        Vec3 arenaPos = PeterFightConstants.arenaSpawnCoords;

        player.setData(DataAttachments.IS_DEAD.get(),new DataAttachments.isDeadRecord(false,0L,0L));
        InventoryLoader.setPlayerInventoryFromNBT("arena_set",player);

        if (SimpleFuncs.IsPlayerUnderdog(player))
        {
            player.getInventory().armor.set(2, new ItemStack(Items.DIAMOND_CHESTPLATE));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
        }
        else if(SimpleFuncs.IsPlayerHandidog(player))
        {
            player.getInventory().armor.set(2, new ItemStack(Items.LEATHER_CHESTPLATE));
        }



        SimpleFuncs.ReallySendPlayer(player,arenaPos);
    }

    public static void End()
    {
        if(peter.level().isClientSide()){return;}

        isDead = true;
        SimpleFuncs.workQueue.clear();
        peter.setHealth(1);
        peter.setAnimation("animation.petergriffin.deathpose");
        currentGoal = "Dead";
        peter.setInvulnerable(true);
        cancelKnockback = true;

        peter.bossInfo.removeAllPlayers();
        peter.bossInfo.setVisible(false);

        SimpleFuncs.queueServerWork(200, () -> peter.setAnimation("animation.petergriffin.sink"));
        SimpleFuncs.queueServerWork(220, PeterFunctions::RemovePeter);
    }


    public static void RemovePeter()
    {
        if(peter.level().isClientSide()){return;}
        peter.remove(Entity.RemovalReason.DISCARDED);
        ClearVariables();
    }

    public static void StartFight(MinecraftServer server, Entity entity)
    {
        if(entity.level().isClientSide()){return;}
        peter = new PeterGriffin(Entities.PETER_GRIFFIN.get(),entity.level());
        peter.setInvisible(true);
        peter.setPos(PeterFightConstants.arenaSpawnCoords);
        canwalk = false;
        entity.level().addFreshEntity(peter);
        currentGoal = "StartingUp";
        serverLevel = (ServerLevel) entity.level();

        for(ServerPlayer player: serverLevel.players())
        {
            peter.bossInfo.setVisible(true);
            peter.bossInfo.addPlayer(player);
        }


        SimpleFuncs.queueServerWork(1,() ->
        {
            peter.setAnimation("animation.petergriffin.spawnin");
        });
        SimpleFuncs.queueServerWork(2,() ->
        {
            peter.setInvisible(false);
        });
        SimpleFuncs.queueServerWork(20,() ->
        {
            SimpleFuncs.PlaySound("fgfmsummer:hiimpeter",serverLevel,peter);

        });
        SimpleFuncs.queueServerWork(55,() ->
        {
            canwalk = true;
            currentGoal = "";
        });

    }

    public static void RandomTarget()
    {
        if(!players.isEmpty())
        {
            currentTarget = players.get(new Random().nextInt(0, players.size()));
            return;
        }
        currentTarget = null;
    }

    public static void PriorityTarget()
    {
        int totalPoints = 0;

        for(ServerPlayer player: players)
        {
            totalPoints += GetPeterPoints(player);
        }

        if(totalPoints == 0)
        {
            RandomTarget();
            return;
        }

        int targetPoint = new Random().nextInt(0,totalPoints);

        totalPoints = 0;

        for(ServerPlayer player: players)
        {
            totalPoints += GetPeterPoints(player);

            if(totalPoints >= targetPoint)
            {
                currentTarget = player;
                return;
            }
        }

        RandomTarget();
    }

    public static void ClosestTarget()
    {
        float closestDistance = 1000;
        ServerPlayer closestPlayer = currentTarget;

        for(ServerPlayer player: players)
        {
            if(peter.distanceTo(player) < closestDistance)
            {
                closestDistance = peter.distanceTo(player);
                closestPlayer = player;
            }
        }

        currentTarget = closestPlayer;
    }

    public static void FurthestTarget()
    {
        float furthestDistance = 0;

        ServerPlayer furthestPlayer = currentTarget;

        for(ServerPlayer player: players)
        {
            if(peter.distanceTo(player) > furthestDistance)
            {
                furthestDistance = peter.distanceTo(player);
                furthestPlayer = player;
            }
        }

        currentTarget = furthestPlayer;
    }

    public static int GetPeterPoints(ServerPlayer player)
    {
        Scoreboard scoreboard = player.getScoreboard();
        return scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("peterFightScores")).get();
    }



    public static void RecalculateMoves()
    {
        currentComboCycles = 0;

        List<String> movesForThisForm = new ArrayList<>();
        if(isAscended)
        {
            movesForThisForm = possibleAscendedMoves;
        }
        else
        {
            movesForThisForm = possibleMoves;
        }
        availableMoves = new ArrayList<>();

        Random random = new Random();

        int availableMoveAmountsToAdd = (int) Math.max(Math.ceil(movesForThisForm.size() * new Random().nextDouble(0,0.5)),1);

        SimpleFuncs.ShuffleList(movesForThisForm);

        List<Integer> moveAmounts = new ArrayList<>();

        for(String string: movesForThisForm)
        {
            moveAmounts.add(1);
        }

        for(int i = 0; i < availableMoveAmountsToAdd; i++)
        {
            int positionToChange = random.nextInt(0,moveAmounts.size());
            moveAmounts.set(positionToChange, moveAmounts.get(positionToChange) + 1);
        }


        for(String currentMove: movesForThisForm)
        {
            for(int i = 0; i < moveAmounts.get(movesForThisForm.indexOf(currentMove)); i++)
            {
                availableMoves.add(currentMove);
            }
        }
    }

    public static void DropItem()
    {
        if(peter.level().isClientSide){return;}

        Vec2 itemSpawnPos1 = PeterFightConstants.itemSpawnPos1;
        Vec2 itemSpawnPos2 = PeterFightConstants.itemSpawnPos2;
        Random random = new Random();
        Vec3 spawnPos = new Vec3(random.nextFloat(itemSpawnPos1.x,itemSpawnPos2.x), 95, random.nextFloat(itemSpawnPos1.y,itemSpawnPos2.y));
        ItemStack stackToDrop = MapWeightCycle.getWeightedRandomItem(PeterFightConstants.itemsToSpawn);

        ItemEntity itemEntity = new ItemEntity(serverLevel, spawnPos.x, spawnPos.y, spawnPos.z, stackToDrop);

        itemEntity.setGlowingTag(true);
        itemEntity.setPickUpDelay(50);
        itemEntity.setInvulnerable(true);
        itemEntity.getItem().setCount(1);

        if(itemEntity.getItem().is(Items.FIREWORK_ROCKET))
        {
            itemEntity.getItem().setCount(3);
        }

        SimpleFuncs.PlaySound("fgfmsummer:chestsound",serverLevel,itemEntity);

        SimpleFuncs.SendSubtitleToAll(Component.literal("New " + itemEntity.getItem().getDisplayName().getString().substring(1, itemEntity.getItem().getDisplayName().getString().length() - 1) + " dropping in the arena").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),peter.getServer());

        serverLevel.addFreshEntity(itemEntity);
    }

//    public static void CheckIfBusyActionIsComplete()
//    {
//        if(animationBusyPairs.containsKey(currentGoal))
//        {
//            if(!Objects.equals(animationBusyPairs.get(currentGoal), currentAnimation))
//            {
//                currentGoal = "";
//            }
//        }
//    }

}
