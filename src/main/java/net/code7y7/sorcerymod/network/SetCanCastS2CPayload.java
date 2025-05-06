package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetCanCastS2CPayload(String button, boolean canCast) implements CustomPayload {
public static final Id<SetCanCastS2CPayload> ID = new Id<>(Packets.SET_CAN_CAST_S2C);
    public static final PacketCodec<RegistryByteBuf, SetCanCastS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SetCanCastS2CPayload::button,
            PacketCodecs.BOOLEAN, SetCanCastS2CPayload::canCast,
            SetCanCastS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
