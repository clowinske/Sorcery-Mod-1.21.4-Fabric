package net.code7y7.sorcerymod.block.CrystalAltarBlock;

import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.entity.custom.UpgradeOrbEntity;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.network.AltarEffectParticlePayload;
import net.code7y7.sorcerymod.network.AltarInteractSoundPayload;
import net.code7y7.sorcerymod.network.CrystalPlaceParticlePayload;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.code7y7.sorcerymod.util.crystal.DualAblilities;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrystalAltarBlockEntity extends BlockEntity {
    Direction facing;
    private static final double PARTICLE_RADIUS = 15.0;
    private static final double ORB_SPAWN_RADIUS = 10.0;
    private boolean orbsSpawned = false;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    public CrystalAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTAL_ALTAR_BE, pos, state);
        this.facing = state.get(CrystalAltarBlock.FACING);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory, registries);
    }
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, this.inventory, true, registries);
    }


    @Override
    public void markDirty() {
        assert world != null;
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        super.markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    public boolean hasInertCrystal() {
        return !inventory.get(0).isEmpty();
    }
    public int getDirectionAngleOffset(){
        if(facing.equals(Direction.WEST))
            return 180;
        else if(facing.equals(Direction.NORTH))
            return -90;
        else if(facing.equals(Direction.SOUTH))
            return 90;
        else
            return 0;
    }
    public void addCrystal(ServerPlayerEntity player, ItemStack stack, boolean isInert) {
        if(world != null && !world.isClient()) {
            ServerPlayNetworking.send(player, new AltarInteractSoundPayload(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.2f));
            if (isInert) {
                inventory.set(0, stack.copyAndEmpty()); //inert crystal goes to the middle (slot 0)
                spawnOrbs(stack, CrystalData.INERT, 0, 0);
            } else {
                for (int i = 1; i < inventory.size(); i++) {
                    if (inventory.get(i).isEmpty()) {
                        inventory.set(i, stack.copy()); //elemental crystals go to slots 1-3
                        markDirty();
                        spawnOrbs(stack, ((InertCrystalItem) stack.getItem()).elementType, i, ((InertCrystalItem) stack.getItem()).getTier(stack));
                        stack.copyAndEmpty();
                        break;
                    }
                }
            }
            markDirty();
        }
    }

    List<UpgradeOrbEntity> entities0 = new ArrayList<>();
    List<UpgradeOrbEntity> entities1 = new ArrayList<>();
    List<UpgradeOrbEntity> entities2 = new ArrayList<>();
    List<UpgradeOrbEntity> entities3 = new ArrayList<>();

    public void spawnOrbs(ItemStack stack, CrystalData crystalType, int slotIndex, int tier) {
        float directionAngle = getDirectionAngleOffset();
        float angle = slotIndex * 120 + 240 + directionAngle;

        if(tier > 3) tier = 3;
        // Adjust the spawn positions based on the direction and angle offset
        for (int i = 1; i <= tier; i++) {
            String ability = crystalType.getAbilities().get((i-1) * 4);
            InertCrystalItem item = (InertCrystalItem) stack.getItem();

            if(i == 1
                    || hasPreviousAbility(stack, i)
                    || i < item.getAppendedTier(stack)
            ) {
                spawnMainOrb(crystalType, slotIndex, i);
                if(item.hasAbilityUnlocked(stack, ability)){
                    spawnSubOrbs(crystalType, slotIndex, i);
                }
            }
        }
    }

    public Boolean hasPreviousAbility(ItemStack crystal, int currentTier){
        if(crystal.getItem() instanceof InertCrystalItem item) {
            CrystalData type = item.elementType;
            if(currentTier == 1)
                return true;
            return item.hasAbilityUnlocked(crystal, type.getAbilities().get((currentTier-2) * 4));
        }
        return false;
    }

    public void syncUnlock(CrystalData crystalType, int slotIndex, int tierAdded, String abilityName){
        int crystalTier = ((InertCrystalItem)getInventory().get(slotIndex).getItem()).getTier(getInventory().get(slotIndex));
        int appendedTier = ((InertCrystalItem) getInventory().get(slotIndex).getItem()).getAppendedTier(getInventory().get(slotIndex));
        if(isMainAbility(crystalType, abilityName)){
            if(tierAdded < 4) {
                if(tierAdded != 3 && tierAdded < crystalTier && tierAdded <= appendedTier+1)
                    spawnMainOrb(crystalType, slotIndex, tierAdded + 1);
                spawnSubOrbs(crystalType, slotIndex, tierAdded);
            }
        }
    }

    private void spawnMainOrb(CrystalData crystalType, int slotIndex, int tier){
        ItemStack stack = getInventory().get(slotIndex);
        float angle = (slotIndex-1)*120 + getDirectionAngleOffset();
        float radius = 1.2f + (tier-1);
        float xOffset = (float) (Math.cos(Math.toRadians(angle)) * radius);
        float zOffset = (float) (Math.sin(Math.toRadians(angle)) * radius);
        float yOffset = -0.1f + (.2f*(tier));

        Vector3f orbPos = new Vector3f(xOffset, yOffset, zOffset).add(pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f);
        String ability = crystalType.getAbilities().get((tier-1) * 4);

        UpgradeOrbEntity entity = UpgradeOrbEntity.createOrb(world, this.pos, ability, crystalType.getInt(), orbPos, slotIndex, tier, 1.0f);
        addEntity(slotIndex, entity);
        world.spawnEntity(entity);
    }

    private void spawnSubOrbs(CrystalData crystalType, int slotIndex, int tier){
        ItemStack stack = getInventory().get(slotIndex);
        float angle = (slotIndex-1)*120 + getDirectionAngleOffset();
        float radius = 1.2f + (tier-1);
        float xOffset = (float) (Math.cos(Math.toRadians(angle)) * radius);
        float zOffset = (float) (Math.sin(Math.toRadians(angle)) * radius);
        float yOffset = -0.1f + (.2f*(tier));

        Vector3f orbPos = new Vector3f(xOffset, yOffset, zOffset).add(pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f);
        double sRadius = .35;
        for(int j = 0; j < 3; j++){ //spawn sub abilities if main ability is unlocked.
            float sXOffset = (float)(Math.cos(Math.toRadians(120*j + getDirectionAngleOffset())) * sRadius);
            float sZOffset = (float)(Math.sin(Math.toRadians(120*j + getDirectionAngleOffset())) * sRadius);
            Vector3f sOrbPos = new Vector3f(sXOffset, 0, sZOffset).add(orbPos.x(), orbPos.y(), orbPos.z());
            UpgradeOrbEntity sEntity = UpgradeOrbEntity.createOrb(world, this.pos, crystalType.getAbilities().get(((tier-1) * 4) + (j+1)), crystalType.getInt(), sOrbPos, slotIndex, tier, 0.5f);
            addEntity(slotIndex, sEntity);
            world.spawnEntity(sEntity);
        }
    }

    public boolean isMainAbility(CrystalData crystalType, String abilityName){
        for(int i = 0; i < crystalType.getAbilities().size()-1; i++){
            if(abilityName.equals(crystalType.getAbilities().get(i)))
                return i % 4 == 0;
        }
        return false;
    }
    private void removeOrbs(int slotIndex) {
        List<UpgradeOrbEntity> list = switch (slotIndex) {
            case 1 -> entities1;
            case 2 -> entities2;
            case 3 -> entities3;
            default -> entities0;
        };
        list.forEach(UpgradeOrbEntity::remove);

        removeEntities(slotIndex);
    }
    private void removeAllOrbs(){
        for(int i = 0; i < 4; i++){
            removeOrbs(i);
        }
    }
    private void addEntity(int slotIndex, UpgradeOrbEntity entity) {
        switch (slotIndex){
            case 1 -> entities1.add(entity);
            case 2 -> entities2.add(entity);
            case 3 -> entities3.add(entity);
            default-> entities0.add(entity);
        }
    }

    private void removeEntities(int slotIndex){
        if(slotIndex == 1)
            entities1.clear();
        if(slotIndex == 2)
            entities2.clear();
        if(slotIndex == 3)
            entities3.clear();
        else
            entities0.clear();
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    private ItemStack crystalToUpgrade = ItemStack.EMPTY;
    public void attemptUpgrade(){
        if(sameTypeCheck()) { //check if every crystal in the altar is the same element type
            if (sameTierCheck()) { //check if every crystal is the same tier
                crystalToUpgrade = getMostAbilitiesCrystal(); //get the crystal with the most abilities
                //removeAllSpawnedEntities();
                //startAnimation();
            } else { //if they are not the same tier
                if(((InertCrystalItem)getHighestTierCrystal().getItem()).getTier(getHighestTierCrystal()) < InertCrystalItem.MAX_CRYSTAL_TIER) {
                    crystalToUpgrade = getHighestTierCrystal(); //set to highest tier
                    //removeAllSpawnedEntities();
                    //startAnimation();
                }
            }
        }
    }
    public ItemStack removeCrystal(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new AltarInteractSoundPayload(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.2f));
        for (int i = inventory.size() - 1; i >= 0; i--) {
            if (!inventory.get(i).isEmpty()) {
                if(i == 0){
                    //removeAllSpawnedEntities();
                    removeAllOrbs();
                } else {
                    //removeAllEntitiesInSlot(i);
                    removeOrbs(i);
                }
                ItemStack removed = inventory.get(i).copy();
                inventory.set(i, ItemStack.EMPTY);

                markDirty();
                return removed;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean containerEmpty(){
        for(int i = 0; i < 4; i++){
            if(!inventory.get(i).isEmpty())
                return false;
        }
        return true;
    }
    public boolean canPlaceElementalSlot(ItemStack toPlace) {
        boolean hasEmptySlot = false;

        for (int i = 1; i < inventory.size(); i++) {
            // Check if the slot is empty
            if (inventory.get(i).isEmpty()) {
                hasEmptySlot = true;
            /*} else {
                // Check the tier of the item in the slot
                if (itemHandler.getStackInSlot(i).getItem() instanceof InertCrystalItem existingCrystal) {
                    if (Objects.equals(existingCrystal.elementName, ((InertCrystalItem) toPlace.getItem()).elementName)) {
                        if(((InertCrystalItem) toPlace.getItem()).getTier(toPlace) != 1){
                            return false; //placing crystal with a tier that is not 1
                        }
                    }
                }*/
            }
        }

        // Return true only if there is at least one empty slot and no lower-tier items
        return hasEmptySlot;
    }
    public ItemStack getMostAbilitiesCrystal() {
        ItemStack highest = ItemStack.EMPTY;
        int highestTier = -1;

        for (int i = 1; i <= 3; i++) {
            ItemStack item = inventory.get(i);

            if (!item.isEmpty() && item.getItem() instanceof InertCrystalItem crystalItem) {
                int most = crystalItem.getUnlockedAbilities(item).size();

                if (most > highestTier) {
                    highestTier = most;
                    highest = item;
                }
            }
        }

        return highest; // Returns the highest-tier crystal or ItemStack.EMPTY if none are valid
    }

    public boolean hasCrystal(){
        for(int i = 1; i < inventory.size(); i++){
            if(!inventory.get(i).isEmpty())
                return true;
        }
        return false;
    }

    public ItemStack getHighestTierCrystal() {
        ItemStack highest = ItemStack.EMPTY;
        int highestTier = -1;

        for (int i = 1; i <= 3; i++) {
            ItemStack item = inventory.get(i);

            if (!item.isEmpty() && item.getItem() instanceof InertCrystalItem crystalItem) {
                int tier = crystalItem.getTier(item);

                if (tier > highestTier) {
                    highestTier = tier;
                    highest = item;
                }
            }
        }

        return highest; // Returns the highest-tier crystal or ItemStack.EMPTY if none are valid
    }

    public boolean sameTierCheck(){
        // check if slots 1, 2, and 3 are not empty
        if (inventory.get(1).isEmpty() ||
                inventory.get(2).isEmpty() ||
                inventory.get(3).isEmpty()) {
            return false;
        }
        // get the items from the slots
        InertCrystalItem item1 = (InertCrystalItem) inventory.get(1).getItem();
        InertCrystalItem item2 = (InertCrystalItem) inventory.get(2).getItem();
        InertCrystalItem item3 = (InertCrystalItem) inventory.get(3).getItem();

        // compare the element names
        return item1.getTier(inventory.get(1)) == (item2.getTier(inventory.get(2))) &&
                item2.getTier(inventory.get(2)) == (item3.getTier(inventory.get(3)));
    }

    public boolean sameTypeCheck() {
        // check if slots 1, 2, and 3 are not empty
        if (inventory.get(1).isEmpty() ||
                inventory.get(2).isEmpty() ||
                inventory.get(3).isEmpty()) {
            return false;
        }
        // get the items from the slots
        InertCrystalItem item1 = (InertCrystalItem) inventory.get(1).getItem();
        InertCrystalItem item2 = (InertCrystalItem) inventory.get(2).getItem();
        InertCrystalItem item3 = (InertCrystalItem) inventory.get(3).getItem();

        // compare the element names
        return item1.elementName.equals(item2.elementName) &&
                item2.elementName.equals(item3.elementName);
    }

    public void setInertTo(String type){
        if(type.equals(CrystalData.FIRE.getName())){
            inventory.set(0, new ItemStack(ModItems.FIRE_CRYSTAL));
        } else if(type.equals(CrystalData.GRAVITY.getName())){
            inventory.set(0, new ItemStack(ModItems.GRAVITY_CRYSTAL));
        } else if(type.equals(CrystalData.ELECTRICITY.getName())){
            inventory.set(0, new ItemStack(ModItems.ELECTRICITY_CRYSTAL));
        } else if(type.equals(CrystalData.BODY.getName())){
            inventory.set(0, new ItemStack(ModItems.BODY_CRYSTAL));
        } else if(type.equals(CrystalData.MIND.getName())){
            inventory.set(0, new ItemStack(ModItems.MIND_CRYSTAL));
        } else if(type.equals(CrystalData.SOUL.getName())){
            inventory.set(0, new ItemStack(ModItems.SOUL_CRYSTAL));
        }
        if (world != null) {
            toUpdatePacket();
        }
    }

    public List<Integer> getCrystalColors(){
        ArrayList<Integer> colors = new ArrayList<>();
        for(ItemStack stack : inventory){
            if(!stack.isEmpty() && stack.getItem() instanceof ElementalCrystalItem item){
                colors.add(CrystalData.getTypeByString(item.elementName).getColorInt());
            }
        }
        return colors;
    }

    public int getCrystalSlot(ElementalCrystalItem item){
        for(int i = 1; i < inventory.size(); i++){
            if(((ElementalCrystalItem)inventory.get(i).getItem()).elementName.equals(item.elementName)){
                return i;
            }
        }
        return -1;
    }

    public boolean canPut(ItemStack stack) {
        if (!(stack.getItem() instanceof InertCrystalItem)) {
            return false; // return false if not a crystal
        } else {
            InertCrystalItem item = (InertCrystalItem) stack.getItem();
            if (!item.hasElement) {
                return inventory.get(0).isEmpty(); // inert crystal goes in slot 0
            } else {
                // Check if there's already a crystal with the same element
                for (int i = 1; i < inventory.size(); i++) {
                    ItemStack existingStack = inventory.get(i);
                    if (!existingStack.isEmpty() && existingStack.getItem() instanceof InertCrystalItem) {
                        InertCrystalItem existingItem = (InertCrystalItem) existingStack.getItem();
                        if (existingItem.elementName.equals(item.elementName)) {
                            return false; // Crystal with same element already exists
                        }
                    }
                }
                return inventory.get(0).isEmpty() && canPlaceElementalSlot(stack);
            }
        }
    }

    private static int tickCount;
    public static void tick(World world, BlockPos blockPos, BlockState blockState, CrystalAltarBlockEntity entity) {
        if (!world.isClient()) {
            tickCount++;
            List<ServerPlayerEntity> entities = getPlayersInRadius((ServerWorld) world, blockPos, PARTICLE_RADIUS);
            List<ServerPlayerEntity> entitiesForOrbs = getPlayersInRadius((ServerWorld) world, blockPos, ORB_SPAWN_RADIUS);
            if (!entities.isEmpty()) {
                for (ServerPlayerEntity player : entities) { //happens to each player in radius
                    ItemStack heldItem = player.getMainHandStack();
                    if (entity.canPut(heldItem)) {
                        InertCrystalItem crystal = (InertCrystalItem) heldItem.getItem();
                        ServerPlayNetworking.send(player, new CrystalPlaceParticlePayload(blockPos, getColor(crystal), 1));
                    }
                    if (!entitiesForOrbs.isEmpty() && !entity.orbsSpawned) {
                        entity.removeAllOrbs();
                        entity.cleanUpOldOrbs();
                        entity.spawnOrbs();
                        entity.orbsSpawned = true;
                    } else if (entitiesForOrbs.isEmpty() && entity.orbsSpawned) {
                        entity.removeAllOrbs();
                        entity.orbsSpawned = false;
                    }
                }
            }
        }
    }

    public void spawnOrbs(){
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                if (i == 0) {
                    spawnOrbs(stack, CrystalData.INERT, 0, 0);
                } else if (stack.getItem() instanceof InertCrystalItem crystalItem) {
                    spawnOrbs(stack, crystalItem.elementType, i, crystalItem.getTier(stack));
                }
            }
        }
    }

    private ItemStack getNext(int slotIndex){
         int index = slotIndex+1;
         if(index > 3){
            index = 1;
         }
         return inventory.get(index);
    }
    private ItemStack getPrevious(int slotIndex){
        int index = slotIndex-1;
        if(index < 1){
            index = 3;
        }
        return inventory.get(index);
    }

    private void cleanUpOldOrbs(){
        if(world == null || world.isClient) return;

        List<UpgradeOrbEntity> nearbyOrbs = world.getEntitiesByClass(
                UpgradeOrbEntity.class,
                new Box(pos).expand(10),
                orb -> orb.isFromAltar(this.pos)
        );
        for(UpgradeOrbEntity orb : nearbyOrbs){
            orb.discard();
        }
    }

    public static List<ServerPlayerEntity> getPlayersInRadius(ServerWorld world, BlockPos blockPos, Double radius){
        Box box = new Box(
                blockPos.getX() - radius, blockPos.getY() - radius, blockPos.getZ() - radius,
                blockPos.getX() + radius, blockPos.getY() + radius, blockPos.getZ() + radius
        );
        List<ServerPlayerEntity> allPlayers = world.getPlayers();
        ArrayList<ServerPlayerEntity> playerList = new ArrayList<>();
        for(ServerPlayerEntity player : allPlayers){
            if(box.contains(player.getPos())){
                playerList.add(player);
            }
        }
        return playerList;
    }

    private static int getColor(InertCrystalItem stack){
        if(stack.elementName == "inert")
            return 0xf0f0f0;
        else
            return CrystalData.getTypeByString(stack.elementName).getColorInt();
    }



    public int getTickCount() {
        return tickCount;
    }
}
