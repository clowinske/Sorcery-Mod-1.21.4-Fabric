package net.code7y7.sorcerymod.block.PortaeSigillumBlock;

import com.mojang.serialization.MapCodec;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlockEntity;
import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.block.ModBlocks;
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

public class PortaeSigillumBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE1 = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    private static final VoxelShape SHAPE = VoxelShapes.union(SHAPE1);

    public PortaeSigillumBlock(Settings settings) {
        super(settings.nonOpaque());
    }

    @Override
    public BlockState getPlacementState (ItemPlacementContext context) {
        return this.getDefaultState();
    }
    @Override
    protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(PortaeSigillumBlock::new);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PortaeSigillumBlockEntity(pos, state);
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
        return validateTicker(type, ModBlockEntities.PORTAE_SIGILLUM_BE, PortaeSigillumBlockEntity::tick);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof PortaeSigillumBlockEntity){
                ItemScatterer.spawn(world, pos, ((PortaeSigillumBlockEntity) blockEntity).getInventory());
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState downState = world.getBlockState(pos.down());
        return downState.isSideSolid(world, pos.down(), Direction.UP, SideShapeType.FULL);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && hand == Hand.MAIN_HAND) {
            PortaeSigillumBlockEntity blockEntity = (PortaeSigillumBlockEntity) world.getBlockEntity(pos);

            if (blockEntity != null) {
                if (stack.isEmpty()) {
                    player.giveItemStack(blockEntity.removeItem());
                } else {
                    blockEntity.addItem(player.getStackInHand(hand));
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }
}
