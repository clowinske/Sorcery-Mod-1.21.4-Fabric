package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record FireSpellParticleS2CPayload(Vector3f pos, double velX, double velY, double velZ) implements CustomPayload {
public static final Id<FireSpellParticleS2CPayload> ID = new Id<>(Packets.FIRE_SPELL_PARTICLE_S2C);
    public static final PacketCodec<RegistryByteBuf, FireSpellParticleS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, FireSpellParticleS2CPayload::pos,
            PacketCodecs.DOUBLE, FireSpellParticleS2CPayload::velX,
            PacketCodecs.DOUBLE, FireSpellParticleS2CPayload::velY,
            PacketCodecs.DOUBLE, FireSpellParticleS2CPayload::velZ,
            FireSpellParticleS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
