package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.component.DungeonSeed;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class DungeonKeyItem extends Item {
    public DungeonKeyItem(Settings settings) {
        super(settings.maxCount(1).rarity(Rarity.EPIC).component(ModDataComponentTypes.DUNGEON_SEED, new DungeonSeed(0)));
    }

    public void setSeed(ItemStack stack, long seed){
        if(stack.isOf(ModItems.DUNGEON_KEY)){
            stack.set(ModDataComponentTypes.DUNGEON_SEED, new DungeonSeed(seed));
        }
    }
    public long getSeed(ItemStack stack){
        if(stack.isOf(ModItems.DUNGEON_KEY)){
            return stack.get(ModDataComponentTypes.DUNGEON_SEED).getValue();
        }
        return -1;
    }

    public long generateRandomSeed(){
        return new Random().nextLong();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(stack.get(ModDataComponentTypes.DUNGEON_SEED).getValue() == 0){
            ((DungeonKeyItem)stack.getItem()).setSeed(stack, ((DungeonKeyItem)stack.getItem()).generateRandomSeed());
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if(Screen.hasShiftDown())
            tooltip.add(Text.literal("Seed: ").append(""+stack.get(ModDataComponentTypes.DUNGEON_SEED).getValue()).withColor(0x898989));
    }
}
