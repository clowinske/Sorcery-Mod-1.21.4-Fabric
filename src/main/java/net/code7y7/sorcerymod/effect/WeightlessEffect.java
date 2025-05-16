package net.code7y7.sorcerymod.effect;

import net.code7y7.sorcerymod.network.IsWeightlessS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class WeightlessEffect extends StatusEffect {
    Vec3d velocity;
    LivingEntity activeEntity;
    boolean isPlayer = false;

    protected WeightlessEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity.horizontalCollision) {
            velocity = entity.getVelocity();
            System.out.println("Horizontal collision!");
        } else if (entity.verticalCollision) {
            velocity = entity.getVelocity();
            System.out.println("Vertical collision!");
        } else {
            entity.setVelocity(velocity);
        }

        if (isPlayer) {
            ServerPlayerEntity player = (ServerPlayerEntity)(entity);
            ServerPlayNetworking.send(player, new IsWeightlessS2CPayload(true));
        }


//        if (getFadeTicks() % 40 == 0) {
//            System.out.println(entity.getName() + " velocity: " + entity.getVelocity().toString()  + ", should be: " + velocity.toString());
//        }

        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        velocity = entity.getVelocity();
        activeEntity = entity;
        activeEntity.setNoDrag(true);
        activeEntity.setNoGravity(true);

        isPlayer = entity.isPlayer();

    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        activeEntity.setNoDrag(false);
        activeEntity.setNoGravity(false);

        if (isPlayer) {
            ServerPlayerEntity player = (ServerPlayerEntity)(activeEntity);
            ServerPlayNetworking.send(player, new IsWeightlessS2CPayload(false));
        }

        super.onRemoved(attributeContainer);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}