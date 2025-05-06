package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;

public record SetCorruptionS2CPayload(float amount, boolean sendToOther) implements CustomPayload {
    public static final Id<SetCorruptionS2CPayload> ID = new Id<>(Packets.SET_CORRUPTION_S2C);
    public static final PacketCodec<RegistryByteBuf, SetCorruptionS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, SetCorruptionS2CPayload::amount,
            PacketCodecs.BOOLEAN, SetCorruptionS2CPayload::sendToOther,
            SetCorruptionS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
