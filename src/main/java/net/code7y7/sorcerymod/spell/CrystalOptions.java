package net.code7y7.sorcerymod.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.code7y7.sorcerymod.component.CrystalUnlockedAbilities;
import net.code7y7.sorcerymod.spell.body.BodyOptions;
import net.code7y7.sorcerymod.spell.electricity.ElectricityOptions;
import net.code7y7.sorcerymod.spell.fire.FireOptions;
import net.code7y7.sorcerymod.spell.gravity.GravityOptions;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public abstract class CrystalOptions {


    public static final Codec<CrystalOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(CrystalOptions::getTypeAsString),
            Codec.unboundedMap(Codec.STRING, AbilityOptions.DISPATCH_CODEC)
                    .fieldOf("options").forGetter(CrystalOptions::getOptions)
    ).apply(instance, (typeString, options) -> {
        CrystalOptionType type = CrystalOptionType.BY_ID.get(typeString);
        if (type == null) {
            throw new IllegalArgumentException("Unknown CrystalOptions type: " + typeString);
        }
        CrystalOptions result = switch (type) {
            case BODY -> new BodyOptions();
            case FIRE -> new FireOptions();
            case ELECTRICITY -> new ElectricityOptions();
            case GRAVITY -> new GravityOptions();
            case MIND -> new MindOptions();
            case SOUL -> new SoulOptions();
            case ECLIPSE -> new EclipseOptions();
            case DISCORD -> new DiscordOptions();
            case RADIANT -> new RadiantOptions();
        };
        result.spellOptions.putAll(options);
        return result;
    }));

    public Map<String, AbilityOptions> spellOptions = new HashMap<>();
    protected CrystalData type;

    public CrystalOptions(CrystalData type) {
        this.type = type;
    }

    public CrystalData getType() {
        return type;
    }

    public String getTypeAsString() {
        return type.getName();
    }

    public Map<String, AbilityOptions> getOptions() {
        return spellOptions;
    }

    public static CrystalOptions createOptionsFor(CrystalData type){
        return switch(type){
            case FIRE -> new FireOptions();
            case ELECTRICITY -> new ElectricityOptions();
            case GRAVITY -> new GravityOptions();
            case MIND -> new MindOptions();
            case BODY -> new BodyOptions();
            case SOUL -> new SoulOptions();
            case ECLIPSE -> new EclipseOptions();
            case DISCORD -> new DiscordOptions();
            case RADIANT -> new RadiantOptions();
            default -> null;
        };

    }

    public Text getName(String option) {
        return Text.translatable("crystal_options.ability.default");
    }
}
