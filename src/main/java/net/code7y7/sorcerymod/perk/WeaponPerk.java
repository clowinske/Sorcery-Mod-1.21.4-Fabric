package net.code7y7.sorcerymod.perk;

import net.code7y7.sorcerymod.item.weapon.WeaponItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public interface WeaponPerk {
    void onAttack(PlayerEntity player, Entity target);
    void onBlock(PlayerEntity player);
    void onTick(PlayerEntity player);
    boolean isCompatible(WeaponItem weapon);
    Identifier getOverlayTexture();
}
