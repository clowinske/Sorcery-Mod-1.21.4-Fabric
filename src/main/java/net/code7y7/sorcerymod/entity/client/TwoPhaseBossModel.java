package net.code7y7.sorcerymod.entity.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class TwoPhaseBossModel extends Model {
    private final ModelPart orb;
    public static final EntityModelLayer BODY = new EntityModelLayer(SorceryMod.createIdentifier("body"), "main");


    public TwoPhaseBossModel(ModelPart root, Function<Identifier, RenderLayer> layerFactory) {
        super(root, layerFactory);
        this.orb = root.getChild("Orb");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("Orb", ModelPartBuilder.create().uv(0, 5).cuboid(8.0F, -4.0F, -2.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 20).cuboid(2.0F, -5.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F))
                .uv(10, 0).cuboid(3.0F, -6.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(10, 0).cuboid(3.0F, 1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(3.0F, -4.0F, 3.0F, 4.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(3.0F, -4.0F, -4.0F, 4.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 5).cuboid(1.0F, -4.0F, -2.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 17.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float r, float g, float b, float a) {
        orb.render(matrices, vertices, light, overlay);
    }
}
