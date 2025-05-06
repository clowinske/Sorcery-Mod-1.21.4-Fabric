package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class IroncladMetabolismOptions extends AbilityOptions {

    public IroncladMetabolismOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.IRONCLAD_METABOLISM;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.body.ironclad_metabolism");
    }
}
