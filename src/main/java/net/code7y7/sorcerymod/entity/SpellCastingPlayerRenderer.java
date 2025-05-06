package net.code7y7.sorcerymod.entity;

import net.code7y7.sorcerymod.spell.SpellPose;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public class SpellCastingPlayerRenderer extends PlayerEntityRenderer {

    public SpellCastingPlayerRenderer(EntityRendererFactory.Context ctx, boolean slim) {
        super(ctx, slim);
        this.model = new SpellCastingPlayerModel<>(ctx.getPart(EntityModelLayers.PLAYER), slim);
    }

    @Override
    public void render(PlayerEntityRenderState livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        SpellCastingPlayerModel<ClientPlayerEntity> model = (SpellCastingPlayerModel<ClientPlayerEntity>) this.model;
        //ClientPlayerEntity playerEntity = livingEntityRenderState.getEntity();
        //SpellPose currentPose = getPlayerSpellPose(playerEntity);
        //model.setSpellCastingPose(currentPose);
        
        super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }
    
    private SpellPose getPlayerSpellPose(AbstractClientPlayerEntity player) {
        return SpellPose.NONE;
    }
}
