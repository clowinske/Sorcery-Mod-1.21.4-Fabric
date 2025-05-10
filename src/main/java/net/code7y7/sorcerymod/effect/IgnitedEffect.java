package net.code7y7.sorcerymod.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

public class IgnitedEffect extends StatusEffect {
    protected IgnitedEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }


    //amplifier 0 behaves normally, amplifier 1 will spread this effect to other entities.
    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (!entity.isSubmergedInWater()) {
            entity.setOnFireFor(1.0f);

            if (amplifier == 1) {
                world.getOtherEntities(entity, entity.getBoundingBox().expand(0.1), e -> e instanceof LivingEntity)
                        .forEach(otherEntity -> {
                            LivingEntity target = (LivingEntity) otherEntity;
                            if (!target.hasStatusEffect(ModEffects.IGNITED)) {
                                target.addStatusEffect(new StatusEffectInstance(ModEffects.IGNITED, 100, 1)); // 200 ticks (10s) duration, amplifier 0
                            }
                        });
            }
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
