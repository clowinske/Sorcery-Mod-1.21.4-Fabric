package net.code7y7.sorcerymod.entity;

import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.entity.custom.FireballEntity;
import net.code7y7.sorcerymod.entity.custom.FiresprayEntity;
import net.code7y7.sorcerymod.entity.custom.UpgradeOrbEntity;
import net.code7y7.sorcerymod.entity.enemy.Class1Enemy;
import net.code7y7.sorcerymod.entity.enemy.TwoPhaseBoss;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    //alive
    public static final EntityType<Class1Enemy> CLASS_1_ENEMY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SorceryMod.MOD_ID, "class_1_enemy"),
            EntityType.Builder.create(Class1Enemy::new, SpawnGroup.MISC)
                    .dimensions(1f, 2f).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, SorceryMod.createIdentifier("class_1_enemy"))));
    public static final EntityType<TwoPhaseBoss> TWO_PHASE_BOSS = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SorceryMod.MOD_ID, "two_phase_boss"),
            EntityType.Builder.create(TwoPhaseBoss::new, SpawnGroup.MISC)
                    .dimensions(1f, 2f).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, SorceryMod.createIdentifier("two_phase_boss"))));


    //not alive
    public static final EntityType<UpgradeOrbEntity> UPGRADE_ORB = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SorceryMod.MOD_ID, "upgrade_orb"),
            EntityType.Builder.create(UpgradeOrbEntity::new, SpawnGroup.MISC)
                    .dimensions(.25f, .25f).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, SorceryMod.createIdentifier("upgrade_orb"))));
    public static final EntityType<FireballEntity> FIREBALL = Registry.register(Registries.ENTITY_TYPE,
            SorceryMod.createIdentifier("fireball"),
                    EntityType.Builder.create(FireballEntity::new, SpawnGroup.MISC)
                            .dimensions(.25f, .25f).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, SorceryMod.createIdentifier("fireball"))));
    public static final EntityType<FiresprayEntity> FIRESPRAY = Registry.register(Registries.ENTITY_TYPE,
            SorceryMod.createIdentifier("firespray"),
            EntityType.Builder.create(FiresprayEntity::new, SpawnGroup.MISC)
                    .dimensions(.35f, .35f).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, SorceryMod.createIdentifier("firespray"))));

    public static void registerModEntities(){
        SorceryMod.LOGGER.info("Registering Mod Entities for " + SorceryMod.MOD_ID);
    }
}
