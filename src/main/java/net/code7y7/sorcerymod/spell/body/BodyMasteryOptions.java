package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class BodyMasteryOptions extends AbilityOptions {

    public BodyMasteryOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.BODY_MASTERY;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.body.body_mastery");
    }
}
