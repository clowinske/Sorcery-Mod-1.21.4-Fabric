package net.code7y7.sorcerymod.effect;

import net.code7y7.sorcerymod.SorceryMod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> STUNNED = registerStatusEffect("stunned",
        new StunnedEffect(StatusEffectCategory.HARMFUL, 0xbae7ff)
            .addAttributeModifier(EntityAttributes.MOVEMENT_SPEED,
                Identifier.of(SorceryMod.MOD_ID,"stunned"),
        -0.8f,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(EntityAttributes.JUMP_STRENGTH,
                Identifier.of(SorceryMod.MOD_ID,"stunned"),
                -1.0f,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SorceryMod.MOD_ID, name), statusEffect);
    }

    public static void registerEffects() {

    }
}
