package net.code7y7.sorcerymod.util.crystal;

import java.util.List;

public record DualSpell(CrystalData elementA, CrystalData elementB, int tier, String abilityId) {

    public static List<DualSpell> dualSpells = List.of(
            new DualSpell(CrystalData.FIRE, CrystalData.ELECTRICITY, 1, "firestorm_burst"),
            new DualSpell(CrystalData.GRAVITY, CrystalData.SOUL, 2, "blackhole_ritual")
            // etc.
    );
}

