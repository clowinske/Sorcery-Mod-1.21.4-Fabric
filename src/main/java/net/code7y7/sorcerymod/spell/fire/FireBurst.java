package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.entity.custom.FiresprayEntity;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.network.FireSpellParticleS2CPayload;
import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class FireBurst implements Ability {
    private int tier;
    private String button;
    private ItemStack crystal;
    @Override
    public void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        this.tier = tier;
        this.button = button;
        this.crystal = crystal;
        ElementalCrystalItem item = (ElementalCrystalItem) crystal.getItem();
        Vec3d rightVec = player.getRotationVector().crossProduct(new Vec3d(0, 1, 0)).normalize();
        double handFactor = button.equals("left") ? -0.4 : 0.4;
        double handFactor1 = button.equals("left") ? -0.05 : 0.05;
        double handFactor2 = button.equals("left")? -1 : 1;
        Vec3d lookVec = player.getRotationVector();
        Vec3d startPos = player.getEyePos().add(rightVec.multiply(handFactor))
                .add(0, -0.3, 0)
                .add(lookVec.multiply(0.5));
        Vec3d startPos1 = player.getEyePos().add(rightVec.multiply(handFactor1))
                .add(0, -0.3, 0);
        Vec3d startPos2 = player.getEyePos().add(rightVec.multiply(handFactor2))
                .add(0, -0.3, 0);

        double reachDistance = player.getBlockInteractionRange();
        Vec3d endPos = startPos.add(lookVec.multiply(reachDistance));

        HitResult blockHit = player.raycast(reachDistance, 1.0F, false);
        EntityHitResult entityHit = ProjectileUtil.raycast(
                player,
                startPos,
                endPos,
                player.getBoundingBox().stretch(lookVec.multiply(reachDistance)).expand(1.0),
                entity -> !entity.isSpectator() && entity.isAlive() && entity != player && !(entity instanceof ProjectileEntity),
                reachDistance * reachDistance
        );

        Vec3d destPos;
        if(entityHit != null && entityHit.getType() == HitResult.Type.ENTITY){
            destPos = entityHit.getPos();
        } else if(blockHit.getType() == HitResult.Type.BLOCK){
            destPos = blockHit.getPos();
        } else {
            destPos = startPos.add(lookVec);
        }

        Vec3d direction = destPos.subtract(startPos);
        Vec3d direction1;

        // Introduce randomness to the direction
        double randomnessFactor = 0.2; // Adjust this value to control the amount of randomness
        Vec3d randomOffset = new Vec3d(
            (Math.random() - 0.5) * randomnessFactor,
            (Math.random() - 0.5) * randomnessFactor,
            (Math.random() - 0.5) * randomnessFactor
        );
        direction = direction.add(randomOffset);


        if(button.equals("left")){
            direction1 = rightVec.negate().add(randomOffset);
        } else {
            direction1 = rightVec.add(randomOffset);
        }
        //normalize to set magnitude to 1
        direction = direction.normalize().multiply(0.4);
        direction1 = direction1.normalize().multiply(0.4);

        Vec3d playerVel = player.getVelocity();

        if(player.isSneaking() && item.getCrystalOptions(crystal).getOptions().get("fire_burst").abilityOptions.get(0) == 1) {
            world.spawnEntity(FiresprayEntity.create(world, player, startPos1, direction1, button, getDamage(), getFireTime()));
            spawnParticle(world, player, startPos2, direction1);
        } else {
            //ClientPlayNetworking.send(new SpawnFiresprayPayload(startPos.toVector3f(), direction.add(playerVel).toVector3f(), button, getDamage(), getFireTime()));
            world.spawnEntity(FiresprayEntity.create(world, player, startPos, direction, button, getDamage(), getFireTime()));
            spawnParticle(world, player, startPos, direction);
        }
    }

    public void spawnParticle(ServerWorld world, ServerPlayerEntity player, Vec3d start, Vec3d direction){
        direction = direction.multiply(0.5);
        List<ServerPlayerEntity> players = world.getPlayers();
        for(ServerPlayerEntity p : players) {
            ServerPlayNetworking.send(p, new FireSpellParticleS2CPayload(start.toVector3f(), direction.x, direction.y, direction.z));
        }
    }

    public float getDamage(){
            return 2.0f + tier;
    }
    public int getFireTime(){
            return 20 + (tier*10);
    }

    @Override
    public void holdStart(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
    }

    @Override
    public void holdRelease(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        SpellHelper.resetHandCharge(player, button);
    }

    @Override
    public double getFocusCost(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        return 3;
    }

    @Override
    public SpellInput getInput() {
        return SpellInput.HOLD;
    }

    @Override
    public String toString() {
        return "fire_burst";
    }
}
