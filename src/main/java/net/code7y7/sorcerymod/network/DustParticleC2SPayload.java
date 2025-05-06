package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record DustParticleC2SPayload(Vector3f pos, int rgb, Vector3f vel) implements CustomPayload {
public static final Id<DustParticleC2SPayload> ID = new Id<>(Packets.DUST_PARTICLE_C2S);
    public static final PacketCodec<RegistryByteBuf, DustParticleC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, DustParticleC2SPayload::pos,
            PacketCodecs.INTEGER, DustParticleC2SPayload::rgb,
            PacketCodecs.VECTOR_3F, DustParticleC2SPayload::vel,
            DustParticleC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
