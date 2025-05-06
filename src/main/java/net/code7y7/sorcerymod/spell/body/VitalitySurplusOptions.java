package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class VitalitySurplusOptions extends AbilityOptions {

    public VitalitySurplusOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.VITALITY_SURPLUS;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.body.vitality_surplus");
    }
}
