package net.code7y7.sorcerymod.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LightningParticle extends SpriteBillboardParticle {
    private final Vector3f destination;
    private final Vector3f color;
    private final boolean hasCore;
    private final float branchChance;
    private final List<Vector3f> points;
    private int segments = 6;
    private  float radius = 0.01f;
    float totalDistance;
    float alphaMultiplier;

    public LightningParticle(ClientWorld world, double x, double y, double z, Vector3f destination, Vector3f color, float radius, boolean hasCore, float branchChance, int ticks, SpriteProvider provider) {
        super(world, x, y, z);
        this.destination = destination;
        this.color = color;
        this.radius = radius;
        this.hasCore = hasCore;
        this.branchChance = branchChance;
        this.setSprite(provider);
        this.maxAge = ticks;
        this.scale(1f);
        this.setAlpha(0.3f);
        this.alphaMultiplier = 1;


        // Precompute the segmented points.
        // Start point is the particle's current position.
        Vector3f startPos = new Vector3f((float) x, (float) y, (float) z);
        Vector3f dest = new Vector3f(destination);
        Vector3f diff = new Vector3f(dest).sub(startPos);
        totalDistance = diff.length();
        diff.normalize();
        segments = Math.max(2, (int) totalDistance);

        // Compute two perpendicular directions (u and v) to use for offsetting.
        Vector3f u = createPerpendicular(diff);
        Vector3f v = new Vector3f(diff).cross(u).normalize();

        points = new ArrayList<>();
        points.add(startPos);

        for (int i = 1; i < segments; i++) {
            float t = (float) i / segments; // interpolation factor
            Vector3f base = new Vector3f(startPos).lerp(dest, t);

            float segmentLength = totalDistance / segments;
            float wiggleAmplitude = segmentLength * 0.05f;

            float endpointFactor = 1.0f;
            if (t < 0.3f) {
                endpointFactor = t / 0.3f;
            } else if (t > 0.7f) {
                endpointFactor = (1 - t) / 0.3f;
            }
            // Generate random offsets in both perpendicular directions.
            float offsetU = (this.random.nextFloat() * 2 - 1) * wiggleAmplitude * endpointFactor;
            float offsetV = (this.random.nextFloat() * 2 - 1) * wiggleAmplitude * endpointFactor;
            Vector3f offset = new Vector3f(u).mul(offsetU).add(new Vector3f(v).mul(offsetV));
            Vector3f point = new Vector3f(base).add(offset);
            points.add(point);
        }
        // Ensure the final point is exactly the destination.
        points.add(dest);

        generateBranches();
    }

    private void generateBranches() {
        if (branchChance <= 0) return;

        for (int i = 1; i < points.size() - 1; i++) { // Skip first and last points
            if (this.random.nextFloat() < branchChance) {
                Vector3f branchPoint = points.get(i);

                // Generate a random direction for the branch
                Vector3f direction = new Vector3f(
                        this.random.nextFloat() - 0.5f,
                        this.random.nextFloat() - 0.5f,
                        this.random.nextFloat() - 0.5f
                ).normalize();

                // Branch length as a fraction of the main lightning's length
                float branchLength = totalDistance * 0.3f;
                Vector3f branchDest = new Vector3f(branchPoint).add(direction.mul(branchLength));

                world.addParticle(new LightningParticleEffect(color, branchDest, radius, hasCore, branchChance * 0.5f, maxAge), branchPoint.x, branchPoint.y, branchPoint.z, 0, 0, 0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.age >= this.maxAge/2)
            this.alphaMultiplier = (maxAge - age) / (float) maxAge;

        if (this.age >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        Quaternionf rotation = camera.getRotation();

        // Calculate the camera's forward vector.
        Vector3f cameraForward = new Vector3f(0, 0, 1);
        rotation.transform(cameraForward);
        cameraForward.normalize();

        // Loop through each segment and render a tube between each successive pair of points.
        for (int i = 0; i < points.size() - 1; i++) {
            Vector3f startWorld = points.get(i);
            Vector3f endWorld = points.get(i + 1);

            // Adjust world coordinates relative to the camera.
            Vector3f start = new Vector3f(
                    startWorld.x() - (float) cameraPos.getX(),
                    startWorld.y() - (float) cameraPos.getY(),
                    startWorld.z() - (float) cameraPos.getZ()
            );
            Vector3f end = new Vector3f(
                    endWorld.x() - (float) cameraPos.getX(),
                    endWorld.y() - (float) cameraPos.getY(),
                    endWorld.z() - (float) cameraPos.getZ()
            );

            // Compute the direction of the segment and generate perpendicular vectors.
            Vector3f segmentDir = new Vector3f(end).sub(start);
            float length = segmentDir.length();
            if (length < 1e-6f) continue;
            segmentDir.normalize();
            Vector3f u = createPerpendicular(segmentDir);
            Vector3f v = new Vector3f(segmentDir).cross(u).normalize();

            u.mul(radius);
            v.mul(radius);


            Vector3f uInner = new Vector3f(u).mul(0.3f);
            Vector3f vInner = new Vector3f(v).mul(0.3f);
            Vector3f innerColor = new Vector3f(1.0f, 1.0f, 1.0f);
            if(hasCore) {
                RenderSystem.depthMask(false);
                RenderSystem.disableDepthTest();
                renderTube(vertexConsumer, start, end, uInner, vInner, innerColor, 1.0f * alphaMultiplier, cameraForward, true);
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(true);
            }

            Vector3f uMid = new Vector3f(u).mul(0.5f);
            Vector3f vMid = new Vector3f(v).mul(0.5f);
            Vector3f midColor = this.color;
            //renderTube(vertexConsumer, start, end, uMid, vMid, midColor, this.alpha/2, cameraForward);

            // Render the outer colored tube.
            renderTube(vertexConsumer, start, end, u, v, this.color, this.alpha * alphaMultiplier, cameraForward, false);

        }
    }

    private void renderTube(VertexConsumer vertexConsumer, Vector3f start, Vector3f end, Vector3f u, Vector3f v, Vector3f color, float alpha, Vector3f cameraForward, boolean renderCaps) {
        Vector3f[] startCorners = calculateCorners(start, u, v);
        Vector3f[] endCorners = calculateCorners(end, u, v);

        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            float depthOffset = i * 0.00f;
            Vector3f offset = new Vector3f(cameraForward).mul(depthOffset);
            buildQuad(vertexConsumer,
                    offsetAdd(startCorners[i], offset),
                    offsetAdd(startCorners[next], offset),
                    offsetAdd(endCorners[i], offset),
                    offsetAdd(endCorners[next], offset),
                    color,
                    alpha
            );
        }
        if(renderCaps) {
            renderCap(vertexConsumer, start, new Vector3f(start).sub(end).normalize(), u, v, color, alpha, 12);
            renderCap(vertexConsumer, end, new Vector3f(end).sub(start).normalize(), u, v, color, alpha, 12);
        }

    }

    private Vector3f offsetAdd(Vector3f original, Vector3f offset) {
        return new Vector3f(original).add(offset);
    }

    private Vector3f createPerpendicular(Vector3f direction) {
        Vector3f axis = Math.abs(direction.y()) > 0.9f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
        return new Vector3f(direction).cross(axis).normalize();
    }

    private Vector3f[] calculateCorners(Vector3f center, Vector3f u, Vector3f v) {
        return new Vector3f[] {
                new Vector3f(center).add(u).add(v),
                new Vector3f(center).add(u).sub(v),
                new Vector3f(center).sub(u).sub(v),
                new Vector3f(center).sub(u).add(v)
        };
    }

    private void buildQuad(VertexConsumer buffer, Vector3f s1, Vector3f s2, Vector3f e1, Vector3f e2, Vector3f color, float alpha) {
        addVertex(buffer, s1, getMaxU(), getMaxV(), color, alpha);
        addVertex(buffer, e1, getMaxU(), getMinV(), color, alpha);
        addVertex(buffer, e2, getMinU(), getMinV(), color, alpha);
        addVertex(buffer, s2, getMinU(), getMaxV(), color, alpha);
    }

    private void addVertex(VertexConsumer buffer, Vector3f pos, float u, float v, Vector3f color, float alpha) {
        buffer.vertex(pos.x, pos.y, pos.z)
                .color(color.x(), color.y(), color.z(), alpha)
                .texture(u, v)
                .light(getLight());
    }

    private void renderCap(VertexConsumer buffer, Vector3f center, Vector3f normal, Vector3f u, Vector3f v, Vector3f color, float alpha, int segments) {
        float angleStep = (float) (2 * Math.PI / segments);
        Vector3f prev = new Vector3f(center).add(new Vector3f(u).add(v));

        for (int i = 1; i <= segments; i++) {
            float angle = i * angleStep;
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);

            Vector3f radial = new Vector3f(u).mul(cos).add(new Vector3f(v).mul(sin));
            Vector3f point = new Vector3f(center).add(radial);

            addVertex(buffer, center, getMinU(), getMinV(), color, alpha);
            addVertex(buffer, prev, getMaxU(), getMinV(), color, alpha);
            addVertex(buffer, point, getMaxU(), getMaxV(), color, alpha);

            prev = point;
        }
    }

    private int getLight() {
        Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        return WorldRenderer.getLightmapCoordinates(
                world,
                new BlockPos(
                        (int) (this.x - cameraPos.x),
                        (int) (this.y - cameraPos.y),
                        (int) (this.z - cameraPos.z)
                )
        );
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
            boolean hasCore = effect.hasCore();
            float branchChance = effect.getBranchChance();
            float radius = effect.getRadius();
            int ticks = effect.getTicks();
            // Assume that LightningParticleEffect now provides a segments value.
            return new LightningParticle(world, x, y, z, destination, color, radius, hasCore, branchChance, ticks, provider);
        }
    }
}