package net.code7y7.sorcerymod.perk.general;

import net.code7y7.sorcerymod.item.weapon.WeaponItem;
import net.code7y7.sorcerymod.perk.WeaponPerk;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class LifestealPerk implements WeaponPerk {
    private static final float LIFE_STEAL_PERCENT = 0.5f;
    @Override
    public void onAttack(PlayerEntity player, Entity target) {
        if(target instanceof LivingEntity livingTarget){
            float damageDealt = 1f;
            player.heal(damageDealt * LIFE_STEAL_PERCENT);
        }
    }

    @Override
    public void onBlock(PlayerEntity player) {

    }

    @Override
    public void onTick(PlayerEntity player) {

    }

    @Override
    public boolean isCompatible(WeaponItem weapon) {
        return true;
    }

    @Override
    public Identifier getOverlayTexture() {
        return null;
    }
}
