package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class FireBurstOptions extends AbilityOptions {
    double crouchArms = 1;

    public FireBurstOptions() {
        abilityOptions.add(crouchArms);
    }

    public int getOptionType(int index){
        //0 = double slider, 1 = int slider, 2 = checkbox
        return switch (index){
            case 0 -> 2;
            default -> 0;
        };
    }

    @Override
    public double getMaxValue(int index, ItemStack stack) {
        return switch (index){
            case 0 -> 1;
            default -> 1;
        };
    }
    @Override
    public Text getName(int index) {
        return switch (index){
            case 0 -> Text.translatable("crystal_options.option.fire_spray.crouch_arms");
            default -> Text.translatable("crystal_options.option.default");
        };
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.FIRE_BURST;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.fire.fire_burst");
    }
}
