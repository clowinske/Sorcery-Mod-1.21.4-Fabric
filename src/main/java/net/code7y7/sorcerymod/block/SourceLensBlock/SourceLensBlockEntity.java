package net.code7y7.sorcerymod.block.SourceLensBlock;

import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SourceLensBlockEntity extends BlockEntity {
    private float rotation = 0;
    public SourceLensBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOURCE_LENS_BE, pos, state);
    }

    public float getRotation(){
        return rotation;
    }

    public void adjustRotation(float amount){
        rotation = (rotation + amount) % 360;
        System.out.println(rotation);
    }



    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putFloat("rotation", rotation);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        rotation = nbt.getFloat("rotation");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = super.toInitialChunkDataNbt(registries);
        nbt.putFloat("rotation", rotation);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
