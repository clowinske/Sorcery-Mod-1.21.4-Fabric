package net.code7y7.sorcerymod.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.AttunementOrbBlock.AttuningOrbBlockEntity;
import net.code7y7.sorcerymod.block.PortaeSigillumBlock.PortaeSigillumBlockEntity;
import net.code7y7.sorcerymod.entity.client.OrbModel;
import net.code7y7.sorcerymod.entity.client.PortaeSigillumModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class PortaeSigillumBERenderer implements BlockEntityRenderer<PortaeSigillumBlockEntity> {

    public PortaeSigillumBERenderer(BlockEntityRendererFactory.Context context) {

    }
    public void render(PortaeSigillumBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null || !world.isClient)
            return;

        PortaeSigillumModel model = new PortaeSigillumModel(PortaeSigillumModel.getTexturedModelData().createModel());
        Identifier TEXTURE = SorceryMod.createIdentifier("textures/block/portae_sigillum.png");

        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));

        double time = world.getTime() % 10000 + tickDelta;
        float speed1 = 0.02f;
        float speed2 = -0.035f;
        float speed3 = 0.05f;

        matrices.push();

        matrices.translate(0.5, 1.55, 0.5);
        float floatOffset = (float)Math.sin(time * 0.05) * 0.01f;
        renderItems(entity, matrices, vertexConsumers, light, time);

        matrices.translate(0, floatOffset, 0);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        // Render layers
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(time * speed1)));
        model.layer1.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(time * speed2)));
        model.layer2.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(time * speed3)));
        model.layer3.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();


        matrices.pop();
    }

    private void renderItems(PortaeSigillumBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, double time){
        List<ItemStack> itemStacks = entity.getInventory();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        Vec3d[] cornerOffsets = new Vec3d[] {
                new Vec3d(-0.3, 0.0, -0.3), // bottom-left
                new Vec3d( 0.3, 0.0, -0.3), // bottom-right
                new Vec3d(-0.3, 0.0,  0.3), // top-left
                new Vec3d( 0.3, 0.0,  0.3)  // top-right
        };

        for (int i = 0; i < itemStacks.size() && i < 4; i++) {
            ItemStack stack = itemStacks.get(i);
            if (!stack.isEmpty()) {
                matrices.push();

                // Move to corner position
                Vec3d offset = cornerOffsets[i];
                matrices.translate(offset.x, offset.y + 0.15 + Math.sin((time + i * 10) * 0.05) * 0.05 - 1, offset.z);

                // Rotate slowly around Y
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) (time * 2 % 360)));

                // Scale down a little (optional)
                matrices.scale(0.4f, 0.4f, 0.4f);

                // Render item flat (or 3D if block)
                itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

                matrices.pop();
            }
        }
    }
}
