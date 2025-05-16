package net.code7y7.sorcerymod.network;

import net.code7y7.sorcerymod.SorceryMod;
import net.minecraft.util.Identifier;

public class Packets {
    public static final Identifier SHOW_CRYSTAL_PLACE_PARTICLE = SorceryMod.createIdentifier("show_crystal_place_particle"); //server to client
    public static final Identifier SHOW_ALTAR_EFFECT_PARTICLE = SorceryMod.createIdentifier("show_altar_effect_particle"); //server to client
    public static final Identifier DUST_PARTICLE_C2S = SorceryMod.createIdentifier("dust_particle_c2s"); //client to server
    public static final Identifier DUST_PARTICLE_S2C = SorceryMod.createIdentifier("dust_particle_s2c"); //server to client
    public static final Identifier SPAWN_FIREBALL = SorceryMod.createIdentifier("spawn_fireball_c2s"); //client to server
    public static final Identifier SPAWN_FIRESPRAY = SorceryMod.createIdentifier("spawn_firespray_c2s"); //client to server
    public static final Identifier ALTAR_INTERACT_SOUND = SorceryMod.createIdentifier("altar_interact_sound"); //server to client
    public static final Identifier SEND_STATE_SELECT_MODE = SorceryMod.createIdentifier("send_state_select_mode"); //client to server
    public static final Identifier SEND_STATE_CAST_MODE = SorceryMod.createIdentifier("send_state_cast_mode"); //client to server
    public static final Identifier SEND_STATE_HAS_CRYSTAL = SorceryMod.createIdentifier("send_state_has_crystal"); //client to server
    public static final Identifier CHARGE_SPELL = SorceryMod.createIdentifier("charge_spell"); //client to server
    public static final Identifier SET_CORRUPTION_S2C = SorceryMod.createIdentifier("set_corruption_s2c"); //server to client
    public static final Identifier SET_CORRUPTION_C2S = SorceryMod.createIdentifier("set_corruption_c2s"); //client to server
    public static final Identifier ADD_VELOCITY = SorceryMod.createIdentifier("add_velocity"); //client to server
    public static final Identifier FIRE_SPELL_PARTICLE_C2S = SorceryMod.createIdentifier("fire_spell_particle_c2s"); //client to server
    public static final Identifier FIRE_SPELL_PARTICLE_S2C = SorceryMod.createIdentifier("fire_spell_particle_s2c"); //server to client
    public static final Identifier LIGHTNING_PARTICLE_C2S = SorceryMod.createIdentifier("lightning_particle_c2s"); //client to server
    public static final Identifier LIGHTNING_PARTICLE_S2C = SorceryMod.createIdentifier("lightning_particle_s2c"); //server to client
    public static final Identifier CAST_SPELL_C2S = SorceryMod.createIdentifier("cast_spell_c2s"); //client to server
    public static final Identifier SET_SPELL_CHARGE_S2C = SorceryMod.createIdentifier("set_spell_charge_s2c"); //server to client
    public static final Identifier SET_SPELL_CHARGE_C2S = SorceryMod.createIdentifier("set_spell_charge_c2s"); //client to server
    public static final Identifier SET_SPELL_TYPE_S2C = SorceryMod.createIdentifier("set_spell_type_s2c"); //server to client
    public static final Identifier SET_SPELL_TYPE_C2S = SorceryMod.createIdentifier("set_spell_type_c2s"); //client to server
    public static final Identifier SET_CAN_CAST_S2C = SorceryMod.createIdentifier("set_can_cast_s2c"); //server to client
    public static final Identifier SET_CAN_CAST_C2S = SorceryMod.createIdentifier("set_can_cast_c2s"); //client to server
    public static final Identifier BLINK_C2S = SorceryMod.createIdentifier("blink_c2s"); //client to server
    public static final Identifier SET_FLIGHT_S2C = SorceryMod.createIdentifier("set_flight_s2c"); //server to client
    public static final Identifier SET_FOCUS_S2C = SorceryMod.createIdentifier("set_focus_s2c"); //server to client
    public static final Identifier SET_FOCUS_C2S = SorceryMod.createIdentifier("set_focus_c2s"); //client to server
    public static final Identifier ADD_TIME_PONDERING_C2S = SorceryMod.createIdentifier("add_time_pondering_c2s"); //client to server
    public static final Identifier SPAWN_PARTICLE_S2C = SorceryMod.createIdentifier("spawn_particle_s2c"); //server to client
    public static final Identifier SET_FOCUS_RECHARGE_S2C = SorceryMod.createIdentifier("set_focus_recharge_s2c"); //server to client
    public static final Identifier IS_WEIGHTLESS_S2C = SorceryMod.createIdentifier("is_weightless_s2c"); //server to client

}
