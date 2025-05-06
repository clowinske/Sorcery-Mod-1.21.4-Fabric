package net.code7y7.sorcerymod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.SorceryModClient;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.network.AddTimePonderingC2SPayload;
import net.code7y7.sorcerymod.sound.ModSounds;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttuningOrbScreen extends Screen {
    private static Identifier BACKGROUND = SorceryMod.createIdentifier("textures/gui/orb/attuning_orb_gui.png");

    PlayerEntity player;
    int slotWidth, slotHeight, backgroundWidth, backgroundHeight, settingsWidth, settingsHeight, settingsIconSize;
    boolean renderingSettingsOverlay;
    int textColor = 0x909090; //default

    public AttuningOrbScreen(PlayerEntity player, Text title) {
        super(title);
        this.player = player;
        backgroundWidth = 314;
        backgroundHeight = 178;
        slotWidth = 20;
        slotHeight = 20;
        this.settingsWidth = 124;
        this.settingsHeight = 82;
        this.settingsIconSize = 16;
    }
    long openTime;
    @Override
    protected void init() {
        super.init();
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
        int x = ((width - backgroundWidth) / 2);
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 0, 0, 314, 178, 512, 512);
    }

    private int hoveredIndex = -1;
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hoveredIndex = -1; //reset

        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawBackground(context, delta, mouseX, mouseY);

        List<ItemStack> crystals = getCrystals(player);
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 40;



        for (int i = 0; i < crystals.size(); i++) {
            int itemX, itemY;
            if (crystals.size() == 1) {
                itemX = centerX - 8;
                itemY = centerY - 8;
            } else {
                double angle = Math.toRadians((360 / crystals.size()) * i - 90);
                itemX = (int) (centerX + radius * Math.cos(angle)) - 8;
                itemY = (int) (centerY + radius * Math.sin(angle)) - 8;
            }
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, itemX-2, itemY-2, 0, 218, slotWidth, slotHeight, 512, 512);
            context.fill(itemX, itemY, itemX + 16, itemY + 16, 0x60FFFFFF); // translucent white box
            if(!renderingSettingsOverlay) {
                context.drawItem(crystals.get(i), itemX, itemY);
            }

            if (mouseX >= itemX && mouseX <= itemX + 16 && mouseY >= itemY && mouseY <= itemY + 16 && !renderingSettingsOverlay) {
                hoveredIndex = i;
                context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, itemX-2, itemY-2, 20, 218, slotWidth, slotHeight, 512, 512);
            }
        }

        if (hoveredIndex != -1) {
            ItemStack hoveredStack = crystals.get(hoveredIndex);
            //context.drawTooltip(textRenderer, hoveredStack, mouseX, mouseY);
        }
        int settingsIconX = centerX - (backgroundWidth/2) + 3;
        int settingsIconY = centerY + (backgroundHeight/2) - 7 - settingsIconSize;
        drawSettingsIcon(context, settingsIconX, settingsIconY, mouseX, mouseY);

        if(renderingSettingsOverlay){
            renderSettingsOverlay(context, mouseX, mouseY);
        }
    }

    private ItemStack selectedCrystal = null;


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (renderingSettingsOverlay) {
                renderingSettingsOverlay = false;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    int themeNum = 5;
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<ItemStack> crystals = getCrystals(player);
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 40;
        int settingsIconX = centerX - (backgroundWidth/2) + 3;
        int settingsIconY = centerY + (backgroundHeight/2) - 7 - settingsIconSize;
        int selected = SorceryModClient.playerData.orbUI;

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
            for (int i = 0; i < crystals.size(); i++) {
                int itemX, itemY;
                if (crystals.size() == 1) {
                    itemX = centerX - 8;
                    itemY = centerY - 8;
                } else {
                    double angle = Math.toRadians((360 / crystals.size()) * i - 90);
                    itemX = (int) (centerX + radius * Math.cos(angle)) - 8;
                    itemY = (int) (centerY + radius * Math.sin(angle)) - 8;
                }

                if (mouseX >= itemX && mouseX <= itemX + 16 && mouseY >= itemY && mouseY <= itemY + 16) {
                    selectedCrystal = crystals.get(i);

                    if (client != null && client.player != null && button == 0) {
                        client.setScreen(new CrystalOptionScreen(i, this));
                        client.player.playSound(ModSounds.ORB_UI_SELECT, 1.0f, (float) (Math.random() + 0.5f));
                    }

                    return true;
                }
            }
            if (mouseX >= settingsIconX+2 && mouseX <= settingsIconX+2 + settingsIconSize && mouseY >= settingsIconY+2 && mouseY <= settingsIconY+2 + settingsIconSize){
                renderingSettingsOverlay = true;
                client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !renderingSettingsOverlay;
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

    public void drawScaledText(DrawContext context, Text text, int x, int y, float scale, int color, boolean centered) {
        int textWidth = textRenderer.getWidth(text);

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);

        float scaledX = centered ? -(textWidth / 2f) : 0;

        context.drawText(textRenderer, text, (int) scaledX, 0, color, false);
        context.getMatrices().pop();
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
    @Override
    public boolean shouldPause() {
        return false;
    }
}
