package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellType;

import java.util.ArrayList;
import java.util.List;

public class BodySpellType extends SpellType {

    @Override
    public List<Ability> getAbilities() {
        List<Ability> abilities = new ArrayList<>();
        abilities.add(new AdrenalineRush());
        abilities.add(new BloodSap());
        return abilities;
    }
}
