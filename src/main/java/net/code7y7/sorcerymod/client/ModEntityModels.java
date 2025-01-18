package net.code7y7.sorcerymod.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.ModBlockEntities;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ModEntityModels {

    public static void registerModels() {
        BlockEntityRendererFactories.register(ModBlockEntities.CRYSTAL_ALTAR_BE, CrystalAltarBERenderer::new);
    }
    public static void registerModBlocksEntities(){
        SorceryMod.LOGGER.info("Registering Mod Blocks Entity Renderers for " + SorceryMod.MOD_ID);
        registerModels();
    }
}
