package net.code7y7.sorcerymod.component;

import com.mojang.serialization.Codec;

public class AppendedCrystalTier {
    private final int level;
    public static final int MAX_LEVEL = 10;

    // Codec for serialization/deserialization
    public static final Codec<AppendedCrystalTier> CODEC = Codec.INT.xmap(AppendedCrystalTier::new, AppendedCrystalTier::getValue);

    public AppendedCrystalTier(int level) {
        if (level < 0 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("Level must be between 0 and " + MAX_LEVEL);
        }
        this.level = level;
    }

    public int getValue() {
        return level;
    }

    @Override
    public String toString() {
        return "AppendedCrystalTier{level=" + level + "}";
    }
}
