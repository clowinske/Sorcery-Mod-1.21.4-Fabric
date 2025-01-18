package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;

public record AltarInteractSoundPayload(SoundEvent sound, float volume, float pitch) implements CustomPayload {
    public static final CustomPayload.Id<AltarInteractSoundPayload> ID = new CustomPayload.Id<>(Packets.ALTAR_INTERACT_SOUND);
    public static final PacketCodec<RegistryByteBuf, AltarInteractSoundPayload> CODEC = PacketCodec.tuple(
            SoundEvent.PACKET_CODEC, AltarInteractSoundPayload::sound,
            PacketCodecs.FLOAT, AltarInteractSoundPayload::volume,
            PacketCodecs.FLOAT, AltarInteractSoundPayload::pitch,
            AltarInteractSoundPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
