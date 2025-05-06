package net.code7y7.sorcerymod.entity.enemy;

import net.code7y7.sorcerymod.entity.EnemyMob;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Class1Enemy extends EnemyMob {
    World world;
    public Class1Enemy(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.world = world;
        if(world instanceof ServerWorld){
            this.initGoals();
        }
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.activateShield((int) this.getHealth(), CrystalData.INERT);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void onShieldBreak() {
        world.playSound(null, getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.HOSTILE, 1.0F, 1.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.add(1, new AttackGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0f));
        super.initGoals();
    }
}
