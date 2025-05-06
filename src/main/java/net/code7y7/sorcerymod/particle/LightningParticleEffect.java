package net.code7y7.sorcerymod.particle;

import com.mojang.serialization.Codec;
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
                            Codecs.VECTOR_3F.fieldOf("color").forGetter(particle -> particle.color),
                            Codecs.VECTOR_3F.fieldOf("destination").forGetter(particle -> particle.destination),
                            Codec.FLOAT.fieldOf("radius").forGetter(particle -> particle.radius),
                            Codec.BOOL.fieldOf("hasCore").forGetter(particle -> particle.hasCore),
                            Codec.FLOAT.fieldOf("branchChance").forGetter(particle -> particle.branchChance),
                            Codec.INT.fieldOf("ticks").forGetter(particle -> particle.ticks)
                    )
                    .apply(instance, LightningParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, LightningParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, particle -> particle.color,
            PacketCodecs.VECTOR_3F, particle -> particle.destination,
            PacketCodecs.FLOAT, particle -> particle.radius,
            PacketCodecs.BOOLEAN, particle -> particle.hasCore,
            PacketCodecs.FLOAT, particle -> particle.branchChance,
            PacketCodecs.INTEGER, particle -> particle.ticks,
            LightningParticleEffect::new
    );
    private final Vector3f color;
    private final float radius;
    private final Vector3f destination;
    private final boolean hasCore;
    private final float branchChance;
    private final int ticks;

    public LightningParticleEffect(Vector3f color, Vector3f destination, float radius, boolean hasCore, float branchChance, int ticks) {
        this.color = color;
        this.radius = radius;
        this.destination = destination;
        this.hasCore = hasCore;
        this.branchChance = branchChance;
        this.ticks = ticks;
    }

    @Override
    public ParticleType<LightningParticleEffect> getType() {
        return ModParticles.LIGHTNING_PARTICLE;
    }

    public Vector3f getColor() {
        return this.color;
    }
    public float getRadius() {
        return radius;
    }
    public Vector3f getDestination() {
        return destination;
    }
    public boolean hasCore() {
        return hasCore;
    }
    public float getBranchChance() {
        return branchChance;
    }
    public int getTicks(){
        return ticks;
    }
}