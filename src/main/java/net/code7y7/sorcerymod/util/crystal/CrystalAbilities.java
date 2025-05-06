package net.code7y7.sorcerymod.util.crystal;

import com.mojang.serialization.Codec;

import java.util.*;

public class CrystalAbilities {
    public static final List<String> FIRE = Arrays.asList
            ("fireball","flaming_imbue","fireball_ricochet","scorch",
            "fire_burst","fire_shield","ignition","fireball_explosion",
            "meteor","fire_mastery","firey_blow","detonation");
    public static final List<String> ELECTRICITY = Arrays.asList
            ("lightning", "electric_imbue", "supercharge", "electrorecovery",
            "lightning_bolt", "chain_lightning", "blink", "arc_homing",
            "electric_laser", "arc_whip", "chain_lightning_bolt", "electric_mastery");
    public static final List<String> GRAVITY = Arrays.asList
            ("push","gravity_imbue","weightless","heavy_blow",
            "pull","repulse","gravitational_anchor","graviton_burst",
            "singularity","anti-gravity_field","gravity_mastery","anti-gravity_mastery");
    public static final List<String> BODY = Arrays.asList
            ("adrenaline_rush","ironclad_metabolism","body3","body4",
            "blood_sap","blood_sap_mastery","vitality_surplus","sanguine_bond",
            "body9","body10","body11","body_mastery");
    public static final List<String> MIND = Arrays.asList
            ("mind1","mind2","mind3","mind4",
            "mind5","mind6","mind7","mind8",
            "mind9","mind10","mind11","mind12");
    public static final List<String> SOUL = Arrays.asList
            ("spectral_chains","soul_imbue","soul3","soul4",
            "soul_drain","revenants_blessing","soul7","soul8",
            "wraith_form","spectral_vision","soul_mastery","soul12");
    public static final List<String> ECLIPSE = Arrays.asList
            ("mind1","mind2","mind3","mind4",
                    "mind5","mind6","mind7","mind8",
                    "mind9","mind10","mind11","mind12");
    public static final List<String> DISCORD = Arrays.asList
            ("mind1","mind2","mind3","mind4",
                    "mind5","mind6","mind7","mind8",
                    "mind9","mind10","mind11","mind12");
    public static final List<String> RADIANT = Arrays.asList
            ("mind1","mind2","mind3","mind4",
                    "mind5","mind6","mind7","mind8",
                    "mind9","mind10","mind11","mind12");
    public static final List<Map<Map<CrystalData, CrystalData>, String>> DUAL = Arrays.asList(
            Collections.singletonMap(
                    Collections.singletonMap(CrystalData.BODY, CrystalData.MIND),
                    "astral_projection"
            )
    );
}
