package net.code7y7.sorcerymod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.item.ModItems;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class UpgradeCrystalCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("sorcerymod")
                    .then(CommandManager.literal("crystal")
                            .then(CommandManager.literal("upgrade")
                                    .executes(UpgradeCrystalCommand::upgradeCrystal))));
        });
    }

    private static int upgradeCrystal(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.translatable("commands.upgradecrystal.no_player"));
            return 0;
        }

        var heldItemStack = player.getMainHandStack();
        var heldItem = heldItemStack.getItem();

        if (heldItem instanceof ElementalCrystalItem crystalItem) {
            if (crystalItem.getTier(heldItemStack) < crystalItem.getMaxTier()) {
                crystalItem.upgradeCrystal(player, heldItemStack, 1);
                context.getSource().sendFeedback(() -> Text.translatable("commands.upgradecrystal.success"), true);
            } else {
                context.getSource().sendError(Text.translatable("commands.upgradecrystal.max_tier"));
            }
        } else {
            context.getSource().sendError(Text.translatable("commands.upgradecrystal.fail"));
        }

        return 1;
    }
}
