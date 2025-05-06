package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record AltarEffectParticlePayload(BlockPos pos) implements CustomPayload {
public static final Id<AltarEffectParticlePayload> ID = new Id<>(Packets.SHOW_ALTAR_EFFECT_PARTICLE);
    public static final PacketCodec<RegistryByteBuf, AltarEffectParticlePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, AltarEffectParticlePayload::pos,
            AltarEffectParticlePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
