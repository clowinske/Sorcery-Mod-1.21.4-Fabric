package net.code7y7.sorcerymod;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGamerules {
    //decides if spell casting projectiles should come from the player's eyes, or their hands.
    public static final GameRules.Key<GameRules.BooleanRule> SIMPLE_CASTING =
            GameRuleRegistry.register("simpleCasting", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

    public static void registerGamerules(){
        SorceryMod.LOGGER.info("Registering Mod Gamerules for " + SorceryMod.MOD_ID);
    }
}
