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

public class GravityPushParticleEffect implements ParticleEffect {
    public static final MapCodec<GravityPushParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codecs.VECTOR_3F.fieldOf("direction").forGetter(particle -> particle.direction),
                            Codecs.POSITIVE_FLOAT.fieldOf("size").forGetter(particle -> particle.size)
                    )
                    .apply(instance, GravityPushParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, GravityPushParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, particle -> particle.direction,
            PacketCodecs.FLOAT, particle -> particle.size,
            GravityPushParticleEffect::new
    );
    private final float size;
    private final Vector3f direction;

    public GravityPushParticleEffect(Vector3f direction, float size) {
        this.direction = direction;
        this.size = size;
    }

    @Override
    public ParticleType<GravityPushParticleEffect> getType() {
        return ModParticles.GRAVITY_PUSH_PARTICLE;
    }

    public Vector3f getDirection() {
        return this.direction;
    }
    public float getSize() {
        return this.size;
    }
}
