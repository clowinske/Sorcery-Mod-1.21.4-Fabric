package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ChainLightningBoltOptions extends AbilityOptions {
    double defaultChains = 1;

    public ChainLightningBoltOptions() {
        abilityOptions.add(defaultChains);
    }

    public double getBoltChains(){
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
            case 0 -> LightningBolt.getMaxChains(stack);
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
            case 0 -> Text.translatable("crystal_options.option.chain_lightning_bolt.chain_count");
            default -> Text.translatable("crystal_options.option.default");
        };
    }

    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.electricity.chain_lightning_bolt");
    }
}
