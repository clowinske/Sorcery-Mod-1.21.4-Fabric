package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.network.LightningParticleS2CPayload;
import net.code7y7.sorcerymod.network.SpawnParticleS2CPayload;
import net.code7y7.sorcerymod.particle.ModParticles;
import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Lightning implements Ability {
    @Override
    public void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        ElementalCrystalItem itemCrystal = (ElementalCrystalItem) crystal.getItem();

        Vec3d rightVec = player.getRotationVector().crossProduct(new Vec3d(0, 1, 0)).normalize();
        double handFactor = button.equals("left") ? -0.4 : 0.4;
        Vec3d lookVec = player.getRotationVector();
        Vec3d startPos = player.getEyePos().add(rightVec.multiply(handFactor))
                .add(0, -0.3, 0)
                .add(lookVec.multiply(0.5));

        // Define detection box parameters
        double range = 5.0; // Detection range in blocks
        double width = 2.0; // Total width of the box
        double height = 4.0; // Total height of the box

        // Calculate box dimensions
        Vec3d forwardOffset = lookVec.multiply(range);
        Vec3d rightOffset = rightVec.multiply(width / 2);
        Vec3d verticalOffset = new Vec3d(0, height / 2, 0);

        // Create detection box
        Box detectionBox = new Box(
                startPos.subtract(rightOffset).subtract(verticalOffset),
                startPos.add(forwardOffset).add(rightOffset).add(verticalOffset)
        );

        // Find living entities in the box
        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                detectionBox,
                e -> e.isAlive() && e != player
        );

        // Find nearest entity
        LivingEntity target = null;
        double closestDistance = Double.MAX_VALUE;
        Vec3d playerPos = player.getPos();

        for (LivingEntity entity : entities) {
            double distance = entity.squaredDistanceTo(playerPos);
            if (distance < closestDistance) {
                closestDistance = distance;
                target = entity;
            }
        }
        Random random = new Random();
        Vec3d destPos;
        if (target != null) {
            // Get entity's bounding box
            Box hitbox = target.getBoundingBox();
            Vec3d playerEye = player.getEyePos();
            for (int i = 0; i < 4; i++) {
                // Generate random point on hitbox surface
                int axis = random.nextInt(3);
                boolean isMinSide = random.nextBoolean();
                double x, y, z;

                switch (axis) {
                    case 0: // X-axis
                        x = isMinSide ? hitbox.minX : hitbox.maxX;
                        y = hitbox.minY + random.nextDouble() * (hitbox.maxY - hitbox.minY);
                        z = hitbox.minZ + random.nextDouble() * (hitbox.maxZ - hitbox.minZ);
                        break;
                    case 1: // Y-axis
                        y = isMinSide ? hitbox.minY : hitbox.maxY;
                        x = hitbox.minX + random.nextDouble() * (hitbox.maxX - hitbox.minX);
                        z = hitbox.minZ + random.nextDouble() * (hitbox.maxZ - hitbox.minZ);
                        break;
                    default: // Z-axis
                        z = isMinSide ? hitbox.minZ : hitbox.maxZ;
                        x = hitbox.minX + random.nextDouble() * (hitbox.maxX - hitbox.minX);
                        y = hitbox.minY + random.nextDouble() * (hitbox.maxY - hitbox.minY);
                        break;
                }

                destPos = new Vec3d(x, y, z);
                spawnParticle(world, player, startPos, destPos, 0.25f, true);
            }
            hitEntity(player, world, target, tier, crystal);
        } else {
            // Block face targeting
            double blockRange = 5.0;
            Vec3d rayStart = player.getEyePos();
            Vec3d rayEnd = rayStart.add(lookVec.multiply(blockRange));
            BlockHitResult blockHit = world.raycast(new RaycastContext(
                    rayStart, rayEnd,
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));
            if (blockHit.getType() == HitResult.Type.BLOCK) {
                // Generate random point on hit block face
                BlockPos blockPos = blockHit.getBlockPos();
                Direction face = blockHit.getSide();
                for (int i = 0; i < 4; i++) {
                    double x = blockPos.getX() + random.nextDouble();
                    double y = blockPos.getY() + random.nextDouble();
                    double z = blockPos.getZ() + random.nextDouble();

                    switch (face.getAxis()) {
                        case X:
                            x = face == Direction.EAST ? blockPos.getX() + 1 : blockPos.getX();
                            break;
                        case Y:
                            y = face == Direction.UP ? blockPos.getY() + 1 : blockPos.getY();
                            break;
                        case Z:
                            z = face == Direction.SOUTH ? blockPos.getZ() + 1 : blockPos.getZ();
                            break;
                    }

                    // Offset slightly to prevent Z-fighting with block surface
                    Vec3d faceNormal = new Vec3d(face.getVector());
                    destPos = new Vec3d(x, y, z).add(faceNormal.multiply(0.05));
                    spawnParticle(world, player, startPos, destPos, 0.4f, true);
                }
                hitBlock(player, world, blockPos, tier, crystal);
            } else {
                // Generate random position in front of player
                double forwardDist = 2.0; // Base distance from start position
                double spread = 1.0; // Spread radius
                for (int i = 0; i < 4; i++) {
                    // Calculate random offset
                    Vec3d randomOffset = new Vec3d(
                            (random.nextDouble() - 0.5) * 2 * spread,
                            (random.nextDouble() - 0.5) * spread, // Less vertical spread
                            (random.nextDouble() - 0.5) * 2 * spread
                    );

                    // Create position in look direction with random offset
                    destPos = startPos.add(lookVec.multiply(forwardDist)).add(randomOffset);
                    spawnParticle(world, player, startPos, destPos, 0.5f, false);
                }
            }
        }
    }

    public static double getMaxChains(ItemStack stack){
        ElementalCrystalItem item = (ElementalCrystalItem)stack.getItem();
        return item.getTier(stack) * 2;
    }
    private void spawnParticle(ServerWorld world, ServerPlayerEntity player, Vec3d startPos, Vec3d destPos, float branchChance, boolean zap) {
        List<ServerPlayerEntity> players = world.getPlayers();
        for(ServerPlayerEntity p : players) {
            ServerPlayNetworking.send(p, new LightningParticleS2CPayload(startPos.toVector3f(), destPos.toVector3f(), CrystalData.ELECTRICITY.getColorVec(), 0.025f, true, branchChance, 1));
            if(zap)
                ServerPlayNetworking.send(p, new SpawnParticleS2CPayload(ModParticles.ZAP_PARTICLE, destPos.toVector3f()));
        }
    }
    private float getDamage(ItemStack crystalStack){
        ElementalCrystalItem crystalItem = (ElementalCrystalItem) crystalStack.getItem();
        return 2;
    }
    private boolean hasChainLightning(ItemStack stack){
        ElementalCrystalItem item = (ElementalCrystalItem)stack.getItem();
        return item.hasAbilityUnlocked(stack, "chain_lightning");
    }

    private void hitEntity(ServerPlayerEntity player, ServerWorld world, LivingEntity entity, int tier, ItemStack crystal) {
        ElementalCrystalItem item = (ElementalCrystalItem)crystal.getItem();
        float damage = getDamage(crystal);
        entity.damage(world, world.getDamageSources().genericKill(), damage);
        entity.maxHurtTime = 0;
        entity.hurtTime = 0;

        if (hasChainLightning(crystal)) {
            int chainCount = (int)((ElectricityOptions) item.getCrystalOptions(crystal)).getChainLightningOptions().getChains(); // can tweak
            double radius = 4 + tier/2.0;

            Vec3d origin = entity.getPos().add(0, entity.getHeight() / 2.0, 0);

            Box area = new Box(
                    origin.x - radius, origin.y - radius, origin.z - radius,
                    origin.x + radius, origin.y + radius, origin.z + radius
            );

            List<LivingEntity> targets = world.getEntitiesByClass(
                    LivingEntity.class,
                    area,
                    (e) -> e.isAlive() && e != entity && e != player && e.isAttackable() && !e.isSpectator()
            );

            Vec3d finalOrigin = origin;
            targets.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(finalOrigin)));
            int chainsDone = 0;

            for (LivingEntity target : targets) {
                Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2.0, 0);
                target.damage(world, world.getDamageSources().indirectMagic(player, entity), damage * 0.75f); // slightly less damage for chains

                spawnParticle(world, player, origin, targetPos, 0.1f, true); // show arc
                origin = targetPos; // optional: make it bounce like a real chain
                chainsDone++;

                if (chainsDone >= chainCount) break;
            }
        }
    }

    private void hitBlock(ServerPlayerEntity player, ServerWorld world, BlockPos pos, int tier, ItemStack crystal) {

    }

    @Override
    public void holdStart(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        /*if(SpellHelper.getHandSpell(player, SpellHelper.otherButton(button)).equals(CrystalData.ELECTRICITY) && SpellHelper.isCharged(player, SpellHelper.otherButton(button))){
            SpellHelper.setCanCast(player, SpellHelper.otherButton(button), false);
        }*/
    }

    @Override
    public void holdRelease(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        //SpellHelper.setCanCast(player, SpellHelper.otherButton(button), true);
    }

    @Override
    public double getFocusCost(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        return 2;
    }

    @Override
    public SpellInput getInput() {
        return SpellInput.HOLD;
    }

    @Override
    public String toString() {
        return "lightning";
    }
}
