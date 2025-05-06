package net.code7y7.sorcerymod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.code7y7.sorcerymod.SorceryMod;
import net.code7y7.sorcerymod.dungeon.DungeonGenerator;
import net.code7y7.sorcerymod.dungeon.Room;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.Structures;

import java.util.List;

public class DungeonCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("spawndungeon")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("seed", StringArgumentType.string())
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                    String seed = StringArgumentType.getString(context, "seed");
    
                                    // Generate dungeon layout
                                    DungeonGenerator generator = new DungeonGenerator((long) seed.hashCode());
                                    List<Room> rooms = generator.generateDungeon();
    
                                    // Place structures
                                    for(Room room : rooms) {
                                        placeStructure(
                                                source.getWorld(),
                                                pos.add(room.getPosition()),
                                                room.getType().name().toLowerCase(),
                                                room.getRotation()
                                        );
                                    }
    
                                    source.sendFeedback(() -> Text.literal("Generated dungeon with seed: " + seed), true);
                                    return 1;
                                })
                        )
                )
            );
        });
    }

    private static void placeStructure(ServerWorld world, BlockPos pos, String structurePath, BlockRotation rotation) {
        world.getServer().execute(() -> {
            StructureTemplateManager manager = world.getStructureTemplateManager();
            Identifier structureIdentifier = SorceryMod.createIdentifier(structurePath);


            // Log the structure path and identifier
            SorceryMod.LOGGER.info("Attempting to load structure: " + structurePath);
            SorceryMod.LOGGER.info("Using identifier: " + structureIdentifier);

            StructureTemplate template = manager.getTemplate(structureIdentifier).orElse(null);

            if (template == null) {
                SorceryMod.LOGGER.error("Missing structure: " + structureIdentifier);
                return;
            }

            StructurePlacementData placement = new StructurePlacementData()
                    .setRotation(rotation)
                    .setIgnoreEntities(false);

            template.place(world, pos, pos, placement, world.random, Block.NOTIFY_ALL);
        });
    }
}

