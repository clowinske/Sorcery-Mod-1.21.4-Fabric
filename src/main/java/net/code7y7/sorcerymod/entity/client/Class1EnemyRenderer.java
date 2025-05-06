package net.code7y7.sorcerymod.entity.client;

import net.code7y7.sorcerymod.client.render.FireballEntityRenderState;
import net.code7y7.sorcerymod.client.render.FireballModel;
import net.code7y7.sorcerymod.entity.enemy.Class1Enemy;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class Class1EnemyRenderer extends EntityRenderer<Class1Enemy, FireballEntityRenderState> {
    ModelPart root;
    FireballModel model;
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/entity/steve.png");

    public Class1EnemyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        root = ctx.getPart(FireballModel.FIREBALL);
        this.model = new FireballModel(root);
    }

    @Override
    public FireballEntityRenderState createRenderState() {
        return new FireballEntityRenderState();
    }

    @Override
    public void render(FireballEntityRenderState livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }
}
