package net.code7y7.sorcerymod.network;

import net.code7y7.sorcerymod.particle.CrystalPlaceParticleEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record CrystalPlaceParticlePayload(BlockPos pos, int rgb, float y) implements CustomPayload {
public static final CustomPayload.Id<CrystalPlaceParticlePayload> ID = new CustomPayload.Id<>(Packets.SHOW_CRYSTAL_PLACE_PARTICLE);
    public static final PacketCodec<RegistryByteBuf, CrystalPlaceParticlePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, CrystalPlaceParticlePayload::pos,
            PacketCodecs.INTEGER, CrystalPlaceParticlePayload::rgb,
            PacketCodecs.FLOAT, CrystalPlaceParticlePayload::y,
            CrystalPlaceParticlePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
