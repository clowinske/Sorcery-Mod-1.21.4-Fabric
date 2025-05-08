package net.code7y7.sorcerymod.block.PortaeSigillumBlock;

import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
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

    public PortaeSigillumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PORTAE_SIGILLUM_BE, pos, state);
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
        }
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
