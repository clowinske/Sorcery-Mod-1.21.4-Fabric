package net.code7y7.sorcerymod.block.PortaeSigillumBlock;

import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PortaeSigillumBlockEntity extends BlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private int craftingProgress = 0;
    public int maxCraftingProgress = 80;
    private boolean crafting = false;
    private static final int CRAFTING_RATE = 1;

    public PortaeSigillumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PORTAE_SIGILLUM_BE, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory, registries);
        this.craftingProgress = nbt.getInt("progress");
    }
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, this.inventory, true, registries);
        nbt.putInt("progress", craftingProgress);
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

    public void startCrafting(){
        if(!crafting){
            System.out.println("started crafting");
            crafting = true;
            craftingProgress = 0;
        }
    }
    private void cancelCrafting(){
        if(crafting) {
            System.out.println("cancelled crafting");
            crafting = false;
            craftingProgress = 0;
            markDirty();
        }
    }

    public boolean isCrafting(){
        return crafting;
    }

    public int getCraftingProgress(){
        return craftingProgress;
    }
    public int getMaxCraftingProgress(){
        return maxCraftingProgress;
    }
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public ItemStack removeItem(){
        for (int i = inventory.size() - 1; i >= 0; i--) {
            if (!inventory.get(i).isEmpty()) {
                ItemStack removed = inventory.get(i).copy();
                inventory.set(i, ItemStack.EMPTY);
                markDirty();
                return removed;
            }
        }
        return ItemStack.EMPTY;
    }
    public void addItem(ItemStack stack){
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, stack.split(1));
                markDirty();
                //stack.decrement(1);
                break;
            }
        }
    }

    private static int tickCount;
    public static void tick(World world, BlockPos blockPos, BlockState blockState, PortaeSigillumBlockEntity entity) {
        if (!world.isClient()) {
            tickCount++;
            if (entity.isCrafting()) {
                if(!checkRecipe(entity.inventory)){
                    entity.cancelCrafting();
                }
                entity.craftingProgress += CRAFTING_RATE;
                entity.markDirty();
                if (entity.craftingProgress >= entity.maxCraftingProgress) {
                    entity.craftingProgress = 0;
                    entity.markDirty();
                    entity.crafting = false;

                    for (int i = 0; i < entity.inventory.size(); i++) {
                        entity.inventory.set(i, ItemStack.EMPTY);
                    }

                    ItemStack output = new ItemStack(Items.DIAMOND);
                    ItemEntity itemEntity = new ItemEntity(world, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, output);
                    world.spawnEntity(itemEntity);

                    entity.markDirty();
                }
                entity.markDirty();
            } else {
                // Check for recipe inputs
                boolean validRecipe = checkRecipe(entity.inventory);
                if (validRecipe) {
                    entity.startCrafting();
                }
            }
        }
    }
    private static boolean checkRecipe(DefaultedList<ItemStack> inventory) {
        // Example: check if all 4 slots contain iron ingots
        for (ItemStack stack : inventory) {
            if (stack.isEmpty() || stack.getItem() != Items.IRON_INGOT) {
                return false;
            }
        }
        return true;
    }
    public List<ServerPlayerEntity> getPlayersInRadius(ServerWorld world, BlockPos blockPos, Double radius){
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

    public int getTickCount() {
        return tickCount;
    }
}
