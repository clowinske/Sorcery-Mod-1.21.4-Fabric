package net.code7y7.sorcerymod.perk.weapon.ShortSword;

import net.code7y7.sorcerymod.item.weapon.WeaponItem;
import net.code7y7.sorcerymod.perk.WeaponPerk;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class SwiftStrikesPerk implements WeaponPerk {
    @Override
    public void onAttack(PlayerEntity player, Entity target) {

    }

    @Override
    public void onBlock(PlayerEntity player) {

    }

    @Override
    public void onTick(PlayerEntity player) {

    }

    @Override
    public boolean isCompatible(WeaponItem weapon) {
        return false;
    }

    @Override
    public Identifier getOverlayTexture() {
        return null;
    }
}
