package net.code7y7.sorcerymod.client.render;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.entity.custom.FireballEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class FireballRenderer extends EntityRenderer<FireballEntity, FireballEntityRenderState> {
    ModelPart root;
    FireballModel model;
    private static final Identifier TEXTURE = SorceryMod.createIdentifier("textures/entity/fireball.png");
    private static final RenderLayer LAYER;

    public FireballRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        root = ctx.getPart(FireballModel.FIREBALL);
        this.model = new FireballModel(root);
    }

    @Override
    protected int getBlockLight(FireballEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(FireballEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0f, -1.25f, 0f);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.pitch));

        this.model.setAngles(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public void updateRenderState(FireballEntity entity, FireballEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.yaw = entity.getLerpedYaw(tickDelta);
        state.pitch = entity.getLerpedPitch(tickDelta);
    }

    @Override
    public FireballEntityRenderState createRenderState() {
        return new FireballEntityRenderState();
    }
    static {
        LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
    }
}


