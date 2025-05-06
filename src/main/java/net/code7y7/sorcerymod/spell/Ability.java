package net.code7y7.sorcerymod.spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public interface Ability {
    void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal);
    void holdStart(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal);
    void holdRelease(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal);
    double getFocusCost(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal);
    SpellInput getInput();
    String toString();
}
