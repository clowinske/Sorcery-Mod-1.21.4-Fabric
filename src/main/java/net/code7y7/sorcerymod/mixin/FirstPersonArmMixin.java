package net.code7y7.sorcerymod.mixin;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.entity.client.SpellModel;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class FirstPersonArmMixin {

    @Unique
    AbstractClientPlayerEntity player;
    @Inject(method = "renderFirstPersonItem", at = @At("TAIL"), cancellable = true)
    private void renderSpellChargeModel(AbstractClientPlayerEntity player,
                                         float tickDelta,
                                         float pitch,
                                         Hand hand,
                                         float swingProgress,
                                         ItemStack item,
                                         float equipProgress,
                                         MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers,
                                         int light,
                                         CallbackInfo ci){
        ci.cancel();

        renderArmSpell("right", player, matrices, vertexConsumers, light);
        renderArmSpell("left", player, matrices, vertexConsumers, light);
        renderArm(matrices, vertexConsumers, light, Arm.LEFT);
        renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
    }


    @Shadow
    public abstract void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm);

    @Unique
    private void renderArmSpell(String handName, AbstractClientPlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float charge = SpellHelper.getHandCharge(player, handName);
        if (charge > 0) {
            float f = handName.equals("right") ? 1.0F : -1.0F;
            //matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f));

            CrystalData elementType = SpellHelper.getHandSpell(player, handName);
            Identifier texture = Identifier.of(SorceryMod.MOD_ID, "textures/entity/spell_charge/" + elementType.getName() + "_spell.png");
            float scale = charge / 100f;

            float time = player.age / 2.0f;

            // Inner cube: heavy jitter
            float jitterXInner = MathHelper.sin(time * 3.1f) * 0.005f;
            float jitterYInner = MathHelper.cos(time * 2.3f) * 0.005f;
            float jitterZInner = MathHelper.sin(time * 4.7f + 1.0f) * 0.005f;
            float rotAngleInner = MathHelper.sin(time * 2.0f) * 2.0f;

            // Middle cube: light jitter
            float jitterXMid = MathHelper.sin(time * 1.9f) * 0.0035f;
            float jitterYMid = MathHelper.cos(time * 1.7f) * 0.0035f;
            float jitterZMid = MathHelper.sin(time * 2.5f + 0.7f) * 0.0035f;
            float rotAngleMid = MathHelper.sin(time * 1.4f) * 2.0f;

            matrices.push();

            // Position the spell charge model depending on hand
            if (handName.equals("right")) {
                matrices.translate(0.7, -scale - 0.4, -1.2);
            } else {
                matrices.translate(-0.7, -scale - 0.4, -1.2);
            }

            matrices.scale(scale, scale, scale);

            SpellModel model = new SpellModel(SpellModel.getTexturedModelData().createModel());
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));


            // Render outer cube (stable)
            model.outerCube.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

            // Render middle cube (slightly unstable)
            matrices.push();
            matrices.translate(jitterXMid, jitterYMid, jitterZMid);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotAngleMid));
            model.middleCube.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();

            // Render inner cube (very unstable)
            int emissiveLight = 0xF000F0;
            matrices.push();
            matrices.translate(jitterXInner, jitterYInner, jitterZInner);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotAngleInner));
            model.innerCube.render(matrices, vertexConsumer, emissiveLight, OverlayTexture.DEFAULT_UV);
            matrices.pop();

            matrices.pop();
        }
    }
}
