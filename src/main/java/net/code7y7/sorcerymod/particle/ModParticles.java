package net.code7y7.sorcerymod.particle;

import net.code7y7.sorcerymod.SorceryMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final ParticleType<LightningParticleEffect> LIGHTNING_PARTICLE = registerParticle("lightning_particle",
            FabricParticleTypes.complex(true, LightningParticleEffect.CODEC, LightningParticleEffect.PACKET_CODEC));
    public static final ParticleType<CrystalPlaceParticleEffect> CRYSTAL_PLACE_PARTICLE = registerParticle("crystal_place_particle",
            FabricParticleTypes.complex(true, CrystalPlaceParticleEffect.CODEC, CrystalPlaceParticleEffect.PACKET_CODEC));
    private static SimpleParticleType registerParticle(String name, SimpleParticleType particle){
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(SorceryMod.MOD_ID,  name), particle);
    }

    private static <T extends ParticleEffect> ParticleType<T> registerParticle(String name, ParticleType<T> particle){
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(SorceryMod.MOD_ID,  name), particle);
    }

    public static void registerParticleTypes(){
        SorceryMod.LOGGER.info("Registering Mod Particles for "+ SorceryMod.MOD_ID);
    }
}
