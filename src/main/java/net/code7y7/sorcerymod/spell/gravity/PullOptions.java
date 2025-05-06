package net.code7y7.sorcerymod.spell.gravity;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class PullOptions extends AbilityOptions {
    public PullOptions(){
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.PULL;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.gravity.pull");
    }
}
