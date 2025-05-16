package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record IsWeightlessS2CPayload(boolean isWeightless) implements CustomPayload {
    public static final Id<IsWeightlessS2CPayload> ID = new Id<>(Packets.IS_WEIGHTLESS_S2C);
    public static final PacketCodec<RegistryByteBuf, IsWeightlessS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, IsWeightlessS2CPayload::isWeightless,
            IsWeightlessS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
