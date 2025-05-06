package net.code7y7.sorcerymod.client;

import net.code7y7.sorcerymod.block.CrystalAltarBlock.CrystalAltarBlockEntity;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.particle.LightningParticleEffect;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

import static javax.swing.UIManager.put;

public class CrystalAltarBERenderer implements BlockEntityRenderer<CrystalAltarBlockEntity > {
    private final List<Map<Map<CrystalData, CrystalData>, Integer>> DUAL_ABILITIES = Arrays.asList(
            new HashMap<>() {{
                put(new HashMap<>() {{
                    put(CrystalData.FIRE, CrystalData.ELECTRICITY);
                }}, 1);
            }},
            new HashMap<>() {{put(new HashMap<>() {{
                    put(CrystalData.ELECTRICITY, CrystalData.FIRE);
                }}, 1);
            }}
    );
    private final Vector3f colorEmpty = new Vector3f(0.7f, 0.7f, 0.7f);
    public CrystalAltarBERenderer(BlockEntityRendererFactory.Context context) {
    }
    @Override
    public void render(CrystalAltarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null)
            return;

        int directionAngle = entity.getDirectionAngleOffset();

        if (world.isClient) {
            BlockPos pos = entity.getPos();
            ItemStack slot0 = entity.getInventory().get(0);

            if (!slot0.isEmpty() && slot0.isOf(ModItems.INERT_CRYSTAL)) {
                renderFloatingItem(world, pos, slot0, entity.getTickCount(), matrices, vertexConsumers, light, overlay, 0, 1.455, 0);
                for (int i = 0; i < ((InertCrystalItem) slot0.getItem()).getTier(slot0); i++) {
                    drawArc(world, pos, 1, colorEmpty, i+1, directionAngle, ((InertCrystalItem) slot0.getItem()).getTier(slot0));
                    drawArc(world, pos, 2, colorEmpty, i+1, directionAngle, ((InertCrystalItem) slot0.getItem()).getTier(slot0));
                    drawArc(world, pos, 3, colorEmpty, i+1, directionAngle, ((InertCrystalItem) slot0.getItem()).getTier(slot0));
                }
            }

            // Variables to store the highest tier and color for each slot
            int[] highestTiers = new int[3];
            Vector3f[] colors = new Vector3f[3];

            // For each crystal (slots 1 to 3), render its floating item and collect data for the arc
            for (int i = 1; i <= 3; i++) {
                ItemStack elementalCrystal = entity.getInventory().get(i);
                if (!elementalCrystal.isEmpty()) {
                    String crystalType = ((ElementalCrystalItem) elementalCrystal.getItem()).elementName;
                    // Position the floating item for the crystal
                    float angle = (120 * (i - 1) + directionAngle);
                    double xOffset = Math.cos(Math.toRadians(angle)) * 0.5;
                    double zOffset = Math.sin(Math.toRadians(angle)) * 0.5;
                    double yOffset = 1.0;

                    renderFloatingItem(world, pos, elementalCrystal, entity.getTickCount(), matrices, vertexConsumers, light, overlay, xOffset, yOffset, zOffset);

                    colors[i - 1] = CrystalData.getTypeByString(crystalType).getColorVec();
                    highestTiers[i - 1] = ((InertCrystalItem) elementalCrystal.getItem()).getTier(elementalCrystal);
                } else {
                    colors[i - 1] = colorEmpty;
                    highestTiers[i - 1] = getHighestTier(entity.getInventory());
                }
            }

            // Draw arcs and circles for each slot
            int globalHighestTier = getHighestTier(entity.getInventory());

            for (int i = 1; i <= 3; i++) {
                drawArc(world, pos, i, colors[i - 1], highestTiers[i - 1], directionAngle, globalHighestTier);
                if (!entity.getInventory().get(i).isEmpty()) {
                    for (int t = 1; t <= globalHighestTier; t++) {
                        Vector3f circleColor = (t <= highestTiers[i - 1]) ? colors[i - 1] : colorEmpty;
                        drawCircle(world, pos, i, t, circleColor, directionAngle);
                    }
                }
            }
            for(int i = 1; i <=3; i++){ //for every slot
                ItemStack thisStack = entity.getInventory().get(i);
                ItemStack nextStack = entity.getInventory().get(getNextSlot(i));
                ItemStack lastStack = entity.getInventory().get(getLastSlot(i));
                ElementalCrystalItem thisItem = null;
                ElementalCrystalItem nextItem = null;
                ElementalCrystalItem lastItem = null;
                if(!entity.getInventory().get(i).isEmpty())
                    thisItem = (ElementalCrystalItem) thisStack.getItem();
                if(!entity.getInventory().get(getNextSlot(i)).isEmpty())
                    nextItem = (ElementalCrystalItem) entity.getInventory().get(getNextSlot(i)).getItem();
                if(!entity.getInventory().get(getLastSlot(i)).isEmpty())
                    lastItem = (ElementalCrystalItem) entity.getInventory().get(getLastSlot(i)).getItem();

                for(int t = 1; t <= globalHighestTier; t++) {//for every tier, up to the highest present crystal's tier
                    if (thisItem != null && thisItem.elementName.equals(CrystalData.FIRE.getName())) {//every fire dual ability
                        if (t == 1 && thisItem.getTier(thisStack) >= t) {
                            if (nextItem != null && nextItem.elementName.equals(CrystalData.ELECTRICITY.getName()) && nextItem.getTier(nextStack) >= t) {
                                drawCircle(world, pos, i, t, new Vector3f(1, 1, 0), directionAngle + 60);
                            }
                            if (lastItem != null && lastItem.elementName.equals(CrystalData.ELECTRICITY.getName()) && lastItem.getTier(lastStack) >= t) {
                                drawCircle(world, pos, i, t, new Vector3f(1, 1, 0), directionAngle - 60);
                            }
                        }
                        if (t == 3 && thisItem.getTier(thisStack) >= t) {
                            if (nextItem != null && nextItem.elementName.equals(CrystalData.GRAVITY.getName()) && nextItem.getTier(nextStack) >= t) {
                                drawCircle(world, pos, i, t, new Vector3f(1, 1, 0), directionAngle + 60);
                            }
                            if (lastItem != null && lastItem.elementName.equals(CrystalData.GRAVITY.getName()) && lastItem.getTier(lastStack) >= t) {
                                drawCircle(world, pos, i, t, new Vector3f(1, 1, 0), directionAngle - 60);
                            }
                        }
                    }
                    if(thisItem != null && thisItem.elementName.equals(CrystalData.ELECTRICITY.getName())) {//every electricity dual ability
                        if (t == 3 && thisItem.getTier(thisStack) >= t) {
                            if (nextItem != null && nextItem.elementName.equals(CrystalData.GRAVITY.getName()) && nextItem.getTier(nextStack) >= t) {
                                drawCircle(world, pos, i, t, new Vector3f(1, 1, 0), directionAngle + 60);
                            }
                            if (lastItem != null && lastItem.elementName.equals(CrystalData.GRAVITY.getName()) && lastItem.getTier(lastStack) >= t) {
                                drawCircle(world, pos, i, t, new Vector3f(1, 1, 0), directionAngle - 60);
                            }
                        }
                    }
                    if(thisItem != null && thisItem.elementName.equals(CrystalData.MIND.getName())) {//every electricity dual ability
                        if (t == 3 && thisItem.getTier(thisStack) >= t) {
                            if (nextItem != null && nextItem.elementName.equals(CrystalData.BODY.getName()) && nextItem.getTier(nextStack) >= t) {
                                drawCircle(world, pos, i, t, new Vector3f(1, 1, 0), directionAngle + 60);
                            }
                            if (lastItem != null && lastItem.elementName.equals(CrystalData.BODY.getName()) && lastItem.getTier(lastStack) >= t) {
                                drawCircle(world, pos, i, t, new Vector3f(1, 1, 0), directionAngle - 60);
                            }
                        }
                    }
                }
            }
        }
    }

    public int getNextSlot(int slot){
        return slot % 3 + 1;
    }
    public int getLastSlot(int slot){
        return (slot == 1) ? 3 : slot - 1;
    }

    private void drawArc(World world, BlockPos pos, int slot, Vector3f color, int tier, int angleOffset, int highestTier) {
        if(world.isClient()) {
            for (int currentTier = 1; currentTier <= highestTier; currentTier++) { // Loop through each tier up to the given tier
                float radius = currentTier + 0.2f;

                // Each slot covers a third of the circle (120° each)
                float startAngle = (slot - 1) * 120f + angleOffset - 60;
                float endAngle = slot * 120f + angleOffset - 60;

                // Determine how many segments the arc should have
                int segments = 2;
                float angleIncrement = (endAngle - startAngle) / segments;

                // Calculate the center of the block
                double centerX = pos.getX() + 0.5;
                double centerY = pos.getY() + 0.7 + (currentTier - 1) * 0.2; // Adjust height per tier
                double centerZ = pos.getZ() + 0.5;

                // Loop over each segment to spawn a custom particle
                for (int i = 0; i < segments; i++) {
                    // Calculate the angle for the next point in the arc
                    float currentAngle = startAngle + i * angleIncrement;
                    float nextAngle = startAngle + (i + 1) * angleIncrement;

                    // Convert angles to radians
                    double radCurrent = Math.toRadians(currentAngle);
                    double radNext = Math.toRadians(nextAngle);

                    // Calculate positions for the segment
                    float startX = (float) (centerX + Math.cos(radCurrent) * radius);
                    float startY = (float) centerY;
                    float startZ = (float) (centerZ + Math.sin(radCurrent) * radius);

                    float destX = (float) (centerX + Math.cos(radNext) * radius);
                    float destY = (float) centerY;
                    float destZ = (float) (centerZ + Math.sin(radNext) * radius);

                    Vector3f destination = new Vector3f(destX, destY, destZ);

                    // Spawn the custom arc particle
                    if (currentTier <= tier) {
                        world.addParticle(new LightningParticleEffect(color, destination, 0.01f, false, 0.0f, 1), startX, startY, startZ, 0, 0, 0);
                    } else {
                        world.addParticle(new LightningParticleEffect(colorEmpty, destination, 0.01f, false, 0.0f, 1), startX, startY, startZ, 0, 0, 0);
                    }
                }
            }
        }
    }

    private void drawCircle(World world, BlockPos pos, int slot, int tier, Vector3f color, int angleOffset) {
        if(world.isClient()) {
            // The distance from the block center where the small circle will be drawn
            float offsetRadius = 1.2f + (tier - 1) * 1.0f;

            // The radius of the small circle (adjust as desired)
            float circleRadius = 0.2f;

            // Use more segments if you want a smoother small circle
            int segments = 6;
            float angleIncrement = 360f / segments;

            // Calculate the center of the block
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.7 + (tier - 1) * 0.2;
            double centerZ = pos.getZ() + 0.5;

            // Determine the angle for the offset position based on the slot
            float angleForOffset = (slot - 1) * 120f + angleOffset;
            double radOffset = Math.toRadians(angleForOffset);

            // Calculate the center of the small circle (offset from the block center)
            double circleCenterX = centerX + Math.cos(radOffset) * offsetRadius;
            double circleCenterZ = centerZ + Math.sin(radOffset) * offsetRadius;

            // Now draw the small circle around the offset center
            for (int i = 0; i < segments; i++) {
                // Compute the current angle for this segment of the small circle
                float currentAngle = i * angleIncrement;
                double radCurrent = Math.toRadians(currentAngle);

                // Calculate the position on the small circle
                float particleX = (float) (circleCenterX + Math.cos(radCurrent) * circleRadius);
                float particleY = (float) centerY;
                float particleZ = (float) (circleCenterZ + Math.sin(radCurrent) * circleRadius);

                // For a smoother effect, compute the next point on the small circle
                float nextAngle = (i + 1) * angleIncrement;
                double radNext = Math.toRadians(nextAngle);
                float destX = (float) (circleCenterX + Math.cos(radNext) * circleRadius);
                float destY = (float) centerY;
                float destZ = (float) (circleCenterZ + Math.sin(radNext) * circleRadius);

                Vector3f destination = new Vector3f(destX, destY, destZ);

                // Spawn the particle for the small circle segment
                world.addParticle(new LightningParticleEffect(color, destination, 0.01f, false, 0.0f, 1), particleX, particleY, particleZ, 0, 0, 0);
            }
        }
    }

    private int getHighestTier(DefaultedList<ItemStack> inventory) {
        int highest = 0;
        for (int i = 1; i <= 3; i++) {
            if (!inventory.get(i).isEmpty() && ((InertCrystalItem) inventory.get(i).getItem()).getTier(inventory.get(i)) > highest) {
                highest = ((InertCrystalItem) inventory.get(i).getItem()).getTier(inventory.get(i));
            }
        }
        return Math.min(highest, 3);
    }

    private void renderFloatingItem(World world, BlockPos pos, ItemStack stack, int tickCount, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int combinedLight, int combinedOverlay, double xOffset, double yOffset, double zOffset) {
        double relativeGameTime = world.getTime() % 10000;
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        double rotation2 = Math.sin(relativeGameTime / 20.0) * 40;
        double offset = Math.sin(relativeGameTime / 10.0) / 12.0;

        if (stack.getItem().equals(ModItems.INERT_CRYSTAL)) {
            rotation2 = (tickCount + relativeGameTime) * 1 % 360;
            offset = 0;
        }

        matrices.push();
        matrices.translate(0.5 + xOffset, yOffset + offset, 0.5 + zOffset);
        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) rotation2));

        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, combinedLight, combinedOverlay, matrices, vertexConsumers, world, 1);

        matrices.pop();
    }
}
