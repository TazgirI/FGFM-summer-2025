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

}
