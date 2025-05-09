package net.code7y7.sorcerymod.component;

import com.mojang.serialization.Codec;

public class DungeonSeed {
    private final long seed;

    // Codec for serialization/deserialization
    public static final Codec<DungeonSeed> CODEC = Codec.LONG.xmap(DungeonSeed::new, DungeonSeed::getValue);

    public DungeonSeed(long seed) {
        this.seed = seed;
    }

    public long getValue() {
        return seed;
    }

    @Override
    public String toString() {
        return "DungeonSeed{value=" + seed + "}";
    }
}
