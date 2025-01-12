package net.code7y7.sorcerymod.item;

import net.minecraft.item.Item;

import java.util.List;

public class ElementalCrystalItem extends InertCrystalItem{
    public ElementalCrystalItem(Settings settings, String name, List<String> abilities) {
        super(settings);
        hasElement = true;
        elementName = name;
        ABILITIES = abilities;
    }
}
