package net.code7y7.sorcerymod.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.code7y7.sorcerymod.SorceryMod;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PlayerSkinRenderer<T extends PlayerEntityRenderState> extends FeatureRenderer<T, PlayerEntityModel> {

    private static final Identifier CUSTOM_SKIN = SorceryMod.createIdentifier("textures/entity/player/skin_01.png");

    public PlayerSkinRenderer(FeatureRendererContext<T, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T state, float limbAngle, float limbDistance) {
        System.out.println("rendering");
        this.getContextModel().render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(state.skinTextures.texture())), light, OverlayTexture.DEFAULT_UV);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(CUSTOM_SKIN));
        this.getContextModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        RenderSystem.disableBlend();
    }
}
