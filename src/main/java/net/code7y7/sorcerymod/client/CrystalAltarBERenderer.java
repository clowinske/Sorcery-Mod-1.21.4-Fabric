package net.code7y7.sorcerymod.client;

import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlockEntity;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.item.property.select.DisplayContextProperty;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.Objects;

public class CrystalAltarBERenderer implements BlockEntityRenderer<CrystalAltarBlockEntity > {
    public CrystalAltarBERenderer(BlockEntityRendererFactory.Context context) {

    }
    @Override
    public void render(CrystalAltarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if(world == null)
            return;
        int SEGMENTS = 6;
        float HEXAGON_RADIUS = 1.5f;
        float ORBIT_RADIUS = .4f;
        int directionAngle = entity.getDirectionAngleOffset();
        if(world.isClient) {
            BlockPos pos = entity.getPos();
            ItemStack slot0 = entity.getInventory().get(0);

            if (!slot0.isEmpty() && slot0.isOf(ModItems.INERT_CRYSTAL)) {
                renderFloatingItem(world, pos, slot0, entity.getTickCount(), matrices, vertexConsumers, light, overlay, 0, 1.5, 0);
                renderCircle(matrices, vertexConsumers, HEXAGON_RADIUS, 0.5f, 0.85f, 0.5f, SEGMENTS, "none", directionAngle, entity);
            } else if(!slot0.isEmpty()) {
                renderFloatingItem(world, pos, slot0, entity.getTickCount(), matrices, vertexConsumers, light, overlay, 0, 1.5, 0);
            }

            for(int i = 0; i < getHighestTier(entity.getInventory()); i++){ //render base lines for each tier
                renderCircle(matrices, vertexConsumers, HEXAGON_RADIUS * (i+1), 0.5f, 0.749f + ((i+1) * 0.1f), 0.5f, SEGMENTS, "none", directionAngle, entity);
            }
            for (int i = 1; i <= 3; i++) { // add each crystals' color circle
                ItemStack elementalCrystal = entity.getInventory().get(i);
                if (!elementalCrystal.isEmpty()) {
                    // angle calculation for each crystal (120 degrees apart)
                    float angle = (120 * (i - 1) + directionAngle);

                    double xOffset = Math.cos(Math.toRadians(angle)) * 0.5;
                    double zOffset = Math.sin(Math.toRadians(angle)) * 0.5;
                    double yOffset = 1.2;

                    renderFloatingItem(world, pos, elementalCrystal, entity.getTickCount(), matrices, vertexConsumers, light, overlay, xOffset, yOffset, zOffset);

                    int crystalTier = ((InertCrystalItem) elementalCrystal.getItem()).getTier(elementalCrystal);
                    if (crystalTier > 3) {
                        crystalTier = 3;
                    }

                    String crystalType = ((InertCrystalItem) elementalCrystal.getItem()).elementName;

                    for (int j = 1; j < crystalTier + 1; j++) {
                        renderCircle(matrices, vertexConsumers, HEXAGON_RADIUS * j, 0.5f, 0.75f + (j * 0.1f), 0.5f, SEGMENTS, i, crystalType, directionAngle, entity); // Big circles

                        double smallXOffset = HEXAGON_RADIUS * (j) * Math.cos(Math.toRadians(angle));
                        double smallZOffset = HEXAGON_RADIUS * (j) * Math.sin(Math.toRadians(angle));

                        renderCircle(matrices, vertexConsumers, ORBIT_RADIUS, 0.5f + (float) smallXOffset, .75f + (j * 0.1f), 0.5f + (float) smallZOffset, SEGMENTS, crystalType, directionAngle, entity); // Little circles
                    }
                }
            }
        }
    }
    private int getHighestTier(DefaultedList<ItemStack> inventory){
        int highest = 0;
        for(int i = 1; i <= 3; i++){
            if(!inventory.get(i).isEmpty() && ((InertCrystalItem)inventory.get(i).getItem()).getTier(inventory.get(i)) > highest){
                highest = ((InertCrystalItem)inventory.get(i).getItem()).getTier(inventory.get(i));
            }
        }
        if(highest > 3)
            highest = 3;
        return highest;
    }
    private void renderCircle(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float radius, float xOffset, float yOffset, float zOffset, int segments, String type, int directionAngle, CrystalAltarBlockEntity entity) {
        VertexConsumer builder = vertexConsumers.getBuffer(RenderLayer.getLightning());

        float lineThickness = 0.05f; // Approx. 1 pixel thick
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        for (int i = 0; i < segments; i++) {
            // Calculate the angles for the current and next segment
            double angle1 = (2 * Math.PI / segments) * i + Math.toRadians(directionAngle);
            double angle2 = (2 * Math.PI / segments) * (i + 1) + Math.toRadians(directionAngle);

            // Calculate the x, z coordinates for the current and next segment
            float x1 = (float) (radius * Math.cos(angle1) + xOffset) + entity.getPos().getX();
            float z1 = (float) (radius * Math.sin(angle1) + zOffset) + entity.getPos().getZ();
            float x2 = (float) (radius * Math.cos(angle2) + xOffset) + entity.getPos().getX();
            float z2 = (float) (radius * Math.sin(angle2) + zOffset) + entity.getPos().getZ();

            // Compute the vector perpendicular to the line segment for thickness
            float dx = x2 - x1;
            float dz = z2 - z1;
            float length = (float) Math.sqrt(dx * dx + dz * dz);
            dx /= length;
            dz /= length;

            // Perpendicular offset vector
            float offsetX = -dz * lineThickness / 2;
            float offsetZ = dx * lineThickness / 2;

            // Define the vertices of the thick line segment as a quad
            float x1a = x1 + offsetX - entity.getPos().getX(), z1a = z1 + offsetZ - entity.getPos().getZ();
            float x1b = x1 - offsetX - entity.getPos().getX(), z1b = z1 - offsetZ - entity.getPos().getZ();
            float x2a = x2 + offsetX - entity.getPos().getX(), z2a = z2 + offsetZ - entity.getPos().getZ();
            float x2b = x2 - offsetX - entity.getPos().getX(), z2b = z2 - offsetZ - entity.getPos().getZ();

            // Determine the color based on the type
            int rgb;
            if ("none".equals(type)) {
                rgb = 0x909090; // Default gray color
            } else {
                rgb = CrystalData.getTypeByString(type).getColor();
            }
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;

            // Draw the quad (two triangles)
            builder.vertex(positionMatrix, x1a, yOffset, z1a).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
            builder.vertex(positionMatrix, x2a, yOffset, z2a).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
            builder.vertex(positionMatrix, x2b, yOffset, z2b).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);

            builder.vertex(positionMatrix, x1a, yOffset, z1a).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
            builder.vertex(positionMatrix, x2b, yOffset, z2b).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
            builder.vertex(positionMatrix, x1b, yOffset, z1b).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
        }
    }
    private void renderCircle(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float radius, float xOffset, float yOffset, float zOffset, int segments, int slot, String type, int directionAngle, CrystalAltarBlockEntity entity) {
        VertexConsumer builder = vertexConsumers.getBuffer(RenderLayer.getLightning());
        float lineThickness = 0.05f; // Approx. 1 pixel thick

        int segmentsPerThird = segments / 3; // Number of segments for one-third of the circle
        double baseAngle = Math.toRadians(120 * (slot - 1));
        double directionRadians = Math.toRadians(directionAngle);
        double angleOffset = baseAngle - (Math.PI / segments * segmentsPerThird / 2) - Math.toRadians(30);

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        for (int i = 0; i < segmentsPerThird; i++) {
            // Calculate the angles for this segment
            double angle1 = angleOffset + (2 * Math.PI / segments) * i + directionRadians;
            double angle2 = angleOffset + (2 * Math.PI / segments) * (i + 1) + directionRadians;

            // Calculate the x, z coordinates for the segment
            float x1 = (float) (radius * Math.cos(angle1) + xOffset);
            float z1 = (float) (radius * Math.sin(angle1) + zOffset);
            float x2 = (float) (radius * Math.cos(angle2) + xOffset);
            float z2 = (float) (radius * Math.sin(angle2) + zOffset);

            // Compute the perpendicular vector for thickness
            float dx = x2 - x1;
            float dz = z2 - z1;
            float length = (float) Math.sqrt(dx * dx + dz * dz);
            dx /= length;
            dz /= length;

            // Perpendicular offset vector
            float offsetX = -dz * lineThickness / 2;
            float offsetZ = dx * lineThickness / 2;

            // Define vertices for the thick line segment as a quad
            float x1a = x1 + offsetX, z1a = z1 + offsetZ;
            float x1b = x1 - offsetX, z1b = z1 - offsetZ;
            float x2a = x2 + offsetX, z2a = z2 + offsetZ;
            float x2b = x2 - offsetX, z2b = z2 - offsetZ;

            // Determine the color based on the type
            int rgb;
            if ("inert".equals(type)) {
                rgb = 0x909090; // Default gray color
            } else {
                rgb = CrystalData.getTypeByString(type).getColor();
            }
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;

            // Render the quad (two triangles)
            builder.vertex(positionMatrix, x1a, yOffset, z1a).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
            builder.vertex(positionMatrix, x2a, yOffset, z2a).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
            builder.vertex(positionMatrix, x2b, yOffset, z2b).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);

            builder.vertex(positionMatrix, x1a, yOffset, z1a).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
            builder.vertex(positionMatrix, x2b, yOffset, z2b).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
            builder.vertex(positionMatrix, x1b, yOffset, z1b).color(r, g, b, 255).normal(0.0f, 1.0f, 0.0f);
        }
    }
    private void renderFloatingItem(World world, BlockPos pos, ItemStack stack, int tickCount, MatrixStack matricies, VertexConsumerProvider vertexConsumers, int combinedLight, int combinedOverlay, double xOffset, double yOffset, double zOffset){
        double relativeGameTime = world.getTime();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        double rotation1 = (tickCount + relativeGameTime) * 5 % 360;
        double rotation2 = Math.sin(relativeGameTime/20.0) * 40;
        double offset = Math.sin(relativeGameTime / 10.0) / 12.0;

        matricies.push();
        matricies.translate(.5 + xOffset, yOffset+offset, 0.5 + zOffset);
        matricies.scale(0.5f,0.5f,0.5f);
        matricies.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)rotation2));

        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, combinedLight, combinedOverlay, matricies, vertexConsumers, world, 1);

        matricies.pop();
    }
    private void renderGhostItem(World world, BlockPos pos, ItemStack stack, int tickCount, MatrixStack matricies, VertexConsumerProvider vertexConsumers, int combinedLight, int combinedOverlay, double xOffset, double yOffset, double zOffset){
        double relativeGameTime = world.getTime();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        double rotation1 = (tickCount + relativeGameTime) * 5 % 360;
        double rotation2 = Math.sin(relativeGameTime/20.0) * 40;
        double offset = Math.sin(relativeGameTime / 10.0) / 12.0;

        matricies.push();
        matricies.translate(.5 + xOffset, yOffset+offset, 0.5 + zOffset);
        matricies.scale(0.5f,0.5f,0.5f);
        matricies.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)rotation2));

        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, combinedLight, combinedOverlay, matricies, vertexConsumers, world, 1);

        matricies.pop();
    }
}
