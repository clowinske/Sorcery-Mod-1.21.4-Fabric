package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.network.AddVelocityPayload;
import net.code7y7.sorcerymod.network.BlinkC2SPayload;
import net.code7y7.sorcerymod.network.LightningParticleS2CPayload;
import net.code7y7.sorcerymod.network.SetFlightS2CPayload;
import net.code7y7.sorcerymod.particle.LightningParticleEffect;
import net.code7y7.sorcerymod.particle.ModParticles;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class Blink {
    static ElectricityOptions options;
    public static void blink(PlayerEntity player, ItemStack crystalStack){
        ClientPlayNetworking.send(new BlinkC2SPayload(crystalStack));
    }

    public static void blink(ServerPlayerEntity player, ItemStack crystalStack) {
        ElementalCrystalItem crystalItem = (ElementalCrystalItem) crystalStack.getItem();
        options = (ElectricityOptions) crystalItem.getCrystalOptions(crystalStack);
        int tier = crystalItem.getTier(crystalStack);
        double blinkDistance = options.getBlinkOptions().getBlinkDistance();
        Vec3d direction;
        Vec3d exitDirection;
        Vec3d exitVelocity;

        if(options.getBlinkOptions().getBlinkType() == 0){ //Option 0: blink in the direction of the player's head rotation
            direction = player.getRotationVector().normalize();
            exitDirection = direction;
            exitVelocity = exitDirection.multiply(0.5);

        } else if(options.getBlinkOptions().getBlinkType() == 1){ //Option 1: blink in the direction of the player's movement direction, negating the y component
            if(player.getMovement().x == 0 && player.getMovement().z == 0){
                direction = player.getMovement().normalize();
                exitDirection = direction;
            } else {
                direction = player.getMovement().subtract(0, player.getMovement().y, 0).normalize();
                exitDirection = direction.add(0, 1, 0);
            }
            exitVelocity = exitDirection.multiply(0.5);

        } else if(options.getBlinkOptions().getBlinkType() == 2){ //Option 2: blink in the direction of the player's movement direction
            direction = player.getMovement().normalize();
            exitDirection = direction;
            exitVelocity = exitDirection.multiply(0.5);
        } else { // Option 3: WASD-based relative to facing
            float yawRad = (float) Math.toRadians(player.getYaw());
            double forward = 0;
            double sideways = 0;

            PlayerInput input = player.getPlayerInput();
            if (input != null) {
                if (input.forward()) forward += 1;
                if (input.backward()) forward -= 1;
                if (input.left()) sideways += 1;
                if (input.right()) sideways -= 1;
            }

            if (forward == 0 && sideways == 0) {
                forward = 1; // default to forward if no input
            }

            // Calculate direction vector relative to player yaw
            double x = -Math.sin(yawRad) * forward + Math.cos(yawRad) * sideways;
            double z = Math.cos(yawRad) * forward + Math.sin(yawRad) * sideways;

            direction = new Vec3d(x, 0, z).normalize();
            exitDirection = direction.add(0, 1, 0); // treat upward like option 1
            exitVelocity = exitDirection.multiply(0.5);
        }

        Vec3d startPos = player.getPos();

        Vec3d targetPos = startPos;

        // Check for valid path
        double step = 0.25; // Step size for checking blocks
        Vec3d lastValid = startPos;
        for (double d = 0; d <= blinkDistance; d += step) {
            Vec3d currentPos = startPos.add(direction.multiply(d));
            // Create a box representing the required space (0.8x0.8 width, 1.5 height)
            Box checkBox = new Box(
                    currentPos.x - 0.4, currentPos.y, currentPos.z - 0.4,
                    currentPos.x + 0.4, currentPos.y + 1.5, currentPos.z + 0.4
            );
            // Check if the area is clear of block collisions
            if (!player.getServerWorld().getBlockCollisions(player, checkBox).iterator().hasNext()) {
                lastValid = currentPos;
            } else {
                break; // Path is blocked, stop here
            }
        }
        targetPos = lastValid;

        // Handle creative mode and gliding adjustments
        if (player.isCreative()) {
            player.getAbilities().flying = false;
            ServerPlayNetworking.send(player, new SetFlightS2CPayload(false));
        }
        if (player.isGliding()) {
            exitVelocity = direction.multiply(2);
        }

        // Teleport the player and apply velocity
        player.requestTeleport(targetPos.x, targetPos.y, targetPos.z);
        ServerPlayNetworking.send(player, new AddVelocityPayload(exitVelocity.toVector3f(), player.getId()));
        addParticles(player.getServerWorld(), startPos, targetPos, 0f);
    }

    public static void addParticles(ServerWorld world, Vec3d start, Vec3d end, float branchChance) {
        List<ServerPlayerEntity> players = world.getPlayers();

        // Send lightning beam packet
        for(int i = 0; i < 10; i++) {
            Vec3d offset = new Vec3d(
                    (world.random.nextDouble() - 0.5) * 1.0,
                    world.random.nextDouble() * 1.8,
                    (world.random.nextDouble() - 0.5) * 1.0
            );
            for (ServerPlayerEntity p : players) {
                ServerPlayNetworking.send(p, new LightningParticleS2CPayload(
                        start.add(offset).toVector3f(), end.add(offset).toVector3f(),
                        CrystalData.ELECTRICITY.getColorVec(),
                        0.05f, true, branchChance, 5));
            }
        }

        // Spawn extra electricity-themed particles at both positions
        for (int i = 0; i < 30; i++) {
            Vec3d offsetStart = start.add(
                    (world.random.nextDouble() - 0.5) * 1.5,
                    world.random.nextDouble() * 1.5,
                    (world.random.nextDouble() - 0.5) * 1.5);
            Vec3d offsetEnd = end.add(
                    (world.random.nextDouble() - 0.5) * 1.5,
                    world.random.nextDouble() * 1.5,
                    (world.random.nextDouble() - 0.5) * 1.5);

            world.spawnParticles(ModParticles.ZAP_PARTICLE, offsetStart.x, offsetStart.y, offsetStart.z, 1, 0, 0, 0, 0);
            world.spawnParticles(ModParticles.ZAP_PARTICLE, offsetEnd.x, offsetEnd.y, offsetEnd.z, 1, 0, 0, 0, 0);

            if (world.random.nextFloat() < 0.3f) {
                world.spawnParticles(ParticleTypes.SMOKE, offsetStart.x, offsetStart.y, offsetStart.z, 1, 0, 0, 0, 0);
                world.spawnParticles(ParticleTypes.SMOKE, offsetEnd.x, offsetEnd.y, offsetEnd.z, 1, 0, 0, 0, 0);
            }
        }
    }

    public static double getFocusCost(ItemStack stack){
        return 15;
    }

    public static double getMaxDistance(ItemStack crystalStack){
        ElementalCrystalItem crystalItem = (ElementalCrystalItem) crystalStack.getItem();
        int tier = crystalItem.getTier(crystalStack);
        return tier * 2 + 5;
    }
    public static int getBlinkTypeNum(){
        return 4;
    }
}
