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
    public static final Item ELECTRICITY_CRYSTAL = registerItem("electricity_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"electricity_crystal"))),
                    CrystalData.ELECTRICITY.getName(), CrystalAbilities.ELECTRICITY));
    public static final Item GRAVITY_CRYSTAL = registerItem("gravity_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"gravity_crystal"))),
                    CrystalData.GRAVITY.getName(), CrystalAbilities.GRAVITY));
    public static final Item MIND_CRYSTAL = registerItem("mind_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"mind_crystal"))),
                    CrystalData.MIND.getName(), CrystalAbilities.MIND));
    public static final Item BODY_CRYSTAL = registerItem("body_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"body_crystal"))),
                    CrystalData.BODY.getName(), CrystalAbilities.BODY));
    public static final Item SOUL_CRYSTAL = registerItem("soul_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"soul_crystal"))),
                    CrystalData.SOUL.getName(), CrystalAbilities.SOUL));
    public static final Item DISCORD_CRYSTAL = registerItem("discord_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"discord_crystal"))),
                    CrystalData.DISCORD.getName(), CrystalAbilities.DISCORD));
    public static final Item ECLIPSE_CRYSTAL = registerItem("eclipse_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"eclipse_crystal"))),
                    CrystalData.ECLIPSE.getName(), CrystalAbilities.ECLIPSE));
    public static final Item RADIANT_CRYSTAL = registerItem("radiant_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"radiant_crystal"))),
                    CrystalData.RADIANT.getName(), CrystalAbilities.RADIANT));
    public static final Item PARTICLE_TEST = registerItem("particle_test",
            new ParticleTest(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"particle_test")))));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(SorceryMod.MOD_ID,  name), item);
    }

    public static void registerModItems(){
        SorceryMod.LOGGER.info("Registering Mod Items for "+ SorceryMod.MOD_ID);
    }
}
