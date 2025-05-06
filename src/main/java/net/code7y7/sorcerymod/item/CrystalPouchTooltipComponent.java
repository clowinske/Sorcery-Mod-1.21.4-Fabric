package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.component.CrystalPouchContentsComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CrystalPouchTooltipComponent implements TooltipComponent {
    private final List<ItemStack> stacks;

    public CrystalPouchTooltipComponent(CrystalPouchContentsComponent component) {
        this.stacks = component.getStacks();
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return 20; // Adjust depending on rows of items
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return stacks.size() * 20; // One icon per item
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            context.drawItem(stack, x + i * 20, y);
            //context.drawItemInSlot(textRenderer, stack, x + i * 20, y);
        }
    }
}

