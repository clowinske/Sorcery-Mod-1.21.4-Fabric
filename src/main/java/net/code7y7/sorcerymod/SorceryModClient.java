package net.code7y7.sorcerymod;

import net.code7y7.sorcerymod.client.ModEntityModels;
import net.code7y7.sorcerymod.network.AltarInteractSoundPayload;
import net.code7y7.sorcerymod.network.CrystalPlaceParticlePayload;
import net.code7y7.sorcerymod.particle.CrystalPlaceParticle;
import net.code7y7.sorcerymod.particle.CrystalPlaceParticleEffect;
import net.code7y7.sorcerymod.particle.LightningParticle;
import net.code7y7.sorcerymod.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class SorceryModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModEntityModels.registerModBlocksEntities();



        ParticleFactoryRegistry.getInstance().register(ModParticles.CRYSTAL_PLACE_PARTICLE, CrystalPlaceParticle.CrystalPlaceParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.LIGHTNING_PARTICLE, LightningParticle.LightningParticleFactory::new);


        ClientPlayNetworking.registerGlobalReceiver(CrystalPlaceParticlePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world != null) {
                    context.client().world.addParticle(new CrystalPlaceParticleEffect(
                            payload.rgb()),
                            payload.pos().getX() + .5, payload.pos().getY() + .8, payload.pos().getZ() + .5,
                            0.0, 0.0, 0.0);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AltarInteractSoundPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world != null) {
                    context.player().playSound(payload.sound(), payload.volume(), payload.pitch());
                }
            });
        });
    }
}
