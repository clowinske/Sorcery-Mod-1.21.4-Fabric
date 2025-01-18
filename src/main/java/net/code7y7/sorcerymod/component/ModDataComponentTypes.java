package net.code7y7.sorcerymod.component;

import net.code7y7.sorcerymod.SorceryMod;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<CrystalTier> CRYSTAL_TIER =
            register("crystal_tier", builder -> builder.codec(CrystalTier.CODEC));
    public static final ComponentType<CrystalUnlockedAbilities> CRYSTAL_UNLOCKED_ABILITIES =
            register("crystal_unlocked_abilities", builder -> builder.codec(CrystalUnlockedAbilities.CODEC));


    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator){
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(SorceryMod.MOD_ID,  name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes(){
        SorceryMod.LOGGER.info("Registering Data Component Types for" + SorceryMod.MOD_ID);
    }
}
