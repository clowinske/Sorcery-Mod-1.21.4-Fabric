package net.code7y7.sorcerymod.block.RitualGatewayBlock;

import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.item.DungeonKeyItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.network.CrystalPlaceParticlePayload;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
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

public class RitualGatewayBlockEntity extends BlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private int craftingProgress = 0;
    public int maxCraftingProgress = 4 * 20;
    public int maxPortalProgress = 10 * 20;
    private boolean crafting = false;
    public static final List<RitualRecipe> RECIPES = new ArrayList<>();
    public static final Item KEY_ITEM = ModItems.DUNGEON_KEY;

    static {
        RECIPES.add(new RitualRecipe(
                List.of(Items.DIAMOND, Items.GOLD_INGOT, Items.EMERALD, Items.AIR),
                ModItems.INERT_CRYSTAL.getDefaultStack()
        ));
        RECIPES.add(new RitualRecipe(
                List.of(ModItems.DUNGEON_KEY_PIECE, ModItems.DUNGEON_KEY_PIECE, ModItems.DUNGEON_KEY_PIECE, ModItems.DUNGEON_KEY_PIECE),
                ModItems.DUNGEON_KEY.getDefaultStack()
        ));
        RECIPES.add(new RitualRecipe(
                List.of(KEY_ITEM, Items.AIR, Items.AIR, Items.AIR),
                ItemStack.EMPTY
        ));
    }


    public RitualGatewayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_GATEWAY_BE, pos, state);
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
            crafting = true;
            craftingProgress = 0;
        }
    }
    private void cancelCrafting(){
        if(crafting) {
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
        boolean isLockItem = stack.getItem() == KEY_ITEM;

        if (isLockItem) {
            for (ItemStack item : inventory) {
                if (item.getItem() == KEY_ITEM) {
                    return; //already has a key
                }
            }

            for (ItemStack item : inventory) {
                if (!item.isEmpty()) {
                    return; //already has other items
                }
            }

            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).isEmpty()) {
                    inventory.set(i, stack.split(1));
                    markDirty();
                    break;
                }
            }
            return;
        }

        for (ItemStack item : inventory) {
            if (item.getItem() == KEY_ITEM) {
                return; //dont allow other items if key is in inventory
            }
        }

        //insert regular items
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, stack.split(1));
                markDirty();
                break;
            }
        }
    }


    private static int tickCount;
    public static void tick(World world, BlockPos blockPos, BlockState blockState, RitualGatewayBlockEntity entity) {
        if (!world.isClient()) {
            tickCount++;
            List<ServerPlayerEntity> entities = getPlayersInRadius((ServerWorld) world, blockPos, 15.0);
            if (!entities.isEmpty()) {
                for (ServerPlayerEntity player : entities) { //happens to each player in radius
                    ItemStack heldItem = player.getMainHandStack();
                    if(heldItem.isOf(ModItems.DUNGEON_KEY) && entity.getInventory().get(0).isEmpty())
                        ServerPlayNetworking.send(player, new CrystalPlaceParticlePayload(blockPos, CrystalData.INERT.getColorInt(), 0));

                }
            }

            if (entity.isCrafting()) {
                if(checkRecipe(entity.inventory) == null){
                    entity.cancelCrafting();
                }
                int maxProgress = entity.maxCraftingProgress;
                if(entity.inventory.get(0).isOf(KEY_ITEM))
                    maxProgress = entity.maxPortalProgress;
                entity.craftingProgress += 1;
                entity.markDirty();
                if (entity.craftingProgress >= maxProgress) {
                    entity.craftingProgress = 0;
                    entity.markDirty();
                    entity.crafting = false;

                    if(checkRecipe(entity.inventory) != null) {
                        ItemStack output = checkRecipe(entity.inventory).getResult();
                        if(output.isOf(ModItems.DUNGEON_KEY))
                            ((DungeonKeyItem) output.getItem()).setSeed(output, ((DungeonKeyItem) output.getItem()).generateRandomSeed());
                        ItemEntity itemEntity = new ItemEntity(world, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, output);
                        world.spawnEntity(itemEntity);
                    }
                    for (int i = 0; i < entity.inventory.size(); i++) {
                        entity.inventory.set(i, ItemStack.EMPTY);
                    }
                    entity.markDirty();
                }
                entity.markDirty();
            } else {
                // Check for recipe inputs
                boolean validRecipe = checkRecipe(entity.inventory) != null;
                if (validRecipe) {
                    entity.startCrafting();
                }
            }
        }
    }
    public static RitualRecipe checkRecipe(List<ItemStack> inputItems) {
        List<Item> inputIngredients = inputItems.stream()
                .map(ItemStack::getItem)
                .toList();

        for (RitualRecipe recipe : RECIPES) {
            if (recipe.getIngredients().containsAll(inputIngredients) &&
                    inputIngredients.containsAll(recipe.getIngredients())) {
                return recipe;
            }
        }
        return null;
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

    public int getTickCount() {
        return tickCount;
    }
}
