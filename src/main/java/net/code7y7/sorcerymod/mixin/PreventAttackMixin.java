package net.code7y7.sorcerymod.mixin;

import net.code7y7.sorcerymod.PlayerData;
import net.code7y7.sorcerymod.SorceryModClient;
import net.code7y7.sorcerymod.StateSaverAndLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PreventAttackMixin {
	@Inject(at = @At("HEAD"), method = "swingHand", cancellable = true)
	private void preventSwing(Hand hand, CallbackInfo info) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if(entity instanceof ServerPlayerEntity serverPlayer){
			if (StateSaverAndLoader.getPlayerState(serverPlayer).hasCrystal && (StateSaverAndLoader.getPlayerState(serverPlayer).selectMode || StateSaverAndLoader.getPlayerState(serverPlayer).castMode)) {
				info.cancel();
			}
		}
	}
}