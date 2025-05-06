package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SendStateCastModePayload(boolean state) implements CustomPayload {
public static final Id<SendStateCastModePayload> ID = new Id<>(Packets.SEND_STATE_CAST_MODE);
    public static final PacketCodec<RegistryByteBuf, SendStateCastModePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, SendStateCastModePayload::state,
            SendStateCastModePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
