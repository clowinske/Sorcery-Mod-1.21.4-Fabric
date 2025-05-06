package net.code7y7.sorcerymod.entity.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.client.render.FireballEntityRenderState;
import net.code7y7.sorcerymod.client.render.FireballModel;
import net.code7y7.sorcerymod.entity.custom.UpgradeOrbEntity;
import net.code7y7.sorcerymod.entity.enemy.Class1Enemy;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class UpgradeOrbRenderer extends EntityRenderer<UpgradeOrbEntity, UpgradeOrbEntityRenderState> {
    ModelPart root;
    UpgradeOrbModel model;
    Identifier TEXTURE = SorceryMod.createIdentifier("textures/entity/upgrade_orb.png");

    public UpgradeOrbRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        root = ctx.getPart(UpgradeOrbModel.UPGRADE_ORB);
        this.model = new UpgradeOrbModel(root);
    }



    @Override
    public UpgradeOrbEntityRenderState createRenderState() {
        return new UpgradeOrbEntityRenderState();
    }

    @Override
    public void render(UpgradeOrbEntityRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();

        int color = state.isUnlocked ? CrystalData.getTypeByInt(state.crystalType).getColorInt() : CrystalData.INERT.getColorInt();
        int innerColor = 0xFF000000 | color;
        int outerColor = 0x32FFFFFF;
        int overlay = OverlayTexture.DEFAULT_UV;
        matrixStack.translate(0 , -state.scale, 0);
        matrixStack.scale(state.scale, state.scale, state.scale);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        this.model.core.render(matrixStack, vertexConsumer, light, overlay, innerColor);

        VertexConsumer vertexConsumer1 = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        this.model.shell.render(matrixStack, vertexConsumer1, light, overlay, outerColor);

        matrixStack.pop();
    }

    @Override
    public void updateRenderState(UpgradeOrbEntity entity, UpgradeOrbEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);

        state.isUnlocked = entity.isUnlocked();
        state.scale = entity.getScale();
        state.crystalType = entity.getCrystalType();
    }
}
