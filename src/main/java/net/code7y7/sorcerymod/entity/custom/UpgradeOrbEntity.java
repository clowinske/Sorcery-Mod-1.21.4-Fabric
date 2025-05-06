package net.code7y7.sorcerymod.entity.custom;

import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlockEntity;
import net.code7y7.sorcerymod.block.ModBlocks;
import net.code7y7.sorcerymod.entity.ModEntities;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.lwjgl.openal.SOFTLoopback;

public class UpgradeOrbEntity extends Entity {
    private static final TrackedData<Integer> CRYSTAL_TYPE = DataTracker.registerData(UpgradeOrbEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TIER = DataTracker.registerData(UpgradeOrbEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> ABILITY_NAME = DataTracker.registerData(UpgradeOrbEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<BlockPos> HOME_BLOCK = DataTracker.registerData(UpgradeOrbEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Vector3f> TARGET_POS = DataTracker.registerData(UpgradeOrbEntity.class, TrackedDataHandlerRegistry.VECTOR_3F);
    private static final TrackedData<Integer> SLOT_INDEX = DataTracker.registerData(UpgradeOrbEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(UpgradeOrbEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public UpgradeOrbEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    public int getCrystalType(){
        return this.dataTracker.get(CRYSTAL_TYPE);
    }

    private void setCrystalType(int type){
        if(type > 9 || type < -1)
            type = 9;
        this.dataTracker.set(CRYSTAL_TYPE, type);
    }
    private void setTier(int tier){
        this.dataTracker.set(TIER, tier);
    }
    private void setAbilityName(String name){
        this.dataTracker.set(ABILITY_NAME, name);
    }
    private void setHomeBlock(BlockPos pos){
        this.dataTracker.set(HOME_BLOCK, pos);
    }
    private void setTargetPos(Vector3f pos){
        this.dataTracker.set(TARGET_POS, pos);
    }
    private void setSlotIndex(int index){
        this.dataTracker.set(SLOT_INDEX, index);
    }
    private void setScale(float scale){
        this.dataTracker.set(SCALE, scale);
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(CRYSTAL_TYPE, 9);
        builder.add(TIER, 0);
        builder.add(ABILITY_NAME, "");
        builder.add(HOME_BLOCK, BlockPos.ORIGIN.up(2));
        builder.add(TARGET_POS, BlockPos.ORIGIN.toCenterPos().toVector3f());
        builder.add(SLOT_INDEX, 0);
        builder.add(SCALE, 1.0f);
    }

    public static UpgradeOrbEntity createOrb(World world, BlockPos homeBlockPos, String abilityName, int crystalType, Vector3f targetPos, int slotIndex, int tier, float scale){
        UpgradeOrbEntity upgradeOrb = new UpgradeOrbEntity(ModEntities.UPGRADE_ORB, world);
        upgradeOrb.setHomeBlock(homeBlockPos);
        upgradeOrb.setAbilityName(abilityName);
        upgradeOrb.setCrystalType(crystalType);
        upgradeOrb.setTargetPos(targetPos);
        upgradeOrb.setSlotIndex(slotIndex);
        upgradeOrb.setTier(tier);
        upgradeOrb.setScale(scale);

        BlockPos spawnPos = homeBlockPos.up();
        upgradeOrb.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        return upgradeOrb;
    }
    public float getScale(){
        return this.dataTracker.get(SCALE);
    }

    @Override
    public void tick() {
        super.tick();
        if(getWorld() != null) {
            BlockState homeBlockState = getWorld().getBlockState(this.dataTracker.get(HOME_BLOCK));
            if (!homeBlockState.getBlock().equals(ModBlocks.CRYSTAL_ALTAR)) {
                this.remove();
            }

            Vector3f target = this.dataTracker.get(TARGET_POS);
            if (target != null) {
                Vec3d currentPos = this.getPos();
                Vec3d targetVec = new Vec3d(target.x(), target.y(), target.z());

                // Hovering effect using sine wave
                double hoverOffset = Math.sin(this.age * 0.1) * 0.025;

                // Apply hover to Y only
                Vec3d hoverTarget = targetVec.add(0, hoverOffset, 0);

                Vec3d direction = hoverTarget.subtract(currentPos);
                double distance = direction.length();

                if (distance > 0.05) {
                    Vec3d motion = direction.normalize().multiply(0.1); //speed
                    this.setPosition(currentPos.add(motion));
                } else {
                    this.setPosition(hoverTarget);
                }
            }
        }
    }

    public void remove(){
        this.discard();
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("crystal_type")) {
            this.dataTracker.set(CRYSTAL_TYPE, nbt.getInt("crystal_type"));
        }

        if (nbt.contains("ability_name")) {
            this.dataTracker.set(ABILITY_NAME, nbt.getString("ability_name"));
        }

        if (nbt.contains("home_block_x") && nbt.contains("home_block_y") && nbt.contains("home_block_z")) {
            BlockPos homeBlock = new BlockPos(
                    nbt.getInt("home_block_x"),
                    nbt.getInt("home_block_y"),
                    nbt.getInt("home_block_z")
            );
            this.dataTracker.set(HOME_BLOCK, homeBlock);
        }

        if (nbt.contains("target_x") && nbt.contains("target_y") && nbt.contains("target_z")) {
            Vector3f target = new Vector3f(
                    (float) nbt.getDouble("target_x"),
                    (float) nbt.getDouble("target_y"),
                    (float) nbt.getDouble("target_z")
            );
            this.dataTracker.set(TARGET_POS, target);
        }

        if (nbt.contains("slot_index")) {
            this.dataTracker.set(SLOT_INDEX, nbt.getInt("slot_index"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("crystal_type", this.dataTracker.get(CRYSTAL_TYPE));
        nbt.putString("ability_name", this.dataTracker.get(ABILITY_NAME));
        nbt.putInt("home_block_x", this.dataTracker.get(HOME_BLOCK).getX());
        nbt.putInt("home_block_y", this.dataTracker.get(HOME_BLOCK).getY());
        nbt.putInt("home_block_z", this.dataTracker.get(HOME_BLOCK).getZ());
        nbt.putDouble("target_x", this.dataTracker.get(TARGET_POS).x());
        nbt.putDouble("target_y", this.dataTracker.get(TARGET_POS).y());
        nbt.putDouble("target_z", this.dataTracker.get(TARGET_POS).z());
        nbt.putInt("slot_index", this.dataTracker.get(SLOT_INDEX));
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    public boolean isUnlocked(){
        CrystalData crystalType = CrystalData.getTypeByInt(this.dataTracker.get(CRYSTAL_TYPE));
        BlockEntity bE = getWorld().getBlockEntity(this.dataTracker.get(HOME_BLOCK));
        if(bE instanceof CrystalAltarBlockEntity blockEntity){
            ItemStack stack = blockEntity.getInventory().get(this.dataTracker.get(SLOT_INDEX));
            if(stack.getItem() instanceof InertCrystalItem item) {
                return item.hasAbilityUnlocked(stack, this.dataTracker.get(ABILITY_NAME));
            }
        }
        return false;
    }
    public void unlock(){
        CrystalData crystalType = CrystalData.getTypeByInt(this.dataTracker.get(CRYSTAL_TYPE));
        BlockEntity bE = getWorld().getBlockEntity(this.dataTracker.get(HOME_BLOCK));
        if(bE instanceof CrystalAltarBlockEntity blockEntity){
            ItemStack stack = blockEntity.getInventory().get(this.dataTracker.get(SLOT_INDEX));
            InertCrystalItem item = (InertCrystalItem)stack.getItem();
            blockEntity.syncUnlock(CrystalData.getTypeByInt(this.dataTracker.get(CRYSTAL_TYPE)), this.dataTracker.get(SLOT_INDEX), this.dataTracker.get(TIER), this.dataTracker.get(ABILITY_NAME));
            item.addCrystalAbility(stack, this.dataTracker.get(ABILITY_NAME));
        }
    }
    public void lock(){
        BlockEntity bE = getWorld().getBlockEntity(this.dataTracker.get(HOME_BLOCK));
        if(bE instanceof CrystalAltarBlockEntity blockEntity){
            ItemStack stack = blockEntity.getInventory().get(this.dataTracker.get(SLOT_INDEX));
            InertCrystalItem item = (InertCrystalItem)stack.getItem();
            //blockEntity.syncUnlock(CrystalData.getTypeByInt(this.dataTracker.get(CRYSTAL_TYPE)), this.dataTracker.get(SLOT_INDEX), this.dataTracker.get(TIER), this.dataTracker.get(ABILITY_NAME));
            item.removeCrystalAbility(stack, this.dataTracker.get(ABILITY_NAME));
        }
    }
    public boolean isFromAltar(BlockPos pos){
        return this.dataTracker.get(HOME_BLOCK).equals(pos);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getStackInHand(hand);
        int cost = this.dataTracker.get(TIER) * 3;
        if(!isUnlocked()) {
            if(player.isSneaking()){

            } else {
                if ((player.getActiveHand().equals(hand) && heldStack.isOf(Items.DIAMOND) && heldStack.getCount() >= cost) || player.isCreative()) {
                    heldStack.decrement(cost);
                    BlockEntity bE = getWorld().getBlockEntity(this.dataTracker.get((HOME_BLOCK)));
                    if (bE instanceof CrystalAltarBlockEntity altar) {
                        ItemStack stack = altar.getInventory().get(this.dataTracker.get(SLOT_INDEX));
                        if (stack.getItem() instanceof InertCrystalItem item) {
                            item.addCrystalAbility(stack, this.dataTracker.get(ABILITY_NAME));
                            item.setAppendedTier(stack, this.dataTracker.get(TIER));
                            unlock();
                            return ActionResult.SUCCESS_SERVER;
                        }
                    }
                }
            }
        } else {
            if(player.isSneaking()) {
                if (!player.isCreative()) {
                    ItemStack stackToGive = Items.DIAMOND.getDefaultStack();
                    stackToGive.setCount(cost);
                    player.giveItemStack(stackToGive);
                }
                lock();
            }
        }
        return super.interact(player, hand);
    }
}