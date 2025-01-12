package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.util.crystal.CrystalAbilities;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item INERT_CRYSTAL = registerItem("inert_crystal",
            new InertCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"inert_crystal")))));
    public static final Item FIRE_CRYSTAL = registerItem("fire_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"fire_crystal"))),
                    CrystalData.FIRE.getName(), CrystalAbilities.FIRE));



    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(SorceryMod.MOD_ID,  name), item);
    }

    public static void registerModItems(){
        SorceryMod.LOGGER.info("Registering Mod Items for "+ SorceryMod.MOD_ID);
    }
}
