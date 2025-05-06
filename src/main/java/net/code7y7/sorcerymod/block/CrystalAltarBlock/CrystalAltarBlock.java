package net.code7y7.sorcerymod.block.CrystalAltarBlock;

import com.mojang.serialization.MapCodec;
import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.block.ModBlocks;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.entity.custom.UpgradeOrbEntity;
import net.code7y7.sorcerymod.item.CrystalPouchItem;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
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
import org.joml.Vector3f;

public class CrystalAltarBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE1 = Block.createCuboidShape(0, 0, 0, 16, 12, 16);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(3, 12, 3, 13, 16, 13);
    private static final VoxelShape SHAPE3 = Block.createCuboidShape(3, 16, 3, 13, 32, 13);
    private static final VoxelShape SHAPE4 = Block.createCuboidShape(3, 32, 3, 13, 41, 13);
    private static final VoxelShape SHAPE = VoxelShapes.union(SHAPE1, SHAPE2, SHAPE3, SHAPE4);


    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    public CrystalAltarBlock(Settings settings) {
        super(settings.nonOpaque());
        this.setDefaultState((this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
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
        Block upBlock = world.getBlockState(pos.up()).getBlock();
        Block upupBlock = world.getBlockState(pos.up(2)).getBlock();
        if(state.getBlock() != newState.getBlock()){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof CrystalAltarBlockEntity){
                ItemScatterer.spawn(world, pos, ((CrystalAltarBlockEntity) blockEntity).getInventory());
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(world.getBlockState(pos.up()).isAir()){
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            ItemScatterer.spawn(world, pos, ((CrystalAltarBlockEntity) blockEntity).getInventory());
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        BlockState topBlock = ModBlocks.CRYSTAL_ALTAR_TOP.getDefaultState().with(FACING, state.get(FACING));
        BlockState tipBlock = ModBlocks.CRYSTAL_ALTAR_TIP.getDefaultState().with(FACING, state.get(FACING));
        world.setBlockState(pos.up(), topBlock);
        world.setBlockState(pos.up(2), tipBlock);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState upState = world.getBlockState(pos.up());
        BlockState upupState = world.getBlockState(pos.up(2));
        return upState.isReplaceable() && upupState.isReplaceable();
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient() && hand == Hand.MAIN_HAND) {
            CrystalAltarBlockEntity blockEntity = (CrystalAltarBlockEntity) world.getBlockEntity(pos);

            if(blockEntity != null) {
                if(stack.isEmpty()) {
                    // Remove crystal when clicking with an empty hand
                    if (!blockEntity.containerEmpty()) {
                        player.giveItemStack(blockEntity.removeCrystal((ServerPlayerEntity) player));
                        return ActionResult.SUCCESS_SERVER;
                    }
                } else if(stack.getItem() instanceof InertCrystalItem){
                    if (blockEntity.canPut(stack)){
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
