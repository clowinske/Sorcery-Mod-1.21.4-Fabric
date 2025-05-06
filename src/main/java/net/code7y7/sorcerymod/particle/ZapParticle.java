package net.code7y7.sorcerymod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ZapParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final float alpha;

    public ZapParticle(ClientWorld world, double x, double y, double z, SpriteProvider provider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        //Random random = Random.create();
        this.spriteProvider = provider;
        this.setSprite(spriteProvider);

        this.maxAge = 20;
        this.alpha = 1.0f;

        float size = 0.2f + this.random.nextFloat() * 0.2f;
        this.scale(size);
        this.setAlpha(alpha);
        this.setVelocity(0, 0, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        if (age >= maxAge) this.markDead();


        float baseAlpha = 1.0f - (this.age / (float) this.maxAge);

        float frequency = 1.75f;
        float sine = MathHelper.sin(age * frequency);

        float flicker = sine > -0.5 ? 1.0f : 0.1f; // full alpha when "on", almost invisible when "off"

        this.setAlpha(baseAlpha * flicker);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    // Particle Factory
    @Environment(EnvType.CLIENT)
    public record ZapParticleFactory(SpriteProvider provider) implements ParticleFactory<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new ZapParticle(world, x, y, z, provider);
        }
    }
}

