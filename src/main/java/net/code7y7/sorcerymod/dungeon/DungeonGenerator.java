package net.code7y7.sorcerymod.dungeon;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {
    private static final int ROOM_SPACING = 15;
    private final Random random;

    public DungeonGenerator(Long seed) {
        this.random = new Random(seed);
    }

    public List<Room> generateDungeon(){
        List<Room> rooms = new ArrayList<>();
        BlockPos currentPos = BlockPos.ORIGIN;

        rooms.add(new Room(Room.Type.SPAWN, currentPos, getRandomRotation()));

        for(int i = 0; i < 3 + random.nextInt(2); i++){
            currentPos = currentPos.add(
                    random.nextInt(ROOM_SPACING) - ROOM_SPACING / 2,
                    0,
                    random.nextInt(ROOM_SPACING) - ROOM_SPACING / 2
            );

            rooms.add(new Room(getRandomRoomType(), currentPos, getRandomRotation()));
        }

        rooms.add(new Room(Room.Type.BOSS, currentPos.add(0, 5, 0), BlockRotation.NONE));

        return rooms;
    }
    private Room.Type getRandomRoomType() {
        Room.Type[] types = {Room.Type.COMBAT, Room.Type.PUZZLE};
        return types[random.nextInt(types.length)];
    }
    private BlockRotation getRandomRotation() {
        return BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
    }
}
