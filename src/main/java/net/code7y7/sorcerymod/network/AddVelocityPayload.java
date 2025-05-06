package net.code7y7.sorcerymod.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.UUID;

public record AddVelocityPayload(Vector3f vec, int id) implements CustomPayload {
    public static final Id<AddVelocityPayload> ID = new Id<>(Packets.ADD_VELOCITY);
    public static final PacketCodec<RegistryByteBuf, AddVelocityPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, AddVelocityPayload::vec,
            PacketCodecs.INTEGER, AddVelocityPayload::id,
            AddVelocityPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
