package net.code7y7.sorcerymod.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class WeightlessEffect extends StatusEffect {
    Vec3d velocity;
    LivingEntity activeEntity;

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

    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        activeEntity.setNoDrag(false);
        activeEntity.setNoGravity(false);
        super.onRemoved(attributeContainer);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}