package net.code7y7.sorcerymod.util.crystal;

import java.util.List;

public enum CrystalData {
    FIRE("fire", true, 0xff9514, 0xFFAE4C, 0xFFAE4C,
            CrystalAbilities.FIRE),

    ELECTRICITY("electricity", true, 0xFF9400, 0x82F4FF, 0xCEFAFF,
         CrystalAbilities.ELECTRICITY),

    GRAVITY("gravity", true, 0xFF9400, 0xF070FF, 0xF7BCFF,
         CrystalAbilities.GRAVITY),

    MIND("mind", true, 0xFF9400, 0x00A835, 0x41D86E,
         CrystalAbilities.FIRE),

    BODY("body", true, 0xFF9400, 0xA53030, 0xBF7272,
         CrystalAbilities.FIRE),

    SOUL("soul", true, 0xFF9400, 0x4F51FF, 0x9B9DFF,
         CrystalAbilities.FIRE),
    ECLIPSE("eclipse", false, 0x000814, 0x000814, 0x0b192e,
         CrystalAbilities.ECLIPSE),
    DISCORD("discord", false, 0x210000, 0x210000, 0x291111,
            CrystalAbilities.DISCORD),
    RADIANT("radiant", false, 0xff9999, 0xff9999, 0xf2c9c9,
            CrystalAbilities.RADIANT);

    final String name;
    final boolean isLight;
    final int mainColor;
    final int textColor;
    final int textColorExtra;
    final List<String> abilities;

    CrystalData(String name, boolean isLight, int mainColor, int textColor, int textColorExtra, List<String> abilities){
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

    public int getColor() {
        return mainColor;
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

    @Override
    public String toString() {
        return name;
    }
}
