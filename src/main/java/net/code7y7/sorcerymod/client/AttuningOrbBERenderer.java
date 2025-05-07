package net.code7y7.sorcerymod.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.AttunementOrbBlock.AttuningOrbBlockEntity;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlockEntity;
import net.code7y7.sorcerymod.entity.client.OrbModel;
import net.code7y7.sorcerymod.entity.client.SpellModel;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.particle.LightningParticleEffect;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttuningOrbBERenderer implements BlockEntityRenderer<AttuningOrbBlockEntity> {

    public AttuningOrbBERenderer(BlockEntityRendererFactory.Context context) {

    }
    public void render(AttuningOrbBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null || !world.isClient)
            return;

        OrbModel model = new OrbModel(OrbModel.getTexturedModelData().createModel());
        Identifier TEXTURE = SorceryMod.createIdentifier("textures/entity/orb.png");

        matrices.push();
        double time = world.getTime() % 10000 + tickDelta;
        double offset = Math.sin(time * 0.05) * 0.05; //amplitude = 0.1, frequency = 0.1
        matrices.translate(0.5, offset-0.35, 0.5);

        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        model.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
