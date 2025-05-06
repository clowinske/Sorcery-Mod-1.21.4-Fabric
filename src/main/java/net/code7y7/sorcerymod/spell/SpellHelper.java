package net.code7y7.sorcerymod.spell;

import net.code7y7.sorcerymod.SorceryModClient;
import net.code7y7.sorcerymod.StateSaverAndLoader;
import net.code7y7.sorcerymod.attachment.ModAttachmentTypes;
import net.code7y7.sorcerymod.component.ModDataComponentTypes;
import net.code7y7.sorcerymod.entity.client.ModTrackedPlayerData;
import net.code7y7.sorcerymod.network.*;
import net.code7y7.sorcerymod.util.crystal.CrystalData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpellHelper {
    public static List<ItemStack> getAttachedCrystals(PlayerEntity player){
        List<ItemStack> list = new ArrayList<>();
        String uuidString = player.getAttached(ModAttachmentTypes.POUCH_ID);
        if (uuidString != null) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack invStack = player.getInventory().getStack(i);
                if (Objects.equals(invStack.get(ModDataComponentTypes.POUCH_ID), uuidString)){
                    if(!invStack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS).getStacks().isEmpty())
                        return invStack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS).getStacks();
                }
            }
        }
        return list;
    }

    public static ItemStack getPouchItem(PlayerEntity player){
        ItemStack itemStack = ItemStack.EMPTY;
        String uuidString = player.getAttached(ModAttachmentTypes.POUCH_ID);
        if (uuidString != null) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack invStack = player.getInventory().getStack(i);
                if (Objects.equals(invStack.get(ModDataComponentTypes.POUCH_ID), uuidString)
                        && !invStack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS).getStacks().isEmpty()) {
                    return invStack;
                }
            }
        }

        return itemStack;
    }

    public static List<ItemStack> getAttachedCrystals(ServerPlayerEntity player){
        List<ItemStack> list = new ArrayList<>();
        String uuidString = player.getAttached(ModAttachmentTypes.POUCH_ID);
        if (uuidString != null) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack invStack = player.getInventory().getStack(i);
                if (Objects.equals(invStack.get(ModDataComponentTypes.POUCH_ID), uuidString)) {
                    if (!invStack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS).getStacks().isEmpty())
                        return invStack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS).getStacks();
                }
            }
        }
        return list;
    }

    public static boolean hasCrystalsAndPouch(PlayerEntity player) {
        String uuidString = player.getAttached(ModAttachmentTypes.POUCH_ID);
        if (uuidString != null) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack invStack = player.getInventory().getStack(i);
                if (Objects.equals(invStack.get(ModDataComponentTypes.POUCH_ID), uuidString)) {
                    return !invStack.get(ModDataComponentTypes.CRYSTAL_POUCH_CONTENTS).getStacks().isEmpty();
                }
            }
        }
        return false;
    }

    public static void resetHandCharge(PlayerEntity player, String button){
        if(button.equals("left")) {
            SorceryModClient.playerData.leftHandCharge = 0;
            ClientPlayNetworking.send(new SetSpellChargeC2SPayload(button, 0));
        } else if(button.equals("right")){
            SorceryModClient.playerData.rightHandCharge = 0;
            ClientPlayNetworking.send(new SetSpellChargeC2SPayload(button, 0));
        } else {
            resetHandCharge(player, "right");
            resetHandCharge(player, "left");
        }
    }
    public static void resetHandCharge(ServerPlayerEntity player, String button){
        if(button.equals("left")) {
            StateSaverAndLoader.getPlayerState(player).leftHandCharge = 0;
        } else {
            StateSaverAndLoader.getPlayerState(player).rightHandCharge = 0;
        }
        ServerPlayNetworking.send(player, new SetSpellChargeS2CPayload(button, 0));
    }

    public static int getCorruption(PlayerEntity player){
        return SorceryModClient.playerData.corruption;
    }
    public static void decrementHandCharge(PlayerEntity player, String button, int amt){
        if(button.equals("left")) {
            SorceryModClient.playerData.leftHandCharge = Math.max(0, SorceryModClient.playerData.leftHandCharge - amt);
            ClientPlayNetworking.send(new SetSpellChargeC2SPayload(button, SorceryModClient.playerData.leftHandCharge));
            if(player instanceof ModTrackedPlayerData data){
                data.setLeftHandCharge(SorceryModClient.playerData.leftHandCharge);
            }
        } else {
            SorceryModClient.playerData.rightHandCharge = Math.max(0, SorceryModClient.playerData.rightHandCharge - amt);
            ClientPlayNetworking.send(new SetSpellChargeC2SPayload(button, SorceryModClient.playerData.rightHandCharge));
            if(player instanceof ModTrackedPlayerData data){
                data.setLeftHandCharge(SorceryModClient.playerData.rightHandCharge);
            }
        }
    }

    public static void decrementHandCharge(ServerPlayerEntity player, String button, int amt){
        if(button.equals("left")) {
            StateSaverAndLoader.getPlayerState(player).leftHandCharge = Math.max(0, StateSaverAndLoader.getPlayerState(player).leftHandCharge - amt);
            ServerPlayNetworking.send(player, new SetSpellChargeS2CPayload(button, (int) StateSaverAndLoader.getPlayerState(player).leftHandCharge));
        } else {
            StateSaverAndLoader.getPlayerState(player).rightHandCharge = Math.max(0, StateSaverAndLoader.getPlayerState(player).rightHandCharge - amt);
            ServerPlayNetworking.send(player, new SetSpellChargeS2CPayload(button, (int) StateSaverAndLoader.getPlayerState(player).rightHandCharge));
        }
    }

    public static boolean isCharged(PlayerEntity player, String button){
        if(button.equals("left")){
            return SorceryModClient.playerData.leftHandCharge == 100;
        } else {
            return SorceryModClient.playerData.rightHandCharge == 100;
        }
    }
    public static boolean isCharged(ServerPlayerEntity player, String button){
        if(button.equals("left")){
            return StateSaverAndLoader.getPlayerState(player).leftHandCharge == 100;
        } else {
            return StateSaverAndLoader.getPlayerState(player).rightHandCharge == 100;
        }
    }

    public static void setHandSpell(PlayerEntity player, String button, CrystalData type){
        if(button.equals("left")){
            SorceryModClient.playerData.leftHandSpell = type.getInt();
            if(player instanceof ModTrackedPlayerData data){
                data.setLeftHandSpell(type.getInt());
                //player.getDataTracker().set();
            }
        } else {
            SorceryModClient.playerData.rightHandSpell = type.getInt();
            if(player instanceof ModTrackedPlayerData data){
                data.setRightHandSpell(type.getInt());
            }
        }
        ClientPlayNetworking.send(new SetSpellTypeC2SPayload(button, type.getInt()));
    }
    public static void setHandSpell(ServerPlayerEntity player, String button, CrystalData type){
        if(button.equals("left")){
            StateSaverAndLoader.getPlayerState(player).leftHandSpell = type.getInt();
        } else {
            StateSaverAndLoader.getPlayerState(player).rightHandSpell = type.getInt();
        }
        ServerPlayNetworking.send(player, new SetSpellTypeS2CPayload(button, type.getInt()));
    }

    public static CrystalData getHandSpell(PlayerEntity player, String button){
        if(button.equals("left")){
            return CrystalData.getTypeByInt(SorceryModClient.playerData.leftHandSpell);
        } else {
            return CrystalData.getTypeByInt(SorceryModClient.playerData.rightHandSpell);
        }
    }
    public static CrystalData getHandSpell(ServerPlayerEntity player, String button){
        if(button.equals("left")){
            return CrystalData.getTypeByInt(StateSaverAndLoader.getPlayerState(player).leftHandSpell);
        } else {
            return CrystalData.getTypeByInt(StateSaverAndLoader.getPlayerState(player).rightHandSpell);
        }
    }

    public static int getHandCharge(PlayerEntity player, String button){
        if(button.equals("left")){
            return SorceryModClient.playerData.leftHandCharge;
        } else {
            return SorceryModClient.playerData.rightHandCharge;
        }
    }

    public static int getHandCharge(ServerPlayerEntity player, String button){
        if(button.equals("left")){
            return StateSaverAndLoader.getPlayerState(player).leftHandCharge;
        } else {
            return StateSaverAndLoader.getPlayerState(player).rightHandCharge;
        }
    }

    public static String otherButton(String button){
        if(button.equals("left")){
            return "right";
        } else {
            return "left";
        }
    }

    public static void setCanCast(PlayerEntity player, String button, boolean canCast){
        if(button.equals("left")){
            SorceryModClient.playerData.canCastLeft = canCast;
        } else {
            SorceryModClient.playerData.canCastRight = canCast;
        }
        ClientPlayNetworking.send(new SetCanCastC2SPayload(button, canCast));
    }
    public static void setCanCast(ServerPlayerEntity player, String button, boolean canCast){
        if(button.equals("left")){
            StateSaverAndLoader.getPlayerState(player).canCastLeft = canCast;
        } else {
            StateSaverAndLoader.getPlayerState(player).canCastRight = canCast;
        }
        ServerPlayNetworking.send(player, new SetCanCastS2CPayload(button, canCast));
        System.out.println("set "+button+" to "+canCast);
    }
    public static boolean canCast(PlayerEntity player, String button){
        if(button.equals("left")){
            return SorceryModClient.playerData.canCastLeft;
        } else {
            return SorceryModClient.playerData.canCastRight;
        }
    }
    public static boolean canCast(ServerPlayerEntity player, String button){
        if(button.equals("left")){
            return StateSaverAndLoader.getPlayerState(player).canCastLeft;
        } else {
            return StateSaverAndLoader.getPlayerState(player).canCastRight;
        }
    }

    public static boolean hasChargedSpell(PlayerEntity player, CrystalData spell){
        return (getHandSpell(player, "left") == spell && isCharged(player, "left")) || (getHandSpell(player, "right") == spell && isCharged(player, "right"));
    }
    public static boolean hasChargedSpell(ServerPlayerEntity player, CrystalData spell){
        return (getHandSpell(player, "left") == spell && isCharged(player, "left")) || (getHandSpell(player, "right") == spell && isCharged(player, "right"));
    }

    public static String getHandWith(PlayerEntity player, CrystalData type){
        if(getHandSpell(player, "left").equals(type) && getHandSpell(player, "right").equals(type)){
            return "both";
        } else if(getHandSpell(player, "left").equals(type)){
            return "left";
        } else if(getHandSpell(player, "right").equals(type)){
            return "right";
        } else {
            return "none";
        }
    }

    public static double getFocus(ServerPlayerEntity player){
        return StateSaverAndLoader.getPlayerState(player).focus;
    }
    public static double getFocus(PlayerEntity player){
        return SorceryModClient.playerData.focus;
    }

    public static void setFocus(ServerPlayerEntity player, double amt){
        StateSaverAndLoader.getPlayerState(player).focus = amt;
        ServerPlayNetworking.send(player, new SetFocusS2CPayload(amt));
    }
    public static void setFocus(PlayerEntity player, double amt){
        SorceryModClient.playerData.focus = amt;
        ClientPlayNetworking.send(new SetFocusC2SPayload(amt));
    }
    public static void addFocus(ServerPlayerEntity player, double amt){
        StateSaverAndLoader.getPlayerState(player).focus = Math.min(200, StateSaverAndLoader.getPlayerState(player).focus + amt);
        ServerPlayNetworking.send(player, new SetFocusS2CPayload(StateSaverAndLoader.getPlayerState(player).focus));
    }
    public static void addFocus(PlayerEntity player, double amt){
        SorceryModClient.playerData.focus = Math.min(200, SorceryModClient.playerData.focus+amt);
        ClientPlayNetworking.send(new SetFocusC2SPayload(SorceryModClient.playerData.focus));
    }
    public static void removeFocus(ServerPlayerEntity player, double amt){
        StateSaverAndLoader.getPlayerState(player).focus = Math.max(0, StateSaverAndLoader.getPlayerState(player).focus - amt);
        ServerPlayNetworking.send(player, new SetFocusS2CPayload(StateSaverAndLoader.getPlayerState(player).focus));
    }
    public static void removeFocus(PlayerEntity player, double amt){
        SorceryModClient.playerData.focus = Math.max(0, SorceryModClient.playerData.focus - amt);
        ClientPlayNetworking.send(new SetFocusC2SPayload(SorceryModClient.playerData.focus));
    }
}
