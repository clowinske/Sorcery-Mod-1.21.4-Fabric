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

public class FireSpellParticleEffect implements ParticleEffect {
    public static final MapCodec<FireSpellParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codecs.POSITIVE_FLOAT.fieldOf("velX").forGetter(particle -> (float) particle.velX),
                            Codecs.POSITIVE_FLOAT.fieldOf("velY").forGetter(particle -> (float) particle.velY),
                            Codecs.POSITIVE_FLOAT.fieldOf("velZ").forGetter(particle -> (float) particle.velZ)
                    )
                    .apply(instance, FireSpellParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, FireSpellParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, particle -> particle.velX,
            PacketCodecs.DOUBLE, particle -> particle.velY,
            PacketCodecs.DOUBLE, particle -> particle.velZ,
            FireSpellParticleEffect::new
    );
    private final double velX, velY, velZ;

    public FireSpellParticleEffect(double velX, double velY, double velZ) {
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
    }

    @Override
    public ParticleType<FireSpellParticleEffect> getType() {
        return ModParticles.FIRE_SPELL_PARTICLE;
    }

    public Vector3f getVelocity() {
        return new Vector3f((float) velX, (float) velY, (float) velZ);
    }
}
