package net.code7y7.sorcerymod.client.render;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.entity.custom.FireballEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;

// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class FireballModel extends EntityModel<FireballEntityRenderState> {
	public static final EntityModelLayer FIREBALL = new EntityModelLayer(SorceryMod.createIdentifier("fireball"), "main");
	private final ModelPart ball;
	public FireballModel(ModelPart root) {
		super(root);
		this.ball = root.getChild("ball");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("ball", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	public ModelPart getBall() {
		return ball;
	}
	@Override
	public void setAngles(FireballEntityRenderState renderState) {
		super.setAngles(renderState);
		this.ball.yaw = renderState.yaw * 0.017453292F;
		this.ball.pitch = renderState.pitch * 0.017453292F;
	}
}