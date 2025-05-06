package net.code7y7.sorcerymod.block.CrystalAltarBlock;

import net.code7y7.sorcerymod.block.ModBlocks;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class CrystalAltarTopBlock extends Block {
    private static final VoxelShape SHAPE1 = Block.createCuboidShape(0, -16, 0, 16, -4, 16);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(3, -4, 3, 13, 0, 13);
    private static final VoxelShape SHAPE3 = Block.createCuboidShape(3, 0, 3, 13, 16, 13);
    private static final VoxelShape SHAPE4 = Block.createCuboidShape(3, 16, 3, 13, 25, 13);
    private static final VoxelShape SHAPE = VoxelShapes.union(SHAPE1, SHAPE2, SHAPE3, SHAPE4);

    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    public CrystalAltarTopBlock(Settings settings){
        super(settings.nonOpaque().pistonBehavior(PistonBehavior.BLOCK));
        this.setDefaultState((this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
    }
    @Override
    public BlockState getPlacementState (ItemPlacementContext context) {
        if(context.getPlayer() != null)
            return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing().getOpposite());
        return this.getDefaultState();
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(SHAPE);
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(SHAPE);
    }
    @Override
    protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
        if(world.getBlockState(pos.down()).isAir() || world.getBlockState(pos.up()).isAir()){
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(ModBlocks.CRYSTAL_ALTAR);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && hand == Hand.MAIN_HAND) {
            CrystalAltarBlockEntity blockEntity = (CrystalAltarBlockEntity) world.getBlockEntity(pos.down());

            if (blockEntity != null) {
                if (stack.isEmpty()) {
                    // Remove crystal when clicking with an empty hand
                    if (!blockEntity.containerEmpty()) {
                        player.giveItemStack(blockEntity.removeCrystal((ServerPlayerEntity) player));
                        return ActionResult.SUCCESS_SERVER;
                    }
                } else if (stack.getItem() instanceof InertCrystalItem) {
                    // Try to add crystal using the canPut method
                    if (blockEntity.canPut(stack)) {
                        boolean isInert = !((InertCrystalItem) stack.getItem()).hasElement;
                        blockEntity.addCrystal((ServerPlayerEntity) player, stack, isInert);
                        return ActionResult.SUCCESS_SERVER;
                    } else {
                        return ActionResult.PASS;
                    }
                }
            }
        }
        return ActionResult.FAIL;
    }

}
