package net.code7y7.sorcerymod;

import net.code7y7.sorcerymod.client.ModEntityModels;
import net.code7y7.sorcerymod.keybind.ModKeybinds;
import net.code7y7.sorcerymod.network.AltarInteractSoundPayload;
import net.code7y7.sorcerymod.network.CrystalPlaceParticlePayload;
import net.code7y7.sorcerymod.particle.CrystalPlaceParticle;
import net.code7y7.sorcerymod.particle.CrystalPlaceParticleEffect;
import net.code7y7.sorcerymod.particle.LightningParticle;
import net.code7y7.sorcerymod.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class SorceryModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModEntityModels.registerModBlocksEntities();
        ModKeybinds.registerModKeybinds();

        ParticleFactoryRegistry.getInstance().register(ModParticles.CRYSTAL_PLACE_PARTICLE, CrystalPlaceParticle.CrystalPlaceParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.LIGHTNING_PARTICLE, LightningParticle.LightningParticleFactory::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
           if(client.currentScreen == null) { //no gui open
               if(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT)){
                   handleLeftClick(client.player);
               }
               if(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT)){
                   handleRightClick(client.player);
               }
           }
        });


        boolean isInDungeon = false;
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if(isInDungeon)
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if(isInDungeon)
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(isInDungeon)
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });


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
    private void handleLeftClick(PlayerEntity player) {
        System.out.println("Charging spell with left click...");
        // Add your custom spell logic here
    }

    private void handleRightClick(PlayerEntity player) {
        System.out.println("Charging spell with right click...");
        // Add your custom spell logic here
    }
}
