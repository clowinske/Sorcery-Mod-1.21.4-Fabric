package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record LightningParticleS2CPayload(Vector3f start, Vector3f dest, Vector3f color, float radius, boolean hasCore, float branchChance, int ticks) implements CustomPayload {
public static final Id<LightningParticleS2CPayload> ID = new Id<>(Packets.LIGHTNING_PARTICLE_S2C);
    public static final PacketCodec<RegistryByteBuf, LightningParticleS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, LightningParticleS2CPayload::start,
            PacketCodecs.VECTOR_3F, LightningParticleS2CPayload::dest,
            PacketCodecs.VECTOR_3F, LightningParticleS2CPayload::color,
            PacketCodecs.FLOAT, LightningParticleS2CPayload::radius,
            PacketCodecs.BOOLEAN, LightningParticleS2CPayload::hasCore,
            PacketCodecs.FLOAT, LightningParticleS2CPayload::branchChance,
            PacketCodecs.INTEGER, LightningParticleS2CPayload::ticks,
            LightningParticleS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
