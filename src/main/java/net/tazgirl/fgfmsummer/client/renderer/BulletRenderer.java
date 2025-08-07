package net.tazgirl.fgfmsummer.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.tazgirl.fgfmsummer.entity.model.BulletModel;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords;

public class BulletRenderer <T extends AbstractArrow> extends EntityRenderer<T>
{
    private final BulletModel<T> model;

    public BulletRenderer(EntityRendererProvider.Context context)
    {
        super(context);

        EntityModelSet modelSet = context.getModelSet();
        this.model = new BulletModel<>(modelSet.bakeLayer(BulletModel.LAYER_LOCATION));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight)
    {
        poseStack.pushPose();

        poseStack.translate(0.0D, 0.0D, 0.0D);
        //poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        VertexConsumer vertexconsumer = buffer.getBuffer(model.renderType(getTextureLocation(entity)));
        model.renderToBuffer(poseStack, vertexconsumer, 1, 1, 1);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T t)
    {
        return ResourceLocation.parse("fgfmsummer:textures/entities/bullet.png");
    }
}
