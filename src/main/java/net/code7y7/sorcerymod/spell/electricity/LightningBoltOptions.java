package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class LightningBoltOptions extends AbilityOptions {

    public LightningBoltOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.LIGHTNING_BOLT;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.electricity.lightning_bolt");
    }
}
