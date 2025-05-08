package net.code7y7.sorcerymod.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.PortaeSigillumBlock.PortaeSigillumBlockEntity;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class PortaeSigillumBERenderer implements BlockEntityRenderer<PortaeSigillumBlockEntity> {

    public PortaeSigillumBERenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(PortaeSigillumBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null || !world.isClient)
            return;

        PortaeSigillumModel model = new PortaeSigillumModel(PortaeSigillumModel.getTexturedModelData().createModel());
        Identifier TEXTURE = SorceryMod.createIdentifier("textures/block/portae_sigillum.png");

        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        int baseColor = 0xFFFFFF;
        int targetColor = 0xFF0000;

        float t = (float) entity.getCraftingProgress() / entity.getMaxCraftingProgress();

        int color = lerpColor(baseColor, targetColor, t);

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
        matrices.translate(0, -0, 0);
        model.layer1.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, color);
        matrices.pop();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(time * speed2)));
        matrices.translate(0, -t/4, 0);
        model.layer2.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, color);
        matrices.pop();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(time * speed3)));
        matrices.translate(0, -t/2, 0);
        model.layer3.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, color);
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
                int progress = entity.getCraftingProgress();
                Vec3d offset = cornerOffsets[i];
                Vec3d interpolated = offset.multiply(1.0 - progress/(float)entity.maxCraftingProgress); // scales toward 0

                matrices.translate(interpolated.x, interpolated.y + 0.15 + Math.sin((time + i * 10) * 0.05) * 0.05 - (1-progress/(float)entity.maxCraftingProgress), interpolated.z);


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
    public static int lerpColor(int colorFrom, int colorTo, float t) {
        int r1 = (colorFrom >> 16) & 0xFF;
        int g1 = (colorFrom >> 8) & 0xFF;
        int b1 = colorFrom & 0xFF;

        int r2 = (colorTo >> 16) & 0xFF;
        int g2 = (colorTo >> 8) & 0xFF;
        int b2 = colorTo & 0xFF;

        int r = (int) MathHelper.lerp(t, r1, r2);
        int g = (int) MathHelper.lerp(t, g1, g2);
        int b = (int) MathHelper.lerp(t, b1, b2);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
}
