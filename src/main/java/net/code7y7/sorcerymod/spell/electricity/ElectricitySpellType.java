package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellType;

import java.util.ArrayList;
import java.util.List;

public class ElectricitySpellType extends SpellType {

    @Override
    public List<Ability> getAbilities() {
        List<Ability> abilities = new ArrayList<>();
        abilities.add(new Lightning());
        abilities.add(new LightningBolt());
        return abilities;
    }
}
