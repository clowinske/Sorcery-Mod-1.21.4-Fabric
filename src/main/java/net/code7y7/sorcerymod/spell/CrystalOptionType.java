package net.code7y7.sorcerymod.spell;

import com.mojang.serialization.Codec;
import net.code7y7.sorcerymod.spell.body.BodyOptions;
import net.code7y7.sorcerymod.spell.electricity.ElectricityOptions;
import net.code7y7.sorcerymod.spell.fire.FireOptions;
import net.code7y7.sorcerymod.spell.gravity.GravityOptions;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CrystalOptionType {
    BODY("body", Codec.unit(new BodyOptions())),
    FIRE("fire", Codec.unit(new FireOptions())),
    ELECTRICITY("electricity", Codec.unit(new ElectricityOptions())),
    GRAVITY("gravity", Codec.unit(new GravityOptions())),
    MIND("mind", Codec.unit(new MindOptions())),
    SOUL("soul", Codec.unit(new SoulOptions())),
    ECLIPSE("eclipse", Codec.unit(new EclipseOptions())),
    DISCORD("discord", Codec.unit(new DiscordOptions())),
    RADIANT("radiant", Codec.unit(new RadiantOptions()));

    private final String id;
    private final Codec<? extends CrystalOptions> codec;

    CrystalOptionType(String id, Codec<? extends CrystalOptions> codec) {
        this.id = id;
        this.codec = codec;
    }

    public String getId() {
        return id;
    }

    public Codec<? extends CrystalOptions> getCodec() {
        return codec;
    }

    public static final Map<String, CrystalOptionType> BY_ID = Arrays.stream(values())
            .collect(Collectors.toMap(CrystalOptionType::getId, Function.identity()));
}
