package net.code7y7.sorcerymod.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.*;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3f;

public class CrystalPlaceParticleEffect implements ParticleEffect {
    public static final MapCodec<CrystalPlaceParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codecs.RGB.fieldOf("color").forGetter(particle -> particle.color)
                    )
                    .apply(instance, CrystalPlaceParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, CrystalPlaceParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, particle -> particle.color, CrystalPlaceParticleEffect::new
    );
    private final int color;

    public CrystalPlaceParticleEffect(int color) {
        this.color = color;
    }

    @Override
    public ParticleType<CrystalPlaceParticleEffect> getType() {
        return ModParticles.CRYSTAL_PLACE_PARTICLE;
    }

    public Vector3f getColor() {
        return ColorHelper.toVector(this.color);
    }
}
