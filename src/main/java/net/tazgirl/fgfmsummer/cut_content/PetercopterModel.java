package net.tazgirl.fgfmsummer.cut_content;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

public class PetercopterModel extends GeoModel<PeterCopter> {

	@Override
	public ResourceLocation getAnimationResource(PeterCopter entity) {
		return ResourceLocation.parse("fgfmsummer:animations/petercopter.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PeterCopter entity) {
		return ResourceLocation.parse("fgfmsummer:geo/petercopter.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PeterCopter entity) {
		return ResourceLocation.parse("fgfmsummer:textures/entities/petercopter.png");
	}


}
