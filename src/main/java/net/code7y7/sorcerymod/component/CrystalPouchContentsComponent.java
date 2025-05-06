package net.code7y7.sorcerymod.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CrystalPouchContentsComponent implements TooltipData {
    public static final CrystalPouchContentsComponent DEFAULT = new CrystalPouchContentsComponent(List.of());

    public static final Codec<CrystalPouchContentsComponent> CODEC =
            ItemStack.CODEC.listOf().flatXmap(
                    CrystalPouchContentsComponent::validate,
                    component -> DataResult.success(component.getStacks())
            );

    public static final PacketCodec<RegistryByteBuf, CrystalPouchContentsComponent> PACKET_CODEC =
            ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(
                    CrystalPouchContentsComponent::new,
                    CrystalPouchContentsComponent::getStacks
            );

    private final List<ItemStack> stacks;

    private CrystalPouchContentsComponent(List<ItemStack> stacks) {
        this.stacks = List.copyOf(stacks);
    }

    public List<ItemStack> getStacks() {
        return stacks;
    }

    public Stream<ItemStack> iterateCopy() {
        return stacks.stream().map(ItemStack::copy);
    }

    public Double getOccupancy() {
        return stacks.size() / 3.0;
    }

    public static boolean isValidCrystal(ItemStack stack) {
        return stack.getItem() instanceof ElementalCrystalItem
                && !(stack.getItem().getClass().equals(InertCrystalItem.class));
    }

    private static DataResult<CrystalPouchContentsComponent> validate(List<ItemStack> stacks) {
        if (stacks.size() > 3) {
            return DataResult.error(() -> "Crystal pouch can hold at most 3 items.");
        }

        Set<Item> unique = new HashSet<>();
        for (ItemStack stack : stacks) {
            if (!isValidCrystal(stack)) {
                return DataResult.error(() -> "Invalid item in crystal pouch: " + stack.getItem());
            }
            if (!unique.add(stack.getItem())) {
                return DataResult.error(() -> "Duplicate crystal type not allowed: " + stack.getItem());
            }
        }

        return DataResult.success(new CrystalPouchContentsComponent(stacks));
    }

    public static class Builder {
        private final List<ItemStack> stacks;

        public Builder() {
            this.stacks = new ArrayList<>();
        }

        public Builder(CrystalPouchContentsComponent existing) {
            this.stacks = new ArrayList<>(existing.stacks);
        }

        public boolean add(ItemStack stack, PlayerEntity player) {
            if (!isValidCrystal(stack)) return false;
            if (stacks.size() >= 3) return false;

            for (ItemStack existing : stacks) {
                if (ItemStack.areItemsAndComponentsEqual(existing, stack)) return false;
            }

            stacks.add(stack.copyWithCount(1));
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            return true;
        }

        public ItemStack removeSelected() {
            if (!stacks.isEmpty()) {
                return stacks.remove(stacks.size() - 1);
            }
            return ItemStack.EMPTY;
        }

        public Builder remove(ItemStack stack) {
            stacks.removeIf(s -> ItemStack.areItemsAndComponentsEqual(s, stack));
            return this;
        }

        public Builder clear() {
            stacks.clear();
            return this;
        }

        public CrystalPouchContentsComponent build() {
            return new CrystalPouchContentsComponent(stacks);
        }
    }

    @Override
    public String toString() {
        return "CrystalPouchContents" + stacks.toString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof CrystalPouchContentsComponent other &&
                ItemStack.stacksEqual(this.stacks, other.stacks));
    }

    @Override
    public int hashCode() {
        return ItemStack.listHashCode(this.stacks);
    }
}
