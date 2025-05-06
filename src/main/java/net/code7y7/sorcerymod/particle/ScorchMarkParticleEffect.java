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
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

public class ScorchMarkParticleEffect implements ParticleEffect {
    public static final MapCodec<ScorchMarkParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Direction.CODEC.fieldOf("face").forGetter(particle -> particle.face)
                    )
                    .apply(instance, ScorchMarkParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, ScorchMarkParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            Direction.PACKET_CODEC, particle -> particle.face, ScorchMarkParticleEffect::new
    );
    private final Direction face;

    public ScorchMarkParticleEffect(Direction face) {
        this.face = face;
    }

    @Override
    public ParticleType<ScorchMarkParticleEffect> getType() {
        return ModParticles.SCORCH_MARK_PARTICLE;
    }

    public Direction getFace(){
        return this.face;
    }
}
