package net.code7y7.sorcerymod.entity;

import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EnemyMob extends PathAwareEntity {
    protected World world;
    protected boolean hasShield = false;
    protected int shieldStrength = 0;
    protected int maxShieldStrength = 20;
    protected CrystalData shieldType = CrystalData.INERT;
    protected EnemyMob(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.world = world;
    }

    public static DefaultAttributeContainer.Builder createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20f)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.ATTACK_DAMAGE, 1);
    }

    public boolean hasShield() {
        return hasShield;
    }

    public void activateShield(int strength, CrystalData type) {
        this.hasShield = true;
        this.shieldStrength = strength;
        this.shieldType = type;
        this.maxShieldStrength = strength;
        // TODO: trigger visuals/particles/sound
    }
    public void deactivateShield(){
        this.hasShield = false;
    }

    public void damageShield(int amount, CrystalData type) {
        if (hasShield) {
            if(type.equals(shieldType)) {
                shieldStrength -= amount / 2;
            } else {
                shieldStrength -= amount;
            }
            if (shieldStrength <= 0) {
                hasShield = false;
                shieldStrength = 0;
                onShieldBreak();
            } else {
                onShieldHit();
            }
        }
    }

    protected void onShieldBreak() {
        // Override for visual effects, particles, sound, etc.
    }

    protected void onShieldHit() {
        // Called every time the shield is damaged but not broken
    }

    public CrystalData getShieldType(){
        return this.shieldType;
    }

    public int getShieldStrength() {
        return this.shieldStrength;
    }

    public int getMaxShieldStrength() {
        return maxShieldStrength;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        System.out.println(this.getHealth() + ", " + this.shieldStrength+"/"+this.maxShieldStrength + " " + this.shieldType.getName());
        if (hasShield) {
            if(shieldType != CrystalData.NONE)
                damageShield((int) amount, CrystalData.NONE);
            return false; //damage absorbed
        }
        return super.damage(world, source, amount);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasShield", hasShield);
        nbt.putInt("ShieldStrength", shieldStrength);
        nbt.putInt("MaxShieldStrength", maxShieldStrength);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("HasShield"))
            hasShield = nbt.getBoolean("HasShield");
        if (nbt.contains("ShieldStrength"))
            shieldStrength = nbt.getInt("ShieldStrength");
        if (nbt.contains("MaxShieldStrength"))
            maxShieldStrength = nbt.getInt("MaxShieldStrength");
    }
}
