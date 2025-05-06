package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetFocusC2SPayload(Double amt) implements CustomPayload {
public static final Id<SetFocusC2SPayload> ID = new Id<>(Packets.SET_FOCUS_C2S);
    public static final PacketCodec<RegistryByteBuf, SetFocusC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, SetFocusC2SPayload::amt,
            SetFocusC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
