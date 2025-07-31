
package net.tazgirl.fgfmsummer.client.renderer;

import net.tazgirl.fgfmsummer.entity.PeterGriffin;
import net.tazgirl.fgfmsummer.entity.model.PeterGriffinModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;


import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class PeterGriffinRenderer extends GeoEntityRenderer<PeterGriffin> {
    public PeterGriffinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PeterGriffinModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(PeterGriffin animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, PeterGriffin entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        float scale = 2f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }
}
