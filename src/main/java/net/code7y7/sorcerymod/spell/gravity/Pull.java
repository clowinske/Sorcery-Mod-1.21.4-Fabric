package net.code7y7.sorcerymod.spell.gravity;

import net.code7y7.sorcerymod.spell.Ability;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Pull implements Ability {
    @Override
    public void trigger(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        SpellHelper.resetHandCharge(player, button);

        int count = 50;
        double spread = 0.001;

        Vec3d startPos = player.getEyePos();
        Vec3d lookVec = player.getRotationVector();

        Vec3d endPos = startPos.add(lookVec.multiply(5));

        Box pushBox = new Box(startPos, endPos).expand(1.5);

        List<Entity> entities = world.getEntitiesByClass(Entity.class, pushBox, entity -> entity != player);

        double maxDistance = 5.0 + 2*tier; // Maximum distance for the push effect
        double baseStrength = 1.0 + tier*0.1; // Base strength of the push



        for (Entity entity : entities) {
            double distance = entity.getPos().distanceTo(startPos);
            double distanceFactor = (distance / maxDistance); // Linear falloff
            distanceFactor = Math.max(0, Math.min(1, distanceFactor)); // Clamp between 0 and 1

            double pullStrength = baseStrength * distanceFactor;

            Vec3d pullVector = lookVec.subtract(lookVec.multiply(2)).multiply(pullStrength);
            entity.addVelocity(pullVector.x, pullVector.y, pullVector.z);
            //ClientPlayNetworking.send(new AddVelocityPayload(pullVector.toVector3f(), entity.getUuidAsString()));
        }

        //player.addVelocity(lookVec.subtract(lookVec.multiply(2)).multiply(0.5));
    }

    @Override
    public void holdStart(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }

    @Override
    public void holdRelease(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {

    }

    @Override
    public double getFocusCost(ServerPlayerEntity player, ServerWorld world, String button, int tier, ItemStack crystal) {
        return 10;
    }

    @Override
    public SpellInput getInput() {
        return SpellInput.DOUBLE;
    }

    @Override
    public String toString() {
        return "pull";
    }
}
