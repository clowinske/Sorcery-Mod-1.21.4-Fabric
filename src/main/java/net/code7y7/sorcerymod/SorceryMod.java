package net.code7y7.sorcerymod;

import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.block.ModBlocks;
import net.code7y7.sorcerymod.client.ModEntityModels;
import net.code7y7.sorcerymod.commands.CrystalAbilitiesCommand;
import net.code7y7.sorcerymod.commands.UpgradeCrystalCommand;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.entity.ModEntities;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.network.AltarInteractSoundPayload;
import net.code7y7.sorcerymod.network.CrystalPlaceParticlePayload;
import net.code7y7.sorcerymod.particle.ModParticles;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SorceryMod implements ModInitializer {
	public static final String MOD_ID = "sorcerymod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
//test las
	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlocksEntities();
		ModItemGroups.registerItemGroups();
		ModDataComponentTypes.registerDataComponentTypes();
		ModParticles.registerParticleTypes();
		ModEntities.registerModEntities();

		CrystalAbilitiesCommand.register();
		UpgradeCrystalCommand.register();

		PayloadTypeRegistry.playS2C().register(CrystalPlaceParticlePayload.ID, CrystalPlaceParticlePayload.CODEC);
		PayloadTypeRegistry.playS2C().register(AltarInteractSoundPayload.ID, AltarInteractSoundPayload.CODEC);

	}

	public static Identifier createIdentifier(String path) {
		return Identifier.of(SorceryMod.MOD_ID, path);
	}
}