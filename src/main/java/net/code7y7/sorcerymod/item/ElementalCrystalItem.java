package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.spell.CrystalOptions;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.item.Item;

import java.util.List;

public class ElementalCrystalItem extends InertCrystalItem{
    public ElementalCrystalItem(Settings settings, CrystalData type, List<String> abilities) {
        super(settings.component(ModDataComponentTypes.CRYSTAL_OPTIONS, CrystalOptions.createOptionsFor(type)));
        hasElement = true;
        elementName = type.getName();
        elementType = type;
        ABILITIES = abilities;
    }
}