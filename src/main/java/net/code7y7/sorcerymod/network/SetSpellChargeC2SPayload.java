package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetSpellChargeC2SPayload(String button, int amt) implements CustomPayload {
public static final Id<SetSpellChargeC2SPayload> ID = new Id<>(Packets.SET_SPELL_CHARGE_C2S);
    public static final PacketCodec<RegistryByteBuf, SetSpellChargeC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SetSpellChargeC2SPayload::button,
            PacketCodecs.INTEGER, SetSpellChargeC2SPayload::amt,
            SetSpellChargeC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
