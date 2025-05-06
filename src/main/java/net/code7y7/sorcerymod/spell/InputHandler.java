package net.code7y7.sorcerymod.spell;

import net.code7y7.sorcerymod.PlayerData;
import net.code7y7.sorcerymod.SorceryModClient;
import net.code7y7.sorcerymod.StateSaverAndLoader;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.network.CastSpellC2SPayload;
import net.code7y7.sorcerymod.network.SetFocusRechargeS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class InputHandler {
    public static void tryCast(PlayerEntity player, String button, ItemStack stack, SpellInput input, SpellInput input1){
        if(stack.getItem() instanceof ElementalCrystalItem crystalItem) {
            SpellType spellType = SpellRegistry.getSpellType(crystalItem.elementName);
            int crystalTier = crystalItem.getTier(stack);
            for (Ability ability : spellType.getAbilities()) {
                if(crystalItem.getUnlockedAbilities(stack).contains(ability.toString())) { //crystal has the ability
                    if (ability.getInput() == input) { //ability matches input
                        ClientPlayNetworking.send(new CastSpellC2SPayload(ability.toString(), button, crystalTier, stack, input, input1));
                    }
                }
            }
        }
    }

    public static void cast(Ability ability, ServerPlayerEntity player, String button, int tier, ItemStack crystal, SpellInput input, SpellInput input1){
        if(input == SpellInput.HOLD){
            if(input1 == SpellInput.HOLD_START){
                ability.holdStart(player, player.getServerWorld(), button, tier, crystal);
            } else if(input1 == SpellInput.HOLD_RELEASE){
                ability.holdRelease(player, player.getServerWorld(), button, tier, crystal);
            }
            ServerPlayNetworking.send(player, new SetFocusRechargeS2CPayload(20));
            if(SpellHelper.getFocus(player)-1 < ability.getFocusCost(player, player.getServerWorld(), button, tier, crystal)){
                SpellHelper.resetHandCharge(player, button);
            }
        }
        ability.trigger(player, player.getServerWorld(), button, tier, crystal);
        if(!player.isCreative())
            SpellHelper.removeFocus(player, ability.getFocusCost(player, player.getServerWorld(), button, tier, crystal));
    }
}
