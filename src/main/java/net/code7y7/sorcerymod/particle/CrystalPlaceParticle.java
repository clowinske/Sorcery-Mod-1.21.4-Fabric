package net.code7y7.sorcerymod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class CrystalPlaceParticle extends SpriteBillboardParticle {
    private final double spawnX;
    private final double spawnY;
    private final double spawnZ;
    private final double r, g, b;

    public CrystalPlaceParticle(ClientWorld world, double x, double y, double z, float r, float g, float b, SpriteProvider provider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        Random random = Random.create();
        this.setSprite(provider);

        // Spawn location (center point)
        this.spawnX = x;
        this.spawnY = y;
        this.spawnZ = z;

        // Randomize initial position around the spawn point
        double offsetRange = 0.25;
        this.x = x + (random.nextDouble() * 2 - 1) * offsetRange;
        this.y = y + (random.nextDouble() * 2 - 1) * offsetRange;
        this.z = z + (random.nextDouble() * 2 - 1) * offsetRange;

        this.r = r;
        this.g = g;
        this.b = b;
        this.maxAge = 15;
        this.scale(0.1f);
        this.setColor(r, g, b);
        this.alpha = 0.0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age >= this.maxAge) {
            this.markDead();
        }

        if (this.age > 1) {
            this.alpha = (float) age/maxAge;
        }

        // Calculate movement back to spawn point
        double dx = this.spawnX - this.x;
        double dy = this.spawnY - this.y;
        double dz = this.spawnZ - this.z;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > 0.01) {
            this.velocityX = Math.clamp(dx * 0.1, -0.05, 0.05);
            this.velocityY = Math.clamp(dy * 0.1, -0.05, 0.05);
            this.velocityZ = Math.clamp(dz * 0.1, -0.05, 0.05);
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    // Particle Factory
    @Environment(EnvType.CLIENT)
    public record CrystalPlaceParticleFactory(SpriteProvider provider) implements ParticleFactory<CrystalPlaceParticleEffect> {
        @Override
        public Particle createParticle(CrystalPlaceParticleEffect effect, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            Vector3f color = effect.getColor();
            return new CrystalPlaceParticle(world, x, y, z, color.x(), color.y(), color.z(), provider);
        }
    }
}