package net.tazgirl.fgfmsummer.client.renderer;

import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.tazgirl.fgfmsummer.entity.model.BulletModel;

public class BombRenderer extends TntMinecartRenderer
{

    public BombRenderer(EntityRendererProvider.Context context)
    {
        super(context);

    }


}
