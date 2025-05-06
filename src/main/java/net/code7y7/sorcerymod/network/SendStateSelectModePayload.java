package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SendStateSelectModePayload(boolean state) implements CustomPayload {
public static final Id<SendStateSelectModePayload> ID = new Id<>(Packets.SEND_STATE_SELECT_MODE);
    public static final PacketCodec<RegistryByteBuf, SendStateSelectModePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, SendStateSelectModePayload::state,
            SendStateSelectModePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
