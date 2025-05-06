package net.code7y7.sorcerymod.entity.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.util.RenderStateAccess;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class SpellChargeFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    public SpellChargeFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        if(MinecraftClient.getInstance().world !=null && MinecraftClient.getInstance().player != null) {
            int leftCharge = ((RenderStateAccess)state).sorcerymod$getLeftHandCharge();
            int rightCharge = ((RenderStateAccess)state).sorcerymod$getRightHandCharge();
            int leftSpell = ((RenderStateAccess)state).sorcerymod$getLeftHandSpell();
            int rightSpell = ((RenderStateAccess)state).sorcerymod$getRightHandSpell();

            if (rightCharge > 0) {
                CrystalData elementTypeR = CrystalData.getTypeByInt(rightSpell);
                Identifier TEXTURER = Identifier.of(SorceryMod.MOD_ID, "textures/entity/spell_charge/" + elementTypeR.getName() + "_spell.png");
                float scale = rightCharge / 100f;
                float time = MinecraftClient.getInstance().player.age / 2.0f;

                SpellModel model = new SpellModel(SpellModel.getTexturedModelData().createModel());
                VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURER));

                matrices.push();
                getContextModel().rightArm.rotate(matrices);
                matrices.translate(-0.045f, -scale + 0.535f, -0.1f);
                matrices.scale(scale, scale, scale);
                model.outerCube.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();

                float jitterX = MathHelper.sin(time * 3.1f) * 0.0035f;
                float jitterY = MathHelper.cos(time * 2.3f) * 0.0035f;
                float jitterZ = MathHelper.sin(time * 4.7f + 1.0f) * 0.0035f;
                float rotAngle = MathHelper.sin(time * 2.0f) * 5.0f;

                matrices.push();
                getContextModel().rightArm.rotate(matrices);
                matrices.translate(-0.045f + jitterX, -scale + 0.535f + jitterY, -0.1f + jitterZ);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotAngle));
                matrices.scale(scale, scale, scale);
                model.middleCube.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();

                float jitterXInner = MathHelper.sin(time * 3.1f) * 0.005f;
                float jitterYInner = MathHelper.cos(time * 2.3f) * 0.005f;
                float jitterZInner = MathHelper.sin(time * 4.7f + 1.0f) * 0.005f;
                float rotAngleInner = MathHelper.sin(time * 2.0f) * 2.0f;
                int emissiveLight = 0xF000F0;

                matrices.push();
                getContextModel().rightArm.rotate(matrices);
                matrices.translate(-0.045f + jitterXInner, -scale + 0.535f + jitterYInner, -0.1f + jitterZInner);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotAngleInner));
                matrices.scale(scale, scale, scale);
                model.innerCube.render(matrices, consumer, emissiveLight, OverlayTexture.DEFAULT_UV);
                matrices.pop();
            }
            if (leftCharge > 0) {
                CrystalData elementTypeR = CrystalData.getTypeByInt(leftSpell);
                Identifier TEXTUREL = Identifier.of(SorceryMod.MOD_ID, "textures/entity/spell_charge/" + elementTypeR.getName() + "_spell.png");
                float scale = leftCharge / 100f;
                float time = MinecraftClient.getInstance().player.age / 2.0f;

                SpellModel model = new SpellModel(SpellModel.getTexturedModelData().createModel());
                VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTUREL));

                matrices.push();
                getContextModel().leftArm.rotate(matrices);
                matrices.translate(0.045f, -scale + 0.535f, -0.1f);
                matrices.scale(scale, scale, scale);
                model.outerCube.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();

                float jitterX = MathHelper.sin(time * 3.1f) * 0.0035f;
                float jitterY = MathHelper.cos(time * 2.3f) * 0.0035f;
                float jitterZ = MathHelper.sin(time * 4.7f + 1.0f) * 0.0035f;
                float rotAngle = MathHelper.sin(time * 2.0f) * 5.0f;

                matrices.push();
                getContextModel().leftArm.rotate(matrices);
                matrices.translate(0.045f + jitterX, -scale + 0.535f + jitterY, -0.1f + jitterZ);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotAngle));
                matrices.scale(scale, scale, scale);
                model.middleCube.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();

                float jitterXInner = MathHelper.sin(time * 3.1f) * 0.005f;
                float jitterYInner = MathHelper.cos(time * 2.3f) * 0.005f;
                float jitterZInner = MathHelper.sin(time * 4.7f + 1.0f) * 0.005f;
                float rotAngleInner = MathHelper.sin(time * 2.0f) * 2.0f;
                int emissiveLight = 0xF000F0;

                matrices.push();
                getContextModel().leftArm.rotate(matrices);
                matrices.translate(0.045f + jitterXInner, -scale + 0.535f + jitterYInner, -0.1f + jitterZInner);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotAngleInner));
                matrices.scale(scale, scale, scale);
                model.innerCube.render(matrices, consumer, emissiveLight, OverlayTexture.DEFAULT_UV);
                matrices.pop();
            }
        }
    }
}
