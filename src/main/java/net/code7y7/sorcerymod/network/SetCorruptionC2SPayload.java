package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetCorruptionC2SPayload(float amount, boolean sendToOther) implements CustomPayload {
    public static final Id<SetCorruptionC2SPayload> ID = new Id<>(Packets.SET_CORRUPTION_C2S);
    public static final PacketCodec<RegistryByteBuf, SetCorruptionC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, SetCorruptionC2SPayload::amount,
            PacketCodecs.BOOLEAN, SetCorruptionC2SPayload::sendToOther,
            SetCorruptionC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
