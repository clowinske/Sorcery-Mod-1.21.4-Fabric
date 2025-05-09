package net.code7y7.sorcerymod.component;

import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import com.mojang.serialization.Codec;
import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.spell.CrystalOptions;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<CrystalTier> CRYSTAL_TIER =
            register("crystal_tier", builder -> builder.codec(CrystalTier.CODEC));
    public static final ComponentType<AppendedCrystalTier> APPENDED_CRYSTAL_TIER =
            register("appended_crystal_tier", builder -> builder.codec(AppendedCrystalTier.CODEC));
    public static final ComponentType<CrystalUnlockedAbilities> CRYSTAL_UNLOCKED_ABILITIES =
            register("crystal_unlocked_abilities", builder -> builder.codec(CrystalUnlockedAbilities.CODEC));
    public static final ComponentType<CrystalPouchContentsComponent> CRYSTAL_POUCH_CONTENTS =
            register("crystal_pouch_contents", builder -> builder.codec(CrystalPouchContentsComponent.CODEC));
    public static final ComponentType<ImbueCharge> IMBUE_CHARGE =
            register("imbue_charge", builder -> builder.codec(ImbueCharge.CODEC));
    public static final ComponentType<String> POUCH_ID =
            register("pouch_id", builder -> builder.codec(Codec.STRING));
    public static final ComponentType<CrystalOptions> CRYSTAL_OPTIONS =
            register("crystal_options", builder -> builder.codec(CrystalOptions.CODEC));
    public static final ComponentType<DungeonSeed> DUNGEON_SEED =
            register("dungeon_seed", builder -> builder.codec(DungeonSeed.CODEC));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator){
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(SorceryMod.MOD_ID,  name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes(){

    }
}
