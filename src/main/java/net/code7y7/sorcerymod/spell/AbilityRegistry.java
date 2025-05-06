package net.code7y7.sorcerymod.spell;

import net.code7y7.sorcerymod.spell.body.AdrenalineRush;
import net.code7y7.sorcerymod.spell.body.BloodSap;
import net.code7y7.sorcerymod.spell.electricity.Lightning;
import net.code7y7.sorcerymod.spell.electricity.LightningBolt;
import net.code7y7.sorcerymod.spell.fire.FireBurst;
import net.code7y7.sorcerymod.spell.fire.Fireball;
import net.code7y7.sorcerymod.spell.gravity.Pull;
import net.code7y7.sorcerymod.spell.gravity.Push;
import net.code7y7.sorcerymod.spell.gravity.Weightless;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AbilityRegistry {
    public static final Map<String, Ability> ABILITIES = new HashMap<>();

    //fire
    public static final Ability FIRE_BALL = register("fireball", new Fireball());
    public static final Ability FIRE_BURST = register("fire_burst", new FireBurst());

    //electricity
    public static final Ability LIGHTNING = register("lightning", new Lightning());
    public static final Ability LIGHTNING_BOLT = register("lightning_bolt", new LightningBolt());

    //gravity
    public static final Ability PUSH = register("push", new Push());
    public static final Ability PULL = register("pull", new Pull());
    public static final Ability WEIGHTLESS = register("weightless", new Weightless());

    //body
    public static final Ability ADRENALINE_RUSH = register("adrenaline_rush", new AdrenalineRush());
    public static final Ability BLOOD_SAP = register("blood_sap", new BloodSap());

    public static Ability register(String name, Ability ability) {
        ABILITIES.put(name, ability);
        return ability;
    }

    public static void registerAbilities(){

    }

    public static Optional<Ability> get(String name) {
        return Optional.ofNullable(ABILITIES.get(name));
    }
}
