package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class ScorchOptions extends AbilityOptions {

    public ScorchOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.SCORCH;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.fire.scorch");
    }
}
