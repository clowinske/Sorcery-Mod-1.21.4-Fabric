package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.entity.custom.FireballEntity;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class AdrenalineRush implements Ability {
    @Override
    public void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        SpellHelper.resetHandCharge(player, button);

        StatusEffectInstance haste;
        StatusEffectInstance speed;
        int duration = 20 * tier + 40;
        if(hasBodyMastery(crystal)){
            haste = new StatusEffectInstance(StatusEffects.HASTE, duration, 2);
            speed = new StatusEffectInstance(StatusEffects.SPEED, duration, 2);
        } else {
            haste = new StatusEffectInstance(StatusEffects.HASTE, duration, 1);
            speed = new StatusEffectInstance(StatusEffects.SPEED, duration, 1);
        }
        player.addStatusEffect(haste);
        player.addStatusEffect(speed);
    }

    private boolean hasBodyMastery(ItemStack stack){
        ElementalCrystalItem item = (ElementalCrystalItem) stack.getItem();
        return item.hasAbilityUnlocked(stack, "body_mastery");
    }

    @Override
    public void holdStart(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }

    @Override
    public void holdRelease(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }
    @Override
    public double getFocusCost(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal){
        return 10;
    }

    @Override
    public SpellInput getInput() {
        return SpellInput.SINGLE;
    }

    @Override
    public String toString() {
        return "adrenaline_rush";
    }
}
