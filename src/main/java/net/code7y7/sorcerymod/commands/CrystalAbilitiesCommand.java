package net.code7y7.sorcerymod.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.code7y7.sorcerymod.item.ElementalCrystalItem;
import net.code7y7.sorcerymod.item.InertCrystalItem;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CrystalAbilitiesCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("sorcerymod")
                    .then(CommandManager.literal("crystal")
                            .then(CommandManager.literal("abilities")
                                    .then(CommandManager.literal("list")
                                            .executes(CrystalAbilitiesCommand::listAbilities))
                                    .then(CommandManager.literal("add")
                                            .then(CommandManager.argument("ability", StringArgumentType.string())
                                                    .suggests(CrystalAbilitiesCommand::suggestAbilities)
                                                    .executes(context -> addAbility(context, StringArgumentType.getString(context, "ability"))))))));
        });
    }

    private static int listAbilities(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.translatable("commands.crystalabilities.no_crystal"));
            return 0;
        }

        ItemStack heldItem = player.getMainHandStack();
        if (heldItem.getItem() instanceof ElementalCrystalItem crystalItem) {
            List<String> abilities = crystalItem.getUnlockedAbilities(heldItem);

            if (abilities != null && !abilities.isEmpty()) {
                context.getSource().sendFeedback(() -> Text.literal("Unlocked Abilities:"), false);
                for (String ability : abilities) {
                    context.getSource().sendFeedback(() -> Text.literal("- " + ability), false);
                }
            } else {
                context.getSource().sendError(Text.literal("commands.crystalabilities.no_abilities"));
            }
        } else {
            context.getSource().sendError(Text.literal("commands.crystalabilities.not_crystal"));
        }

        return 1;
    }

    private static int addAbility(CommandContext<ServerCommandSource> context, String ability) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.translatable("commands.crystalabilities.no_player"));
            return 0;
        }

        ItemStack heldItem = player.getMainHandStack();
        if (heldItem.getItem() instanceof ElementalCrystalItem crystalItem) {
            List<String> availableAbilities = crystalItem.ABILITIES;

            if (availableAbilities.contains(ability)) {
                if (crystalItem.addCrystalAbility(heldItem, ability)) {
                    context.getSource().sendFeedback(() -> Text.translatable("commands.crystalabilities.added").append(ability), false);
                } else {
                    context.getSource().sendError(Text.translatable("commands.crystalabilities.unlocked").append(ability));
                }
            } else {
                context.getSource().sendError(Text.translatable("commands.crystalabilities.invalid").append(ability));
            }
        } else {
            context.getSource().sendError(Text.translatable("commands.crystalabilities.no_crystal"));
        }

        return 1;
    }

    private static CompletableFuture<Suggestions> suggestAbilities(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return builder.buildFuture();
        }

        ItemStack heldItem = player.getMainHandStack();
        if (heldItem.getItem() instanceof ElementalCrystalItem crystalItem) {
            List<String> availableAbilities = crystalItem.ABILITIES;
            List<String> unlockedAbilities = crystalItem.getUnlockedAbilities(heldItem);

            if (availableAbilities != null && !availableAbilities.isEmpty()) {
                for (String ability : availableAbilities) {
                    if (!unlockedAbilities.contains(ability)) {
                        builder.suggest(ability);
                    }
                }
            }
        }

        return builder.buildFuture();
    }
}
