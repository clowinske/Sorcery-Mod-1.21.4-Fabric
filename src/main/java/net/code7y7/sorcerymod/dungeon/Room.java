package net.code7y7.sorcerymod.dungeon;

import com.sun.jdi.Mirror;
import net.minecraft.client.data.VariantSettings;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

public class Room {
    public enum Type{
        SPAWN, COMBAT, PUZZLE, BOSS;
    }

    private final Type type;
    private final BlockPos position;
    private final BlockRotation rotation;

    public Room(Type type, BlockPos position, BlockRotation rotation){
        this.type = type;
        this.position = position;
        this.rotation = rotation;
    }
    public Type getType(){return type;}
    public BlockPos getPosition(){return position;}
    public BlockRotation getRotation(){return rotation;}
}
