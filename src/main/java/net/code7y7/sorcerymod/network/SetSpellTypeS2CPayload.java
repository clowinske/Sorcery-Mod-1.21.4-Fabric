package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetSpellTypeS2CPayload(String button, int amt) implements CustomPayload {
public static final Id<SetSpellTypeS2CPayload> ID = new Id<>(Packets.SET_SPELL_TYPE_S2C);
    public static final PacketCodec<RegistryByteBuf, SetSpellTypeS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SetSpellTypeS2CPayload::button,
            PacketCodecs.INTEGER, SetSpellTypeS2CPayload::amt,
            SetSpellTypeS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
