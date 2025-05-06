package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ChainLightningOptions extends AbilityOptions {
    double defaultChains = 1;

    public ChainLightningOptions() {
        abilityOptions.add(defaultChains);
    }

    public double getChains(){
        return abilityOptions.get(0);
    }
    @Override
    public int getOptionType(int index){
        //0 = double slider, 1 = int slider, 2 = checkbox
        return switch (index){
            case 0 -> 1;
            default -> 0;
        };
    }

    @Override
    public double getMaxValue(int index, ItemStack stack) {
        return switch (index){
            case 0 -> Lightning.getMaxChains(stack);
            default -> 1;
        };
    }
    @Override
    public double getMinValue(int index, ItemStack stack) {
        return switch (index){
            case 0 -> 1;
            default -> 0;
        };
    }
    @Override
    public Text getName(int index) {
        return switch (index){
            case 0 -> Text.translatable("crystal_options.option.chain_lightning.chain_count");
            default -> Text.translatable("crystal_options.option.default");
        };
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.CHAIN_LIGHTNING;
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.electricity.chain_lightning");
    }
}
