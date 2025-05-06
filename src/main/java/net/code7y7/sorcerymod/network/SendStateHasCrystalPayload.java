package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SendStateHasCrystalPayload(boolean state) implements CustomPayload {
public static final Id<SendStateHasCrystalPayload> ID = new Id<>(Packets.SEND_STATE_HAS_CRYSTAL);
    public static final PacketCodec<RegistryByteBuf, SendStateHasCrystalPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, SendStateHasCrystalPayload::state,
            SendStateHasCrystalPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
