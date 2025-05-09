package net.code7y7.sorcerymod.block.PortaeSigillumBlock;

import net.minecraft.item.Item;

import java.util.List;

public class RitualRecipe {
    private final List<Item> ingredients;
    private final Item result;
    public RitualRecipe(List<Item> extraIngredients, Item result) {
        this.ingredients = extraIngredients;
        this.result = result;
    }

    public List<Item> getIngredients() { return ingredients; }
    public Item getResult() { return result; }
}
