package net.code7y7.sorcerymod.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.code7y7.sorcerymod.spell.body.*;
import net.code7y7.sorcerymod.spell.electricity.*;
import net.code7y7.sorcerymod.spell.fire.*;
import net.code7y7.sorcerymod.spell.gravity.PullOptions;
import net.code7y7.sorcerymod.spell.gravity.PushOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityOptions {

    public static final Codec<AbilityOptions> BASE_CODEC = Codec.DOUBLE.listOf()
            .xmap(AbilityOptions::new, o -> o.abilityOptions);

    public static final Codec<AbilityOptions> DISPATCH_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(opt -> opt.getType().getId()),
            Codec.DOUBLE.listOf().fieldOf("ability_options").forGetter(opt -> opt.abilityOptions)
    ).apply(instance, (typeId, options) -> {
        AbilityOptionType type = AbilityOptionType.BY_ID.get(typeId);
        if (type == null) return new AbilityOptions(options); // fallback
        AbilityOptions result;
        result = switch (type) {
            case FIREBALL -> new FireBallOptions();
            case FIRE_BURST -> new FireBurstOptions();
            case FLAMING_IMBUE -> new FlamingImbueOptions();
            case SCORCH -> new ScorchOptions();
            case FIREBALL_RICOCHET -> new FireballRicochetOptions();

            case LIGHTNING -> new LightningOptions();
            case LIGHTNING_BOLT -> new LightningBoltOptions();
            case CHAIN_LIGHTNING -> new ChainLightningOptions();
            case CHAIN_LIGHTNING_BOLT -> new ChainLightningBoltOptions();
            case ELECTRIC_IMBUE -> new ElectricImbueOptions();
            case BLINK -> new BlinkOptions();

            case PUSH -> new PushOptions();
            case PULL -> new PullOptions();

            case ADRENALINE_RUSH -> new AdrenalineRushOptions();
            case BLOOD_SAP -> new BloodSapOptions();
            case BLOOD_SAP_MASTERY -> new BloodSapMasteryOptions();
            case IRONCLAD_METABOLISM -> new IroncladMetabolismOptions();
            case SANGUINE_BOND -> new SanguineBondOptions();
            case VITALITY_SURPLUS -> new VitalitySurplusOptions();
            case BODY_MASTERY -> new BodyMasteryOptions();
            default -> new AbilityOptions();
        };
        result.abilityOptions = options;
        return result;
    }));

    public List<Double> abilityOptions;
    public AbilityOptionType type;

    public AbilityOptions() {
        this.abilityOptions = new ArrayList<>();
    }

    public AbilityOptions(List<Double> abilityOptions) {
        this.abilityOptions = abilityOptions;
    }

    public int getOptionType(int index) {
        // 0 = double slider, 1 = int slider, 2 = checkbox
        return 0;
    }

    public double getMaxValue(int index, ItemStack stack) {
        return 1;
    }
    public AbilityOptionType getType(){
        return type;
    }

    public double getMinValue(int index, ItemStack stack) {
        return 0;
    }

    public Text getName(int index) {
        return Text.translatable("crystal_options.option.default");
    }

    public Text getName() {
        return Text.translatable("crystal_options.ability.default");
    }
}
