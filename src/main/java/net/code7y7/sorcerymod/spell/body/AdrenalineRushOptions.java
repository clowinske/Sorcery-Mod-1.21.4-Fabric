package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class AdrenalineRushOptions extends AbilityOptions {

    public AdrenalineRushOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.ADRENALINE_RUSH;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.body.adrenaline_rush");
    }
}
