package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.entity.custom.FireballEntity;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class Fireball implements Ability {
    @Override
    public void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        SpellHelper.resetHandCharge(player, button);

        ElementalCrystalItem item = (ElementalCrystalItem) crystal.getItem();
        Vec3d lookVec = player.getRotationVector();

        double reachDistance = 100.0;
        Vec3d startPos = player.getEyePos();
        Vec3d endPos = startPos.add(lookVec.multiply(reachDistance));

        // Get block hit result
        HitResult blockHit = player.raycast(reachDistance, 1.0F, false);

        // Get entity hit result
        EntityHitResult entityHit = ProjectileUtil.raycast(
                player,
                startPos,
                endPos,
                player.getBoundingBox().stretch(lookVec.multiply(reachDistance)).expand(1.0),
                entity -> !entity.isSpectator() && entity.isAlive() && entity != player,
                reachDistance * reachDistance
        );

        // Determine closest hit
        HitResult finalHit = blockHit;
        if (entityHit != null) {
            double blockDistance = startPos.squaredDistanceTo(blockHit.getPos());
            double entityDistance = startPos.squaredDistanceTo(entityHit.getPos());

            if (entityDistance < blockDistance) {
                finalHit = entityHit;
            }
        }

        Vec3d destPos = finalHit.getPos();
        world.spawnEntity(FireballEntity.create(world, player,
                lookVec,
                destPos,
                button,
                hasRicochet(crystal, item),
                hasFireyBlow(crystal, item),
                getSpellDamage(crystal, item),
                getSpellFireticks(crystal, item),
                getMaxRicochets(crystal, item)
        ));
    }

    public boolean hasRicochet(ItemStack stack, ElementalCrystalItem crystal){
        return crystal.hasAbilityUnlocked(stack, "fireball_ricochet");
    }
    public boolean hasFireyBlow(ItemStack stack, ElementalCrystalItem crystal){
        return crystal.hasAbilityUnlocked(stack, "firey_blow");
    }
    public float getSpellDamage(ItemStack stack, ElementalCrystalItem crystal){
        return 6.0f;
    }
    public float getSpellFireticks(ItemStack stack, ElementalCrystalItem crystal){
        return crystal.getTier(stack) * 20;
    }
    public int getMaxRicochets(ItemStack stack, ElementalCrystalItem crystal){
        return (int) Math.round(crystal.getCrystalOptions(stack).getOptions().get("fireball_ricochet").abilityOptions.get(0));
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
        return "fireball";
    }
}
