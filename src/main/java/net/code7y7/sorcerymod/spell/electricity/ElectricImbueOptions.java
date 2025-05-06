package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class ElectricImbueOptions extends AbilityOptions {

    public ElectricImbueOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.ELECTRIC_IMBUE;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.electricity.electric_imbue");
    }
}
