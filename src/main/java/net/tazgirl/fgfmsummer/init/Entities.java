package net.tazgirl.fgfmsummer.init;

import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.Arrow;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;
import net.tazgirl.fgfmsummer.FGFMSummer;
import net.tazgirl.fgfmsummer.entity.InvincibleItemFrame;
import net.tazgirl.fgfmsummer.entity.PeterArrow;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Entities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, FGFMSummer.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<PeterGriffin>> PETER_GRIFFIN = register("peter_griffin",
            EntityType.Builder.<PeterGriffin>of(PeterGriffin::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

                    .sized(0.6f, 1.8f));



    public static final DeferredHolder<EntityType<?>, EntityType<InvincibleItemFrame>> INVINCIBLE_ITEM_FRAME = register("invincible_item_frame",
            EntityType.Builder.<InvincibleItemFrame>of(InvincibleItemFrame::new, MobCategory.MISC).noSave()
                    .sized(0.375F, 0.5F)
                    .eyeHeight(0.0625F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<PeterArrow>> PETER_ARROW = register(
            "peter_arrow",
            EntityType.Builder.<PeterArrow>of(PeterArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20)
    );



    // Start of user code block custom entities
    // End of user code block custom entities
    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
    }

    @SubscribeEvent
    public static void init(RegisterSpawnPlacementsEvent event) {
        PeterGriffin.init(event);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PETER_GRIFFIN.get(), PeterGriffin.createAttributes().build());
    }
}
