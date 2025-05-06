package net.code7y7.sorcerymod.util.crystal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public enum CrystalData {
    FIRE(0, "fire", true, 0xff9514, 0xff9514, 0xFFAE4C,
            CrystalAbilities.FIRE),

    ELECTRICITY(1, "electricity", true, 0x82F4FF, 0x82F4FF, 0xCEFAFF,
         CrystalAbilities.ELECTRICITY),

    GRAVITY(2, "gravity", true, 0xF070FF, 0xF070FF, 0xF7BCFF,
         CrystalAbilities.GRAVITY),

    MIND(3, "mind", true, 0x00A835, 0x00A835, 0x41D86E,
         CrystalAbilities.MIND),

    BODY(4, "body", true, 0xA53030, 0xA53030, 0xBF7272,
         CrystalAbilities.BODY),

    SOUL(5, "soul", true, 0x4F51FF, 0x4F51FF, 0x9B9DFF,
         CrystalAbilities.SOUL),
    ECLIPSE(6, "eclipse", false, 0x001433, 0x001433, 0x0b192e,
         CrystalAbilities.ECLIPSE),
    DISCORD(7, "discord", false, 0x330000, 0x330000, 0x291111,
            CrystalAbilities.DISCORD),
    RADIANT(8, "radiant", false, 0xff9999, 0xff9999, 0xf2c9c9,
            CrystalAbilities.RADIANT),
    INERT(9, "inert", false, 0x909090, 0x909090, 0xc7c7c7,
            List.of("")),
    NONE(-1, "none", false, 0x000000, 0x000000, 0x000000,
          List.of(""));


    final int mapIndex;
    final String name;
    final boolean isLight;
    final int mainColor;
    final int textColor;
    final int textColorExtra;

    final List<String> abilities;
    public static final Codec<CrystalData> CODEC = Codec.STRING.xmap(
            name -> Arrays.stream(values()).filter(v -> v.name.equals(name)).findFirst().orElse(INERT),
            CrystalData::getName
    );

    CrystalData(int mapindex, String name, boolean isLight, int mainColor, int textColor, int textColorExtra, List<String> abilities){
        this.mapIndex = mapindex;
        this.name = name;
        this.isLight = isLight;
        this.mainColor = mainColor;
        this.textColor = textColor;
        this.textColorExtra = textColorExtra;
        this.abilities = abilities;
    }

    public String getName() {
        return name;
    }

    public int getColorInt() {
        return mainColor;
    }
    public Vector3f getColorVec() {
        float red = ((mainColor >> 16) & 0xFF) / 255.0f;
        float green = ((mainColor >> 8) & 0xFF) / 255.0f;
        float blue = (mainColor & 0xFF) / 255.0f;
        return new Vector3f(red, green, blue);
    }

    public String getColorHex() {
        return String.format("#%06X", (0xFFFFFF & mainColor));
    }

    public int getTextColor() {
        return textColor;
    }

    public String getTextColorHex() {
        return String.format("#%06X", (0xFFFFFF & textColor));
    }

    public int getTextExtraColor() {
        return textColorExtra;
    }

    public String getTextExtraColorHex() {
        return String.format("#%06X", (0xFFFFFF & textColorExtra));
    }

    public boolean isLight(){
        return isLight;
    }

    public static CrystalData getTypeByString(String string) {
        for (CrystalData type : values()) {
            if (type.name.equalsIgnoreCase(string)) {
                return type;
            }
        }
        return null; // Or throw an exception like IllegalArgumentException
    }
    public static CrystalData getTypeByInt(int index){
        for (CrystalData type : values()) {
            if (type.mapIndex == index){
                return type;
            }
        }
        return CrystalData.NONE; // Or throw an exception like IllegalArgumentException
    }
    public int getInt(){
        return this.mapIndex;
    }

    @Override
    public String toString() {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public List<String> getAbilities(){
        return abilities;
    }
}
