package net.code7y7.sorcerymod;

import net.code7y7.sorcerymod.block.ModBlocks;
import net.code7y7.sorcerymod.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup SORCERY_MOD_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(SorceryMod.MOD_ID, "sorcery_mod"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.INERT_CRYSTAL))
                    .displayName(Text.translatable("itemgroup.sorcerymod.sorcerymod_group"))
                    .entries(((displayContext, entries) -> {

                        entries.add(ModItems.FIRE_CRYSTAL);
                        entries.add(ModItems.ELECTRICITY_CRYSTAL);
                        entries.add(ModItems.GRAVITY_CRYSTAL);
                        entries.add(ModItems.MIND_CRYSTAL);
                        entries.add(ModItems.BODY_CRYSTAL);
                        entries.add(ModItems.SOUL_CRYSTAL);
                        //entries.add(ModItems.DISCORD_CRYSTAL);
                        //entries.add(ModItems.ECLIPSE_CRYSTAL);
                        //entries.add(ModItems.RADIANT_CRYSTAL);
                        entries.add(ModItems.INERT_CRYSTAL);
                        entries.add(ModBlocks.CRYSTAL_ALTAR);
                        entries.add(ModBlocks.ATTUNING_ORB);
                        entries.add(ModItems.CRYSTAL_POUCH);
                        entries.add(ModBlocks.RITUAL_GATEWAY);
                        entries.add(ModItems.PRISMATIC_CRYSTAL);
                        entries.add(ModItems.DUNGEON_KEY_PIECE);
                        entries.add(ModItems.DUNGEON_KEY);

                    })).build());
    public static final ItemGroup WEAPONS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(SorceryMod.MOD_ID, "weapons"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.LONG_SWORD))
                    .displayName(Text.translatable("itemgroup.sorcerymod.weapons_group"))
                    .entries(((displayContext, entries) -> {

                        entries.add(ModItems.LONG_SWORD);

                    })).build());
    public static void registerItemGroups(){
    }
}
