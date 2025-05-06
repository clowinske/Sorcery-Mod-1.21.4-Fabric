package net.code7y7.sorcerymod;

import net.code7y7.sorcerymod.attachment.ModAttachmentTypes;
import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.block.ModBlocks;
import net.code7y7.sorcerymod.commands.CorruptionCommand;
import net.code7y7.sorcerymod.commands.CrystalAbilitiesCommand;
import net.code7y7.sorcerymod.commands.DungeonCommand;
import net.code7y7.sorcerymod.commands.UpgradeCrystalCommand;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.entity.BossMob;
import net.code7y7.sorcerymod.entity.EnemyMob;
import net.code7y7.sorcerymod.entity.ModEntities;
import net.code7y7.sorcerymod.entity.custom.FireballEntity;
import net.code7y7.sorcerymod.entity.custom.FiresprayEntity;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.network.*;
import net.code7y7.sorcerymod.particle.ModParticles;
import net.code7y7.sorcerymod.screen.ModScreens;
import net.code7y7.sorcerymod.sound.ModSounds;
import net.code7y7.sorcerymod.spell.*;
import net.code7y7.sorcerymod.spell.electricity.Blink;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class SorceryMod implements ModInitializer {
	public static final String MOD_ID = "sorcerymod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlocksEntities();
		ModItemGroups.registerItemGroups();
		ModDataComponentTypes.registerDataComponentTypes();
		ModParticles.registerParticleTypes();
		ModEntities.registerModEntities();
		ModScreens.registerModScreens();
		ModSounds.registerSounds();
		ModGamerules.registerGamerules();
		ModStats.register();

		CrystalAbilitiesCommand.register();
		UpgradeCrystalCommand.register();
		CorruptionCommand.register();
		DungeonCommand.register();

		ModAttachmentTypes.register();
		SpellRegistry.registerSpells();
		AbilityRegistry.registerAbilities();

		//Entity attributes
		FabricDefaultAttributeRegistry.register(ModEntities.CLASS_1_ENEMY, EnemyMob.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.TWO_PHASE_BOSS, BossMob.createAttributes());


		//client packets
		PayloadTypeRegistry.playS2C().register(CrystalPlaceParticlePayload.ID, CrystalPlaceParticlePayload.CODEC);
		PayloadTypeRegistry.playS2C().register(AltarEffectParticlePayload.ID, AltarEffectParticlePayload.CODEC);
		PayloadTypeRegistry.playS2C().register(AltarInteractSoundPayload.ID, AltarInteractSoundPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SetCorruptionS2CPayload.ID, SetCorruptionS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(DustParticleS2CPayload.ID, DustParticleS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(FireSpellParticleS2CPayload.ID, FireSpellParticleS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(LightningParticleS2CPayload.ID, LightningParticleS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(AddVelocityPayload.ID, AddVelocityPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SetSpellChargeS2CPayload.ID, SetSpellChargeS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SetSpellTypeS2CPayload.ID, SetSpellTypeS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SetCanCastS2CPayload.ID, SetCanCastS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SetFlightS2CPayload.ID, SetFlightS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SetFocusS2CPayload.ID, SetFocusS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SpawnParticleS2CPayload.ID, SpawnParticleS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SetFocusRechargeS2CPayload.ID, SetFocusRechargeS2CPayload.CODEC);


		//server packets
		PayloadTypeRegistry.playC2S().register(SendStateSelectModePayload.ID, SendStateSelectModePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SendStateCastModePayload.ID, SendStateCastModePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SendStateHasCrystalPayload.ID, SendStateHasCrystalPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ChargeSpellPayload.ID, ChargeSpellPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetCorruptionC2SPayload.ID, SetCorruptionC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(DustParticleC2SPayload.ID, DustParticleC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SpawnFireballPayload.ID, SpawnFireballPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SpawnFiresprayPayload.ID, SpawnFiresprayPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(FireSpellParticleC2SPayload.ID, FireSpellParticleC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(LightningParticleC2SPayload.ID, LightningParticleC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(CastSpellC2SPayload.ID, CastSpellC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetSpellChargeC2SPayload.ID, SetSpellChargeC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetSpellTypeC2SPayload.ID, SetSpellTypeC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetCanCastC2SPayload.ID, SetCanCastC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BlinkC2SPayload.ID, BlinkC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetFocusC2SPayload.ID, SetFocusC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(AddTimePonderingC2SPayload.ID, AddTimePonderingC2SPayload.CODEC);

		renderEvent();

		initializeServerPackets();
	}
	public static Identifier createIdentifier(String path) {
		return Identifier.of(SorceryMod.MOD_ID, path);
	}

	public static final Identifier INITIAL_SYNC = createIdentifier("initial_sync");
	private static void initializeServerPackets() {

		ServerPlayNetworking.registerGlobalReceiver(SendStateSelectModePayload.ID, (payload, connection) -> {
			ServerPlayerEntity player = connection.player();
			StateSaverAndLoader.getPlayerState(player).selectMode = payload.state();
		});

		ServerPlayNetworking.registerGlobalReceiver(SendStateCastModePayload.ID, (payload, connection) -> {
			ServerPlayerEntity player = connection.player();
			StateSaverAndLoader.getPlayerState(player).castMode = payload.state();
		});

		ServerPlayNetworking.registerGlobalReceiver(SendStateHasCrystalPayload.ID, (payload, connection) -> {
			ServerPlayerEntity player = connection.player();
			StateSaverAndLoader.getPlayerState(player).hasCrystal = payload.state();
		});

		ServerPlayNetworking.registerGlobalReceiver(SetCorruptionC2SPayload.ID, (payload, connection) -> {
			ServerPlayerEntity player = connection.player();
			StateSaverAndLoader.getPlayerState(player).corruption = (int) payload.amount();
			if(payload.sendToOther()){
				ClientPlayNetworking.send(new SetCorruptionS2CPayload(StateSaverAndLoader.getPlayerState(player).corruption, false));
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(ChargeSpellPayload.ID, (payload, connection) -> {
			ServerPlayerEntity player = connection.player();
			if(payload.button().equals("left"))
				StateSaverAndLoader.getPlayerState(player).leftHandCharge = payload.amt();
			else
				StateSaverAndLoader.getPlayerState(player).rightHandCharge = payload.amt();
		});

		ServerPlayNetworking.registerGlobalReceiver(DustParticleC2SPayload.ID, (payload, connection) -> {
			List<ServerPlayerEntity> players = connection.server().getOverworld().getPlayers();
			for(ServerPlayerEntity p : players){
				ServerPlayNetworking.send(p, new DustParticleS2CPayload(payload.pos(), payload.rgb(), payload.vel()));
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(FireSpellParticleC2SPayload.ID, (payload, connection) -> {
			List<ServerPlayerEntity> players = connection.server().getOverworld().getPlayers();
			for(ServerPlayerEntity p : players){
				ServerPlayNetworking.send(p, new FireSpellParticleS2CPayload(payload.pos(), payload.velX(), payload.velY(), payload.velZ()));
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(SpawnFireballPayload.ID, (payload, connection) -> {
			ServerPlayerEntity player = connection.player();
			World world = player.getWorld();
			world.spawnEntity(FireballEntity.create(world, player, new Vec3d(payload.lookVec().x, payload.lookVec().y, payload.lookVec().z),
					new Vec3d(payload.destPos().x, payload.destPos().y, payload.destPos().z),
					payload.hand(), payload.shouldRicochet(), payload.shouldKnockback(),
					payload.damage(), payload.fireticks(), payload.maxRicochets()));
		});

		ServerPlayNetworking.registerGlobalReceiver(SpawnFiresprayPayload.ID, (payload, connection) -> {
			ServerPlayerEntity player = connection.player();
			World world = player.getWorld();
			world.spawnEntity(FiresprayEntity.create(world, player,
					new Vec3d(payload.startPos().x, payload.startPos().y, payload.startPos().z),
					new Vec3d(payload.direction().x, payload.direction().y, payload.direction().z),
					payload.hand(), payload.damage(), payload.fireticks()));
		});

		ServerPlayNetworking.registerGlobalReceiver(LightningParticleC2SPayload.ID, (payload, connection) -> {
			List<ServerPlayerEntity> players = connection.server().getOverworld().getPlayers();
			for(ServerPlayerEntity p : players){
				ServerPlayNetworking.send(p, new LightningParticleS2CPayload(payload.start(), payload.dest(), payload.color(), payload.radius(), payload.hasCore(), payload.branchChance(), payload.ticks()));
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(CastSpellC2SPayload.ID, (payload, connection) -> {
			Optional<Ability> ability = AbilityRegistry.get(payload.ability());
			ServerPlayerEntity player = connection.player();
			String button = payload.button();
			int crystalTier = payload.crystalTier();
			ItemStack crystal = payload.crystal();
			SpellInput input = payload.input();
			SpellInput input1 = payload.input1();
			if(StateSaverAndLoader.getPlayerState(player).focus >= ability.get().getFocusCost(player, player.getServerWorld(), button, crystalTier, crystal))
				InputHandler.cast(ability.get(), player, button, crystalTier, crystal, input, input1);
		});

		ServerPlayNetworking.registerGlobalReceiver(SetSpellChargeC2SPayload.ID, (payload, connection) -> {
			if(payload.button().equals("left")) {
				StateSaverAndLoader.getPlayerState(connection.player()).leftHandCharge = payload.amt();
			} else {
				StateSaverAndLoader.getPlayerState(connection.player()).rightHandCharge = payload.amt();
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(SetSpellTypeC2SPayload.ID, (payload, connection) -> {
			if(payload.button().equals("left")) {
				StateSaverAndLoader.getPlayerState(connection.player()).leftHandSpell = payload.amt();
			} else {
				StateSaverAndLoader.getPlayerState(connection.player()).rightHandSpell = payload.amt();
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(SetCanCastC2SPayload.ID, (payload, connection) -> {
			if(payload.button().equals("left")) {
				StateSaverAndLoader.getPlayerState(connection.player()).canCastLeft = payload.canCast();
			} else {
				StateSaverAndLoader.getPlayerState(connection.player()).canCastRight = payload.canCast();
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(BlinkC2SPayload.ID, (payload, connection) -> {
			Blink.blink(connection.player(), payload.crystalStack());
		});

		ServerPlayNetworking.registerGlobalReceiver(SetFocusC2SPayload.ID, (payload, connection) -> {
			StateSaverAndLoader.getPlayerState(connection.player()).focus = payload.amt();
		});

		ServerPlayNetworking.registerGlobalReceiver(AddTimePonderingC2SPayload.ID, (payload, connection) -> {
			connection.player().increaseStat(ModStats.TIME_PONDERING_THE_ORB, (int) payload.amt());
		});
	}

	public static void renderEvent(){

	}
}