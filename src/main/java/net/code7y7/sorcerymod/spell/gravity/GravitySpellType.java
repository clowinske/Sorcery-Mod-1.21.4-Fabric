package net.code7y7.sorcerymod.spell.gravity;

import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellType;

import java.util.ArrayList;
import java.util.List;

public class GravitySpellType extends SpellType {

    @Override
    public List<Ability> getAbilities() {
        List<Ability> abilities = new ArrayList<>();
        abilities.add(new Push());
        abilities.add(new Pull());
        abilities.add(new Weightless());
        return abilities;
    }
}
