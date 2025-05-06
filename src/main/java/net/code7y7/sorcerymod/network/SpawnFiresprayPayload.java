package net.code7y7.sorcerymod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record SpawnFiresprayPayload(Vector3f startPos, Vector3f direction, String hand, float damage, float fireticks) implements CustomPayload {
    public static final Id<SpawnFiresprayPayload> ID = new Id<>(Packets.SPAWN_FIRESPRAY);
    public static final PacketCodec<RegistryByteBuf, SpawnFiresprayPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, SpawnFiresprayPayload::startPos,
            PacketCodecs.VECTOR_3F, SpawnFiresprayPayload::direction,
            PacketCodecs.STRING, SpawnFiresprayPayload::hand,
            PacketCodecs.FLOAT, SpawnFiresprayPayload::damage,
            PacketCodecs.FLOAT, SpawnFiresprayPayload::fireticks,
            SpawnFiresprayPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
