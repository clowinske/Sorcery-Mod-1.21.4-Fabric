package net.code7y7.sorcerymod.block;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block CRYSTAL_ALTAR = registerBlock("crystal_altar",
            new CrystalAltarBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SorceryMod.MOD_ID,"crystal_altar")))));

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(SorceryMod.MOD_ID, name), block);
    }
    public static void registerBlockItem(String name, Block block){
        Registry.register(Registries.ITEM, Identifier.of(SorceryMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SorceryMod.MOD_ID, name)))));
    }
    public static void registerModBlocks(){
        SorceryMod.LOGGER.info("Registering Mod Blocks for " + SorceryMod.MOD_ID);
    }
}
