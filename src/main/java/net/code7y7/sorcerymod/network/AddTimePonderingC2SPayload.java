package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AddTimePonderingC2SPayload(long amt) implements CustomPayload {
public static final Id<AddTimePonderingC2SPayload> ID = new Id<>(Packets.ADD_TIME_PONDERING_C2S);
    public static final PacketCodec<RegistryByteBuf, AddTimePonderingC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.LONG, AddTimePonderingC2SPayload::amt,
            AddTimePonderingC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
