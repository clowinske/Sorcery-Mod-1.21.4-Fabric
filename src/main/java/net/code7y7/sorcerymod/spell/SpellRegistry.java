package net.code7y7.sorcerymod.spell;

import net.code7y7.sorcerymod.spell.body.BodySpellType;
import net.code7y7.sorcerymod.spell.discord.DiscordSpellType;
import net.code7y7.sorcerymod.spell.eclipse.EclipseSpellType;
import net.code7y7.sorcerymod.spell.electricity.ElectricitySpellType;
import net.code7y7.sorcerymod.spell.fire.FireSpellType;
import net.code7y7.sorcerymod.spell.gravity.GravitySpellType;
import net.code7y7.sorcerymod.spell.mind.MindSpellType;
import net.code7y7.sorcerymod.spell.radiant.RadiantSpellType;
import net.code7y7.sorcerymod.spell.soul.SoulSpellType;
import net.code7y7.sorcerymod.util.crystal.CrystalData;

import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {
    private static final Map<String, SpellType> SPELL_TYPES = new HashMap<>();

    public static void registerSpells() {
        SPELL_TYPES.put(CrystalData.FIRE.getName(), new FireSpellType());
        SPELL_TYPES.put(CrystalData.GRAVITY.getName(), new GravitySpellType());
        SPELL_TYPES.put(CrystalData.ELECTRICITY.getName(), new ElectricitySpellType());
        SPELL_TYPES.put(CrystalData.MIND.getName(), new MindSpellType());
        SPELL_TYPES.put(CrystalData.BODY.getName(), new BodySpellType());
        SPELL_TYPES.put(CrystalData.SOUL.getName(), new SoulSpellType());
        SPELL_TYPES.put(CrystalData.ECLIPSE.getName(), new EclipseSpellType());
        SPELL_TYPES.put(CrystalData.DISCORD.getName(), new DiscordSpellType());
        SPELL_TYPES.put(CrystalData.RADIANT.getName(), new RadiantSpellType());
    }
    public static SpellType getSpellType(String id) {
        return SPELL_TYPES.get(id);
    }

}
