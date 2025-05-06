package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.particle.GravityPushParticleEffect;
import net.code7y7.sorcerymod.particle.LightningParticleEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class ParticleTest extends Item {

    public ParticleTest(Settings settings) {
        super(settings);
    }

    Vec3d start;
    Vec3d dest;

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = player.getWorld();
        if (player.isSneaking()) {
            start = context.getHitPos();
            return ActionResult.SUCCESS;
        } else {
            dest = context.getHitPos();
            return ActionResult.SUCCESS;
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking() && (start !=null || dest !=null)) {
            start = null;
            dest = null;
            return ActionResult.SUCCESS;
        } else if(!player.isSneaking()){
            Vector3f direction = player.getRotationVector().toVector3f();
            Vector3f pos = player.getEyePos().add(direction.x, direction.y, direction.z).toVector3f();
            world.addParticle(new GravityPushParticleEffect(direction, 0.5f), pos.x, pos.y, pos.z, 0, 0, 0);
            return ActionResult.SUCCESS;
        }
        return super.use(world, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(selected && start !=null && dest !=null) {
            world.addParticle(new LightningParticleEffect(new Vector3f(0, 0, 1), dest.toVector3f(), 0.02f, false, 0.5f, 1), start.x, start.y, start.z, 0, 0, 0);
        }
    }
}
