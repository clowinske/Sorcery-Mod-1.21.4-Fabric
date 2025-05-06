package net.code7y7.sorcerymod.spell.electricity;

import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.network.LightningParticleS2CPayload;
import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class LightningBolt implements Ability {
    @Override
    public void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        ElementalCrystalItem itemCrystal = (ElementalCrystalItem) crystal.getItem();
        SpellHelper.resetHandCharge(player, button);

        Vec3d lookVec = player.getRotationVector();
        Vec3d eyePos = player.getEyePos();

        // Raycast from the eye position (clean and accurate)
        double reachDistance = 10.0;
        Vec3d endPos = eyePos.add(lookVec.multiply(reachDistance));

        Vec3d hitPos;
        Entity autoTarget = findAutoAimTarget(player, 10.0, 7.0); // 10 blocks, 5° cone
        if (autoTarget != null) {
            Vec3d targetPos = autoTarget.getPos().add(0, autoTarget.getStandingEyeHeight() / 2.0, 0);
            lookVec = targetPos.subtract(eyePos).normalize();
            System.out.println(autoTarget);
            hitEntity(player, world, autoTarget, tier, crystal);
            hitPos = targetPos;
        } else {
            // Fall back to block raycast if no entity was hit
            BlockHitResult blockHit = world.raycast(new RaycastContext(
                    eyePos,
                    endPos,
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));
            hitPos = blockHit.getType() != HitResult.Type.MISS ? blockHit.getPos() : endPos;
        }

        // Visual particle start from hand (for show only)
        Vec3d rightVec = player.getRotationVector().crossProduct(new Vec3d(0, 1, 0)).normalize();
        double handOffset = button.equals("left") ? -0.4 : 0.4;
        Vec3d handPos = player.getEyePos()
                .add(rightVec.multiply(handOffset))
                .add(0, -0.3, 0)
                .add(lookVec.multiply(0.5));

        spawnParticle(world, player, handPos, hitPos, 0.1f);
    }

    private boolean hasChainLightningBolt(ItemStack stack){
        ElementalCrystalItem item = (ElementalCrystalItem)stack.getItem();
        return item.hasAbilityUnlocked(stack, "chain_lightning_bolt");
    }

    private Entity findAutoAimTarget(ServerPlayerEntity player, double maxDistance, double maxAngleDegrees) {
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVector();
        double cosThreshold = Math.cos(Math.toRadians(maxAngleDegrees));

        Entity bestTarget = null;
        double closestDistSq = maxDistance * maxDistance;

        for (Entity entity : player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(eyePos, eyePos.add(lookVec.multiply(maxDistance))).expand(3.0),
                (e) -> e.isAlive() && e != player && e.isAttackable() && !e.isSpectator()
        )) {
            Vec3d toEntity = entity.getPos().add(0, entity.getStandingEyeHeight() / 2.0, 0).subtract(eyePos);
            double distSq = toEntity.lengthSquared();
            Vec3d toEntityNorm = toEntity.normalize();
            double dot = lookVec.dotProduct(toEntityNorm);

            if (dot > cosThreshold && distSq < closestDistSq) {
                closestDistSq = distSq;
                bestTarget = entity;
            }
        }

        return bestTarget;
    }



    private void spawnParticle(ServerWorld world, ServerPlayerEntity player, Vec3d startPos, Vec3d destPos, float branchChance) {
        List<ServerPlayerEntity> players = world.getPlayers();
        for(ServerPlayerEntity p : players) {
            ServerPlayNetworking.send(p, new LightningParticleS2CPayload(startPos.toVector3f(), destPos.toVector3f(), CrystalData.ELECTRICITY.getColorVec(), 0.1f, true, branchChance, 3));
        }
    }

    public static double getMaxChains(ItemStack stack){
        ElementalCrystalItem item = (ElementalCrystalItem)stack.getItem();
        return item.getTier(stack) * 2;
    }

    private void hitEntity(ServerPlayerEntity player, ServerWorld world, Entity entity, int tier, ItemStack crystal) {
        ElementalCrystalItem item = (ElementalCrystalItem)crystal.getItem();
        float damage = getDamage(crystal);
        entity.damage(world, world.getDamageSources().genericKill(), damage);

        if (hasChainLightningBolt(crystal)) {
            int chainCount = (int)((ElectricityOptions) item.getCrystalOptions(crystal)).getChainLightningBoltOptions().getBoltChains(); // can tweak
            double radius = 5.0 + tier * 1.5;

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

                spawnParticle(world, player, origin, targetPos, 0.1f); // show arc
                origin = targetPos; // optional: make it bounce like a real chain
                chainsDone++;

                if (chainsDone >= chainCount) break;
            }
        }
    }
    private float getDamage(ItemStack crystalStack){
        ElementalCrystalItem crystalItem = (ElementalCrystalItem) crystalStack.getItem();
        return 4;
    }

    private void hitBlock(ServerPlayerEntity player, ServerWorld world, BlockPos pos, int tier, ItemStack crystal) {

    }

    @Override
    public void holdStart(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }

    @Override
    public void holdRelease(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }

    @Override
    public double getFocusCost(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        return 20;
    }

    @Override
    public SpellInput getInput() {
        return SpellInput.SINGLE;
    }

    @Override
    public String toString() {
        return "lightning_bolt";
    }
}
