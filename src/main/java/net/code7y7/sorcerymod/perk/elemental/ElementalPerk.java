package net.code7y7.sorcerymod.perk.elemental;

import net.code7y7.sorcerymod.perk.WeaponPerk;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.entity.Entity;

public abstract class ElementalPerk implements WeaponPerk {
    private final CrystalData element;

    protected ElementalPerk(CrystalData element) {
        this.element = element;
    }
    public abstract void applyElementalEffect(Entity target);
}
