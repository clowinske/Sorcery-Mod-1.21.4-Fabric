package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetSpellChargeS2CPayload(String button, int amt) implements CustomPayload {
public static final Id<SetSpellChargeS2CPayload> ID = new Id<>(Packets.SET_SPELL_CHARGE_S2C);
    public static final PacketCodec<RegistryByteBuf, SetSpellChargeS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SetSpellChargeS2CPayload::button,
            PacketCodecs.INTEGER, SetSpellChargeS2CPayload::amt,
            SetSpellChargeS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
