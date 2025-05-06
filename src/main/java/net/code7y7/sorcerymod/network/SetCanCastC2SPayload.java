package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetCanCastC2SPayload(String button, boolean canCast) implements CustomPayload {
public static final Id<SetCanCastC2SPayload> ID = new Id<>(Packets.SET_CAN_CAST_C2S);
    public static final PacketCodec<RegistryByteBuf, SetCanCastC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SetCanCastC2SPayload::button,
            PacketCodecs.BOOLEAN, SetCanCastC2SPayload::canCast,
            SetCanCastC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
