package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellType;

import java.util.ArrayList;
import java.util.List;

public class FireSpellType extends SpellType {

    @Override
    public List<Ability> getAbilities() {
        List<Ability> abilities = new ArrayList<>();
        abilities.add(new Fireball());
        abilities.add(new FireBurst());
        return abilities;
    }
}
