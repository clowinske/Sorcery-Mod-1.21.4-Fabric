package net.code7y7.sorcerymod.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.List;

public class CrystalUnlockedAbilities {
    private final List<String> strings;

    public static final int MAX_SIZE = 12;

    public static final Codec<CrystalUnlockedAbilities> CODEC = Codec.STRING.listOf()
            .flatXmap(
                    list -> validateSize(list).map(CrystalUnlockedAbilities::new),
                    stringList -> validateSize(stringList.strings).map(result -> result) // Pass-through validation
            );

    private static DataResult<List<String>> validateSize(List<String> list) {
        if (list.size() <= MAX_SIZE) {
            return DataResult.success(list);
        }
        return DataResult.error(() -> "List must not contain more than " + MAX_SIZE + " strings.");
    }

    public CrystalUnlockedAbilities(List<String> strings) {
        if (strings.size() > MAX_SIZE) {
            throw new IllegalArgumentException("List must not contain more than " + MAX_SIZE + " strings.");
        }
        this.strings = strings;
    }

    public List<String> getAbilities() {
        return strings;
    }

    @Override
    public String toString() {
        return "StringList{" +
                "strings=" + strings +
                '}';
    }
}
