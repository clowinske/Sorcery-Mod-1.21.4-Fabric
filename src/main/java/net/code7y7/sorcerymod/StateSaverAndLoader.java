package net.code7y7.sorcerymod;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {

    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putBoolean("castMode", playerData.castMode);
            playerNbt.putBoolean("selectMode", playerData.selectMode);
            playerNbt.putBoolean("hasCrystal", playerData.hasCrystal);
            playerNbt.putBoolean("leftMouseDown", playerData.leftMouseDown);
            playerNbt.putBoolean("rightMouseDown", playerData.rightMouseDown);
            playerNbt.putInt("leftHandCharge", playerData.leftHandCharge);
            playerNbt.putInt("leftHandCharge", playerData.rightHandCharge);
            playerNbt.putInt("corruption", playerData.corruption);
            playerNbt.putBoolean("canCastLeft", playerData.canCastLeft);
            playerNbt.putBoolean("canCastRight", playerData.canCastRight);
            playerNbt.putBoolean("canBlink", playerData.canBlink);
            playerNbt.putBoolean("hasJumped", playerData.hasJumped);
            playerNbt.putDouble("focus", playerData.focus);
            playerNbt.putInt("focusRechargeCooldown", playerData.focusRechargeCooldown);
            playerNbt.putInt("orbUI", playerData.orbUI);
            playerNbt.putBoolean("bodySpellHealed", playerData.bodySpellHealed);

            playersNbt.put(uuid.toString(), playerNbt);
        });

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtCompound playersNbt = nbt.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            playerData.castMode = playersNbt.getCompound(key).getBoolean("castMode");
            playerData.selectMode = playersNbt.getCompound(key).getBoolean("selectMode");
            playerData.hasCrystal = playersNbt.getCompound(key).getBoolean("hasCrystal");
            playerData.leftMouseDown = playersNbt.getCompound(key).getBoolean("leftMouseDown");
            playerData.rightMouseDown = playersNbt.getCompound(key).getBoolean("rightMouseDown");
            playerData.leftHandCharge = playersNbt.getCompound(key).getInt("leftHandCharge");
            playerData.rightHandCharge = playersNbt.getCompound(key).getInt("rightHandCharge");
            playerData.corruption = playersNbt.getCompound(key).getInt("corruption");
            playerData.canCastLeft = playersNbt.getCompound(key).getBoolean("canCastLeft");
            playerData.canCastRight = playersNbt.getCompound(key).getBoolean("canCastRight");
            playerData.canBlink = playersNbt.getCompound(key).getBoolean("canBlink");
            playerData.hasJumped = playersNbt.getCompound(key).getBoolean("hasBlinked");
            playerData.focus = playersNbt.getCompound(key).getDouble("focus");
            playerData.focusRechargeCooldown = playersNbt.getCompound(key).getInt("focusRechargeCooldown");
            playerData.orbUI = playersNbt.getCompound(key).getInt("orbUI");
            playerData.bodySpellHealed = playersNbt.getCompound(key).getBoolean("bodySpellHealed");


            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt,
            null
    );


    public static StateSaverAndLoader getServerState(MinecraftServer server){
            PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
            StateSaverAndLoader state = manager.getOrCreate(type, SorceryMod.MOD_ID);

            state.markDirty();
            return state;
    }
    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());

        // Either get the player by the uuid, or we don't have data for him yet, make a new player state
        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());

        return playerState;
    }
}
