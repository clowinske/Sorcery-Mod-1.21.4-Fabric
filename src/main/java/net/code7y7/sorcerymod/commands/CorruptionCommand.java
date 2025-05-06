package net.code7y7.sorcerymod.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.code7y7.sorcerymod.StateSaverAndLoader;
import net.code7y7.sorcerymod.network.SetCorruptionS2CPayload;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CorruptionCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("sorcerymod")
                    .then(CommandManager.literal("corruption")
                            .then(CommandManager.literal("set")
                                    .then(CommandManager.argument("player", EntityArgumentType.player())
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 100))
                                                    .executes(context -> setCorruption(context, EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))))
                            .then(CommandManager.literal("get")
                                    .then(CommandManager.argument("player", EntityArgumentType.player())
                                            .executes(context -> getCorruption(context, EntityArgumentType.getPlayer(context, "player")))))
                            .then(CommandManager.literal("add")
                                    .then(CommandManager.argument("player", EntityArgumentType.player())
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                                    .executes(context -> addCorruption(context, EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))))));
        });
    }

    private static int setCorruption(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, int amount) throws CommandSyntaxException {
        StateSaverAndLoader.getPlayerState(player).corruption = amount;
        ServerPlayNetworking.send(player, new SetCorruptionS2CPayload(amount, true));
        context.getSource().sendFeedback(()-> Text.translatable("commands.corruption.set", player.getDisplayName(), amount), true);
        return 1;
    }

    private static int getCorruption(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException{
        context.getSource().sendFeedback(()-> Text.translatable("commands.corruption.get", player.getDisplayName(), StateSaverAndLoader.getPlayerState(player).corruption), true);
        return (int) StateSaverAndLoader.getPlayerState(player).corruption;
    }

    private static int addCorruption(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, int amount) throws CommandSyntaxException {
        ServerPlayNetworking.send(player, new SetCorruptionS2CPayload(Math.min(StateSaverAndLoader.getPlayerState(player).corruption + amount, 100), true));
        context.getSource().sendFeedback(()-> Text.translatable("commands.sorcerymod.corruption.add", amount, player.getDisplayName(), StateSaverAndLoader.getPlayerState(player).corruption), true);
        return 0;
    }
}
