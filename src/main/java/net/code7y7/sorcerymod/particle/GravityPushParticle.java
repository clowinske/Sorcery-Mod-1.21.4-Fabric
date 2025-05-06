package net.code7y7.sorcerymod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class GravityPushParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final Vector3f direction;

    public GravityPushParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz, double directionX, double directionY, double directionZ, float size, SpriteProvider provider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.spriteProvider = provider;
        this.setSprite(spriteProvider);
        this.maxAge = 20;
        this.scale = size;
        // Store the direction from parameters and normalize it
        this.direction = new Vector3f((float)directionX, (float)directionY, (float)directionZ);
        if (this.direction.lengthSquared() > 1.0E-6F) {
            this.direction.normalize();
        } else {
            // Default to facing upwards if direction is zero
            this.direction.set(0, 1, 0);
        }
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        if (this.age >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        float x = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.getX());
        float y = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.getY());
        float z = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.getZ());

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F)
        };

        Quaternionf rotation = new Quaternionf().rotationTo(0.0F, 0.0F, 1.0F, this.direction.x(), this.direction.y(), this.direction.z());
        for (Vector3f vertex : vertices) {
            vertex.rotate(rotation);
            vertex.mul(this.scale);
            vertex.add(x, y, z);
        }

        // Original UV coordinates of the sprite
        float baseMinU = this.getMinU();
        float baseMaxU = this.getMaxU();
        float baseMinV = this.getMinV();
        float baseMaxV = this.getMaxV();

        // Calculate quarter UV coordinates (top-left quadrant)
        float halfU = (baseMaxU - baseMinU) * 0.5f;
        float halfV = (baseMaxV - baseMinV) * 0.5f;
        float quarterMaxU = baseMinU + halfU;
        float quarterMaxV = baseMinV + halfV;

        int light = this.getBrightness(tickDelta);

        // Render front face
        vertexConsumer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .texture(quarterMaxU, quarterMaxV)
                .color(red, green, blue, alpha)
                .light(light);

        vertexConsumer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .texture(baseMinU, quarterMaxV)
                .color(red, green, blue, alpha)
                .light(light);

        vertexConsumer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .texture(baseMinU, baseMinV)
                .color(red, green, blue, alpha)
                .light(light);

        vertexConsumer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .texture(quarterMaxU, baseMinV)
                .color(red, green, blue, alpha)
                .light(light);

        // Render back face (reversed winding order)
        vertexConsumer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .texture(quarterMaxU, baseMinV)
                .color(red, green, blue, alpha)
                .light(light);

        vertexConsumer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .texture(baseMinU, baseMinV)
                .color(red, green, blue, alpha)
                .light(light);

        vertexConsumer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .texture(baseMinU, quarterMaxV)
                .color(red, green, blue, alpha)
                .light(light);

        vertexConsumer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .texture(quarterMaxU, quarterMaxV)
                .color(red, green, blue, alpha)
                .light(light);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public record GravityPushParticleFactory(SpriteProvider provider) implements ParticleFactory<GravityPushParticleEffect> {
        @Override
        public Particle createParticle(GravityPushParticleEffect parameters, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new GravityPushParticle(world, x, y, z, dx, dy, dz, parameters.getDirection().x(), parameters.getDirection().y(), parameters.getDirection().z(), parameters.getSize(), provider);
        }
    }
}

