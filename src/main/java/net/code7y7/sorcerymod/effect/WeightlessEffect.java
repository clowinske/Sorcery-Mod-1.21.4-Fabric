package net.code7y7.sorcerymod.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class WeightlessEffect extends StatusEffect {
    protected WeightlessEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        return super.applyUpdateEffect(world, entity, 0);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return super.canApplyUpdateEffect(duration, 0);
    }
}