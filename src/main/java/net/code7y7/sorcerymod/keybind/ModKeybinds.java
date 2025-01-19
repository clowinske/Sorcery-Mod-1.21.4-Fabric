package net.code7y7.sorcerymod.keybind;

import net.code7y7.sorcerymod.SorceryMod;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static KeyBinding SPELL_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
    "key.sorcerymod.spell",
    InputUtil.Type.KEYSYM,
    GLFW.GLFW_KEY_G,
    "category.sorcerymod.sorcerymod"
    ));

    public static void registerModKeybinds(){
        SorceryMod.LOGGER.info("Registering Mod Keybinds for " + SorceryMod.MOD_ID);
    }
}
