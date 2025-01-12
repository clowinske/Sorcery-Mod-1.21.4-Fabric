package net.code7y7.sorcerymod;

import net.code7y7.sorcerymod.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SorceryMod implements ModInitializer {
	public static final String MOD_ID = "sorcerymod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
	}
}