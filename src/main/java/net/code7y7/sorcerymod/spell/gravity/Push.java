package net.code7y7.sorcerymod.spell.gravity;

import net.code7y7.sorcerymod.network.*;
import net.code7y7.sorcerymod.sound.ModSounds;
import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;

public class Push implements Ability {
    @Override
    public void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        SpellHelper.resetHandCharge(player, button);
        int count = 50;
        double spread = 0.001;

        Vec3d startPos = player.getEyePos();
        Vec3d lookVec = player.getRotationVector();

        Vec3d endPos = startPos.add(lookVec.multiply(5));

        float tierMultiplier = 0.1f * tier;
        float groundMultiplier = calculateGroundMultiplier(player, tier);

        Box pushBox = new Box(startPos, endPos).expand(1.5);

        List<Entity> entities = world.getEntitiesByClass(Entity.class, pushBox, entity -> entity != player);

        double maxDistance = 5.0 + 2*tier; // Maximum distance for the push effect
        double baseStrength = 1.0 + tier*0.1; // Base strength of the push



        for (Entity entity : entities) {
            Vec3d entityPos = entity.getPos();
            Vec3d pushDirection = entityPos.subtract(startPos).normalize();

            double distance = entity.getPos().distanceTo(startPos);
            double distanceFactor = 1.0 - (distance / maxDistance); // Linear falloff
            distanceFactor = Math.max(0, Math.min(1, distanceFactor)); // Clamp between 0 and 1

            double pushStrength = baseStrength * distanceFactor;

            Vec3d pushVector = pushDirection.multiply(pushStrength);
            entity.addVelocity(pushVector.x, pushVector.y, pushVector.z);
            //ClientPlayNetworking.send(new AddVelocityPayload(pushVector.toVector3f(), entity.getUuidAsString()));
        }

        Vec3d velocityVector = lookVec.subtract(lookVec.multiply(2)).multiply(groundMultiplier * 0.5);
        ServerPlayNetworking.send(player, new AddVelocityPayload(velocityVector.toVector3f(), player.getId()));

        List<ServerPlayerEntity> players = world.getPlayers();
        for(ServerPlayerEntity p : players) {
            world.playSound(p, player.getX(), player.getY(), player.getZ(), ModSounds.GRAVITY_PUSH, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
        particle(world, player);

        Vec3d path = endPos.subtract(startPos);
        double length = path.length();
        Vec3d step = path.normalize().multiply(length / count);

        for (Entity entity : entities) {
            Vec3d entityPos = entity.getPos();
            Vec3d pushDirection = entityPos.subtract(startPos).normalize();
            Vec3d particleEndPos = entityPos.add(pushDirection.multiply(5));

            Vec3d particlePath = particleEndPos.subtract(entityPos);
            double particleLength = particlePath.length();
            Vec3d particleStep = particlePath.normalize().multiply(particleLength / count);

            for (int i = 0; i < count; i++) {
                Vec3d pos = entityPos.add(particleStep.multiply(i)).add(
                        world.random.nextGaussian() * spread,
                        world.random.nextGaussian() * spread,
                        world.random.nextGaussian() * spread
                );
                world.addParticle(new DustParticleEffect(CrystalData.GRAVITY.getColorInt(), 1.0f), pos.x, pos.y+0.5, pos.z, 0, 0, 0);
            }
        }
    }

    @Override
    public void holdStart(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }

    public void particle(ServerWorld world, ServerPlayerEntity player){
        double radius = 0.25; // Quarter block radius
        int particleCount = 8; // Number of particles around the circle

        // Get player's head rotation in radians
        float yaw = (float) Math.toRadians(player.getHeadYaw());
        float pitch = (float) Math.toRadians(player.getPitch());

        // Player's eye position
        double px = player.getX();
        double py = player.getEyeY();
        double pz = player.getZ();

        // Calculate the player's look (forward) vector (normalized)
        double lx = -Math.sin(yaw) * Math.cos(pitch);
        double ly = -Math.sin(pitch);
        double lz = Math.cos(yaw) * Math.cos(pitch);
        Vec3d forward = new Vec3d(lx, ly, lz).normalize();

        // Determine the center of the circle half a block in front of the player
        double centerX = px + forward.x * 0.5;
        double centerY = py + forward.y * 0.5;
        double centerZ = pz + forward.z * 0.5;

        // Spawn the center particle moving along the look direction
        spawnParticle(world, centerX, centerY, centerZ, forward.x * 2, forward.y * 2, forward.z * 2);

        // Use world up as a reference; if forward is nearly vertical, choose an alternate up vector
        Vec3d worldUp = new Vec3d(0, 1, 0);
        if (Math.abs(forward.dotProduct(worldUp)) > 0.99) {
            worldUp = new Vec3d(1, 0, 0);
        }
        // Compute a right vector (perpendicular to both forward and worldUp)
        Vec3d right = forward.crossProduct(worldUp).normalize();
        // Recompute an "up" vector for the circle's plane (perpendicular to both forward and right)
        Vec3d circleUp = right.crossProduct(forward).normalize();

        // Spawn circle particles around the new center position
        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI / particleCount) * i;
            // Calculate the offset in the circle's plane using the right and circleUp vectors
            double offsetX = right.x * Math.cos(angle) * radius + circleUp.x * Math.sin(angle) * radius;
            double offsetY = right.y * Math.cos(angle) * radius + circleUp.y * Math.sin(angle) * radius;
            double offsetZ = right.z * Math.cos(angle) * radius + circleUp.z * Math.sin(angle) * radius;

            spawnParticle(world, centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 0, 0, 0);
        }
    }

    public void spawnParticle(ServerWorld world, double x, double y, double z, double dx, double dy, double dz){
        List<ServerPlayerEntity> players = world.getPlayers();
        for(ServerPlayerEntity p : players) {
            ServerPlayNetworking.send(p, new DustParticleS2CPayload(
                    new Vector3f((float) x, (float) y, (float) z),
                    CrystalData.GRAVITY.getColorInt(),
                    new Vector3f((float) dx, (float) dy, (float) dz)
            ));
        }
    }

    @Override
    public void holdRelease(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }

    @Override
    public double getFocusCost(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        return 10;
    }

    @Override
    public SpellInput getInput() {
        return SpellInput.SINGLE;
    }

    @Override
    public String toString() {
        return "push";
    }

    public float calculateGroundMultiplier(PlayerEntity player, int tier) {

        final float maxHeight = 5 + 7 * tier;
        final float groundMultiplier = 0.25f;

        if (player.isOnGround()) {
            return groundMultiplier;
        }

        //float multiplier = (float) Math.exp(-decayConstant * (height - maxHeight));

        return 1;
    }
}
