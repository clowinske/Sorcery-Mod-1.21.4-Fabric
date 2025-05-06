package net.code7y7.sorcerymod.mixin;

import net.code7y7.sorcerymod.entity.client.ModTrackedPlayerData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ModTrackedPlayerData {

    private static final TrackedData<Integer> LEFT_HAND_CHARGE =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> RIGHT_HAND_CHARGE =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> LEFT_HAND_SPELL =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> RIGHT_HAND_SPELL =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CORRUPTION =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initLeftHandCharge(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(LEFT_HAND_CHARGE, 0);
        builder.add(RIGHT_HAND_CHARGE, 0);
        builder.add(LEFT_HAND_SPELL, 9);
        builder.add(RIGHT_HAND_SPELL, 9);
        builder.add(CORRUPTION, 0);
    }

    public void setLeftHandCharge(int charge) {

        this.dataTracker.set(LEFT_HAND_CHARGE, charge);

    }
    public int getLeftHandCharge() {
        return this.dataTracker.get(LEFT_HAND_CHARGE);
    }

    public void setRightHandCharge(int charge) {
        this.dataTracker.set(RIGHT_HAND_CHARGE, charge);
    }
    public int getRightHandCharge() {
        return this.dataTracker.get(RIGHT_HAND_CHARGE);
    }

    public void setLeftHandSpell(int index) {
        this.dataTracker.set(LEFT_HAND_SPELL, index);
    }
    public int setLeftHandSpell() {
        return this.dataTracker.get(LEFT_HAND_CHARGE);
    }

    public void setRightHandSpell(int index) {
        this.dataTracker.set(RIGHT_HAND_SPELL, index);
    }
    public int setRightHandSpell() {
        return this.dataTracker.get(RIGHT_HAND_CHARGE);
    }
}
