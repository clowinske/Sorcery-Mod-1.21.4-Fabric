package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.spell.AbilityOptionType;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class FireballRicochetOptions extends AbilityOptions {
    double bounces = 1;

    public FireballRicochetOptions() {
        abilityOptions.add(bounces);
    }

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
            case 0 -> maxRicochets(stack);
            default -> 1;
        };
    }
    public double getMinValue(int index, ItemStack stack){
        return switch (index){
            case 0 -> 1;
            default -> 1;
        };
    }
    @Override
    public Text getName(int index) {
        return switch (index){
            case 0 -> Text.translatable("crystal_options.option.fireball_ricochet.ricochet_bounces");
            default -> Text.translatable("crystal_options.option.default");
        };
    }
    @Override
    public Text getName() {
        return Text.translatable("crystal_options.ability.fire.fireball_ricochet");
    }
    @Override
    public AbilityOptionType getType() {
        return AbilityOptionType.FIREBALL_RICOCHET;
    }
    private int maxRicochets(ItemStack stack){
        int tier = ((ElementalCrystalItem) stack.getItem()).getTier(stack);
        return tier;
    }
}
