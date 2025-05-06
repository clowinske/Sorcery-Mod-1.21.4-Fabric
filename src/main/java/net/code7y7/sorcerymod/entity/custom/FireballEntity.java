package net.code7y7.sorcerymod.entity.custom;

import net.code7y7.sorcerymod.ModGamerules;
import net.code7y7.sorcerymod.entity.ModEntities;
import net.code7y7.sorcerymod.network.SpawnParticleS2CPayload;
import net.code7y7.sorcerymod.particle.ScorchMarkParticleEffect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FireballEntity extends ProjectileEntity {
    private static final TrackedData<Boolean> SHOULD_RICOCHET = DataTracker.registerData(FireballEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SHOULD_KNOCKBACK = DataTracker.registerData(FireballEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(FireballEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FIRETIME = DataTracker.registerData(FireballEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> MAX_RICOCHETS = DataTracker.registerData(FireballEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private int ricochetCount;
    float EXPLOSION_POWER = 0.1f;

    public FireballEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.ricochetCount = 0;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SHOULD_RICOCHET, false);
        builder.add(SHOULD_KNOCKBACK, false);
        builder.add(DAMAGE, 6f);
        builder.add(FIRETIME, 40f);
        builder.add(MAX_RICOCHETS, 1);
    }

    public static FireballEntity create(World world, LivingEntity owner, Vec3d direction) {
        FireballEntity fireball = new FireballEntity(ModEntities.FIREBALL, world);
        fireball.setOwner(owner);
        fireball.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        fireball.setVelocity(direction.multiply(1));
        return fireball;
    }

    public static FireballEntity create(World world, LivingEntity owner, Vec3d direction, Vec3d destPos, String hand, boolean shouldRic, boolean shouldKnockback, float damage, float fireticks, int maxRicochets) {
        FireballEntity fireball = new FireballEntity(ModEntities.FIREBALL, world);
        fireball.setOwner(owner);
        fireball.setShouldRic(shouldRic);
        fireball.setShouldKnockback(shouldKnockback);
        fireball.setDamage(damage);
        fireball.setFireTime(fireticks);
        fireball.setMaxRicochets(maxRicochets);

        Vec3d spawnPos;
        if (world instanceof ServerWorld serverWorld && !serverWorld.getGameRules().getBoolean(ModGamerules.SIMPLE_CASTING)) {
            Vec3d rightVec = owner.getRotationVector().crossProduct(new Vec3d(0, 1, 0)).normalize();
            double handFactor = hand.equals("left") ? -0.5 : 0.5;
            spawnPos = owner.getEyePos()
                    .add(rightVec.multiply(handFactor))
                    .add(0, -0.2, 0);
        } else {
            spawnPos = owner.getEyePos();
        }

        fireball.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
        Vec3d fireballDirection = destPos.subtract(spawnPos).normalize();
        fireball.setVelocity(fireballDirection.multiply(1));
        return fireball;
    }

    public void setShouldRic(boolean should) {
        this.dataTracker.set(SHOULD_RICOCHET, should);
    }

    public boolean shouldRicochet() {
        return this.dataTracker.get(SHOULD_RICOCHET);
    }

    public void setShouldKnockback(boolean should) {
        this.dataTracker.set(SHOULD_KNOCKBACK, should);
    }

    public boolean shouldKnockback() {
        return this.dataTracker.get(SHOULD_KNOCKBACK);
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
    public void setMaxRicochets(int max){
        this.dataTracker.set(MAX_RICOCHETS, max);
    }
    public int getMaxRicochets(){
        return this.dataTracker.get(MAX_RICOCHETS);
    }


    @Override
    public void tick() {
        if (this.age > 100) {
            this.discard();
            return;
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
        Vec3d vec3d = hitResult.getType() != HitResult.Type.MISS ?
                hitResult.getPos() :
                this.getPos().add(this.getVelocity());

        this.setPosition(vec3d);
        this.tickBlockCollision();
        super.tick();

        if (this.getWorld().isClient) {
            getWorld().addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + 0.125, this.getZ(), 0, 0, 0);
        }

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
                if (shouldKnockback()) {
                    knockback(target);
                }
            }

            // Entity ricochet handling
            if (shouldRicochet()) {
                // Calculate reflection normal from entity's center
                Vec3d entityCenter = target.getBoundingBox().getCenter();
                Vec3d fireballPos = this.getPos();
                Vec3d normal = fireballPos.subtract(entityCenter).normalize();

                // Reflect velocity using same formula as block ricochet
                Vec3d velocity = this.getVelocity();
                Vec3d reflectedVelocity = velocity.subtract(normal.multiply(2 * velocity.dotProduct(normal)));

                this.setVelocity(reflectedVelocity);
                this.velocityDirty = true;
                this.ricochetCount++;

                // Check ricochet limit
                if (this.ricochetCount > getMaxRicochets()) {
                    this.discard();
                }
            } else {
                this.discard();
            }
        }
    }

    private void knockback(Entity entity) {
        entity.addVelocity(this.getVelocity().multiply(2));
        entity.addVelocity(0, 0.5, 0);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;

            Direction hitSide = blockHitResult.getSide();
            BlockPos blockPos = blockHitResult.getBlockPos();
            Vec3d spawnPos = Vec3d.ofCenter(blockPos).add(Vec3d.of(hitSide.getVector()).multiply(0.51));

            addScorch(hitSide, spawnPos);

            if (shouldRicochet()) {
                Vec3d normal = Vec3d.of(blockHitResult.getSide().getVector());
                Vec3d velocity = this.getVelocity();
                Vec3d reflectedVelocity = velocity.subtract(normal.multiply(2 * velocity.dotProduct(normal)));


                this.setVelocity(reflectedVelocity);
                this.velocityDirty = true;
                this.ricochetCount++;

                if (this.ricochetCount > getMaxRicochets()) {
                    this.discard();
                }
            } else {
                this.discard();
            }
        }
    }

    private void addScorch(Direction face, Vec3d spawnPos){
        if(!getWorld().isClient) {
            for (ServerPlayerEntity player : ((ServerWorld) getWorld()).getPlayers()) {
                player.networkHandler.sendPacket(new ParticleS2CPacket(new ScorchMarkParticleEffect(face), true, true, spawnPos.x, spawnPos.y, spawnPos.z, 0, 0, 0, 0, 1));
            }
        }
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

