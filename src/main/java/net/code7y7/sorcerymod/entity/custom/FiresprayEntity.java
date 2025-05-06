package net.code7y7.sorcerymod.entity.custom;

import net.code7y7.sorcerymod.ModGamerules;
import net.code7y7.sorcerymod.entity.ModEntities;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FiresprayEntity extends ProjectileEntity {
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(FiresprayEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FIRETIME = DataTracker.registerData(FiresprayEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public FiresprayEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(DAMAGE, 6f);
        builder.add(FIRETIME, 40f);
    }

    public static FiresprayEntity create(World world, LivingEntity owner, Vec3d direction) {
        FiresprayEntity firespray = new FiresprayEntity(ModEntities.FIRESPRAY, world);
        firespray.setOwner(owner);
        firespray.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        firespray.setVelocity(direction.multiply(1));
        return firespray;
    }

    public static FiresprayEntity create(World world, LivingEntity owner, Vec3d startPos, Vec3d direction, String hand, float damage, float fireticks) {
        FiresprayEntity firespray = new FiresprayEntity(ModEntities.FIRESPRAY, world);
        firespray.setOwner(owner);
        firespray.setDamage(damage);
        firespray.setFireTime(fireticks);

        firespray.setPosition(startPos.x, startPos.y, startPos.z);
        firespray.setVelocity(direction);
        return firespray;
    }

    public void setDamage(float damage){
        this.dataTracker.set(DAMAGE, damage);
    }
    public float getDamage(){
        return this.dataTracker.get(DAMAGE);
    }
    public void setFireTime(float ticks){
        this.dataTracker.set(FIRETIME, ticks);
    }
    public float getFireTime(){
        return this.dataTracker.get(FIRETIME);
    }


    @Override
    public void tick() {
        if (this.age > 20) {
            this.discard();
            return;
        }

        double decelerationFactor = 0.9;

        Vec3d currentVelocity = this.getVelocity();
        Vec3d newVelocity = currentVelocity.multiply(decelerationFactor);
        this.setVelocity(newVelocity);

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
        Vec3d vec3d = hitResult.getType() != HitResult.Type.MISS ?
                hitResult.getPos() :
                this.getPos().add(this.getVelocity());

        this.setPosition(vec3d);
        this.tickBlockCollision();
        super.tick();

        if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
            this.hitOrDeflect(hitResult);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Entity target = entityHitResult.getEntity();
            Entity owner = this.getOwner();

            // Damage handling
            if (target.canBeHitByProjectile()) {
                target.damage(serverWorld, this.getDamageSources().genericKill(), getDamage());
                target.setOnFireFor(2);
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            World world = this.getWorld();
            BlockState blockState = world.getBlockState(blockPos);
            if(blockState.isOf(Blocks.CAMPFIRE) && !blockState.get(CampfireBlock.LIT)){
                world.setBlockState(blockPos, blockState.with(CampfireBlock.LIT, true));
            } else if(blockState.isOf(Blocks.CANDLE) && !blockState.get(CandleBlock.LIT)){
                world.setBlockState(blockPos, blockState.with(CandleBlock.LIT, true));
            } else if(blockState.isOf(Blocks.CANDLE_CAKE) && !blockState.get(CandleCakeBlock.LIT)){
                world.setBlockState(blockPos, blockState.with(CandleCakeBlock.LIT, true));
            }
            this.discard();
        }
        super.onCollision(hitResult);
    }

    protected RaycastContext.ShapeType getRaycastShapeType() {
        return RaycastContext.ShapeType.COLLIDER;
    }

    @Override
    protected boolean canHit(Entity entity) {
        boolean bool = true;
        if(entity instanceof ServerPlayerEntity player){
            bool = !player.isCreative();
        }
        return super.canHit(entity) && !entity.noClip && bool;
    }
}

