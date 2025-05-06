package net.code7y7.sorcerymod.util;

import net.code7y7.sorcerymod.SorceryMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModTags {
    public static class Blocks{
        private static TagKey<Block> createTag(String name){
            return TagKey.of(RegistryKeys.BLOCK, SorceryMod.createIdentifier(name));
        }
    }
    public static class Items{
        public static final TagKey<Item> VALID_IMBUE_TOOLS = createTag("valid_imbue_tools");
        private static TagKey<Item> createTag(String name){
            return TagKey.of(RegistryKeys.ITEM, SorceryMod.createIdentifier(name));
        }
    }
}
