package net.code7y7.sorcerymod.entity;

import net.code7y7.sorcerymod.entity.EnemyMob;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BossMob extends EnemyMob {
    protected int maxPhases;
    protected int currentPhase;
    protected BlockPos spawnPosition = BlockPos.ORIGIN;

    protected BossMob(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.maxPhases = 0;
        this.currentPhase = 0;
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 100.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("MaxPhases", this.maxPhases);
        nbt.putInt("CurrentPhase", this.currentPhase);
        nbt.putInt("SpawnX", spawnPosition.getX());
        nbt.putInt("SpawnY", spawnPosition.getY());
        nbt.putInt("SpawnZ", spawnPosition.getZ());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        int x = 0;
        int y = 0;
        int z = 0;
        if (nbt.contains("MaxPhases")) {
            this.maxPhases = nbt.getInt("MaxPhases");
        }
        if (nbt.contains("CurrentPhase")) {
            this.currentPhase = nbt.getInt("CurrentPhase");
        }
        if(nbt.contains("SpawnX"))
            x = nbt.getInt("SpawnX");
        if(nbt.contains("SpawnY"))
            y = nbt.getInt("SpawnY");
        if(nbt.contains("SpawnZ"))
            z = nbt.getInt("SpawnZ");
        this.spawnPosition = new BlockPos(x, y, z);
    }
    public int getMaxPhases() {
        return maxPhases;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public void setPhase(int phase) {
        if (phase >= 0 && phase < maxPhases) {
            this.currentPhase = phase;
            onPhaseChanged(phase);
        }
    }

    public void nextPhase() {
        if (currentPhase < maxPhases - 1) {
            setPhase(currentPhase + 1);
        }
    }

    /**
     * Called whenever the phase changes. Override this in subclasses to implement custom behavior.
     */
    protected void onPhaseChanged(int newPhase) {
        // Subclasses can override for special phase behavior
    }

    @Override
    public boolean isPersistent() {
        return true;
    }
}

