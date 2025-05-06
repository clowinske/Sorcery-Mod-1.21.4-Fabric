package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class FlamingImbueOptions extends AbilityOptions {

    public FlamingImbueOptions() {
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.FLAMING_IMBUE;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.fire.flaming_imbue");
    }
}
