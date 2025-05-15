package net.code7y7.sorcerymod.perk;

import net.code7y7.sorcerymod.item.weapon.WeaponType;
import net.code7y7.sorcerymod.perk.elemental.ElementalPerk;
import net.code7y7.sorcerymod.perk.elemental.InfernoStrikePerk;
import net.code7y7.sorcerymod.perk.general.LifestealPerk;
import net.code7y7.sorcerymod.perk.weapon.LongSword.CleavingStrikePerk;
import net.code7y7.sorcerymod.perk.weapon.ShortSword.SwiftStrikesPerk;

import java.util.*;

public class PerkRegistry {
    private static final Map<WeaponType, List<WeaponPerk>> WEAPON_PERKS = new HashMap<>();
    private static final List<WeaponPerk> GENERAL_PERKS = new ArrayList<>();
    private static final List<ElementalPerk> ELEMENTAL_PERKS = new ArrayList<>();

    public static void initialize() {
        GENERAL_PERKS.add(new LifestealPerk());

        WEAPON_PERKS.put(WeaponType.SHORT_SWORD, Arrays.asList(
                new SwiftStrikesPerk()
        ));

        WEAPON_PERKS.put(WeaponType.LONG_SWORD, Arrays.asList(
                new CleavingStrikePerk()
        ));

        ELEMENTAL_PERKS.add(new InfernoStrikePerk());
    }

    public static List<WeaponPerk> getGeneralPerks() {
        return Collections.unmodifiableList(GENERAL_PERKS);
    }
    public static List<WeaponPerk> getWeaponSpecificPerks(WeaponType type) {
        return Collections.unmodifiableList(WEAPON_PERKS.getOrDefault(type, Collections.emptyList()));
    }
    public static List<ElementalPerk> getElementalPerks() {
        return Collections.unmodifiableList(ELEMENTAL_PERKS);
    }
}
