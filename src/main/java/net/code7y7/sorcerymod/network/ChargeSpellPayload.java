package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;

public record ChargeSpellPayload(int amt, String button) implements CustomPayload {
    public static final Id<ChargeSpellPayload> ID = new Id<>(Packets.CHARGE_SPELL);
    public static final PacketCodec<RegistryByteBuf, ChargeSpellPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ChargeSpellPayload::amt,
            PacketCodecs.STRING, ChargeSpellPayload::button,
            ChargeSpellPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
