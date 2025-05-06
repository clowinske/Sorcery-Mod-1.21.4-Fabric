package net.code7y7.sorcerymod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3f;

public class ScorchMarkParticle extends SpriteBillboardParticle {
    private final Direction face;
    protected ScorchMarkParticle(ClientWorld clientWorld, double d, double e, double f, Direction face, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);
        this.face = face;
        this.setSprite(spriteProvider);
        this.maxAge = 100;
        this.gravityStrength = 0f;
        this.velocityMultiplier = 0f;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d pos = camera.getPos();
        float ox = (float)(this.x - pos.x);
        float oy = (float)(this.y - pos.y);
        float oz = (float)(this.z - pos.z);

        // Centered scorch mark of size 0.5 x 0.5
        float size = 0.5f;
        float halfSize = size / 2f;

        // Get block position
        BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
        VoxelShape shape = world.getBlockState(blockPos).getOutlineShape(world, blockPos);
        Box box = shape.isEmpty() ? new Box(blockPos) : shape.getBoundingBox();

        // Get particle quad aligned to face
        Vector3f[] corners = getQuadCorners(face, ox, oy, oz, halfSize);

        // Clip against block bounds (in camera space)
        // Simplified: only render if all corners are inside the block face
        for (Vector3f corner : corners) {
            Vec3d worldCorner = new Vec3d(corner.x() + pos.x, corner.y() + pos.y, corner.z() + pos.z);
            if (!box.contains(worldCorner)) {
                //return; // Skip rendering if any part is off the block face
            }
        }

        // Draw quad
        drawQuad(vertexConsumer, corners);
    }
    private Vector3f[] getQuadCorners(Direction face, float x, float y, float z, float s) {
        switch (face) {
            case UP:
            case DOWN:
                return new Vector3f[] {
                        new Vector3f(x - s, y, z - s),
                        new Vector3f(x - s, y, z + s),
                        new Vector3f(x + s, y, z + s),
                        new Vector3f(x + s, y, z - s)
                };
            case NORTH:
            case SOUTH:
                return new Vector3f[] {
                        new Vector3f(x - s, y - s, z),
                        new Vector3f(x - s, y + s, z),
                        new Vector3f(x + s, y + s, z),
                        new Vector3f(x + s, y - s, z)
                };
            case EAST:
            case WEST:
            default:
                return new Vector3f[] {
                        new Vector3f(x, y - s, z - s),
                        new Vector3f(x, y + s, z - s),
                        new Vector3f(x, y + s, z + s),
                        new Vector3f(x, y - s, z + s)
                };
        }
    }
    private void drawQuad(VertexConsumer vc, Vector3f[] c) {
        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int light = 0xF000F0;

        vc.vertex(c[0].x(), c[0].y(), c[0].z()).texture(minU, maxV).light(light).color(0xFFFFFF);
        vc.vertex(c[1].x(), c[1].y(), c[1].z()).texture(minU, minV).light(light).color(0xFFFFFF);
        vc.vertex(c[2].x(), c[2].y(), c[2].z()).texture(maxU, minV).light(light).color(0xFFFFFF);
        vc.vertex(c[3].x(), c[3].y(), c[3].z()).texture(maxU, maxV).light(light).color(0xFFFFFF);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    // Particle Factory
    @Environment(EnvType.CLIENT)
    public record ScorchMarkParticleFactory(SpriteProvider provider) implements ParticleFactory<ScorchMarkParticleEffect> {
        @Override
        public Particle createParticle(ScorchMarkParticleEffect effect, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            Direction face = effect.getFace();

            return new ScorchMarkParticle(world, x, y, z, face, provider);
        }
    }
}
