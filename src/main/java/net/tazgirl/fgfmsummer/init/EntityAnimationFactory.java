package net.tazgirl.fgfmsummer.init;

import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.tazgirl.fgfmsummer.entity.PeterGriffin;


@EventBusSubscriber
public class EntityAnimationFactory {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (event != null && event.getEntity() != null) {
            if (event.getEntity() instanceof PeterGriffin syncable) {
                String animation = syncable.getSyncedAnimation();
                if (!animation.equals("undefined")) {
                    syncable.setAnimation("undefined");
                    syncable.animationprocedure = animation;
                }
            }
        }
    }
}
