package net.code7y7.sorcerymod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class FireSpellParticle extends SpriteBillboardParticle {
    private final double spawnX;
    private final double spawnY;
    private final double spawnZ;
    //private final double velX, velY, velZ;

    public FireSpellParticle(ClientWorld world, double x, double y, double z, double velX, double velY, double velZ, SpriteProvider provider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        Random random = Random.create();
        this.setSprite(provider);

        // Spawn location (center point)
        this.spawnX = x;
        this.spawnY = y;
        this.spawnZ = z;

        // Randomize initial position around the spawn point

        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;
        this.maxAge = 20;
        this.scale(0.5f);
        this.alpha = 1.0f;
    }

    @Override
    public void tick() {
        double decelerationFactor = 0.9;

        this.move(velocityX, velocityY, velocityZ);
        this.velocityX = velocityX * decelerationFactor;
        this.velocityY = velocityY * decelerationFactor;
        this.velocityZ = velocityZ * decelerationFactor;
        super.tick();
        if (this.age >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    // Particle Factory
    @Environment(EnvType.CLIENT)
    public record FireSpellParticleFactory(SpriteProvider provider) implements ParticleFactory<FireSpellParticleEffect> {
        @Override
        public Particle createParticle(FireSpellParticleEffect effect, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            Vector3f vel = effect.getVelocity();
            return new FireSpellParticle(world, x, y, z, vel.x, vel.y, vel.z, provider);
        }
    }
}

