package net.tazgirl.fgfmsummer.init;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.tazgirl.fgfmsummer.client.renderer.BulletRenderer;
import net.tazgirl.fgfmsummer.client.renderer.PeterGriffinRenderer;
import net.tazgirl.fgfmsummer.entity.model.BulletModel;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Entities.PETER_GRIFFIN.get(), PeterGriffinRenderer::new);
        event.registerEntityRenderer(Entities.INVINCIBLE_ITEM_FRAME.get(), ItemFrameRenderer::new);
        event.registerEntityRenderer(Entities.PETER_ARROW.get(), BulletRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BulletModel.LAYER_LOCATION, BulletModel::createBodyLayer);
    }
}
