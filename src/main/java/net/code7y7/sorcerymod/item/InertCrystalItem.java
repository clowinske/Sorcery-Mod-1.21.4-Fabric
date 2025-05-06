package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.component.AppendedCrystalTier;
import net.code7y7.sorcerymod.component.CrystalTier;
import net.code7y7.sorcerymod.component.CrystalUnlockedAbilities;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.code7y7.sorcerymod.spell.CrystalOptions;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
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
    public static final int MAX_INERT_TIER = 2;
    public boolean hasElement;
    public String elementName;
    public CrystalData elementType;
    private static List<String> toAdd = new ArrayList<>(Arrays.asList("1"));
    public final static CustomModelDataComponent defaultCustomData = new CustomModelDataComponent(List.of(), List.of(), List.copyOf(toAdd), List.of());
    public final static CrystalUnlockedAbilities defaultUnlockedAbilities = new CrystalUnlockedAbilities(List.of());

    public InertCrystalItem(Settings settings) {
        super(settings.maxCount(1)
                .component(DataComponentTypes.CUSTOM_MODEL_DATA, defaultCustomData)
                .component(ModDataComponentTypes.CRYSTAL_TIER, new CrystalTier(DEFAULT_CRYSTAL_TIER))
                .component(ModDataComponentTypes.CRYSTAL_UNLOCKED_ABILITIES, defaultUnlockedAbilities)
                .component(ModDataComponentTypes.APPENDED_CRYSTAL_TIER, new AppendedCrystalTier(DEFAULT_CRYSTAL_TIER)));

        hasElement = false;
        elementName = "inert";
        elementType = CrystalData.INERT;
    }


    public void setTier(ItemStack stack, int tier){
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
        return Objects.requireNonNull(stack.get(ModDataComponentTypes.CRYSTAL_TIER)).getValue();
    }
    public int getMaxTier(){
        if(elementName.equals("inert")){
            return MAX_INERT_TIER;
        }
        return MAX_CRYSTAL_TIER;
    }

    public void setAppendedTier(ItemStack stack, int tier){
        AppendedCrystalTier level;
        if(tier >= getTier(stack))
            level = new AppendedCrystalTier(tier);
        else
            level = new AppendedCrystalTier(getTier(stack));
        stack.set(ModDataComponentTypes.APPENDED_CRYSTAL_TIER, level);
    }
    public int getAppendedTier(ItemStack stack){
        return Objects.requireNonNull(stack.get(ModDataComponentTypes.APPENDED_CRYSTAL_TIER)).getValue();
    }
    public void upgradeCrystal(ItemStack stack, int amt){
        if(amt > 0) {
            if (getTier(stack) < MAX_CRYSTAL_TIER) {
                setTier(stack, getTier(stack) + amt);
            }
        } else if(amt < 0){
            if (getTier(stack) > 1) {
                setTier(stack, getTier(stack) + amt);
            }
        }
        //set ability options to new maximum value when upgraded, if already at maximum
        List<String> unlockedAbilities = stack.get(ModDataComponentTypes.CRYSTAL_UNLOCKED_ABILITIES).getAbilities();
        for(String ability : unlockedAbilities){
            AbilityOptions options = stack.get(ModDataComponentTypes.CRYSTAL_OPTIONS).getOptions().get(ability);
            if(options != null) {
                for (int i = 0; i < options.abilityOptions.size(); i++) {
                    System.out.println("set " + ability + " from " + options.abilityOptions.get(i) + " to " + options.getMaxValue(i, stack));
                    setCrystalOptions(stack, ability, i, options.getMaxValue(i, stack));
                }
            }
        }
    }
    public CrystalOptions getCrystalOptions(ItemStack stack){
        return stack.get(ModDataComponentTypes.CRYSTAL_OPTIONS);
    }

    public void setCrystalOptions(ItemStack stack, String abilityName, int optionIndex, double value){
        CrystalOptions options = getCrystalOptions(stack);
        options.spellOptions.get(abilityName).abilityOptions.set(optionIndex, value);
        stack.set(ModDataComponentTypes.CRYSTAL_OPTIONS, options);
    }

    public boolean addCrystalAbility(ItemStack stack, String ability) {
        ArrayList<String> abilities = new ArrayList<>(getUnlockedAbilities(stack));

        if (!abilities.contains(ability)) {

            abilities.add(ability);

            stack.set(ModDataComponentTypes.CRYSTAL_UNLOCKED_ABILITIES, new CrystalUnlockedAbilities(abilities));
            return true;
        }
        return false;
    }

    public boolean removeCrystalAbility(ItemStack stack, String ability) {
        ArrayList<String> abilities = new ArrayList<>(getUnlockedAbilities(stack)); //list declared

        if (abilities.contains(ability)) {

            abilities.remove(ability);

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
        int text = CrystalData.getTypeByString(elementName).getTextColor();
        int textExtra = CrystalData.getTypeByString(elementName).getTextExtraColor();

        super.appendTooltip(stack, context, tooltip, type);
        if (Screen.hasShiftDown()) {
            int tier = stack.get(ModDataComponentTypes.CRYSTAL_TIER).getValue();
            tooltip.add(Text.literal("Tier: ").withColor(textExtra).append(Text.literal("" + (tier == 0 ? DEFAULT_CRYSTAL_TIER : tier)).withColor(text)));
        }
    }
    public String getCrystalAbility(int tier, int index){
        return ABILITIES.get((tier-1)*4 + index );
    }
    public String getCrystalAbility(int index){
        return ABILITIES.get(index);
    }
}