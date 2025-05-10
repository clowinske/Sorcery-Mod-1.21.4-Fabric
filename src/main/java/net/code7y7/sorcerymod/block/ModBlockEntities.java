package net.code7y7.sorcerymod.block;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.AttunementOrbBlock.AttuningOrbBlockEntity;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlockEntity;
import net.code7y7.sorcerymod.block.RitualGatewayBlock.RitualGatewayBlockEntity;
import net.code7y7.sorcerymod.block.SourceLensBlock.SourceLensBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<CrystalAltarBlockEntity> CRYSTAL_ALTAR_BE =
            registerBlockEntity("crystal_altar_be", CrystalAltarBlockEntity::new, ModBlocks.CRYSTAL_ALTAR);
    public static final BlockEntityType<AttuningOrbBlockEntity> ATTUNING_ORB_BE =
            registerBlockEntity("attuning_orb_be", AttuningOrbBlockEntity::new, ModBlocks.ATTUNING_ORB);
    public static final BlockEntityType<SourceLensBlockEntity> SOURCE_LENS_BE =
            registerBlockEntity("source_lens_be", SourceLensBlockEntity::new, ModBlocks.SOURCE_LENS);
    public static final BlockEntityType<RitualGatewayBlockEntity> RITUAL_GATEWAY_BE =
            registerBlockEntity("ritual_gateway_be", RitualGatewayBlockEntity::new, ModBlocks.RITUAL_GATEWAY);

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity (String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create( factory, blocks ).build();
        RegistryKey<BlockEntityType<?>> key = RegistryKey.of(RegistryKeys.BLOCK_ENTITY_TYPE, Identifier.of(SorceryMod.MOD_ID, name));
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, key, type);
    }

    public static void registerModBlocksEntities(){
        SorceryMod.LOGGER.info("Registering Mod Blocks Entities for " + SorceryMod.MOD_ID);
    }
}
