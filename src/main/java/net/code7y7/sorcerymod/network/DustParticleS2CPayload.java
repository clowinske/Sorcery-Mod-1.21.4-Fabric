package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record DustParticleS2CPayload(Vector3f pos, int rgb, Vector3f vel) implements CustomPayload {
public static final Id<DustParticleS2CPayload> ID = new Id<>(Packets.DUST_PARTICLE_S2C);
    public static final PacketCodec<RegistryByteBuf, DustParticleS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, DustParticleS2CPayload::pos,
            PacketCodecs.INTEGER, DustParticleS2CPayload::rgb,
            PacketCodecs.VECTOR_3F, DustParticleS2CPayload::vel,
            DustParticleS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
