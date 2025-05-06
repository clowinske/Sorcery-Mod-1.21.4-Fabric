package net.code7y7.sorcerymod.entity.client;

import net.code7y7.sorcerymod.client.render.FireballEntityRenderState;
import net.code7y7.sorcerymod.client.render.FireballModel;
import net.code7y7.sorcerymod.entity.enemy.Class1Enemy;
import net.code7y7.sorcerymod.entity.enemy.TwoPhaseBoss;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TwoPhaseBossRenderer extends EntityRenderer<TwoPhaseBoss, FireballEntityRenderState> {
    ModelPart root;
    FireballModel model;
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/entity/steve.png");

    public TwoPhaseBossRenderer(EntityRendererFactory.Context ctx) {
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
