package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class SanguineBondOptions extends AbilityOptions {

    public SanguineBondOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.SANGUINE_BOND;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.body.sanguine_bond");
    }
}
