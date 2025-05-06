package net.code7y7.sorcerymod.component;

import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class ImbueHelper {
    public static int getImbueValue(ItemStack stack){
        if(!stack.contains(ModDataComponentTypes.IMBUE_CHARGE)) {
            stack.set(ModDataComponentTypes.IMBUE_CHARGE, new ImbueCharge(9, 0));
        }
        return stack.get(ModDataComponentTypes.IMBUE_CHARGE).getValue();
    }
    public static int getImbueType(ItemStack stack){
        if(!stack.contains(ModDataComponentTypes.IMBUE_CHARGE)) {
            stack.set(ModDataComponentTypes.IMBUE_CHARGE, new ImbueCharge(9, 0));
        }
        return stack.get(ModDataComponentTypes.IMBUE_CHARGE).getType();
    }
    public static ImbueCharge getImbueCharge(ItemStack stack){
        return stack.get(ModDataComponentTypes.IMBUE_CHARGE);
    }

    public static void incrementImbueCharge(CrystalData type, int amount, ItemStack stack){
        int typeInt = type.getInt();
        if(getImbueType(stack) != typeInt){
            decrementCharge(stack, amount);
            if(getImbueValue(stack) == 0){
                stack.set(ModDataComponentTypes.IMBUE_CHARGE, new ImbueCharge(typeInt, 0));
            }
        } else {
            stack.set(ModDataComponentTypes.IMBUE_CHARGE, new ImbueCharge(typeInt, Math.min(getImbueValue(stack) + amount, 100)));
        }
    }

    public static void decrementCharge(ItemStack stack, int amount){
        stack.set(ModDataComponentTypes.IMBUE_CHARGE, new ImbueCharge(getImbueType(stack), Math.max(0, getImbueValue(stack)-amount)));
    }
}
