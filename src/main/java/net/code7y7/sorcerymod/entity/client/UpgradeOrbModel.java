package net.code7y7.sorcerymod.entity.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.client.render.FireballEntityRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class UpgradeOrbModel extends EntityModel<UpgradeOrbEntityRenderState> {
	public static final EntityModelLayer UPGRADE_ORB = new EntityModelLayer(SorceryMod.createIdentifier("upgrade_orb"), "main");
	public final ModelPart core;
	public final ModelPart shell;
	public UpgradeOrbModel(ModelPart root) {
		super(root);
		this.core = root.getChild("core");
		this.shell = root.getChild("shell");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("core", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -0.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 17.0F, 0.0F));

		modelPartData.addChild("shell", ModelPartBuilder.create().uv(0, 8).cuboid(-2.0F, -0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}

	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, float alpha) {
		core.render(matrices, vertexConsumer, light, overlay, color);
		shell.render(matrices, vertexConsumer, light, overlay, 0xFF000000);
	}

	@Override
	public void setAngles(UpgradeOrbEntityRenderState state) {
		super.setAngles(state);
	}
}