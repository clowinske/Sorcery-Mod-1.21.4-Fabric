package net.code7y7.sorcerymod.client.render;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.entity.custom.FireballEntity;
import net.code7y7.sorcerymod.entity.custom.FiresprayEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class FiresprayRenderer extends EntityRenderer<FiresprayEntity, FireballEntityRenderState> {

    public FiresprayRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(FireballEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

    }


    @Override
    public FireballEntityRenderState createRenderState() {
        return new FireballEntityRenderState();
    }
}


