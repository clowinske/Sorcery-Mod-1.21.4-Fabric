package net.code7y7.sorcerymod.sound;

import net.code7y7.sorcerymod.SorceryMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent GRAVITY_PUSH = registerSoundEvent("gravity_push");
    public static final SoundEvent ORB_UI_CLICK = registerSoundEvent("orb_ui_click");
    public static final SoundEvent ORB_UI_SELECT = registerSoundEvent("orb_ui_select");


    private static SoundEvent registerSoundEvent(String name){
        Identifier id = SorceryMod.createIdentifier(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    public static void registerSounds(){
        SorceryMod.LOGGER.info("Registering Sounds for "+ SorceryMod.MOD_ID);
    }
}
