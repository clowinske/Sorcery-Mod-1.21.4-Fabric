package net.code7y7.sorcerymod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AltarEffectParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final double originX, originY, originZ;
    private final float sinPhi, cosPhi, alpha, speed;
    private final int direction;
    int color0 = 0xFFFFFF;
    int color1 = 0xD8D8D8;
    int color2 = 0xA9A9A9;
    int color3 = 0x797979;
    int color4 = 0x4D4D4D;
    int color5 = 0x223024;
    int color6 = 0x30222A;
    int color7 = 0x222230;
    


    List<Integer> colors = new ArrayList<Integer>(Arrays.asList(color1, color2, color3, color4, color5, color6, color7));

    public AltarEffectParticle(ClientWorld world, double x, double y, double z, SpriteProvider provider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        //Random random = Random.create();
        this.spriteProvider = provider;
        this.setSprite(spriteProvider);

        double offsetRange = 0.1;
        this.direction = this.random.nextBoolean() ? 1 : -1;
        this.maxAge = 100;
        this.sinPhi = 0.5f;
        this.cosPhi = (float) (Math.sqrt(3) / 2) * direction; //cos(30°)
        this.alpha = 1.0f;
        this.speed = 0.05f + this.random.nextFloat() * 0.1f;
        // Set initial sprite and color
        this.setSpriteForAge(spriteProvider);
        Vector3f randomColor = getRandomColor();
        this.setColor(randomColor.x, randomColor.y, randomColor.z);
        float size = 0.1f + this.random.nextFloat() * 0.2f;
        this.scale(size);
        this.setAlpha(alpha);
        this.originX = x + (this.random.nextDouble() - 0.5) * offsetRange;
        this.originY = y + (this.random.nextDouble() - 0.5) * offsetRange;
        this.originZ = z + (this.random.nextDouble() - 0.5) * offsetRange;
    }

    private Vector3f getRandomColor() {
        int c = colors.get(this.random.nextInt(colors.size()));
        return new Vector3f(((c >> 16) & 0xff) / 255.0f, ((c >> 8) & 0xff) / 255.0f, (c & 0xff) / 255.0f);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        if (this.dead) return;

        float theta = this.age * speed; // Adjust speed here
        float radius = 0.75f;

        this.x = originX + radius * MathHelper.cos(theta) - radius;
        this.y = originY + radius * MathHelper.sin(theta) * sinPhi;
        this.z = originZ - radius * MathHelper.sin(theta) * cosPhi;

        // Fade out over time
        this.setAlpha(1.0f - (this.age / (float) this.maxAge));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    // Particle Factory
    @Environment(EnvType.CLIENT)
    public record AltarEffectParticleFactory(SpriteProvider provider) implements ParticleFactory<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new AltarEffectParticle(world, x, y, z, provider);
        }
    }
}

