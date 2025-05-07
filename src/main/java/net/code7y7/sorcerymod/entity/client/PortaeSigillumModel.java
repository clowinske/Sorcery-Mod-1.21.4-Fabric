package net.code7y7.sorcerymod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class PortaeSigillumModel extends Model {
	public final ModelPart layer1;
	public final ModelPart layer2;
	public final ModelPart layer3;
	public PortaeSigillumModel(ModelPart root) {
		super(root, RenderLayer::getEntityTranslucent);
		this.layer1 = root.getChild("layer1");
		this.layer2 = root.getChild("layer2");
		this.layer3 = root.getChild("layer3");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData layer1 = modelPartData.addChild("layer1",
				ModelPartBuilder.create().uv(-32, 64).cuboid(-16.0F, -0.01F, -16.0F, 32.0F, 0.0F, 32.0F),
				ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData layer2 = modelPartData.addChild("layer2",
				ModelPartBuilder.create().uv(-32, 32).cuboid(-16.0F, 0.5F, -16.0F, 32.0F, 0.0F, 32.0F),
				ModelTransform.pivot(0.0F, 23.0F, 0.0F));

		ModelPartData layer3 = modelPartData.addChild("layer3",
				ModelPartBuilder.create().uv(-32, 0).cuboid(-16.0F, 0.0F, -16.0F, 32.0F, 0.0F, 32.0F),
				ModelTransform.pivot(0.0F, 23.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 96);
	}

	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float r, float g, float b, float a) {
		layer1.render(matrices, vertices, light, overlay);
		layer2.render(matrices, vertices, light, overlay);
		layer3.render(matrices, vertices, light, overlay);
	}
}