package net.code7y7.sorcerymod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.code7y7.sorcerymod.attachment.ModAttachmentTypes;
import net.code7y7.sorcerymod.client.ModEntityModels;
import net.code7y7.sorcerymod.client.render.FireballModel;
import net.code7y7.sorcerymod.client.render.FireballRenderer;
import net.code7y7.sorcerymod.client.render.FiresprayRenderer;
import net.code7y7.sorcerymod.component.CrystalPouchContentsComponent;
import net.code7y7.sorcerymod.component.ImbueHelper;
import net.code7y7.sorcerymod.entity.EnemyMob;
import net.code7y7.sorcerymod.entity.ModEntities;
import net.code7y7.sorcerymod.entity.client.*;
import net.code7y7.sorcerymod.entity.enemy.TwoPhaseBoss;
import net.code7y7.sorcerymod.item.CrystalPouchTooltipComponent;
import net.code7y7.sorcerymod.item.CrystalPouchTooltipData;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.keybind.ModKeybinds;
import net.code7y7.sorcerymod.network.*;
import net.code7y7.sorcerymod.particle.*;
import net.code7y7.sorcerymod.screen.ModScreens;
import net.code7y7.sorcerymod.spell.InputHandler;
import net.code7y7.sorcerymod.spell.SpellInput;
import net.code7y7.sorcerymod.spell.electricity.Blink;
import net.code7y7.sorcerymod.spell.SpellHelper;
import net.code7y7.sorcerymod.util.ModTags;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SorceryModClient implements ClientModInitializer {
    private static final long DOUBLE_CLICK_THRESHOLD = 200; // milliseconds
    private static final long HOLD_THRESHOLD = 8; // ticks
    private static final int FOCUS_COOLDOWN = 40; // ticks
    private static final Identifier SPELL_SELECTION_TEXTURE = SorceryMod.createIdentifier("textures/gui/spell_selection.png");
    private static final Identifier SPELL_SELECTION_ADDITION_TEXTURE_L = SorceryMod.createIdentifier("textures/gui/spell_selection_addition_l.png");
    private static final Identifier SPELL_SELECTION_ADDITION_TEXTURE_R = SorceryMod.createIdentifier("textures/gui/spell_selection_addition_r.png");
    private static final Identifier CAST_MODE_TEXTURE = SorceryMod.createIdentifier("textures/gui/cast_mode.png");
    private static final Identifier FOCUS_BAR_TEXTURE = SorceryMod.createIdentifier("textures/gui/focus.png");

    public static List<ItemStack> ElementalCrystalsMain = new ArrayList<>();
    private static List<ItemStack> ElementalCrystalsL = new ArrayList<>();
    private static List<ItemStack> ElementalCrystalsR = new ArrayList<>();
    public static PlayerData playerData = new PlayerData();

    public static boolean mixinClientCondition = playerData.hasCrystal && (playerData.selectMode || playerData.castMode);

    private boolean lastHasCrystal = false;
    private List<ItemStack> lastElementalCrystalsMain = new ArrayList<>();
    private boolean leftClickProcessed = false;
    private boolean rightClickProcessed = true;
    private boolean toggleProcessed = true;
    private boolean leftMouseReleased = true;
    private boolean rightMouseReleased = true;
    boolean rightMousedDown;
    boolean leftMousedDown;
    private int leftHeldTicks;
    private int rightHeldTicks;
    private boolean leftHoldTriggered = false; // Tracks if handleHold() has been called for the left button
    private boolean rightHoldTriggered = false; // Tracks if handleHoldRelease() has been called for the right button
    private long lastLeftPressTime = 0; // Time of the last left button press
    private long lastRightPressTime = 0; // Time of the last right button press


    @Override
    public void onInitializeClient() {
        ModEntityModels.registerModBlocksEntities();
        ModKeybinds.registerModKeybinds();
        ModScreens.registerModScreens();


        ParticleFactoryRegistry.getInstance().register(ModParticles.ALTAR_EFFECT_PARTICLE, AltarEffectParticle.AltarEffectParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ZAP_PARTICLE, ZapParticle.ZapParticleFactory::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.CRYSTAL_PLACE_PARTICLE, CrystalPlaceParticle.CrystalPlaceParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.LIGHTNING_PARTICLE, LightningParticle.LightningParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.GRAVITY_PUSH_PARTICLE, GravityPushParticle.GravityPushParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FIRE_SPELL_PARTICLE, FireSpellParticle.FireSpellParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SCORCH_MARK_PARTICLE, ScorchMarkParticle.ScorchMarkParticleFactory::new);

        EntityModelLayerRegistry.registerModelLayer(Class1EnemyModel.BODY, Class1EnemyModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.CLASS_1_ENEMY, Class1EnemyRenderer::new);

        EntityRendererRegistry.register(ModEntities.TWO_PHASE_BOSS, TwoPhaseBossRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(UpgradeOrbModel.UPGRADE_ORB, UpgradeOrbModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.UPGRADE_ORB, UpgradeOrbRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(FireballModel.FIREBALL, FireballModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.FIREBALL, FireballRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIRESPRAY, FiresprayRenderer::new);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType == EntityType.PLAYER) {
                registrationHelper.register(new SpellChargeFeatureRenderer((PlayerEntityRenderer) entityRenderer));
            }
        }));

        WorldRenderEvents.END.register(this::onWorldRender);

        HudRenderCallback.EVENT.register(this::bodySpellHud);
        ClientTickEvents.END_CLIENT_TICK.register(this::bodyClientTick);


        ClientTickEvents.END_CLIENT_TICK.register(this::soulClientTick);

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof CrystalPouchTooltipData component) {
                return new CrystalPouchTooltipComponent(component.contents());
            }
            return null;
        });


        HudRenderCallback.EVENT.register((context, hudRenderer) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            if (player != null) {
                boolean currentHasCrystal = !SpellHelper.getAttachedCrystals(player).isEmpty();

                if (currentHasCrystal != lastHasCrystal) {
                    // State change detected for hasCrystal
                    lastHasCrystal = currentHasCrystal;
                    playerData.hasCrystal = currentHasCrystal;

                    if (playerData.hasCrystal) {
                        ClientPlayNetworking.send(new SendStateSelectModePayload(playerData.hasCrystal));
                    }
                }

                List<ItemStack> currentElementalCrystalsMain = SpellHelper.getAttachedCrystals(player);

                if (!currentElementalCrystalsMain.equals(lastElementalCrystalsMain)) {
                    // ElementalCrystalsMain list changed
                    System.out.println("list changed");
                    playerData.castMode = false;
                    lastElementalCrystalsMain = new ArrayList<>(currentElementalCrystalsMain);

                    ElementalCrystalsMain = new ArrayList<>();
                    for (ItemStack crystal : currentElementalCrystalsMain) {
                        if (crystal != null && !crystal.isEmpty()) {
                            ElementalCrystalsMain.add(crystal);
                        }
                    }
                    ElementalCrystalsL = new ArrayList<>(ElementalCrystalsMain);
                    ElementalCrystalsR = new ArrayList<>(ElementalCrystalsMain);
                    SpellHelper.resetHandCharge(player, "left");
                    SpellHelper.resetHandCharge(player, "right");
                    if (!ElementalCrystalsMain.isEmpty()) {
                        SpellHelper.setHandSpell(player, "left", CrystalData.getTypeByString(((ElementalCrystalItem) ElementalCrystalsL.get(0).getItem()).elementName));
                        SpellHelper.setHandSpell(player, "right", CrystalData.getTypeByString(((ElementalCrystalItem) ElementalCrystalsR.get(0).getItem()).elementName));
                    }
                }

                if (SpellHelper.hasCrystalsAndPouch(player) && !player.isSpectator() && !client.options.hudHidden) {
                    // Get the screen dimensions
                    int screenWidth = client.getWindow().getScaledWidth();
                    int screenHeight = client.getWindow().getScaledHeight();

                    int textureWidth = 64;
                    int textureHeight = 64;
                    int HOTBAR_HEIGHT = 22;
                    int xL = ((screenWidth - textureWidth) / 2) - textureWidth - 24 - 21;
                    int yL = screenHeight - textureHeight - HOTBAR_HEIGHT - 4;
                    int xR = ((screenWidth - textureWidth) / 2) + textureWidth * 2 + 2 - 21;
                    int yR = screenHeight - textureHeight - HOTBAR_HEIGHT - 4;

                    RenderSystem.setShaderTexture(0, SPELL_SELECTION_TEXTURE);
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); //reset color
                    if (!playerData.selectMode) {
                        context.drawTexture(RenderLayer::getGuiTextured, SPELL_SELECTION_TEXTURE, xL, yL, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
                        context.drawTexture(RenderLayer::getGuiTextured, SPELL_SELECTION_TEXTURE, xR, yR, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
                    } else {
                        context.drawTexture(RenderLayer::getGuiTextured, SPELL_SELECTION_ADDITION_TEXTURE_L, xL, yL, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
                        context.drawTexture(RenderLayer::getGuiTextured, SPELL_SELECTION_ADDITION_TEXTURE_R, xR, yR, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
                    }

                    if (playerData.castMode) {
                        int leftColor = CrystalData.getTypeByString(((ElementalCrystalItem) ElementalCrystalsL.get(0).getItem()).elementName).getColorInt();
                        int rightColor = CrystalData.getTypeByString(((ElementalCrystalItem) ElementalCrystalsR.get(0).getItem()).elementName).getColorInt();

                        RenderSystem.setShaderTexture(0, CAST_MODE_TEXTURE);

                        /*RenderSystem.setShaderColor(
                                ((rightColor >> 16) & 0xFF) / 255f,
                                ((rightColor >> 8) & 0xFF) / 255f,
                                (rightColor & 0xFF) / 255f,
                                1.0f);*/
                        context.drawTexture(RenderLayer::getGuiTextured, CAST_MODE_TEXTURE, xR, yR, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

                        /*RenderSystem.setShaderColor(
                                ((leftColor >> 16) & 0xFF) / 255f,
                                ((leftColor >> 8) & 0xFF) / 255f,
                                (leftColor & 0xFF) / 255f,
                                1.0f);*/
                        context.drawTexture(RenderLayer::getGuiTextured, CAST_MODE_TEXTURE, xL, yL, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // Reset color
                    }

                    context.drawItem(ElementalCrystalsL.get(0), xL + 3 + 21, yL + 48 - 3);
                    context.drawItem(ElementalCrystalsR.get(0), xR + 3 + 21, yR + 48 - 3);

                    //draw focus bar
                    //context.drawTexture(RenderLayer::getGuiTextured, FOCUS_BAR_TEXTURE, (screenWidth-16)/2, screenHeight-64, 0, 0, 16, 16, 16, 16);

                    if (playerData.selectMode) {
                        // Handle left-click to move the first item to the last position
                        if (client.mouse.wasLeftButtonClicked()) {
                            if (!leftClickProcessed && !ElementalCrystalsL.isEmpty()) {
                                SpellHelper.resetHandCharge(player, "left");
                                ItemStack firstItem = ElementalCrystalsL.remove(0);
                                ElementalCrystalsL.add(firstItem);
                                leftClickProcessed = true;

                                CrystalData spellType = CrystalData.getTypeByString(((ElementalCrystalItem) ElementalCrystalsL.get(0).getItem()).elementName);
                                SpellHelper.setHandSpell(client.player, "left", spellType);

                            }
                        } else {
                            leftClickProcessed = false;
                        }

                        // Handle right-click to move the first item to the last position
                        if (client.mouse.wasRightButtonClicked()) {
                            if (!rightClickProcessed && !ElementalCrystalsR.isEmpty()) {
                                SpellHelper.resetHandCharge(player, "right");
                                ItemStack firstItem = ElementalCrystalsR.remove(0);
                                ElementalCrystalsR.add(firstItem);
                                rightClickProcessed = true;

                                CrystalData spellType = CrystalData.getTypeByString(((ElementalCrystalItem) ElementalCrystalsR.get(0).getItem()).elementName);
                                SpellHelper.setHandSpell(client.player, "right", spellType);

                            }
                        } else {
                            rightClickProcessed = false;
                        }

                        for (int i = ElementalCrystalsL.size() - 1; i > 0; i--) {
                            int xPosL = xL + 3 + 21;
                            int yPosL = yL + 48 - 3 - (i * 16) - (4 * i);
                            context.drawItem(ElementalCrystalsL.get(i), xPosL, yPosL);
                        }
                        for (int i = ElementalCrystalsR.size() - 1; i > 0; i--) {
                            int xPosR = xR + 3 + 21;
                            int yPosR = yR + 48 - 3 - (i * 16) - (4 * i);
                            context.drawItem(ElementalCrystalsR.get(i), xPosR, yPosR);
                        }
                    }
                }
            }
        });


        ServerPlayConnectionEvents.JOIN.register((handler, connection, world) -> {
            playerData.canCastLeft = true;
            playerData.canCastRight = true;
            int skinNumber = 4;
            if (playerData.corruptionSkin == 0) {
                int skinIndex = (int) Math.floor(Math.random() * skinNumber) + 1;
                playerData.corruptionSkin = skinIndex;
                StateSaverAndLoader.getPlayerState(handler.player).corruptionSkin = skinIndex;
                System.out.println("Index: " + skinIndex);
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (ImbueHelper.getImbueValue(stack) > 0) {
                lines.add(Text.literal("Imbued: " + ImbueHelper.getImbueValue(stack) + "%").withColor(CrystalData.getTypeByInt(ImbueHelper.getImbueType(stack)).getTextColor()));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
            boolean castModeIsToggle = true;

            if (client.world != null && client.player != null) {
                playerData.leftMouseDown = GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
                playerData.rightMouseDown = GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

                if (SpellHelper.hasCrystalsAndPouch(client.player)) {
                    if (SpellHelper.isCharged(client.player, "right") && hasImbueUnlocked(ElementalCrystalsR.get(0))) {
                        ItemStack stack = client.player.getMainHandStack();
                        int increaseAmt = ((ElementalCrystalItem) ElementalCrystalsR.get(0).getItem()).getTier(ElementalCrystalsR.get(0));
                        CrystalData type = SpellHelper.getHandSpell(client.player, "right");
                        if (!stack.isEmpty() && isValidImbueTool(stack)) {
                            ImbueHelper.incrementImbueCharge(type, increaseAmt, stack);
                            SpellHelper.removeFocus(client.player, 2);
                        }
                    }
                    if (SpellHelper.isCharged(client.player, "left") && hasImbueUnlocked(ElementalCrystalsL.get(0))) {
                        ItemStack stack = client.player.getOffHandStack();
                        int increaseAmt = ((ElementalCrystalItem) ElementalCrystalsL.get(0).getItem()).getTier(ElementalCrystalsL.get(0));
                        CrystalData type = SpellHelper.getHandSpell(client.player, "left");
                        if (!stack.isEmpty() && isValidImbueTool(stack)) {
                            ImbueHelper.incrementImbueCharge(type, increaseAmt, stack);
                            SpellHelper.removeFocus(client.player, 2);
                        }
                    }
                }


                if (!client.player.isSpectator()) {
                    boolean jumpPressed = client.options.jumpKey.isPressed();
                    boolean jumpJustPressed = client.options.jumpKey.wasPressed();

                    if (client.player.isOnGround()) {
                        playerData.hasJumped = false;
                    } else if (!jumpPressed && !playerData.hasJumped) {
                        playerData.hasJumped = true;
                    }

                    if (jumpJustPressed && !client.player.isOnGround() && playerData.hasJumped) {
                        if (SpellHelper.hasChargedSpell(client.player, CrystalData.ELECTRICITY)) {
                            if (playerData.canBlink) {
                                List<ItemStack> list;
                                if (SpellHelper.getHandWith(client.player, CrystalData.ELECTRICITY).equals("left")) {
                                    list = ElementalCrystalsL;
                                } else {
                                    list = ElementalCrystalsR;
                                }
                                if (!list.isEmpty()) {
                                    ElementalCrystalItem item = (ElementalCrystalItem) list.get(0).getItem();
                                    if (item.hasAbilityUnlocked(list.get(0), "blink") && playerData.focus >= Blink.getFocusCost(list.get(0))) {
                                        Blink.blink(client.player, list.get(0));
                                        playerData.focusRechargeCooldown = 20;
                                        SpellHelper.removeFocus(client.player, Blink.getFocusCost(list.get(0)));
                                    }
                                }
                                playerData.canBlink = false;
                            }
                        }
                    }
                } else {
                    playerData.canBlink = false;
                }
                if (client.player.isOnGround() && !playerData.canBlink) {
                    playerData.canBlink = true;
                }


                //client.player.sendMessage(Text.literal(new BigDecimal(SpellHelper.getFocus(client.player)).setScale(1, RoundingMode.HALF_UP).doubleValue()+" "+ playerData.focusRechargeCooldown), true);


                playerData.selectMode = ModKeybinds.SPELL_SELECT_KEYBIND.isPressed();

                ClientPlayNetworking.send(new SendStateSelectModePayload(ModKeybinds.SPELL_SELECT_KEYBIND.isPressed()));
                if (castModeIsToggle) { //if toggle mode, player presses button once to switch to cast mode
                    boolean castModeIsPressed = ModKeybinds.SPELL_KEYBIND.isPressed();

                    if (castModeIsPressed && !client.player.isSpectator() && SpellHelper.hasCrystalsAndPouch(client.player)) {
                        if (!toggleProcessed) {
                            if (playerData.castMode && client.player.isSneaking())
                                SpellHelper.resetHandCharge(client.player, "both");
                            playerData.castMode = !playerData.castMode;
                            ClientPlayNetworking.send(new SendStateCastModePayload(playerData.castMode));
                            toggleProcessed = true;
                        }
                    } else {
                        toggleProcessed = false;
                    }
                } else { //if not toggle mode, player holds down keybind for cast mode
                    playerData.castMode = ModKeybinds.SPELL_KEYBIND.isPressed();
                    ClientPlayNetworking.send(new SendStateCastModePayload(ModKeybinds.SPELL_KEYBIND.isPressed()));
                }
                if (leftMousedDown)
                    handleLeftMouseHeld(client.player);
                if (rightMousedDown)
                    handleRightMouseHeld(client.player);

                if (client.currentScreen == null && playerData.castMode && playerData.hasCrystal) {
                    long currentTime = System.currentTimeMillis();

                    if (!playerData.selectMode && !client.player.isSpectator()) {
                        //check dual


                        //left click
                        if (playerData.leftMouseDown) {
                            if (SpellHelper.getHandCharge(client.player, "left") < 100) {
                                leftClickProcessed = false;
                                incrementCharge(client.player, "left", ElementalCrystalsL.get(0));
                                playerData.focusRechargeCooldown = FOCUS_COOLDOWN;
                                leftMouseReleased = false;
                            } else if (leftMouseReleased) {
                                castMouseDown(client.player, "left", currentTime);
                                leftMouseReleased = false;
                            }
                        } else {
                            leftMouseReleased = true;
                            if (leftMousedDown)
                                castMouseUp(client.player, "left", currentTime);
                        }
                        //right click
                        if (playerData.rightMouseDown) {
                            if (SpellHelper.getHandCharge(client.player, "right") < 100) {
                                incrementCharge(client.player, "right", ElementalCrystalsR.get(0));
                                playerData.focusRechargeCooldown = FOCUS_COOLDOWN;
                                rightMouseReleased = false;
                            } else if (rightMouseReleased) {
                                castMouseDown(client.player, "right", currentTime);
                                rightMouseReleased = false;
                            }
                        } else {
                            rightMouseReleased = true;
                            if (rightMousedDown)
                                castMouseUp(client.player, "right", currentTime);
                        }
                    }
                } else {
                    leftMouseReleased = true;
                    rightMouseReleased = true;
                    leftMousedDown = false;
                    rightMousedDown = false;
                    SpellHelper.setCanCast(client.player, "left", true);
                    SpellHelper.setCanCast(client.player, "right", true);

                }
                //charging cancelled, decrement
                if (!playerData.leftMouseDown || client.currentScreen != null || !playerData.hasCrystal || !playerData.castMode) {
                    if (SpellHelper.getHandCharge(client.player, "left") < 100)
                        decrementCharge(client.player, "left");
                }
                if (!playerData.rightMouseDown || client.currentScreen != null || !playerData.hasCrystal || !playerData.castMode) {
                    if (SpellHelper.getHandCharge(client.player, "right") < 100)
                        decrementCharge(client.player, "right");
                }

                if (playerData.focusRechargeCooldown > 0)
                    playerData.focusRechargeCooldown--;
                double averageTier = 1;
                if (!ElementalCrystalsMain.isEmpty()) {
                    for (ItemStack stack : ElementalCrystalsMain) {
                        if (!stack.isEmpty())
                            averageTier += ((ElementalCrystalItem) stack.getItem()).getTier(stack);
                    }
                    averageTier = (averageTier - 1) / ElementalCrystalsMain.size();
                }
                double rechargeMultiplier = averageTier;
                double rate = 0.5 * rechargeMultiplier;
                if (playerData.focus < 200 && playerData.focusRechargeCooldown == 0)
                    SpellHelper.addFocus(client.player, rate);
            }
        });


        boolean isInDungeon = false;
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (isInDungeon || (playerData.hasCrystal && (playerData.castMode || playerData.selectMode)))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (isInDungeon || (playerData.hasCrystal && (playerData.castMode || playerData.selectMode)))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (isInDungeon || (playerData.hasCrystal && (playerData.castMode || playerData.selectMode)))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        ClientPlayNetworking.registerGlobalReceiver(CrystalPlaceParticlePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world != null) {
                    context.client().world.addParticle(new CrystalPlaceParticleEffect(
                                    payload.rgb()),
                            payload.pos().getX() + .5, payload.pos().getY() + .5 + payload.y(), payload.pos().getZ() + .5,
                            0.0, 0.0, 0.0);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AltarEffectParticlePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world != null) {
                    context.client().world.addParticle(ModParticles.ALTAR_EFFECT_PARTICLE,
                            payload.pos().getX() + .5 + 0.75, payload.pos().getY() + 1.455, payload.pos().getZ() + .5,
                            0.0, 0.0, 0.0);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AltarInteractSoundPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world != null) {
                    context.player().playSound(payload.sound(), payload.volume(), payload.pitch());
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(DustParticleS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world != null) {
                    context.client().world.addParticle(new DustParticleEffect(payload.rgb(), 1.0f), payload.pos().x(), payload.pos().y(), payload.pos().z(),
                            payload.vel().x(), payload.vel().y(), payload.vel().z());
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(FireSpellParticleS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world != null) {
                    context.client().world.addParticle(new FireSpellParticleEffect(payload.velX(), payload.velY(), payload.velZ()), payload.pos().x(), payload.pos().y(), payload.pos().z(),
                            0, 0, 0);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(LightningParticleS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world != null) {
                    context.client().world.addParticle(new LightningParticleEffect(payload.color(), payload.dest(), payload.radius(), payload.hasCore(), payload.branchChance(), payload.ticks()), payload.start().x, payload.start().y, payload.start().z, 0, 0, 0);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SetCorruptionS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                playerData.corruption = (int) payload.amount();
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SetSpellChargeS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (payload.button().equals("left")) {
                    playerData.leftHandCharge = payload.amt();
                    if (context.player() instanceof ModTrackedPlayerData data) {
                        data.setLeftHandCharge(payload.amt());
                    }
                } else {
                    playerData.rightHandCharge = payload.amt();
                    if (context.player() instanceof ModTrackedPlayerData data) {
                        data.setRightHandCharge(payload.amt());
                    }
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SetSpellTypeS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (payload.button().equals("left")) {
                    playerData.leftHandSpell = payload.amt();
                    if (context.player() instanceof ModTrackedPlayerData data) {
                        data.setLeftHandSpell(payload.amt());
                    }
                } else {
                    playerData.rightHandSpell = payload.amt();
                    if (context.player() instanceof ModTrackedPlayerData data) {
                        data.setRightHandSpell(payload.amt());
                    }
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SetCanCastS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (payload.button().equals("left"))
                    playerData.canCastLeft = payload.canCast();
                else
                    playerData.canCastRight = payload.canCast();
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AddVelocityPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                Entity entity = context.player().getWorld().getEntityById(payload.id());
                context.player().addVelocity(new Vec3d(payload.vec().x, payload.vec().y, payload.vec().z));
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SetFlightS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                context.player().getAbilities().flying = payload.state();
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SetFocusS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                playerData.focus = payload.amt();
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SpawnParticleS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                context.player().getWorld().addParticle((ParticleEffect) payload.particle(), payload.pos().x, payload.pos().y, payload.pos().z, 0, 0, 0);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SetFocusRechargeS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                playerData.focusRechargeCooldown = payload.amount();
            });
        });
    }

    private void onWorldRender(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if(client.world != null && client.player != null) {
            Camera camera = client.gameRenderer.getCamera();
            MatrixStack matrixStack = context.matrixStack();
            LivingEntity selectedEntity1;
            selectedEntity1 = findAutoAimTarget(client.player, 10, 25);
            if (selectedEntity1 != null) {
                if(selectedEntity1 instanceof EnemyMob entity)
                    client.player.sendMessage(Text.literal(entity.getHealth()+", " + entity.getShieldStrength()+ " " + entity.getShieldType().getName()), true);
            }
        }
    }
    private static final Identifier TEXTURE = SorceryMod.createIdentifier("textures/gui/focus.png");
    private void renderBillboard(MatrixStack matrices) {
        // Setup rendering state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        // Bind texture and shader
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Get tessellator and buffer builder
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.begin(
                VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION_TEXTURE
        );

        // Get the model matrix
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float size = 0.5f;

        // Build quad vertices (counter-clockwise winding)
        buffer.vertex(matrix, -size, size, 0).texture(0, 0);
        buffer.vertex(matrix, size, size, 0).texture(1, 0);
        buffer.vertex(matrix, size, -size, 0).texture(1, 1);
        buffer.vertex(matrix, -size, -size, 0).texture(0, 1);

        // Submit the draw call
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        // Restore render state
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
    private LivingEntity findAutoAimTarget(PlayerEntity player, double maxDistance, double maxAngleDegrees) {
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVector();
        double cosThreshold = Math.cos(Math.toRadians(maxAngleDegrees));

        LivingEntity bestTarget = null;
        double closestDistSq = maxDistance * maxDistance;

        for (LivingEntity entity : player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(eyePos, eyePos.add(lookVec.multiply(maxDistance))).expand(3.0),
                (e) -> e.isAlive() && e != player && e.isAttackable() && !e.isSpectator()
        )) {
            if (!player.canSee(entity)) continue; // Line of sight check

            Vec3d toEntity = entity.getPos().add(0, entity.getStandingEyeHeight() / 2.0, 0).subtract(eyePos);
            double distSq = toEntity.lengthSquared();
            Vec3d toEntityNorm = toEntity.normalize();
            double dot = lookVec.dotProduct(toEntityNorm);

            if (dot > cosThreshold && distSq < closestDistSq) {
                closestDistSq = distSq;
                bestTarget = entity;
            }
        }

        return bestTarget;
    }

    private boolean hasImbueUnlocked(ItemStack stack){
        if(!stack.isEmpty()) {
            ElementalCrystalItem item = (ElementalCrystalItem) stack.getItem();
            CrystalData type = item.elementType;
            return switch (type) {
                case FIRE -> item.hasAbilityUnlocked(stack, "flaming_imbue");
                case ELECTRICITY -> item.hasAbilityUnlocked(stack, "electric_imbue");
                case GRAVITY -> item.hasAbilityUnlocked(stack, "gravity_imbue");
                case null, default -> false;
            };
        }
        return false;
    }

    private boolean isValidImbueTool(ItemStack stack){
        return stack.isIn(ModTags.Items.VALID_IMBUE_TOOLS);
    }

    private void castMouseDown(PlayerEntity player, String button, long currentTime) {
        if (button.equals("left")) {
            leftMousedDown = true;
            handleLeftMouseDown(player, currentTime);
        } else {
            rightMousedDown = true;
            handleRightMouseDown(player, currentTime);
        }
    }

    private void castMouseUp(PlayerEntity player, String button, long currentTime) {
        if (button.equals("left")) {
            leftMousedDown = false;
            handleLeftMouseUp(player, currentTime);
        } else {
            rightMousedDown = false;
            handleRightMouseUp(player, currentTime);
        }
    }

    private void handleLeftMouseDown(PlayerEntity player, long currentTime) { //called one time
        if (leftHeldTicks >= HOLD_THRESHOLD) { //hold detected
            onHold(player, "left");
        }
    }
    private void handleLeftMouseHeld(PlayerEntity player){ //called every tick
        leftHeldTicks++;
        if (leftHeldTicks >= HOLD_THRESHOLD) { //hold detected
            onHold(player, "left");
        }
    }

    private void handleLeftMouseUp(PlayerEntity player, long currentTime) { //called one time
        if(leftHeldTicks >= HOLD_THRESHOLD) {
            endHold(player, "left");
        } else {
            onClick(player, "left");
        }
        leftHeldTicks = 0;
    }

    private void handleRightMouseDown(PlayerEntity player, long currentTime) { //called one time
        if (rightHeldTicks >= HOLD_THRESHOLD) { //hold detected
            onHold(player, "right");
        }
    }
    private void handleRightMouseHeld(PlayerEntity player) { //called every tick
        rightHeldTicks++;
        if (rightHeldTicks >= HOLD_THRESHOLD) { //hold detected
            onHold(player, "right");
        }
    }

    private void handleRightMouseUp(PlayerEntity player, long currentTime) { //called one time
        if(rightHeldTicks >= HOLD_THRESHOLD) {
            endHold(player, "right");
        } else {
            onClick(player, "right");
        }
        rightHeldTicks = 0;
    }

    private void decrementCharge(PlayerEntity player, String hand){
        int DISCHARGE_RATE = 10;
        SpellHelper.decrementHandCharge(player, hand, DISCHARGE_RATE);
    }

    private void incrementCharge(PlayerEntity player, String hand, ItemStack stack){
        if(!stack.isEmpty()) {
            ElementalCrystalItem crystal = (ElementalCrystalItem) stack.getItem();
            int CHARGE_RATE = crystal.getTier(stack) * 2 + 1;
            if (hand.equals("left")) {
                playerData.leftHandCharge = Math.min(100, playerData.leftHandCharge += CHARGE_RATE);
                ClientPlayNetworking.send(new ChargeSpellPayload(playerData.leftHandCharge, "left"));
            } else {
                playerData.rightHandCharge = Math.min(100, playerData.rightHandCharge += CHARGE_RATE);
                ClientPlayNetworking.send(new ChargeSpellPayload(playerData.rightHandCharge, "right"));
            }
        }
    }

    private void onClick(PlayerEntity player, String button) {
        if(SpellHelper.canCast(player, button)) {
            ItemStack stack = button.equals("left") ? ElementalCrystalsL.get(0) : ElementalCrystalsR.get(0);
            InputHandler.tryCast(player, button, stack, SpellInput.SINGLE, SpellInput.SINGLE);
        }
    }

    private void onDoubleClick(PlayerEntity player, String button) {
        if(SpellHelper.canCast(player, button)) {
            ItemStack stack = button.equals("left") ? ElementalCrystalsL.get(0) : ElementalCrystalsR.get(0);
            InputHandler.tryCast(player, button, stack, SpellInput.DOUBLE, SpellInput.DOUBLE);
        }
    }

    private void onHold(PlayerEntity player, String button) { //every tick
        // Handle hold
        if(SpellHelper.canCast(player, button)) {
            if (button.equals("left")) {
                if (!leftHoldTriggered) {
                    startHold(player, "left");
                    leftHoldTriggered = true; // Mark that startHold() has been called
                }
                doHold(player, "left");
            } else if (button.equals("right")) {
                if (!rightHoldTriggered) {
                    startHold(player, "right");
                    rightHoldTriggered = true; // Mark that startHold() has been called
                }
                doHold(player, "right");
            }
        }
    }

    private void startHold(PlayerEntity player, String button) {
        if(SpellHelper.canCast(player, button)) {
            List<ItemStack> list;
            if(button.equals("left")){
                list = ElementalCrystalsL;
            } else {
                list = ElementalCrystalsR;
            }
            if(!list.isEmpty()) {
                ItemStack stack = list.get(0);
                InputHandler.tryCast(player, button, stack, SpellInput.HOLD, SpellInput.HOLD_START);
                //System.out.println(button + " Start Hold");
            }
        }
    }
    private void doHold(PlayerEntity player, String button){
        if(SpellHelper.canCast(player, button)) {
            ItemStack stack;
            if (button.equals("left")) {
                if (!ElementalCrystalsL.isEmpty()) {
                    stack = ElementalCrystalsL.get(0);
                    InputHandler.tryCast(player, button, stack, SpellInput.HOLD, SpellInput.HOLD);
                } else {
                    leftMousedDown = false;
                }
            } else {
                if (!ElementalCrystalsR.isEmpty()) {
                    stack = ElementalCrystalsR.get(0);
                    InputHandler.tryCast(player, button, stack, SpellInput.HOLD, SpellInput.HOLD);
                } else {
                    rightMousedDown = false;
                }
            }
        }
    }
    private void endHold(PlayerEntity player, String button) {
        if(SpellHelper.canCast(player, button)) {
            ItemStack stack = button.equals("left") ? ElementalCrystalsL.get(0) : ElementalCrystalsR.get(0);
            InputHandler.tryCast(player, button, stack, SpellInput.HOLD, SpellInput.HOLD_RELEASE);
            if(button.equals("left"))
                leftHoldTriggered = false;
            else
                rightHoldTriggered = false;
            //System.out.println(button + " End Hold");
        }
    }

    private boolean hasElytraEquipped(PlayerEntity player){
        for(int i = 0; i < player.getInventory().armor.size()-1; i++){
            if(player.getInventory().getArmorStack(i).isOf(Items.ELYTRA)){
                return true;
            }
        }
        return false;
    }

    private void onWorldRender(){

    }

    private void bodySpellHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (SpellHelper.hasChargedSpell(player, CrystalData.BODY)) {
            double distance = 10;
            double angle = 25;
            Entity selectedEntity = findAutoAimTarget(player, distance, angle);

            if (selectedEntity instanceof LivingEntity target) {
                int x = 10;
                int y = client.getWindow().getScaledHeight() / 2 - 40;

                // If it's a player, draw their face
                if (target instanceof AbstractClientPlayerEntity targetPlayer) {
                    Identifier skinTexture = targetPlayer.getSkinTextures().texture();

                    drawContext.drawTexture(RenderLayer::getGuiTextured, skinTexture, x, y, 8, 8, 8, 8, 64, 64); //main skin
                    drawContext.drawTexture(RenderLayer::getGuiTextured, skinTexture, x, y, 40, 8, 8, 8, 64, 64); //hat layer
                    x += 12;
                }

                // Name
                drawContext.drawText(client.textRenderer, target.getName(), x, y, 0xFFFFFF, true);

                // Health bar
                float maxHealth = target.getMaxHealth();
                float currentHealth = target.getHealth();
                int barWidth = 80;
                int barHeight = 8;
                int healthBarWidth = (int)((currentHealth / maxHealth) * barWidth);

                y += 12;
                drawContext.fill(x, y, x + barWidth, y + barHeight, 0xFF555555); // background
                drawContext.fill(x, y, x + healthBarWidth, y + barHeight, 0xFFAA0000); // health

                // Health text
                y += 10;
                String healthText = String.format("%.1f / %.1f", currentHealth, maxHealth);
                drawContext.drawText(client.textRenderer, healthText, x, y, 0xAAAAAA, true);
            }
        }
    }

    private void bodyClientTick(MinecraftClient client) {
        if(SpellHelper.hasChargedSpell(client.player, CrystalData.BODY)) {
            Entity selectedEntity;
            double distance = 10;
            double angle = 25;
            selectedEntity = findAutoAimTarget(client.player, distance, angle);
            if (selectedEntity != null) {
                client.world.addParticle(new DustParticleEffect(CrystalData.BODY.getColorInt(), 1f), selectedEntity.getX(), selectedEntity.getY() + selectedEntity.getHeight()+0.25, selectedEntity.getZ(), 0, 0, 0);
            }
        }
    }

    private void soulClientTick(MinecraftClient client) {
        if(SpellHelper.hasChargedSpell(client.player, CrystalData.SOUL)) {
            Entity selectedEntity;
            double distance = 10;
            double angle = 25;
            selectedEntity = findAutoAimTarget(client.player, distance, angle);
            if (selectedEntity != null) {
                client.world.addParticle(new DustParticleEffect(CrystalData.SOUL.getColorInt(), 1f), selectedEntity.getX(), selectedEntity.getY() + selectedEntity.getHeight()+0.25, selectedEntity.getZ(), 0, 0, 0);
            }
        }
    }
}
