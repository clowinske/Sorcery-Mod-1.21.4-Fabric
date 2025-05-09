package net.code7y7.sorcerymod.client;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.block.PortaeSigillumBlock.PortaeSigillumBlockEntity;
import net.code7y7.sorcerymod.entity.client.PortaeSigillumModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
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

import static net.code7y7.sorcerymod.block.PortaeSigillumBlock.PortaeSigillumBlockEntity.KEY_ITEM;

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

        float t = (float) Math.min(entity.getCraftingProgress(), entity.getMaxCraftingProgress()) / entity.getMaxCraftingProgress();

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
        if (entity.getCraftingProgress() > 0 && entity.getInventory().get(0).isOf(KEY_ITEM)) {
            renderText(entity, matrices, vertexConsumers, light);
        }
    }

    private void renderText(PortaeSigillumBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        int remainingTicks = entity.maxPortalProgress - entity.getCraftingProgress();
        float remainingSeconds = remainingTicks / 20.0f;

        String text = String.format("%.1f", remainingSeconds);

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        // Push a new matrix for the text
        matrices.push();

        // Move up +1 block (render above block center)
        matrices.translate(0.5, 1.0, 0.5);

        // Rotate so it faces the player
        Camera camera = client.gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        // Scale down (since font is designed for screen pixels, world units are bigger)
        float scale = 0.02f;
        matrices.scale(-scale, -scale, scale); // negative X/Y flips text upright

        // Center text horizontally
        float x = -textRenderer.getWidth(text) / 2f;
        float y = 0;

        textRenderer.draw(text, x, y, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    private void renderItems(PortaeSigillumBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, double time){
        List<ItemStack> itemStacks = entity.getInventory();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        // Check if lock item is present
        ItemStack keyItemStack = null;
        for (ItemStack stack : itemStacks) {
            if (!stack.isEmpty() && stack.getItem() == KEY_ITEM) {
                keyItemStack = stack;
                break;
            }
        }

        if (keyItemStack != null) {
            matrices.push();
            int progress = entity.getCraftingProgress();

            float floatOffset = (float)Math.sin(time * 0.05) * 0.05f;
            matrices.translate(0, 0.15 + floatOffset - (1 - progress / (float)entity.maxPortalProgress), 0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(time * 2 % 360)));
            matrices.scale(0.5f, 0.5f, 0.5f);

            itemRenderer.renderItem(keyItemStack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

            matrices.pop();
        } else {
            // Render normal items in corners
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

                    int progress = entity.getCraftingProgress();
                    Vec3d offset = cornerOffsets[i];
                    Vec3d interpolated = offset.multiply(1.0 - progress / (float)entity.maxCraftingProgress);

                    matrices.translate(interpolated.x, interpolated.y + 0.15 + Math.sin((time + i * 10) * 0.05) * 0.05 - (1 - progress / (float)entity.maxCraftingProgress), interpolated.z);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) (time * 2 % 360)));
                    matrices.scale(0.4f, 0.4f, 0.4f);

                    itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

                    matrices.pop();
                }
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
