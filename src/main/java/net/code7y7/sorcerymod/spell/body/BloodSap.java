package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.StateSaverAndLoader;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BloodSap implements Ability {

    public double focusCost;
    @Override
    public void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        focusCost = 1;
        LivingEntity selectedEntity = findAutoAimTarget(player, 10, 25);
        ElementalCrystalItem item = (ElementalCrystalItem) crystal.getItem();

        int ticks = 10;
        if (world.getTime() % ticks != 0 || selectedEntity == null) return;

        boolean canBeHealed = selectedEntity.getHealth() < selectedEntity.getMaxHealth();
        boolean hasVitalitySurplus = false;
                //item.hasAbilityUnlocked(crystal, "vitality_surplus");
        float maxAbsorptionPoints = 4.0F;
        float healAmount = getHealth(crystal);
        float healthCost = getDamage(crystal);
        if(selectedEntity.hasInvertedHealingAndHarm()){
            selectedEntity.damage(world, world.getDamageSources().genericKill(), healAmount);
            StateSaverAndLoader.getPlayerState(player).bodySpellHealed = true;
            if (!player.isCreative()) {
                if(player.getHealth() <= healthCost)
                    player.damage(world, world.getDamageSources().genericKill(), healthCost);
                else
                    player.setHealth(player.getHealth() - healthCost);
            }
        } else if (canBeHealed) {
            StateSaverAndLoader.getPlayerState(player).bodySpellHealed = true;
            selectedEntity.heal(healAmount);

            if (!player.isCreative()) {
                player.setHealth(player.getHealth() - healthCost);
            }

            // Optional: healing particles
            world.spawnParticles(ParticleTypes.HEART, selectedEntity.getX(), selectedEntity.getY() + 1, selectedEntity.getZ(), 3, 0.3, 0.5, 0.3, 0.01);
        } else {
            focusCost = 0;

            if (hasVitalitySurplus && healAmount > 0) {
                float currentAbsorption = selectedEntity.getAbsorptionAmount();
                float newAbsorption = Math.min(currentAbsorption + healAmount, maxAbsorptionPoints);

                if (newAbsorption > currentAbsorption) {
                    // Forcefully reset then set, to guarantee change
                    selectedEntity.setAbsorptionAmount(0f);
                    selectedEntity.setAbsorptionAmount(newAbsorption);

                    if (!player.isCreative()) {
                        player.setHealth(player.getHealth() - healthCost);
                    }

                    if (selectedEntity instanceof ServerPlayerEntity targetPlayer) {
                        targetPlayer.networkHandler.sendPacket(new HealthUpdateS2CPacket(
                                targetPlayer.getHealth(),
                                (int) targetPlayer.getAbsorptionAmount(),
                                targetPlayer.getHungerManager().getSaturationLevel()
                        ));
                    }

                    world.spawnParticles(ParticleTypes.END_ROD, selectedEntity.getX(), selectedEntity.getY() + 1, selectedEntity.getZ(), 5, 0.2, 0.3, 0.2, 0.01);
                    player.sendMessage(Text.literal("Absorption applied: " + newAbsorption), false);
                }
            }

        }
    }

    private float getDamage(ItemStack crystal){
        ElementalCrystalItem item = (ElementalCrystalItem) crystal.getItem();
        if(item.hasAbilityUnlocked(crystal, "blood_sap_mastery"))
            return 1f;
        return 2f;
    }
    private float getHealth(ItemStack crystal){
        return 2f;
    }

    private LivingEntity findAutoAimTarget(ServerPlayerEntity player, double maxDistance, double maxAngleDegrees) {
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVector();
        double cosThreshold = Math.cos(Math.toRadians(maxAngleDegrees));

        LivingEntity bestTarget = null;
        double closestDistSq = maxDistance * maxDistance;

        for (LivingEntity entity : player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(eyePos, eyePos.add(lookVec.multiply(maxDistance))).expand(3.0),
                (e) -> e.isAlive() && e != player && e.isAttackable() && !e.isSpectator()
        )) {
            if (!player.canSee(entity)) continue; // Line of sight check

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

    private boolean hasBodyMastery(ItemStack stack){
        ElementalCrystalItem item = (ElementalCrystalItem) stack.getItem();
        return item.hasAbilityUnlocked(stack, "body_mastery");
    }
    private boolean hasSanguineBond(ItemStack stack){
        ElementalCrystalItem item = (ElementalCrystalItem) stack.getItem();
        return item.hasAbilityUnlocked(stack, "sanguine_bond");
    }

    @Override
    public void holdStart(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }

    @Override
    public void holdRelease(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        if(StateSaverAndLoader.getPlayerState(player).bodySpellHealed) {
            SpellHelper.resetHandCharge(player, button);
            if(hasSanguineBond(crystal)){
                LivingEntity selectedEntity = findAutoAimTarget(player, 10, 25);
                int amplifier = 0;
                int duration = 60;
                if(hasBodyMastery(crystal)) {
                    amplifier = 2;
                    duration = 100;
                }
                if(selectedEntity != null) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, amplifier), player);
                    selectedEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, amplifier), player);
                }
            }
        }
        StateSaverAndLoader.getPlayerState(player).bodySpellHealed = false;
    }
    @Override
    public double getFocusCost(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal){
        return focusCost;
    }

    @Override
    public SpellInput getInput() {
        return SpellInput.HOLD;
    }

    @Override
    public String toString() {
        return "blood_sap";
    }
}
