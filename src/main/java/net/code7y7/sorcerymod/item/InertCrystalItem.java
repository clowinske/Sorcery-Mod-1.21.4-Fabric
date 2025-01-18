package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.component.CrystalTier;
import net.code7y7.sorcerymod.component.CrystalUnlockedAbilities;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InertCrystalItem extends Item {
    //list of available abilites
    public List<String> ABILITIES;
    private static final int DEFAULT_CRYSTAL_TIER = 1;
    public static final int MAX_CRYSTAL_TIER = 10;
    public boolean hasElement;
    public String elementName;
    private static List<String> toAdd = new ArrayList<>(Arrays.asList("1"));
    public final static CustomModelDataComponent defaultCustomData = new CustomModelDataComponent(List.of(), List.of(), List.copyOf(toAdd), List.of());
    public final static CrystalUnlockedAbilities defaultUnlockedAbilities = new CrystalUnlockedAbilities(List.of());

    public InertCrystalItem(Settings settings) {
        super(settings.maxCount(1)
                .component(DataComponentTypes.CUSTOM_MODEL_DATA, defaultCustomData)
                .component(ModDataComponentTypes.CRYSTAL_TIER, new CrystalTier(DEFAULT_CRYSTAL_TIER))
                .component(ModDataComponentTypes.CRYSTAL_UNLOCKED_ABILITIES, new CrystalUnlockedAbilities(List.of())));

        hasElement = false;
        elementName = "inert";
    }

    public void setTier(ServerPlayerEntity player, ItemStack stack, int tier){
        CrystalTier level = new CrystalTier(tier);
        stack.set(ModDataComponentTypes.CRYSTAL_TIER, level);

        List<String> toAdd = new ArrayList<>();
        if(level.getValue() <= 3)
            toAdd.add("" + level.getValue());
        else if(level.getValue() > 3 && level.getValue() < MAX_CRYSTAL_TIER)
            toAdd.add("3");
        else if(level.getValue() == MAX_CRYSTAL_TIER)
            toAdd.add("3");
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.copyOf(toAdd), List.of()));
    }
    public int getTier(ItemStack stack){
        return stack.get(ModDataComponentTypes.CRYSTAL_TIER).getValue();
    }
    public int getMaxTier(){
        return MAX_CRYSTAL_TIER;
    }
    public void upgradeCrystal(ServerPlayerEntity player, ItemStack stack, int amt){
        if(getTier(stack) < MAX_CRYSTAL_TIER){
            setTier(player, stack, getTier(stack)+amt);
        }
    }

    public boolean addCrystalAbility(ItemStack stack, String ability) {
        ArrayList<String> abilities = new ArrayList<>(getUnlockedAbilities(stack)); //list declared

        if (!abilities.contains(ability)) {

            abilities.add(ability); //this line

            stack.set(ModDataComponentTypes.CRYSTAL_UNLOCKED_ABILITIES, new CrystalUnlockedAbilities(abilities));
            return true;
        }
        return false;
    }
    public List<String> getUnlockedAbilities(ItemStack stack) {
        return stack.get(ModDataComponentTypes.CRYSTAL_UNLOCKED_ABILITIES).getAbilities();
    }
    public boolean hasAbilityUnlocked(ItemStack stack, String ability) {
        return getUnlockedAbilities(stack).contains(ability);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int text;
        int textExtra;

        if(!Objects.equals(elementName, "inert")){
            text = CrystalData.getTypeByString(elementName).getTextColor();
            textExtra = CrystalData.getTypeByString(elementName).getTextExtraColor();
        } else {
            text = 0x909090;
            textExtra = 0xc7c7c7;
        }

        super.appendTooltip(stack, context, tooltip, type);
        if (Screen.hasShiftDown()) {
            int tier = stack.get(ModDataComponentTypes.CRYSTAL_TIER).getValue();
            tooltip.add(Text.literal("Tier: ").withColor(textExtra).append(Text.literal("" + (tier == 0 ? DEFAULT_CRYSTAL_TIER : tier)).withColor(text)));
        }
    }
    public String getCrystalAbility(int tier, int index){
        return ABILITIES.get((tier-1)*4 + index );
    }
}