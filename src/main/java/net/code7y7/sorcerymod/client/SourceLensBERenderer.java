package net.code7y7.sorcerymod.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlockEntity;
import net.code7y7.sorcerymod.block.ModBlocks;
import net.code7y7.sorcerymod.block.SourceLensBlock.SourceLensBlockEntity;
import net.code7y7.sorcerymod.client.render.LensModel;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class SourceLensBERenderer implements BlockEntityRenderer<SourceLensBlockEntity> {
    private LensModel lensModel;
    private BakedModel rotatorModel;
    public SourceLensBERenderer(BlockEntityRendererFactory.Context context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        BakedModelManager modelManager = mc.getBakedModelManager();
        this.rotatorModel = modelManager.getModel(SorceryMod.createIdentifier("blocks/rotator"));
        this.lensModel = new LensModel(context.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(SourceLensBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getRotation()));
        matrices.translate(-0.5, -0.5, -0.5);

        matrices.pop();
    }


}
