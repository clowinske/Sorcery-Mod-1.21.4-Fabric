package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetFocusS2CPayload(double amt) implements CustomPayload {
public static final Id<SetFocusS2CPayload> ID = new Id<>(Packets.SET_FOCUS_S2C);
    public static final PacketCodec<RegistryByteBuf, SetFocusS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, SetFocusS2CPayload::amt,
            SetFocusS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
