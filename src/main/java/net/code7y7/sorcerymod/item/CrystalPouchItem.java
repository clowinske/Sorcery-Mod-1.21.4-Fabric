package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.attachment.ModAttachmentTypes;
import net.code7y7.sorcerymod.component.CrystalPouchContentsComponent;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.sound.ModSounds;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CrystalPouchItem extends Item {
    private static final int ITEM_BAR_COLOR = CrystalData.INERT.getTextColor();
    private static final int FULL_ITEM_BAR_COLOR = CrystalData.INERT.getTextColor();

    public CrystalPouchItem(Settings settings) {
        super(settings.maxCount(1).component(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, CrystalPouchContentsComponent.DEFAULT));
    }

    public static float getAmountFilled(ItemStack stack) {
        CrystalPouchContentsComponent contents = stack.getOrDefault(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, CrystalPouchContentsComponent.DEFAULT);
        return contents.getOccupancy().floatValue();
    }
    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        CrystalPouchContentsComponent contents = stack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS);
        if (contents == null) return false;

        ItemStack clickedStack = sanitize(slot.getStack());
        CrystalPouchContentsComponent.Builder builder = new CrystalPouchContentsComponent.Builder(contents);

        if (clickType == ClickType.LEFT) {
            if (clickedStack.isEmpty()) {
                return false;
            }

            if (!isValidCrystal(clickedStack, contents.getStacks())) {
                playInsertFailSound(player);
                return true;
            }

            if (builder.add(clickedStack, player)) {
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }

            stack.set(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, builder.build());
            return true;

        } else if (clickType == ClickType.RIGHT && clickedStack.isEmpty()) {
            ItemStack removed = sanitize(builder.removeSelected());
            if (!removed.isEmpty()) {
                slot.insertStack(removed);
                playRemoveOneSound(player);
            }

            stack.set(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, builder.build());
            return true;
        }

        return false;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursor) {
        CrystalPouchContentsComponent contents = (CrystalPouchContentsComponent) stack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS);
        if (contents == null) return false;

        otherStack = sanitize(otherStack);
        CrystalPouchContentsComponent.Builder builder = new CrystalPouchContentsComponent.Builder(contents);

        if (clickType == ClickType.LEFT) {
            if (otherStack.isEmpty()) {
                return false;
            }

            if (!isValidCrystal(otherStack, contents.getStacks())) {
                playInsertFailSound(player);
                return true;
            }

            if (builder.add(otherStack, player)) {
                playInsertSound(player);
                otherStack.decrement(1);
            } else {
                playInsertFailSound(player);
            }

            stack.set(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, builder.build());
            return true;

        } else if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
            ItemStack removed = sanitize(builder.removeSelected());
            if (!removed.isEmpty()) {
                cursor.set(removed);
                playRemoveOneSound(player);
            }

            stack.set(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, builder.build());
            return true;
        }

        return false;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // This logic should run **on the server** to persist data
        if (!Objects.equals(stack.get(ModDataComponentTypes.POUCH_ID), user.getAttached(ModAttachmentTypes.POUCH_ID)) || !user.hasAttached(ModAttachmentTypes.POUCH_ID)) {
            String newId = UUID.randomUUID().toString();
            stack.set(ModDataComponentTypes.POUCH_ID, newId);
            user.setAttached(ModAttachmentTypes.POUCH_ID, newId);

            // Only play sound on client
            if (world.isClient()) {
                user.playSound(ModSounds.ORB_UI_CLICK, 1f, 1f);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public boolean isItemBarVisible(ItemStack stack) {
        return getAmountFilled(stack) > 0;
    }

    public int getItemBarStep(ItemStack stack) {
        return (int)(13 * getAmountFilled(stack));
    }

    public int getItemBarColor(ItemStack stack) {
        int color = CrystalData.INERT.getColorInt();
        ItemStack firstStack = stack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS).getStacks().getFirst();
        if(firstStack.getItem() instanceof ElementalCrystalItem item)
            color = item.elementType.getColorInt();

        //return getAmountFilled(stack) >= 1.0F ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
        return color;
    }

    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable((CrystalPouchContentsComponent) stack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS))
                .map(CrystalPouchTooltipData::new);
    }

    public void onItemEntityDestroyed(ItemEntity entity) {
        CrystalPouchContentsComponent contents = (CrystalPouchContentsComponent) entity.getStack().get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS);
        if (contents != null) {
            entity.getStack().set(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, CrystalPouchContentsComponent.DEFAULT);
            ItemUsage.spawnItemContents(entity, contents.iterateCopy().collect(Collectors.toList()));
        }
    }

    public static ItemStack sanitize(ItemStack stack) {
        return (stack == null || stack.isEmpty() || stack.getCount() <= 0 || stack.isOf(Items.AIR))
                ? ItemStack.EMPTY
                : stack;
    }

    private static boolean isValidCrystal(ItemStack stack, List<ItemStack> pouchInventory) {
        if (!(stack.getItem() instanceof ElementalCrystalItem newCrystal)) {
            return false;
        }

        for (ItemStack slotStack : pouchInventory) {
            if (slotStack.getItem() instanceof ElementalCrystalItem existingCrystal) {
                if (existingCrystal.elementType == newCrystal.elementType) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean addItem(ItemStack pouchStack, ItemStack itemToAdd, PlayerEntity player) {
        CrystalPouchContentsComponent contents = pouchStack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS);
        if (contents == null) return false;

        itemToAdd = sanitize(itemToAdd);
        if (itemToAdd.isEmpty()) return false;

        if (!isValidCrystal(itemToAdd, contents.getStacks())) {
            playInsertFailSound(player);
            return false;
        }

        CrystalPouchContentsComponent.Builder builder = new CrystalPouchContentsComponent.Builder(contents);
        if (builder.add(itemToAdd, player)) {
            pouchStack.set(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, builder.build());
            playInsertSound(player);
            return true;
        } else {
            playInsertFailSound(player);
            return false;
        }
    }
    public ItemStack removeItem(ItemStack pouchStack, PlayerEntity player) {
        CrystalPouchContentsComponent contents = pouchStack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS);
        if (contents == null) return ItemStack.EMPTY;

        CrystalPouchContentsComponent.Builder builder = new CrystalPouchContentsComponent.Builder(contents);
        ItemStack removed = sanitize(builder.removeSelected());
        if (!removed.isEmpty()) {
            pouchStack.set(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, builder.build());
            playRemoveOneSound(player);
            return removed;
        }

        return ItemStack.EMPTY;
    }

    private static void playInsertSound(PlayerEntity player) {
        player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
    }

    private static void playInsertFailSound(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 0.5F, 0.8F);
    }

    private static void playRemoveOneSound(PlayerEntity player) {
        player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
    }
}
