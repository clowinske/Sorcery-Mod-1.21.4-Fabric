package net.code7y7.sorcerymod.entity.enemy;

import net.code7y7.sorcerymod.entity.BossMob;
import net.code7y7.sorcerymod.entity.ModEntities;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TwoPhaseBoss extends BossMob {
    private final List<UUID> chickenIds = new ArrayList<>();
    private boolean waitingForChickens = false;
    public TwoPhaseBoss(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.maxPhases = 2;
        this.currentPhase = 0;


    }
    public BlockPos getSpawnPosition() {
        return spawnPosition;
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        BlockPos groundPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.getBlockPos());
        this.requestTeleport(groundPos.getX() + 0.5, groundPos.getY(), groundPos.getZ() + 0.5);
        this.spawnPosition = groundPos;

        return super.initialize(world, difficulty, spawnReason, entityData);
    }
    @Override
    public void tick() {
        super.tick();
        if (currentPhase == 0 && this.getHealth() <= this.getMaxHealth() / 2) {
            nextPhase();
        }

        if (waitingForChickens && !getWorld().isClient) {
            checkChickensAlive((ServerWorld) this.getWorld());

            for (UUID id : chickenIds) {
                Entity entity = ((ServerWorld)getWorld()).getEntity(id);

                entity.setGlowing(true);
                Vec3d from = entity.getPos().add(0, entity.getHeight() * 0.5, 0);
                Vec3d to = this.getPos().add(0, this.getHeight() * 0.5, 0);
                spawnConnectionParticles(from, to);
            }
        }
    }

    private void spawnConnectionParticles(Vec3d from, Vec3d to){
        Vec3d dir = to.subtract(from).normalize().multiply(0.2);
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.ENCHANT,
                    from.x + dir.x * i,
                    from.y + dir.y * i,
                    from.z + dir.z * i,
                    0, 0, 0);
        }
    }



    private void checkChickensAlive(ServerWorld world) {
        chickenIds.removeIf(uuid -> {
            Entity entity = world.getEntity(uuid);
            return entity == null || !entity.isAlive();
        });

        if (chickenIds.isEmpty()) {
            waitingForChickens = false;
            deactivateShield();
            this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
            this.goalSelector.add(2, new WanderAroundGoal(this, 1.0));
            this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
            this.goalSelector.add(4, new LookAroundGoal(this));
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        if (newPhase == 1) {
            this.requestTeleport(spawnPosition.getX() + 0.5, spawnPosition.getY(), spawnPosition.getZ() + 0.5);
            this.activateShield(1, CrystalData.NONE);
            this.goalSelector.clear(goal -> true);
            this.waitingForChickens = true;

            if (!this.getWorld().isClient) {
                spawnChickens();
            }
        }
    }
    @Override
    public void travel(Vec3d movementInput) {
        if (waitingForChickens) {
            // No movement at all
            this.setVelocity(Vec3d.ZERO);
            return;
        }
        super.travel(movementInput); // Default movement
    }
    private void spawnChickens() {
        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians(i * 90);
            double dx = Math.cos(angle) * 3;
            double dz = Math.sin(angle) * 3;
            BlockPos spawnPos = this.getBlockPos().add((int)dx, 0, (int)dz);
            //ChickenEntity chicken = EntityType.CHICKEN.create(world, SpawnReason.TRIGGERED);
            ZombieEntity chicken = EntityType.ZOMBIE.create(world, SpawnReason.TRIGGERED);
            if (chicken != null) {
                chicken.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                world.spawnEntity(chicken);
                chickenIds.add(chicken.getUuid());
            }
        }
    }
}