package net.code7y7.sorcerymod.network;

import net.code7y7.sorcerymod.entity.custom.FireballEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public record SpawnFireballPayload(Vector3f lookVec, Vector3f destPos, String hand, boolean shouldRicochet, boolean shouldKnockback, float damage, float fireticks, int maxRicochets) implements CustomPayload {
    public static final Id<SpawnFireballPayload> ID = new Id<>(Packets.SPAWN_FIREBALL);
    public static final PacketCodec<RegistryByteBuf, SpawnFireballPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, SpawnFireballPayload::lookVec,
            PacketCodecs.VECTOR_3F, SpawnFireballPayload::destPos,
            PacketCodecs.STRING, SpawnFireballPayload::hand,
            PacketCodecs.BOOLEAN, SpawnFireballPayload::shouldRicochet,
            PacketCodecs.BOOLEAN, SpawnFireballPayload::shouldKnockback,
            PacketCodecs.FLOAT, SpawnFireballPayload::damage,
            PacketCodecs.FLOAT, SpawnFireballPayload::fireticks,
            PacketCodecs.INTEGER, SpawnFireballPayload::maxRicochets,
            SpawnFireballPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
