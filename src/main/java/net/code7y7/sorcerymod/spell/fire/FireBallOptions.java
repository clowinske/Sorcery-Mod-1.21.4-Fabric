package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class FireBallOptions extends AbilityOptions {

    public FireBallOptions() {
    }

    public int getOptionType(int index){
        //0 = double slider, 1 = int slider, 2 = checkbox
        return switch (index){
            default -> 0;
        };
    }

    @Override
    public double getMaxValue(int index, ItemStack stack) {
        return switch (index){
            default -> 1;
        };
    }
    @Override
    public Text getName(int index) {
        return switch (index){
            default -> Text.translatable("crystal_options.option.default");
        };
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.FIREBALL;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.fire.fireball");
    }
}
