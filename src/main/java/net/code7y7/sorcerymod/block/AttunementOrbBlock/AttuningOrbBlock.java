package net.code7y7.sorcerymod.block.AttunementOrbBlock;

import com.mojang.serialization.MapCodec;
import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.code7y7.sorcerymod.screen.AttuningOrbScreen;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class AttuningOrbBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 13, 14);

    public AttuningOrbBlock(Settings settings) {
        super(settings.nonOpaque());
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(world.isClient()){
            ClientScreenOpener.open(player, this.getName());
        }

        return ActionResult.SUCCESS;
    }

    private static class ClientScreenOpener {
        static void open(PlayerEntity player, Text name) {
            MinecraftClient.getInstance().setScreen(new AttuningOrbScreen(player, name));
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.ATTUNING_ORB_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
    }
    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState downState = world.getBlockState(pos.down());
        return downState.isSideSolid(world, pos, Direction.UP, SideShapeType.CENTER);
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(AttuningOrbBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AttuningOrbBlockEntity(pos, state);
    }
}
