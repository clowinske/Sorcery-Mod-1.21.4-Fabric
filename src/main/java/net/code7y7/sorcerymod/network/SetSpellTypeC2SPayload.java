package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetSpellTypeC2SPayload(String button, int amt) implements CustomPayload {
public static final Id<SetSpellTypeC2SPayload> ID = new Id<>(Packets.SET_SPELL_TYPE_C2S);
    public static final PacketCodec<RegistryByteBuf, SetSpellTypeC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SetSpellTypeC2SPayload::button,
            PacketCodecs.INTEGER, SetSpellTypeC2SPayload::amt,
            SetSpellTypeC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
