package net.code7y7.sorcerymod.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class LightningParticle extends Particle {
    private final Vector3f destination;
    private final Vector3f color;

    public LightningParticle(ClientWorld world, double x, double y, double z, Vector3f destination, Vector3f color) {
        super(world, x, y, z);
        this.destination = destination;
        this.color = color;

        // Customizable lifetime
        this.maxAge = 10 + random.nextInt(10); // Random lifespan between 10 and 20 ticks
        this.scale(1f); // Adjust thickness of the beam
    }

    @Override
    public void tick() {
        super.tick();

        // Fade out towards the end of the particle's life
        this.alpha = Math.clamp(1.0f - (float) this.age / this.maxAge, 0.0f, 1.0f);

        if (this.age >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d interpolatedPosition = camera.getPos();
        double startX = Math.lerp(tickDelta, this.prevPosX, this.x) - interpolatedPosition.x;
        double startY = Math.lerp(tickDelta, this.prevPosY, this.y) - interpolatedPosition.y;
        double startZ = Math.lerp(tickDelta, this.prevPosZ, this.z) - interpolatedPosition.z;

        double endX = this.destination.x - interpolatedPosition.x;
        double endY = this.destination.y - interpolatedPosition.y;
        double endZ = this.destination.z - interpolatedPosition.z;

        // Draw the lightning beam
        RenderSystem.setShaderColor(color.x(), color.y(), color.z(), this.alpha);
        MatrixStack matrixStack = new MatrixStack();
        int light = 240;
        renderBeam(
                vertexConsumer,
                matrixStack,
                startX,
                startY,
                startZ,
                endX,
                endY,
                endZ,
                0.0f, // uStart
                0.0f, // vStart
                1.0f, // uEnd
                1.0f,  // vEnd
                light
        );
    }

    public static void renderBeam(VertexConsumer vertexConsumer, MatrixStack matrixStack,
                                  double startX, double startY, double startZ,
                                  double endX, double endY, double endZ,
                                  float uStart, float vStart, float uEnd, float vEnd, int light) {
        // Calculate beam direction and length
        float dx = (float) (endX - startX);
        float dy = (float) (endY - startY);
        float dz = (float) (endZ - startZ);
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Normalize direction vector
        Vector3f direction = new Vector3f(dx / length, dy / length, dz / length);

        // Set up perpendicular vectors for beam thickness
        Vector3f perpendicular1 = new Vector3f(direction.z(), 0, -direction.x());
        perpendicular1.normalize();

        Vector3f perpendicular2 = new Vector3f(direction.x() * direction.y(), -direction.z() * direction.z(), direction.z());
        perpendicular2.normalize();

        // UV step based on the number of segments
        int segments = 8; // Adjust for smoothness
        float uvStep = (uEnd - uStart) / segments;

        // Draw quads along the beam
        matrixStack.push();
        RenderSystem.disableCull(); // Disable culling for better beam appearance

        for (int i = 0; i < segments; i++) {
            // Current and next segment position
            float t1 = i / (float) segments;
            float t2 = (i + 1) / (float) segments;

            double x1 = startX + dx * t1;
            double y1 = startY + dy * t1;
            double z1 = startZ + dz * t1;

            double x2 = startX + dx * t2;
            double y2 = startY + dy * t2;
            double z2 = startZ + dz * t2;

            // UV coordinates
            float v1 = vStart + t1 * (vEnd - vStart);
            float v2 = vStart + t2 * (vEnd - vStart);

            // Create vertices for this quad
            addVertex(vertexConsumer, matrixStack, x1, y1, z1, -perpendicular1.x(), -perpendicular1.y(), -perpendicular1.z(), uStart, v1, light);
            addVertex(vertexConsumer, matrixStack, x1, y1, z1, perpendicular1.x(), perpendicular1.y(), perpendicular1.z(), uEnd, v1, light);
            addVertex(vertexConsumer, matrixStack, x2, y2, z2, perpendicular1.x(), perpendicular1.y(), perpendicular1.z(), uEnd, v2, light);
            addVertex(vertexConsumer, matrixStack, x2, y2, z2, -perpendicular1.x(), -perpendicular1.y(), -perpendicular1.z(), uStart, v2, light);
        }

        matrixStack.pop();
    }

    private static void addVertex(VertexConsumer vertexConsumer, MatrixStack matrixStack,
                                  double x, double y, double z,
                                  double offsetX, double offsetY, double offsetZ,
                                  float u, float v, int light) {
        vertexConsumer.vertex(matrixStack.peek().getPositionMatrix(),
                        (float) (x + offsetX), (float) (y + offsetY), (float) (z + offsetZ))
                .texture(u, v)
                .color(255, 255, 255, 255)
                .light(light);
    }
    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public record LightningParticleFactory(SpriteProvider provider) implements ParticleFactory<LightningParticleEffect> {
        @Override
        public Particle createParticle(LightningParticleEffect effect, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            Vector3f color = effect.getColor();
            Vector3f destination = effect.getDestination();
            return new LightningParticle(world, x, y, z, destination, color);
        }
    }
}


