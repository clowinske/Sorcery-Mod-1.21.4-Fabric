package net.code7y7.sorcerymod.spell;

import com.mojang.serialization.Codec;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum AbilityOptionType {
    FIREBALL("fireball", () -> AbilityOptions.BASE_CODEC),
    FIRE_BURST("fire_burst", () -> AbilityOptions.BASE_CODEC),
    FLAMING_IMBUE("flaming_imbue", () -> AbilityOptions.BASE_CODEC),
    SCORCH("scorch", () -> AbilityOptions.BASE_CODEC),
    FIREBALL_RICOCHET("fireball_ricochet", () -> AbilityOptions.BASE_CODEC),

    LIGHTNING("lightning", () -> AbilityOptions.BASE_CODEC),
    LIGHTNING_BOLT("lightning_bolt", () -> AbilityOptions.BASE_CODEC),
    CHAIN_LIGHTNING("chain_lightning", () -> AbilityOptions.BASE_CODEC),
    CHAIN_LIGHTNING_BOLT("chain_lightning_bolt", () -> AbilityOptions.BASE_CODEC),
    ELECTRIC_IMBUE("electric_imbue", () -> AbilityOptions.BASE_CODEC),
    BLINK("blink", () -> AbilityOptions.BASE_CODEC),

    PUSH("push", () -> AbilityOptions.BASE_CODEC),
    PULL("pull", () -> AbilityOptions.BASE_CODEC),

    ADRENALINE_RUSH("adrenaline_rush", () -> AbilityOptions.BASE_CODEC),
    BLOOD_SAP("blood_sap", () -> AbilityOptions.BASE_CODEC),
    BLOOD_SAP_MASTERY("blood_sap_mastery", () -> AbilityOptions.BASE_CODEC),
    IRONCLAD_METABOLISM("ironclad_metabolism", () -> AbilityOptions.BASE_CODEC),
    SANGUINE_BOND("sanguine_bond", () -> AbilityOptions.BASE_CODEC),
    VITALITY_SURPLUS("vitality_surplus", () -> AbilityOptions.BASE_CODEC),
    BODY_MASTERY("body_mastery", () -> AbilityOptions.BASE_CODEC);


    private final String id;
    private final Supplier<Codec<? extends AbilityOptions>> codecSupplier;

    AbilityOptionType(String id, Supplier<Codec<? extends AbilityOptions>> codecSupplier) {
        this.id = id;
        this.codecSupplier = codecSupplier;
    }

    public String getId() {
        return id;
    }

    public Codec<? extends AbilityOptions> getCodec() {
        return codecSupplier.get();
    }

    public static final Map<String, AbilityOptionType> BY_ID = Arrays.stream(values())
            .collect(Collectors.toMap(AbilityOptionType::getId, Function.identity()));
}

