package net.code7y7.sorcerymod.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InertCrystalItem extends Item {
    //list of available abilites
    public List<String> ABILITIES;
    //unlocked abilities
    public List<String> unlockedAbilities;
    private final int DEFAULT_CRYSTAL_TIER = 1;
    public static final int MAX_CRYSTAL_TIER = 10;
    public boolean hasElement;
    public String elementName;
    private static List<String> toAdd = new ArrayList<>(Arrays.asList("1"));
    public final static CustomModelDataComponent defaultCustomData = new CustomModelDataComponent(List.of(), List.of(), List.copyOf(toAdd), List.of());

    public InertCrystalItem(Settings settings) {
        super(settings.maxCount(1).component(DataComponentTypes.CUSTOM_MODEL_DATA, defaultCustomData));
        hasElement = false;
        elementName = "inert";
        unlockedAbilities = new ArrayList<>();
        List<String> toAdd = new ArrayList<>();
        if(this.hasElement) {
            toAdd.add("1");
            this.getDefaultStack().set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.copyOf(toAdd), List.of()));
        }
    }

    /*public void setTier(ItemStack stack, CrystalTier tier){
        stack.set(ModDataComponentTypes.CRYSTAL_TIER.get(), tier);
        List<String> toAdd = new ArrayList<>();
        if(tier.getValue() <= 3)
            toAdd.add("" + tier.getValue());
        else if(tier.getValue() == MAX_CRYSTAL_TIER)
            toAdd.add("4");
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(null, null, toAdd, null));
    }*/
}
