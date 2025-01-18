package net.code7y7.sorcerymod.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3f;

public class LightningParticleEffect implements ParticleEffect {
    public static final MapCodec<LightningParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codecs.RGB.fieldOf("color").forGetter(particle -> particle.color),
                            Codecs.VECTOR_3F.fieldOf("destination").forGetter(particle -> particle.destination)
                    )
                    .apply(instance, LightningParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, LightningParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, particle -> particle.color,
            PacketCodecs.VECTOR_3F, particle -> particle.destination

            , LightningParticleEffect::new
    );
    private final int color;
    private final Vector3f destination;

    public LightningParticleEffect(int color, Vector3f destination) {
        this.color = color;
        this.destination = destination;
    }

    @Override
    public ParticleType<LightningParticleEffect> getType() {
        return ModParticles.LIGHTNING_PARTICLE;
    }

    public Vector3f getColor() {
        return ColorHelper.toVector(this.color);
    }
    public Vector3f getDestination() {
        return destination;
    }
}