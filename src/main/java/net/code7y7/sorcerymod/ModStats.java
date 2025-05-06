package net.code7y7.sorcerymod;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class ModStats {
    public static final Identifier TIME_PONDERING_THE_ORB = SorceryMod.createIdentifier("time_pondering_the_orb");

    public static void register() {
        Registry.register(Registries.CUSTOM_STAT, TIME_PONDERING_THE_ORB, TIME_PONDERING_THE_ORB);
        Stats.CUSTOM.getOrCreateStat(TIME_PONDERING_THE_ORB, StatFormatter.TIME);
    }
}
