package net.code7y7.sorcerymod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.SorceryModClient;
import net.code7y7.sorcerymod.component.CrystalPouchContentsComponent;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.network.AddTimePonderingC2SPayload;
import net.code7y7.sorcerymod.sound.ModSounds;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.code7y7.sorcerymod.spell.CrystalOptions;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.util.crystal.CrystalAbilities;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrystalOptionScreen extends Screen {
    private ItemStack crystal;
    private final Screen parent;
    private final int backgroundWidth, backgroundHeight,
            index,
            itemBorderWidth, itemBorderHeight,
            descriptionBoxWidth, descriptionBoxHeight,
            tabWidth, tabHeight,
            button1Size, button2Size,
            button3Size, button4Size,
            buttonCenter1Size, buttonCenter2Size,
            tierBorderWidth, tierBorderHeight,
            selectedTextureSize1, selectedTextureSize2,
            settingsIconSize,
            slotHeight, slotWidth,
            settingsWidth, settingsHeight,
            creativeTabWidth, creativeTabHeight;

    int textColor = 0x909090; //default

    private boolean renderingSettingsOverlay;

    private static Identifier BACKGROUND = SorceryMod.createIdentifier("textures/gui/orb/attuning_orb_gui.png");
    protected CrystalOptionScreen(int index, Screen parent) {
        super(Text.literal("Crystal Options"));
        this.parent = parent;
        this.index = index;
        this.backgroundWidth = 314;
        this.backgroundHeight = 178;
        this.itemBorderWidth = 38;
        this.itemBorderHeight = 38;
        this.descriptionBoxWidth = 76;
        this.descriptionBoxHeight = 160;
        this.tabWidth = 38;
        this.tabHeight = 20;
        this.button1Size = 12;
        this.button2Size = 12;
        this.button3Size = 6;
        this.button4Size = 6;
        this.buttonCenter1Size = 4;
        this.buttonCenter2Size = 2;
        this.tierBorderWidth = 136;
        this.tierBorderHeight = 160;
        this.selectedTextureSize1 = 8;
        this.selectedTextureSize2 = 4;
        this.settingsIconSize = 16;
        this.slotWidth = 20;
        this.slotHeight = 20;
        this.settingsWidth = 124;
        this.settingsHeight = 82;
        this.creativeTabWidth = 76;
        this.creativeTabHeight = 30;
    }
    long openTime;
    @Override
    protected void init() {
        this.crystal = getCrystals(client.player).get(index);
        int centerX = width / 2;
        int centerY = height / 2;
        selectedIndex = -1;
        renderingSettingsOverlay = false;
        setUITheme(SorceryModClient.playerData.orbUI);
        openTime = System.currentTimeMillis();
    }

    @Override
    public void removed() {
        super.removed();
        long closeTime = System.currentTimeMillis();
        long timeSpent = closeTime - openTime;
        ClientPlayNetworking.send(new AddTimePonderingC2SPayload(timeSpent/20));
    }

    private void setUITheme(int i){
        SorceryModClient.playerData.orbUI = i;
        BACKGROUND = SorceryMod.createIdentifier("textures/gui/orb/attuning_orb_gui"+i+".png");
        if(i == 0)
            textColor = 0x909090;
        if(i == 1)
            textColor = 0x3D3D3D;
        if(i == 2)
            textColor = 0x5E564F;
    }
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 0, 0, backgroundWidth, backgroundHeight, 512, 512);
    }
    int descriptionX;
    int descriptionY;
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        int centerX = width / 2;
        int centerY = height / 2;

        //draw item tabs
        int tabsX = centerX - (backgroundWidth/2)-tabHeight-9;
        int tabsY = centerY - (backgroundHeight/2);
        drawTabs(context, tabsX, tabsY, mouseX, mouseY);

        drawBackground(context, delta, mouseX, mouseY);

        //draw item border
        int itemBorderX = centerX - (backgroundWidth/2) + (itemBorderWidth/2) + 9;
        int itemBorderY = centerY - (38/2) - 10;
        drawSelectedCrystalWithBorder(context, itemBorderX, itemBorderY);

        //draw description box
        descriptionX = centerX + (backgroundWidth/2) - descriptionBoxWidth - 9;
        descriptionY = centerY-(backgroundHeight/2)+9;
        drawDescriptionBorder(context, descriptionX, descriptionY);

        drawDescription(context, descriptionX, descriptionY, mouseX, mouseY);

        int tierBorderX = centerX - (tierBorderWidth/2);
        int tierBorderY = centerY-(backgroundHeight/2)+9;
        drawTierBorder(context, tierBorderX, tierBorderY);

        int tiersX = centerX - (button2Size/2);
        int tiersY = centerY + (backgroundHeight/2)-44;
        drawTiers(context, tiersX, tiersY, mouseX, mouseY);

        int settingsIconX = centerX - (backgroundWidth/2) + 3;
        int settingsIconY = centerY + (backgroundHeight/2) - 7 - settingsIconSize;
        drawSettingsIcon(context, settingsIconX, settingsIconY, mouseX, mouseY);

        if(client.player.isCreative()){
            int creativeTabX = centerX - (backgroundWidth/2)+9;
            int creativeTabY = centerY - (backgroundHeight/2)+9;
            drawCreativeTab(context, creativeTabX, creativeTabY, mouseX, mouseY);
        }

        if(renderingSettingsOverlay){
            renderSettingsOverlay(context, mouseX, mouseY);
        }
    }
    int themeNum = 5;
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ElementalCrystalItem item = (ElementalCrystalItem)crystal.getItem();
        CrystalData crystalType = item.elementType;
        int centerX = width / 2;
        int centerY = height / 2;
        int x = centerX - (button2Size/2);
        int y = centerY + (backgroundHeight/2)-44;
        int selected = SorceryModClient.playerData.orbUI;
        if(button == 0) {
            if(renderingSettingsOverlay){
                if (mouseX >= centerX + settingsWidth / 2 - 6 && mouseX <= centerX + settingsWidth / 2 - 6 + 3 && mouseY >= centerY - settingsHeight / 2 + 3 && mouseY <= centerY - settingsHeight / 2 + 6) {
                    renderingSettingsOverlay = false;
                    client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                }
                int buttonX = centerX - (settingsWidth/2) + 10;
                int buttonY = centerY - (settingsHeight/2) + 3 + 8 + 3;
                for(int i = 0; i < themeNum; i++){
                    if(mouseX >= buttonX + (i*(slotWidth+1)) && mouseX <= buttonX + (i*(slotWidth+1)) + slotWidth && mouseY >= buttonY && mouseY <= buttonY + slotHeight && i != selected) {
                        setUITheme(i);
                        client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                    }
                }
            } else {
                if(mouseX >= centerX - (backgroundWidth/2)+9+(creativeTabWidth/2)-slotWidth-1 && mouseX <= centerX - (backgroundWidth/2)+9+(creativeTabWidth/2)-1 && mouseY >= centerY - (backgroundHeight/2)+9+5 && mouseY <= centerY - (backgroundHeight/2)+9+5+slotHeight) {
                    if(Screen.hasShiftDown()) {
                        if (item.getTier(crystal) > 1) {
                            item.upgradeCrystal(crystal, -1);
                            client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                            return true;
                        }
                    } else {
                        if (item.getTier(crystal) < item.getMaxTier()) {
                            item.upgradeCrystal(crystal, 1);
                            client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                            return true;
                        }
                    }
                }

                if(mouseX >= centerX - (backgroundWidth/2)+9+(creativeTabWidth/2)+2 && mouseX <= centerX - (backgroundWidth/2)+9+(creativeTabWidth/2)+2 + slotWidth && mouseY >= centerY - (backgroundHeight/2)+9+5 && mouseY <= centerY - (backgroundHeight/2)+9+5+slotHeight && selectedIndex != -1) {
                    boolean hasUnlocked = item.hasAbilityUnlocked(crystal, crystalType.getAbilities().get(selectedIndex));
                    if(!hasUnlocked) {
                        item.addCrystalAbility(crystal, crystalType.getAbilities().get(selectedIndex));
                        client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                        return true;
                    }
                }

                if (hoveredTab != -1) { // left click
                    client.setScreen(new CrystalOptionScreen(hoveredTab, parent));
                    client.player.playSound(ModSounds.ORB_UI_SELECT, 1.0f, 1.0f);
                    return true;
                }
                for (int i = 0; i < 3; i++) {
                    int xPos = x;
                    int yPos = y - (i * 50);
                    if (mouseX >= xPos && mouseX <= xPos + button2Size && mouseY >= yPos && mouseY <= yPos + button2Size) {
                        if (selectedIndex != i * 4) {
                            client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                        } else {
                            //other sound
                        }
                        selectedIndex = (i * 4);
                        return true;
                    }
                    for (int j = 0; j < 3; j++) {
                        int radius = 12;
                        double angle = Math.toRadians(120 * j - 90);
                        int xOffset = (int) (radius * Math.cos(angle)) - 8;
                        int yOffset = (int) (radius * Math.sin(angle)) - 8;

                        xPos = x + (button1Size / 2) + (button3Size / 2) + 2 + xOffset;
                        yPos = y + (button1Size) - (i * 50) + yOffset;

                        if (mouseX >= xPos && mouseX <= xPos + button3Size && mouseY >= yPos && mouseY <= yPos + button3Size) {
                            if (selectedIndex != i * 4 + j + 1) {
                                client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                            } else {
                                //other sound (ability clicked but not unlocked)
                            }
                            selectedIndex = (i * 4) + j + 1;
                            return true;
                        }
                    }
                }
                if(selectedIndex != -1) {
                    String abilityName = item.getCrystalAbility(selectedIndex);
                    AbilityOptions abilityOptions = item.getCrystalOptions(crystal).getOptions().get(abilityName);
                    if (abilityOptions == null) return super.mouseClicked(mouseX, mouseY, button);

                    for (int i = 0; i < abilityOptions.abilityOptions.size(); i++) {
                        int optionType = abilityOptions.getOptionType(i);
                        double optionValue = abilityOptions.abilityOptions.get(i);

                        int thisY = descriptionY + i * 28 + 24 + 16; // your slider Y + offset
                        int sliderX = descriptionX;
                        int sliderY = thisY;
                        int sliderW = 76;
                        int sliderH = 3;

                        int buttonX = centerX + (backgroundWidth/2) - descriptionBoxWidth - 9 + (descriptionBoxWidth/2)-6;
                        int buttonY = thisY - 1 ;
                        int buttonW = 12;
                        int buttonH = 7;
                        if(optionType == 2) {
                            if(mouseX >= buttonX && mouseX <= buttonX + buttonW && mouseY >= buttonY && mouseY <= buttonY + buttonH) { //hovering
                                //abilityOptions.abilityOptions.set(i, Math.abs(optionValue-1));
                                ((ElementalCrystalItem) crystal.getItem()).setCrystalOptions(crystal, abilityName, i, optionValue-1);
                                client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                                return true;
                            }
                        }

                        if(optionType == 0 || optionType == 1) {
                            if(mouseX >= sliderX && mouseX <= sliderX + sliderW && mouseY >= sliderY && mouseY <= sliderY + sliderH) {
                                double min = abilityOptions.getMinValue(i, crystal);
                                double max = abilityOptions.getMaxValue(i, crystal);
                                draggingSlider = new DraggingSlider(i, min, max, optionType == 1);
                                updateSliderValue(mouseX);
                                return true;
                            }
                        }
                    }
                }
            }

            int settingsIconX = centerX - (backgroundWidth/2) + 3;
            int settingsIconY = centerY + (backgroundHeight/2) - 7 - settingsIconSize;
            if(mouseX >= settingsIconX+2 && mouseX <= settingsIconX+2 + settingsIconSize && mouseY >= settingsIconY+2 && mouseY <= settingsIconY+2 + settingsIconSize && !renderingSettingsOverlay){
                renderingSettingsOverlay = true;
                client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if(client != null && client.player !=null && SpellHelper.getPouchItem(client.player) != ItemStack.EMPTY) {
            CrystalPouchContentsComponent contents = SpellHelper.getPouchItem(client.player).get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS);
            CrystalPouchContentsComponent.Builder builder = new CrystalPouchContentsComponent.Builder(contents);

            List<ItemStack> list = new ArrayList<>(SpellHelper.getAttachedCrystals(client.player));
            list.set(index, crystal);

            for(ItemStack stack : list){
                builder.add(stack, client.player);
            }

            SpellHelper.getPouchItem(client.player).set(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS, builder.build());
        }
        super.close();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingSlider != null) {
            draggingSlider = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingSlider != null) {
            updateSliderValue(mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (renderingSettingsOverlay) {
                // If overlay is open, just close the overlay
                renderingSettingsOverlay = false;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    private void updateSliderValue(double mouseX) {
        if (selectedIndex == -1 || draggingSlider == null) return;

        ElementalCrystalItem item = (ElementalCrystalItem) crystal.getItem();
        String abilityName = item.getCrystalAbility(selectedIndex);
        AbilityOptions abilityOptions = item.getCrystalOptions(crystal).getOptions().get(abilityName);
        if (abilityOptions == null) return;

        int i = draggingSlider.optionIndex;
        double min = draggingSlider.min;
        double max = draggingSlider.max;
        int pixelsDistance = 60;
        int sliderX = descriptionX + 6; // this is where your slider drawing begins (knob track)

        // offset mouseX to center of knob (5px knob, so 2.5 offset)
        double relX = Math.max(0, Math.min(mouseX - sliderX - 2, pixelsDistance));
        double percent = relX / pixelsDistance;
        double value = min + percent * (max - min);

        if (draggingSlider.isInt) {
            value = Math.round(value);
        }
        //abilityOptions.abilityOptions.set(i, value);
        ((ElementalCrystalItem) crystal.getItem()).setCrystalOptions(crystal, abilityName, i, value);
    }

    private void drawCreativeTab(DrawContext context, int x, int y, int mouseX, int mouseY){
        ElementalCrystalItem item = (ElementalCrystalItem) crystal.getItem();
        CrystalData crystalType = item.elementType;
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 390, 0, creativeTabWidth, creativeTabHeight, 512, 512);

        if(mouseX >= x+(creativeTabWidth/2)-slotWidth-1 && mouseX <= x+(creativeTabWidth/2)-1 && mouseY >= y+5 && mouseY <= y+5+slotHeight) {
            if(Screen.hasShiftDown()) {
                if (item.getTier(crystal) > 1) {
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) - slotWidth - 1, y + 5, 20, 218, slotWidth, slotHeight, 512, 512);
                } else {
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) - slotWidth - 1, y + 5, 0, 218, slotWidth, slotHeight, 512, 512);
                }
            } else {
                if (item.getTier(crystal) < item.getMaxTier()) {
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) - slotWidth - 1, y + 5, 20, 218, slotWidth, slotHeight, 512, 512);
                } else {
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) - slotWidth - 1, y + 5, 0, 218, slotWidth, slotHeight, 512, 512);
                }
            }
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) - slotWidth - 1, y + 5, 0, 218, slotWidth, slotHeight, 512, 512);
        }
        if(mouseX >= x+(creativeTabWidth/2)+2 && mouseX <= x+(creativeTabWidth/2)+2 + slotWidth && mouseY >= y+5 && mouseY <= y+5+slotHeight && selectedIndex != -1) {
            boolean hasUnlocked = item.hasAbilityUnlocked(crystal, crystalType.getAbilities().get(selectedIndex));
            if(!hasUnlocked) {
                context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) + 2, y + 5, 20, 218, slotWidth, slotHeight, 512, 512);
            } else {
                context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) + 2, y + 5, 0, 218, slotWidth, slotHeight, 512, 512);
            }
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) + 2, y + 5, 0, 218, slotWidth, slotHeight, 512, 512);
        }
        if(Screen.hasShiftDown()){
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) - slotWidth + 1, y + 7, 76, 222, 16, 16, 512, 512); //upgrade crystal icon
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) - slotWidth + 1, y + 7, 76, 206, 16, 16, 512, 512); //upgrade crystal icon
        }
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + (creativeTabWidth / 2) + 4, y + 7, 76, 238, 16, 16, 512, 512); //unlock ability icon
    }

    private void renderSettingsOverlay(DrawContext context, int mouseX, int mouseY){
        int selected = SorceryModClient.playerData.orbUI;
        int centerX = width / 2;
        int centerY = height / 2;
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, centerX-settingsWidth/2, centerY-settingsHeight/2, 252, 178, settingsWidth, settingsHeight, 512, 512);
        if (mouseX >= centerX + settingsWidth / 2 - 6 && mouseX <= centerX + settingsWidth / 2 - 6 + 2 && mouseY >= centerY - settingsHeight / 2 + 3 && mouseY <= centerY - settingsHeight / 2 + 5) {
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, centerX + settingsWidth / 2 - 6, centerY - settingsHeight / 2 + 3, 376, 181, 3, 3, 512, 512);
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, centerX + settingsWidth / 2 - 6, centerY - settingsHeight / 2 + 3, 376, 178, 3, 3, 512, 512);
        }
        drawScaledText(context, Text.translatable("crystalorb.ui.theme"), centerX, centerY-settingsHeight/2+4, 1f, textColor, true);

        int buttonX = centerX - (settingsWidth/2) + 10;
        int buttonY = centerY - (settingsHeight/2) + 3 + 8 + 3;
        for(int i = 0; i < themeNum; i++){
            if (mouseX >= buttonX + (i * (slotWidth + 1)) && mouseX <= buttonX + (i * (slotWidth + 1)) + slotWidth && mouseY >= buttonY && mouseY <= buttonY + slotHeight) {
                context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, buttonX + (i * (slotWidth + 1)), buttonY, 20, 218, slotWidth, slotHeight, 512, 512);
            } else {
                context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, buttonX + (i * (slotWidth + 1)), buttonY, 0, 218, slotWidth, slotHeight, 512, 512);

            }
            if(i == selected)
                context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, buttonX + (i*(slotWidth+1)), buttonY, 40, 218, slotWidth, slotHeight, 512, 512);

            int iconX = buttonX + (i*((slotWidth/2 + 11)))+3;
            int iconY = buttonY + 3;

            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, iconX, iconY, 102, 206 +(i*14), 14, 14, 512, 512);
        }
    }

    private void drawSettingsIcon(DrawContext context, int x, int y, int mouseX, int mouseY){
        if (mouseX >= x+2 && mouseX <= x+2 + settingsIconSize && mouseY >= y+2 && mouseY <= y+2 + settingsIconSize && !renderingSettingsOverlay) {
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 20, 218, slotWidth, slotHeight, 512, 512);
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 0, 218, slotWidth, slotHeight, 512, 512);
        }
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x+2, y+2, 76, 190, settingsIconSize, settingsIconSize, 512, 512);
    }

    private void drawTierBorder(DrawContext context, int x, int y){
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 116, 178, tierBorderWidth, tierBorderHeight, 512, 512);
    }
    public void drawDescription(DrawContext context, int x, int y, int mouseX, int mouseY){
        ElementalCrystalItem item = (ElementalCrystalItem) crystal.getItem();
        CrystalOptions spellOptions = item.getCrystalOptions(crystal);
        int sliderWidth = 76;
        int sliderHeight = 3;
        if(selectedIndex != -1) {
            Text abilityName;
            int optionsSize;
            if(item.hasAbilityUnlocked(crystal, item.getCrystalAbility(selectedIndex))){
                AbilityOptions abilityOptions = spellOptions.getOptions().get(item.getCrystalAbility(selectedIndex));
                if(abilityOptions != null) {
                    abilityName = abilityOptions.getName();
                    optionsSize = abilityOptions.abilityOptions.size();

                    if(optionsSize == 0){
                        drawScaledText(context, Text.translatable("crystal_options.option.no_options"), x + (descriptionBoxWidth/2), y+24, 0.75f, textColor, true);
                    } else {
                        for (int i = 0; i < optionsSize; i++) {
                            int thisY = y + i * 28 + 24;
                            Text optionText = abilityOptions.getName(i);
                            int optionType = abilityOptions.getOptionType(i);

                            double doubleValue = new BigDecimal(abilityOptions.abilityOptions.get(i)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                            int intValue = (int) Math.round(abilityOptions.abilityOptions.get(i));
                            boolean boolValue = (int) Math.round(abilityOptions.abilityOptions.get(i)) == 1;

                            double minValue = abilityOptions.getMinValue(i, crystal);
                            double maxValue = abilityOptions.getMaxValue(i, crystal);

                            if(optionType == 0 || optionType == 1) { //slider type. (0 = double values, 1 = int values)
                                context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, thisY + 16, 314, 160, sliderWidth, sliderHeight, 512, 512);
                                int pixelsDistance = 60; //pixels of slider to work with
                                int posOnSlider = (int) (((doubleValue-minValue) / (maxValue - minValue)) * pixelsDistance);

                                if(optionType == 1){ //add notches on the slider for the knob to snap to
                                    int values = (int)(maxValue - minValue)+1;
                                    for(int j = 0; j < values-1; j++){
                                        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + 8 + (j*(pixelsDistance/(values-1))), thisY + 16, 329, 163, 1, 3, 512, 512);
                                    }
                                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + 8 + pixelsDistance-1, thisY + 16, 329, 163, 1, 3, 512, 512);
                                }
                                int sliderX = descriptionX;
                                int sliderY = thisY + 15;
                                int sliderW = 76;
                                int sliderH = 3;

                                //draw slider knob
                                if (mouseX >= sliderX && mouseX <= sliderX + sliderW && mouseY >= sliderY && mouseY <= sliderY + sliderH) { //hovering
                                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + 6 + posOnSlider, thisY+15, 319, 163, 5, 5, 512, 512);
                                } else {//default
                                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + 6 + posOnSlider, thisY+15, 314, 163, 5, 5, 512, 512);
                                }
                                if(draggingSlider != null && draggingSlider.optionIndex == i)//selected
                                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x + 6 + posOnSlider, thisY+15, 324, 163, 5, 5, 512, 512);
                            } else if(optionType == 2){
                                int buttonX = x + (descriptionBoxWidth/2)-6;
                                int buttonY = thisY + 15;
                                int buttonW = 12;
                                int buttonH = 7;
                                if (mouseX >= buttonX && mouseX <= buttonX + buttonW && mouseY >= buttonY && mouseY <= buttonY + buttonH) { //hovering
                                    if (!boolValue) { //off
                                        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, buttonX, buttonY, 338, 169, 12, 7, 512, 512);
                                    } else { //on
                                        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, buttonX, buttonY, 350, 169, 12, 7, 512, 512);
                                    }
                                } else {
                                    if (!boolValue) { //off
                                        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, buttonX, buttonY, 314, 169, 12, 7, 512, 512);
                                    } else { //on
                                        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, buttonX, buttonY, 326, 169, 12, 7, 512, 512);
                                    }
                                }
                            }
                            drawScaledText(context, optionText, x + (descriptionBoxWidth/2), thisY, 0.75f, textColor, true);
                            if(optionType==0)
                                drawScaledText(context, Text.literal(String.valueOf(doubleValue)), x + (descriptionBoxWidth/2), thisY + 7, 0.75f, textColor, true);
                            else if(optionType == 1)
                                drawScaledText(context, Text.literal(String.valueOf(intValue)), x + (descriptionBoxWidth/2), thisY + 7, 0.75f, textColor, true);
                            //else if(optionType == 2)
                                //drawScaledText(context, Text.literal(String.valueOf(boolValue)), x + (descriptionBoxWidth/2), thisY + 7, 0.75f, textColor, true);
                        }
                    }
                } else {
                    abilityName = Text.translatable("crystal_options.ability.default");
                }
            } else {
                abilityName = Text.translatable("crystal_options.ability.unknown");
            }

            drawScaledText(context, abilityName, x+(descriptionBoxWidth/2), y+13, 0.65f, textColor, true);
        }
    }

    private static int selectedIndex = -1;
    private void drawTiers(DrawContext context, int x, int y, int mouseX, int mouseY){
        if(client != null && client.player != null) {
            ElementalCrystalItem item = (ElementalCrystalItem)crystal.getItem();
            CrystalData elementType = item.elementType;
            int color = elementType.getColorInt();
            int argb = 0xFF000000 | color;


            for(int i = 0; i < 3; i++){ //for every tier
                int xPos = x;
                int yPos = y - (i*50);
                if (mouseX >= xPos && mouseX <= xPos + button2Size && mouseY >= yPos && mouseY <= yPos + button2Size && !renderingSettingsOverlay) {
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, xPos, yPos, 88, 178, button2Size, button2Size, 512, 512);
                    if(item.hasAbilityUnlocked(crystal, item.getCrystalAbility((i * 4))) && item.getCrystalOptions(crystal).getOptions().get(item.getCrystalAbility((i * 4))) != null)
                        context.drawTooltip(textRenderer, item.getCrystalOptions(crystal).getOptions().get(item.getCrystalAbility((i * 4))).getName(), mouseX, mouseY);
                    else
                        context.drawTooltip(textRenderer, Text.translatable("crystal_options.ability.unknown").withoutStyle(), mouseX, mouseY);
                } else {
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, xPos, yPos, 76, 178, button2Size, button2Size, 512, 512);
                }
                if(i * 4 == selectedIndex){
                    //render selected texture
                    context.drawTexture(RenderLayer::getGuiTexturedOverlay, BACKGROUND, xPos+(button1Size/2)-(selectedTextureSize1/2), yPos + (button1Size /2) - (selectedTextureSize1/2), 108, 184, selectedTextureSize1, selectedTextureSize1, 512, 512);
                }
                if(item.hasAbilityUnlocked(crystal, elementType.getAbilities().get(i*4))){
                    context.drawTexture(RenderLayer::getGuiTexturedOverlay, BACKGROUND, xPos+(button1Size/2)-(buttonCenter1Size/2), yPos + (button1Size /2) - (buttonCenter1Size/2), 101, 184, buttonCenter1Size, buttonCenter1Size, 512, 512, argb);
                }

                for(int j = 0; j < 3; j++){ //for every sub-ability
                    int radius = 12;
                    double angle = Math.toRadians(120 * j - 90);
                    int xOffset = (int) (radius * Math.cos(angle)) - 8;
                    int yOffset = (int) (radius * Math.sin(angle)) - 8;

                    xPos = x + (button1Size/2) + (button3Size/2) + 2 + xOffset;
                    yPos = y + (button1Size) - (i * 50) + yOffset;
                    int offset = 0;
                    if(i==0)
                        offset = 1;
                    if (mouseX >= xPos && mouseX <= xPos - offset + button3Size && mouseY >= yPos && mouseY <= yPos - offset + button3Size && !renderingSettingsOverlay) {
                        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, xPos, yPos, 106, 178, button3Size, button3Size, 512, 512);
                        if(item.hasAbilityUnlocked(crystal, item.getCrystalAbility((i * 4) + (j + 1))) && item.getCrystalOptions(crystal).getOptions().get(item.getCrystalAbility((i * 4) + (j + 1))) != null)
                            context.drawTooltip(textRenderer, item.getCrystalOptions(crystal).getOptions().get(item.getCrystalAbility((i * 4) + (j + 1))).getName(), mouseX, mouseY);
                        else
                            context.drawTooltip(textRenderer, Text.translatable("crystal_options.ability.unknown").withoutStyle(), mouseX, mouseY);
                    } else {
                        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, xPos, yPos, 100, 178, button3Size, button3Size, 512, 512);
                    }
                    if((i * 4) + (j + 1) == selectedIndex){
                        //render selected texture
                        context.drawTexture(RenderLayer::getGuiTexturedOverlay, BACKGROUND, xPos+(button3Size/2)-(selectedTextureSize2/2), yPos + (button3Size /2) - (selectedTextureSize2/2), 104, 187, selectedTextureSize2, selectedTextureSize2, 512, 512);
                    }

                    if(item.hasAbilityUnlocked(crystal, elementType.getAbilities().get((i*4)+j+1))){
                        //RenderSystem.setShaderColor(red, green, blue, 1.0f);
                        context.drawTexture(RenderLayer::getGuiTexturedOverlay, BACKGROUND, xPos+(button3Size/2)-(buttonCenter2Size/2), yPos + (button3Size /2) - (buttonCenter2Size/2), 105, 184, buttonCenter2Size, buttonCenter2Size, 512, 512, argb);
                    }
                }
            }
        }
    }


    @Nullable
    private int hoveredTab = -1;
    private void drawTabs(DrawContext context, int x, int y, int mouseX, int mouseY) {
        hoveredTab = -1; // Reset each frame
        if(client != null && client.player != null) {
            List<ItemStack> items = getCrystals(client.player);
            for(int i = 0; i < items.size(); i++) {
                int tabY = y + (i * tabHeight);
                boolean isSelected = items.get(i).equals(crystal);
                boolean isHovered = (mouseX >= x && mouseX <= x + tabWidth - 9 &&
                        mouseY >= tabY + 1 && mouseY <= tabY - 1 + tabHeight);

                if (isSelected) {
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, tabY, 0, 198, tabWidth, tabHeight, 512, 512);
                    context.drawItem(items.get(i), x + 12, tabY + 2);
                } else if (isHovered && !renderingSettingsOverlay) {
                    hoveredTab = i; // store hovered item
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x - 9, tabY, 0, 178, tabWidth, tabHeight, 512, 512);
                    context.drawItem(items.get(i), x + 3, tabY + 2);
                    context.fill(x + 2, tabY + 2, x + 29, tabY + 18, 0x80FFFFFF);
                } else {
                    context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, tabY, 0, 178, tabWidth, tabHeight, 512, 512);
                    context.drawItem(items.get(i), x + 12, tabY + 2);
                }
            }
        }
    }
    @Override
    public boolean shouldCloseOnEsc() {
        return !renderingSettingsOverlay;
    }

    private void drawDescriptionBorder(DrawContext context, int x, int y){
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 314, 0, descriptionBoxWidth, descriptionBoxHeight, 512, 512);
    }

    private void drawSelectedCrystalWithBorder(DrawContext context, int x, int y){
        ElementalCrystalItem item = (ElementalCrystalItem) crystal.getItem();
        CrystalData crystalType = ((ElementalCrystalItem) crystal.getItem()).elementType;
        String crystalName = crystalType.toString();

        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 38, 178, itemBorderWidth, itemBorderHeight, 512, 512);

        // Draw item inside border
        drawItemScaled(context, crystal, x + 3, y + 3, 2f);

        // Center text under the border
        int textX = x + (itemBorderWidth / 2); // 38 is border width
        int textY = y + 38 + 3;

        int text = crystalType.getTextColor();
        int textExtra = crystalType.getTextExtraColor();
        int tier = item.getTier(crystal);
        Text tierText = Text.literal("Tier: ").withColor(textExtra).append(Text.literal("" + (tier == 0 ? 1 : tier)).withColor(text));
        //context.drawText(textRenderer, crystalName, textX, textY, textColor, false);
        //context.drawText(textRenderer, tierText, textX, textY+8, textColor, false);
        drawScaledText(context, Text.literal(crystalName), textX, textY, 1.0f, textColor, true);
        drawScaledText(context, tierText, textX, textY+10, 1.0f, textColor, true);

    }

    public void drawScaledText(DrawContext context, Text text, int x, int y, float scale, int color, boolean centered) {
        int textWidth = textRenderer.getWidth(text);

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);

        float scaledX = centered ? -(textWidth / 2f) : 0;

        context.drawText(textRenderer, text, (int) scaledX, 0, color, false);
        context.getMatrices().pop();
    }


    public static void drawItemScaled(DrawContext context, ItemStack stack, int x, int y, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1f);
        context.drawItem(stack, 0, 0);
        context.getMatrices().pop();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private List<ItemStack> getCrystals(PlayerEntity player){
        List<ItemStack> total = new ArrayList<>();
        List<ItemStack> attachedCrystals = SpellHelper.getAttachedCrystals(player);
        for(ItemStack stack : attachedCrystals){
            if (stack != ItemStack.EMPTY){
                total.add(stack);
            }
        }
        return total;
    }

    @Nullable
    private DraggingSlider draggingSlider = null;

    private static class DraggingSlider {
        public final int optionIndex;
        public final double min;
        public final double max;
        public final boolean isInt;

        public DraggingSlider(int optionIndex, double min, double max, boolean isInt) {
            this.optionIndex = optionIndex;
            this.min = min;
            this.max = max;
            this.isInt = isInt;
        }
    }
}

