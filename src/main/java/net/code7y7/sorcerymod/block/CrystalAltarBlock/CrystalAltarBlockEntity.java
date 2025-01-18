package net.code7y7.sorcerymod.block.CrystalAltarBlock;

import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.network.AltarInteractSoundPayload;
import net.code7y7.sorcerymod.network.CrystalPlaceParticlePayload;
import net.code7y7.sorcerymod.particle.CrystalPlaceParticleEffect;
import net.code7y7.sorcerymod.particle.ModParticles;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrystalAltarBlockEntity extends BlockEntity {
    Direction facing = this.getCachedState().get(CrystalAltarBlock.FACING);
    /*private List<UpgradeOrbEntity> spawnedEntities = new ArrayList<>();
    private List<UpgradeOrbEntity> slotEntities1 = new ArrayList<>();
    private List<UpgradeOrbEntity> slotEntities2 = new ArrayList<>();
    private List<UpgradeOrbEntity> slotEntities3 = new ArrayList<>();*/
    private static final double PARTICLE_RADIUS = 15.0;
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
        ServerPlayNetworking.send(player, new AltarInteractSoundPayload(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.2f));
        if (isInert) {
            inventory.set(0, stack.copyAndEmpty()); //inert crystal goes to the middle (slot 0)
            //spawnOrbs("inert", 0, 0);
        } else {
            for (int i = 1; i < inventory.size(); i++) {
                if (inventory.get(i).isEmpty()) {
                    inventory.set(i, stack.copyAndEmpty()); //elemental crystals go to slots 1-3
                    //spawnOrbs(((InertCrystalItem)itemHandler.getStackInSlot(i).getItem()).elementName, i , ((InertCrystalItem)itemHandler.getStackInSlot(i).getItem()).getTier(stack));
                }
            }
        }
        //player.playSound(SoundEvents.ENTITY_ITEM_PICKUP);
        markDirty();
        //attemptUpgrade();
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
                } else {
                    //removeAllEntitiesInSlot(i);
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

    public boolean canPut(ItemStack stack){
        if(!(stack.getItem() instanceof InertCrystalItem)){
            return false; //return false if not a crystal
        } else {
            InertCrystalItem item = (InertCrystalItem) stack.getItem();
            if(!item.hasElement){
                return inventory.get(1).isEmpty(); //no item in first slot, so no elemental crystals
            } else {
                return inventory.get(0).isEmpty() && canPlaceElementalSlot(stack);
            }
        }
    }

    private static int tickCount;
    public static void tick(World world, BlockPos blockPos, BlockState blockState, CrystalAltarBlockEntity entity) {
        if (!world.isClient()) {
            tickCount++;
            if (tickCount % 2 == 0) {
                List<ServerPlayerEntity> entities = getPlayersInRadius((ServerWorld) world, blockPos);
                if (!entities.isEmpty()) {
                    for (ServerPlayerEntity player : entities) {
                        ItemStack heldItem = player.getMainHandStack();
                        if (entity.canPut(heldItem)) {
                            InertCrystalItem crystal = (InertCrystalItem) heldItem.getItem();
                            ServerPlayNetworking.send(player, new CrystalPlaceParticlePayload(blockPos, getColor(crystal)));
                        }
                    }
                }
            }
        }
    }

    public static List<ServerPlayerEntity> getPlayersInRadius(ServerWorld world, BlockPos blockPos){
        Box box = new Box(
                blockPos.getX() - PARTICLE_RADIUS, blockPos.getY() - PARTICLE_RADIUS, blockPos.getZ() - PARTICLE_RADIUS,
                blockPos.getX() + PARTICLE_RADIUS, blockPos.getY() + PARTICLE_RADIUS, blockPos.getZ() + PARTICLE_RADIUS
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
            return CrystalData.getTypeByString(stack.elementName).getColor();
    }



    public int getTickCount() {
        return tickCount;
    }
}
