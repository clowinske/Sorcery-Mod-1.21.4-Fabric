package net.code7y7.sorcerymod.entity.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SpellModel extends Model {
    public static final Identifier TEXTURE = Identifier.of(SorceryMod.MOD_ID, "textures/entity/spell_charge.png");
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(SorceryMod.createIdentifier("spell"), "main");
    public final ModelPart innerCube;
    public final ModelPart middleCube;
    public final ModelPart outerCube;

    public SpellModel(ModelPart root) {
        super(root, RenderLayer::getEntityTranslucent);
        this.outerCube = root.getChild("outerCube");
        this.middleCube = root.getChild("middleCube");
        this.innerCube = root.getChild("innerCube");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("outerCube",
                ModelPartBuilder.create()
                        .uv(0, 10)
                        .cuboid(-2.0F, -9.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        // MIDDLE CUBE
        modelPartData.addChild("middleCube",
                ModelPartBuilder.create()
                        .uv(0, 4)
                        .cuboid(-1.5F, -8.5F, -1.5F, 3.0F, 3.0F, 3.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        // INNER CUBE
        modelPartData.addChild("innerCube",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float r, float g, float b, float a) {
        outerCube.render(matrices, vertices, light, overlay);
        middleCube.render(matrices, vertices, light, overlay);
        innerCube.render(matrices, vertices, light, overlay);

    }
}

