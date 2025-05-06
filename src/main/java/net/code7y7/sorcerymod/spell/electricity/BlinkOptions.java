package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlinkOptions extends AbilityOptions {
    double defaultBlinkDistance = 5;
    double defaultBlinkType = 2;
    public BlinkOptions() {
        abilityOptions.add(defaultBlinkDistance);
        abilityOptions.add(defaultBlinkType);
    }
    public double getBlinkDistance(){
        return abilityOptions.get(0);
    }
    public double getBlinkType(){
        return abilityOptions.get(1);
    }

    @Override
    public int getOptionType(int index){
        //0 = double slider, 1 = int slider, 2 = checkbox
        return switch (index){
            case 0 -> 0;
            case 1 -> 1;
            default -> 0;
        };
    }

    @Override
    public double getMaxValue(int index, ItemStack stack) {
        return switch (index){
            case 0 -> Blink.getMaxDistance(stack);
            case 1 -> Blink.getBlinkTypeNum()-1;
            default -> 1;
        };
    }

    @Override
    public double getMinValue(int index, ItemStack stack) {
        return switch (index){
            case 0 -> 0;
            case 1 -> 0;
            default -> 0;
        };
    }

    @Override
    public Text getName(int index) {
        return switch (index){
          case 0 -> Text.translatable("crystal_options.option.blink.blink_distance");
          case 1 -> Text.translatable("crystal_options.option.blink.blink_type");
          default -> Text.translatable("crystal_options.option.default");
        };
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.BLINK;
    }

    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.electricity.blink");
    }
}
