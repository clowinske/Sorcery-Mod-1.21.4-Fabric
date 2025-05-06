package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record FireSpellParticleC2SPayload(Vector3f pos, double velX, double velY, double velZ) implements CustomPayload {
public static final Id<FireSpellParticleC2SPayload> ID = new Id<>(Packets.FIRE_SPELL_PARTICLE_C2S);
    public static final PacketCodec<RegistryByteBuf, FireSpellParticleC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, FireSpellParticleC2SPayload::pos,
            PacketCodecs.DOUBLE, FireSpellParticleC2SPayload::velX,
            PacketCodecs.DOUBLE, FireSpellParticleC2SPayload::velY,
            PacketCodecs.DOUBLE, FireSpellParticleC2SPayload::velZ,
            FireSpellParticleC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
