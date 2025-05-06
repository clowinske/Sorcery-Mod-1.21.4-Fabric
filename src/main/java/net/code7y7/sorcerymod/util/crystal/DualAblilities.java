package net.code7y7.sorcerymod.util.crystal;

import org.apache.commons.lang3.tuple.Triple;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DualAblilities {
    public static final Map<Triple<CrystalData, CrystalData, Integer>, List<String>> DUAL_ABILITIES = new HashMap<>();

    static {
        // Example: Fire + Electricity at tier 1 unlocks "Plasma Arc"
        DUAL_ABILITIES.put(Triple.of(CrystalData.FIRE, CrystalData.ELECTRICITY, 1), List.of("plasma_arc"));

        // You can add as many as you want
        DUAL_ABILITIES.put(Triple.of(CrystalData.GRAVITY, CrystalData.SOUL, 2), List.of("soul_anchor"));
    }

    public static List<String> getDualAbilities(CrystalData a, CrystalData b, int tier) {
        // Try both orders: (A,B) and (B,A)
        List<String> list = DUAL_ABILITIES.get(Triple.of(a, b, tier));
        if (list == null) {
            list = DUAL_ABILITIES.get(Triple.of(b, a, tier));
        }
        return list != null ? list : Collections.emptyList();
    }
}
