package net.code7y7.sorcerymod.block;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.AttunementOrbBlock.AttuningOrbBlock;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlock;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarTipBlock;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarTopBlock;
import net.code7y7.sorcerymod.block.RitualGatewayBlock.RitualGatewayBlock;
import net.code7y7.sorcerymod.block.SourceLensBlock.SourceLensBlock;
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
            new CrystalAltarBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SorceryMod.MOD_ID,"crystal_altar")))), true);
    public static final Block CRYSTAL_ALTAR_TOP = registerBlock("crystal_altar_top",
            new CrystalAltarTopBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SorceryMod.MOD_ID,"crystal_altar_top")))), false);
    public static final Block CRYSTAL_ALTAR_TIP = registerBlock("crystal_altar_tip",
            new CrystalAltarTipBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SorceryMod.MOD_ID,"crystal_altar_tip")))), false);
    public static final Block ATTUNING_ORB = registerBlock("attuning_orb",
            new AttuningOrbBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SorceryMod.MOD_ID,"attuning_orb")))), true);
    public static final Block SOURCE_LENS = registerBlock("source_lens",
            new SourceLensBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SorceryMod.MOD_ID,"source_lens")))), true);
    public static final Block RITUAL_GATEWAY = registerBlock("ritual_gateway",
            new RitualGatewayBlock(AbstractBlock.Settings.create().strength(4f).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SorceryMod.MOD_ID,"ritual_gateway")))), true);

    private static Block registerBlock(String name, Block block, boolean registerItem){
        if(registerItem)
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
