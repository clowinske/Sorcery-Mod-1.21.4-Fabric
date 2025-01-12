package net.code7y7.sorcerymod;

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

                        entries.add(ModItems.INERT_CRYSTAL);
                        entries.add(ModItems.FIRE_CRYSTAL);

                    })).build());

    public static void registerItemGroups(){

        SorceryMod.LOGGER.info("Registering Item Groups for " + SorceryMod.MOD_ID);
    }
}
