package net.tazgirl.fgfmsummer.entity.model;

import net.tazgirl.fgfmsummer.entity.PeterGriffin;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

public class PeterGriffinModel extends GeoModel<PeterGriffin> {
    @Override
    public ResourceLocation getAnimationResource(PeterGriffin entity) {
        return ResourceLocation.parse("fgfmsummer:animations/petergriffin.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(PeterGriffin entity) {
        return ResourceLocation.parse("fgfmsummer:geo/petergriffin.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PeterGriffin entity) {
        return ResourceLocation.parse("fgfmsummer:textures/entities/" + entity.getTexture() + ".png");
    }

    @Override
    public void setCustomAnimations(PeterGriffin animatable, long instanceId, AnimationState animationState) {
        GeoBone head = getAnimationProcessor().getBone("Head");
        if (head != null) {
            EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

    }
}
