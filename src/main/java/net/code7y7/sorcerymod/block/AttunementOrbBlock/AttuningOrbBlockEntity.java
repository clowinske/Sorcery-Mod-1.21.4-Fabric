package net.code7y7.sorcerymod.block.AttunementOrbBlock;

import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class AttuningOrbBlockEntity extends BlockEntity {
    public AttuningOrbBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATTUNING_ORB_BE, pos, state);
    }

    private static int tickCount;

    public static void tick(World world, BlockPos blockPos, BlockState blockState, AttuningOrbBlockEntity entity) {
        if (world instanceof ServerWorld serverWorld) {
            tickCount++;
        }
    }
    public int getTickCount() {
        return tickCount;
    }
}
