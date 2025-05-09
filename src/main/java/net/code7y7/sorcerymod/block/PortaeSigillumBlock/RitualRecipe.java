package net.code7y7.sorcerymod.block.PortaeSigillumBlock;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class RitualRecipe {
    private final List<Item> ingredients;
    private final ItemStack result;
    public RitualRecipe(List<Item> extraIngredients, ItemStack result) {
        this.ingredients = extraIngredients;
        this.result = result;
    }

    public List<Item> getIngredients() { return ingredients; }
    public ItemStack getResult() { return result; }
}
