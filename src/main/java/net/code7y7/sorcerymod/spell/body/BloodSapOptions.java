package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

public class BloodSapOptions extends AbilityOptions {

    public BloodSapOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.BLOOD_SAP;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.body.blood_sap");
    }
}
