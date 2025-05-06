package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetFlightS2CPayload(boolean state) implements CustomPayload {
public static final Id<SetFlightS2CPayload> ID = new Id<>(Packets.SET_FLIGHT_S2C);
    public static final PacketCodec<RegistryByteBuf, SetFlightS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, SetFlightS2CPayload::state,
            SetFlightS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
