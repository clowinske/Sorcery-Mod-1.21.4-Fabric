package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LightningOptions extends AbilityOptions {

    public LightningOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.LIGHTNING;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.electricity.lightning");
    }
}
