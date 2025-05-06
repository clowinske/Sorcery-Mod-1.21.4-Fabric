package net.code7y7.sorcerymod.block.SourceLensBlock;

import com.mojang.serialization.MapCodec;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SourceLensBlock extends BlockWithEntity {
    private final float ROTATION_INCREMENT = 15;
    public SourceLensBlock(Settings settings) {
        super(settings);
    }
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(SourceLensBlock::new);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SourceLensBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(!world.isClient()){
            SourceLensBlockEntity blockEntity = (SourceLensBlockEntity) world.getBlockEntity(pos);
            if(blockEntity != null) {
                int direction = player.isSneaking() ? -1 : 1;
                blockEntity.adjustRotation(ROTATION_INCREMENT * direction);
                blockEntity.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
        }
        return ActionResult.SUCCESS;
    }
}