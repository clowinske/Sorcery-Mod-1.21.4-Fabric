package net.code7y7.sorcerymod.mixin;

import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.util.RenderStateAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void sorcery$injectUpdateRenderState(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState state, float f, CallbackInfo ci) {

        ((RenderStateAccess)state).sorcerymod$setLeftHandCharge(SpellHelper.getHandCharge(abstractClientPlayerEntity, "left"));
        System.out.println(abstractClientPlayerEntity.getName() + ", " + ((RenderStateAccess)state).sorcerymod$getLeftHandCharge());
        ((RenderStateAccess)state).sorcerymod$setRightHandCharge(SpellHelper.getHandCharge(abstractClientPlayerEntity, "right"));
        ((RenderStateAccess)state).sorcerymod$setLeftHandSpell(SpellHelper.getHandSpell(abstractClientPlayerEntity, "left").getInt());
        ((RenderStateAccess)state).sorcerymod$setRightHandSpell(SpellHelper.getHandSpell(abstractClientPlayerEntity, "right").getInt());
        ((RenderStateAccess)state).sorcerymod$setCorruption(SpellHelper.getCorruption(abstractClientPlayerEntity));
    }
}
