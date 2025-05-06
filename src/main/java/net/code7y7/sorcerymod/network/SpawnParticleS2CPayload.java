package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import org.joml.Vector3f;

public record SpawnParticleS2CPayload(ParticleType<?> particle, Vector3f pos) implements CustomPayload {
    public static final Id<SpawnParticleS2CPayload> ID = new Id<>(Packets.SPAWN_PARTICLE_S2C);

    public static final PacketCodec<RegistryByteBuf, SpawnParticleS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.registryValue(RegistryKeys.PARTICLE_TYPE), SpawnParticleS2CPayload::particle,
            PacketCodecs.VECTOR_3F, SpawnParticleS2CPayload::pos,
            SpawnParticleS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
