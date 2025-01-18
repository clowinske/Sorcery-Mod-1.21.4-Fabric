package net.code7y7.sorcerymod.block.CrystalAltarBlock;

import com.mojang.serialization.MapCodec;
import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrystalAltarBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 12, 16);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    public CrystalAltarBlock(Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(FACING, Direction.NORTH ));
    }

    @Override
    public BlockState getPlacementState (ItemPlacementContext context) {
        if(context.getPlayer() != null)
            return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing().getOpposite());
        return this.getDefaultState();
    }
    @Override
    protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(CrystalAltarBlock::new);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalAltarBlockEntity(pos, state);

    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.CRYSTAL_ALTAR_BE, CrystalAltarBlockEntity::tick);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof CrystalAltarBlockEntity){
                ItemScatterer.spawn(world, pos, ((CrystalAltarBlockEntity) blockEntity).getInventory());
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && hand.equals(hand.MAIN_HAND)) { //happens on server and only executes on main hand

            CrystalAltarBlockEntity blockEntity = (CrystalAltarBlockEntity) world.getBlockEntity(pos); //defines the Crystal Altar Block Entity #use is executing from

            if (stack.getItem() instanceof InertCrystalItem && !((InertCrystalItem) stack.getItem()).hasElement) { //if heldItem is a crystal that does not have an element (inert)
                if (blockEntity.containerEmpty()) { //if there are no elemental or inert crystals
                    blockEntity.addCrystal((ServerPlayerEntity) player, stack, true); //add inert crystal

                    return ActionResult.SUCCESS_SERVER; //Added inert crystal to altar
                } else
                    return ActionResult.PASS; //inert crystal is already in altar

            } else if (stack.getItem() instanceof InertCrystalItem && ((InertCrystalItem) stack.getItem()).hasElement) { //if heldItem is a crystal that has an element
                if (!blockEntity.hasInertCrystal() && blockEntity.canPlaceElementalSlot(stack)) { //if altar has no inert crystal, and empty elemental slots

                    blockEntity.addCrystal((ServerPlayerEntity) player, stack, false); //add elemental crystal

                    return ActionResult.SUCCESS_SERVER; //Added elemental crystal to altar

                } else
                    return ActionResult.PASS; //has inert crystal, or no free slots
            } else if (stack.isEmpty()) { //clicked with empty hand
                if (!blockEntity.containerEmpty()) {  //if there is something to take out
                    player.giveItemStack(blockEntity.removeCrystal((ServerPlayerEntity) player));
                    return ActionResult.SUCCESS_SERVER; // removed crystal from altar
                }
            } else {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.FAIL; //if nothing else happens
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return super.onUse(state, world, pos, player, hit);
    }
}
