package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.particle.LightningParticleEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleTest extends Item {

    public ParticleTest(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();;
        World world = player.getWorld();
        Vec3d start = player.getEyePos();
        Vec3d dest = context.getHitPos();
        for(int i = 0; i< 3; i++) {
            world.addParticle(new LightningParticleEffect(0x990000, dest.toVector3f()), start.x, start.y, start.z, 0, 0, 0);

            world.addParticle(ParticleTypes.BUBBLE, start.x, start.y, start.z, 0, 0, 0);
            world.addParticle(ParticleTypes.BUBBLE
                    , dest.x, dest.y, dest.z, 0, 0, 0);
        }
        return super.useOnBlock(context);
    }
}
