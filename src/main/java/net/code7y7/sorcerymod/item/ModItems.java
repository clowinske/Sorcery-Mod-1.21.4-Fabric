package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.util.crystal.CrystalAbilities;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item INERT_CRYSTAL = registerItem("inert_crystal",
            new InertCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"inert_crystal")))));
    public static final Item FIRE_CRYSTAL = registerItem("fire_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"fire_crystal"))),
                    CrystalData.FIRE, CrystalAbilities.FIRE));
    public static final Item ELECTRICITY_CRYSTAL = registerItem("electricity_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"electricity_crystal"))),
                    CrystalData.ELECTRICITY, CrystalAbilities.ELECTRICITY));
    public static final Item GRAVITY_CRYSTAL = registerItem("gravity_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"gravity_crystal"))),
                    CrystalData.GRAVITY, CrystalAbilities.GRAVITY));
    public static final Item MIND_CRYSTAL = registerItem("mind_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"mind_crystal"))),
                    CrystalData.MIND, CrystalAbilities.MIND));
    public static final Item BODY_CRYSTAL = registerItem("body_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"body_crystal"))),
                    CrystalData.BODY, CrystalAbilities.BODY));
    public static final Item SOUL_CRYSTAL = registerItem("soul_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"soul_crystal"))),
                    CrystalData.SOUL, CrystalAbilities.SOUL));
    public static final Item DISCORD_CRYSTAL = registerItem("discord_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"discord_crystal"))),
                    CrystalData.DISCORD, CrystalAbilities.DISCORD));
    public static final Item ECLIPSE_CRYSTAL = registerItem("eclipse_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"eclipse_crystal"))),
                    CrystalData.ECLIPSE, CrystalAbilities.ECLIPSE));
    public static final Item RADIANT_CRYSTAL = registerItem("radiant_crystal",
            new ElementalCrystalItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"radiant_crystal"))),
                    CrystalData.RADIANT, CrystalAbilities.RADIANT));
    public static final Item PARTICLE_TEST = registerItem("particle_test",
            new ParticleTest(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"particle_test")))));
    public static final Item CRYSTAL_POUCH = registerItem("crystal_pouch",
            new CrystalPouchItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID,"crystal_pouch")))));
    public static final Item PRISMATIC_CRYSTAL = registerItem("prismatic_crystal",
            new Item(new Item.Settings().rarity(Rarity.RARE).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID, "prismatic_crystal")))));
    public static final Item DUNGEON_KEY = registerItem("dungeon_key",
            new DungeonKeyItem(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID, "dungeon_key")))));
    public static final Item DUNGEON_KEY_PIECE = registerItem("dungeon_key_piece",
            new Item(new Item.Settings().maxCount(4).rarity(Rarity.RARE).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID, "dungeon_key_piece")))));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(SorceryMod.MOD_ID,  name), item);
    }

    public static void registerModItems(){
    }
}
