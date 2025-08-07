package net.tazgirl.fgfmsummer.peter_fight;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
import net.tazgirl.fgfmsummer.dirty.packets.GamemodePayload;
import net.tazgirl.fgfmsummer.cut_content.PeterCopter;
import net.tazgirl.fgfmsummer.entity.PeterArrow;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;
import net.tazgirl.fgfmsummer.init.DataAttachments;
import net.tazgirl.fgfmsummer.init.Entities;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.*;

@EventBusSubscriber(modid = FGFMSummer.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PeterFunctions
{
    static List<ServerPlayer> players = new ArrayList<>();
    static Map<String, String> animationBusyPairs = new HashMap<>(); // Key is goal while performing action, value is animation while performing action
    static String currentGoal = "";
    static ServerPlayer currentTarget = null;
    public static PeterGriffin peter = null;
    public static ServerLevel serverLevel = null;

    public static double speacialMoveChance = 0.33;
    public static double priorityChance = 0.25;

    public static List<String> availableMoves = new ArrayList<>();
    //public static List<String> possibleMoves = List.of("Slam","StopIt", "Combo", "Gun");
    public static List<String> possibleMoves = List.of("Gun");
    public static List<String> possibleAscendedMoves = List.of("Slam","StopIt", "Combo");

    public static boolean isAscended = false;
    public static boolean isDead = false;

    public static boolean punishDamage = false;

    public static boolean removeInvulnFrames = false;

    public static boolean cancelKnockback = false;

    public static int currentComboCycles;

    static String currentAnimation;

    @SubscribeEvent
    public static void OnServerStart(ServerStartingEvent event)
    {
        event.getServer().getScoreboard().setDisplayObjective(DisplaySlot.SIDEBAR,event.getServer().getScoreboard().getObjective("peterFightScores"));

        animationBusyPairs.put("Slapping", "animation.petergriffin.slap");
        animationBusyPairs.put("StoppingIt", "animation.petergriffin.stopit");
        animationBusyPairs.put("StartingUp","animation.petergriffin.spawnin");
        animationBusyPairs.put("Dead","animation.petergriffin.deathpose");
        animationBusyPairs.put("Ascending","animation.petergriffin.rise");
        animationBusyPairs.put("Slamming","animation.petergriffin.slam");
        animationBusyPairs.put("Gunning", "");
    }

    public static void Slap()
    {
        if(peter.level().isClientSide()){return;}

        if(currentTarget != null)
        {
            peter.getNavigation().moveTo(currentTarget, 1.5);

            if(SimpleFuncs.DistanceBetween(peter.position(), currentTarget.position()) <= 2)
            {
                peter.setAnimation("animation.petergriffin.slap");
                currentGoal = "Slapping";

                SimpleFuncs.queueServerWork(2, () ->
                {
                    DamageSource damageSource = new DamageSource(peter.level().registryAccess().holderOrThrow(FgfmDamageTypes.PETER_DAMAGE), peter);
                    if(currentTarget != null) {currentTarget.hurt(damageSource, 4);}
                    currentTarget = null;
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
            peter.getNavigation().moveTo(currentTarget, 1.5);

            if(SimpleFuncs.DistanceBetween(peter.position(), currentTarget.position()) <= 2)
            {
                peter.setAnimation("animation.petergriffin.slap");
                currentGoal = "Slapping";

                SimpleFuncs.queueServerWork(2, () ->
                {
                    DamageSource damageSource = new DamageSource(peter.level().registryAccess().holderOrThrow(FgfmDamageTypes.PETER_DAMAGE), peter);
                    if(currentTarget != null) {currentTarget.hurt(damageSource, 4);}
                    if(currentTarget.getData(DataAttachments.IS_DEAD.get()).isDead())
                    {
                        currentGoal = "";
                        currentTarget = null;
                    }
                });
                SimpleFuncs.queueServerWork(9, () ->
                {

                    if (currentTarget != null)
                    {
                        if(new Random().nextInt(1,10) - currentComboCycles < 0)
                        {
                            currentGoal = "Combo";
                        }
                        else
                        {
                            currentGoal = "";
                            currentTarget = null;
                        }
                    }

                });


            }


        }
    }

    public static void StopIt()
    {
        if(peter.level().isClientSide()){return;}

        serverLevel.playSound(null, peter.position().x, peter.position().y, peter.position().z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("fgfmsummer:petergriffinstopit")), SoundSource.RECORDS,1,1);

        peter.setAnimation("animation.petergriffin.stopit");
        currentGoal = "StoppingIt";

        cancelKnockback = true;

        SimpleFuncs.queueServerWork(41,() ->
                {
                    punishDamage = true;
                    removeInvulnFrames = true;

                });
        SimpleFuncs.queueServerWork(161,() ->
        {
            punishDamage = false;
            currentGoal = "";
            cancelKnockback = false;
            removeInvulnFrames = false;
        });



    }

    public static void Gun()
    {
        if(peter.level().isClientSide()){return;}
        currentGoal = "Gunning";

        peter.setAnimation("animation.petergriffin.gun");
        serverLevel.playSound(null,peter.position().x, peter.position().y, peter.position().z, SoundEvent.createVariableRangeEvent(ResourceLocation.parse("fgfmsummer:howthegunworks")), SoundSource.RECORDS, 1.0f, 1.0f);

        SimpleFuncs.queueServerWork(22, PeterFunctions::GunShoot);
        SimpleFuncs.queueServerWork(32,() -> {currentGoal = ""; peter.setAnimation("Empty");});
    }

    public static void GunShoot()
    {
        if(!players.isEmpty())
        {
            for(ServerPlayer player: players)
            {
                PeterArrow arrow = new PeterArrow(EntityType.ARROW, peter.level());
                arrow.setPos(peter.position().x, peter.position().y + 2, peter.position().z);
                peter.level().addFreshEntity(arrow);
                Vec3 targetPos = new Vec3(player.position().x - arrow.getX(), player.getEyeY() - arrow.getY(),player.getZ() - arrow.getZ());
                arrow.shoot(targetPos.x, targetPos.y, targetPos.z, 2f, 0);
            }
        }
    }

    private static void Slam()
    {
        if(peter.level().isClientSide()){return;}
        if(currentTarget == null){return;}

        ClosestTarget();

        peter.getNavigation().moveTo(currentTarget, 1.5);

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
            serverLevel.playSound(null,peter.position().x, peter.position().y, peter.position().z, SoundEvent.createVariableRangeEvent(ResourceLocation.parse("fgfmsummer:whynohole")), SoundSource.RECORDS, 1.0f, 1.0f);
            peter.setAnimation("animation.petergriffin.slam");
            currentGoal = "Slamming";

            SimpleFuncs.queueServerWork(49,() -> peter.level().explode(peter, new DamageSource(peter.level().registryAccess().holderOrThrow(DamageTypes.EXPLOSION), peter), new PeterExplosionHandler(), peter.position(), 5f, false, Level.ExplosionInteraction.TRIGGER));
            SimpleFuncs.queueServerWork(61, () -> currentGoal = "");
        }
    }

    public static void PeterTick(PeterGriffin peterEntity)
    {
        if (peterEntity.level().isClientSide || isDead) return;

        players.clear();

        SimpleFuncs.sendMessageToAllPlayers(peterEntity.getServer(), Component.literal(currentGoal + " " + currentAnimation));

        peter = peterEntity;

        if(peter.level() instanceof ServerLevel serverSideLevel)
        {
            serverLevel = serverSideLevel;
        }


        if (serverLevel != null)
        {
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

            }
        }

        if(IsCurrentActionABusy() || players.isEmpty()) {return;}

        if(availableMoves.isEmpty()){RecalculateMoves();}


        currentAnimation = peter.animationprocedure;


        if(currentTarget == null)
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

        if(!peter.getNavigation().isInProgress()){peter.getNavigation().moveTo(currentTarget, 1);}

        if(Objects.equals(currentGoal, "Slap")) {Slap();}

        if(Objects.equals(currentGoal, "StopIt")) {StopIt();}

        if(Objects.equals(currentGoal, "Slam")) {Slam();}

        if(Objects.equals(currentGoal, "Combo")) {Combo();}

        if(Objects.equals(currentGoal, "Gun")){Gun();}





        System.out.println(currentAnimation + " " + currentGoal);

    }



    private static boolean IsCurrentActionABusy()
    {
        return animationBusyPairs.containsKey(currentGoal);
    }


    public static void AscendPeter()
    {
        if(peter.level().isClientSide()){return;}

        SimpleFuncs.workQueue.clear();
        peter.setHealth(1);
        peter.setAnimation("animation.petergriffin.rise");
        cancelKnockback = true;
        peter.setInvulnerable(true);
        currentGoal = "Ascending";


        SimpleFuncs.queueServerWork(43, () ->
        {
            peter.setHealth(peter.getMaxHealth());
            isAscended = true;
            peter.setTexture("amogusgriffin");
            peter.setCustomName(Component.literal("Ascended Peter"));
        });
        SimpleFuncs.queueServerWork(80, () ->
        {
            peter.setInvulnerable(false);
            cancelKnockback = false;
            currentGoal = "";
        });

    }

    public static void ClearVariables()
    {
        players = new ArrayList<>();
        animationBusyPairs = new HashMap<>(); // Key is goal while performing action, value is animation while performing action
        currentGoal = "";
        currentTarget = null;
        peter = null;
        serverLevel = null;

        isAscended = false;
        isDead = false;

        punishDamage = false;

        cancelKnockback = false;

        currentAnimation = "StartingUp";
    }

    public static void Setup(MinecraftServer server)
    {

        ClearVariables();

        Vec3 arenaPos = PeterFightConstants.arenaSpawnCoords;

       GlobalConstants.gamemode = "PeterFight";

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
        if(peter.level().isClientSide()){return;}


        Vec3 arenaPos = PeterFightConstants.arenaSpawnCoords;

        player.setData(DataAttachments.IS_DEAD.get(),new DataAttachments.isDeadRecord(false,0L,0L));
        InventoryLoader.setPlayerInventoryFromNBT("arena_set",player);

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
        entity.level().addFreshEntity(peter);
        currentGoal = "StartingUp";
        serverLevel = (ServerLevel) entity.level();


        SimpleFuncs.queueServerWork(1,() ->
        {
            peter.setAnimation("animation.petergriffin.spawnin");
            peter.setInvisible(false);
        });
        SimpleFuncs.queueServerWork(20,() ->
        {
            serverLevel.playSound(null, peter.position().x, peter.position().y, peter.position().z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("fgfmsummer:hiimpeter")), SoundSource.RECORDS,1,1);

        });
        SimpleFuncs.queueServerWork(55,() ->
        {
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
                closestPlayer = player;
            }
        }

        currentTarget = closestPlayer;
    }

    public static int GetPeterPoints(ServerPlayer player)
    {
        Scoreboard scoreboard = player.getScoreboard();
        return scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()),scoreboard.getObjective("peterFightScores")).get();
    }

    public static void RecalculateMoves()
    {
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
