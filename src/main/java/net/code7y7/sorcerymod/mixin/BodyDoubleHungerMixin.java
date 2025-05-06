package net.code7y7.sorcerymod.mixin;

import net.code7y7.sorcerymod.entity.client.ModTrackedPlayerData;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(Item.class)
public abstract class BodyDoubleHungerMixin {
    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void eatFood(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> ci) {
        if(stack.contains(DataComponentTypes.FOOD)){
            if(user instanceof ServerPlayerEntity player){
                if(shouldDouble(player)) {
                    int food = stack.get(DataComponentTypes.FOOD).nutrition();
                    float saturation = stack.get(DataComponentTypes.FOOD).saturation();
                    player.getHungerManager().add(food, saturation);
                }
            }
        }
    }

    @Unique
    private boolean shouldDouble(ServerPlayerEntity player){
        //temporary solution
        List<ItemStack> crystalList = player.getInventory().main.stream()
                .filter(stack -> stack.getItem() instanceof ElementalCrystalItem)
                .collect(Collectors.groupingBy(stack -> ((ElementalCrystalItem) stack.getItem()).elementName))
                .values().stream()
                .map(list -> list.get(0))
                .limit(3)
                .collect(Collectors.toList());
        for(ItemStack stack : crystalList){
            if(stack.isOf(ModItems.BODY_CRYSTAL))
                return ((ElementalCrystalItem)stack.getItem()).hasAbilityUnlocked(stack, "ironclad_metabolism");
        }
        return false;
    }
}
