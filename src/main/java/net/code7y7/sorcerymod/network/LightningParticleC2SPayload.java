package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record LightningParticleC2SPayload(Vector3f start, Vector3f dest, Vector3f color, float radius, boolean hasCore, float branchChance, int ticks) implements CustomPayload {
public static final Id<LightningParticleC2SPayload> ID = new Id<>(Packets.LIGHTNING_PARTICLE_C2S);
    public static final PacketCodec<RegistryByteBuf, LightningParticleC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, LightningParticleC2SPayload::start,
            PacketCodecs.VECTOR_3F, LightningParticleC2SPayload::dest,
            PacketCodecs.VECTOR_3F, LightningParticleC2SPayload::color,
            PacketCodecs.FLOAT, LightningParticleC2SPayload::radius,
            PacketCodecs.BOOLEAN, LightningParticleC2SPayload::hasCore,
            PacketCodecs.FLOAT, LightningParticleC2SPayload::branchChance,
            PacketCodecs.INTEGER, LightningParticleC2SPayload::ticks,
            LightningParticleC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
