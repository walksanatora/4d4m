package net.walksanator.world_split;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.walksanator.world_split.blocks.Nullifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;


import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.math.MathHelper.floor;


public class WorldSplit implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("worldsplit");
	public static Nullifier NULLIFIER = Registry.register(Registry.BLOCK,new Identifier("worldsplit","nullifier"),new Nullifier(FabricBlockSettings.of(Material.AMETHYST)));
	public static Item NULLIFIER_ITEM =  Registry.register(Registry.ITEM, new Identifier("worldsplit", "nullifier"), new BlockItem(NULLIFIER, new FabricItemSettings()));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("simplex")
						.then(CommandManager.argument("modifier",DoubleArgumentType.doubleArg()))
						.then(CommandManager.argument("range", IntegerArgumentType.integer(0)))
				.executes(context -> {
					int squareSize = context.getArgument("range",Integer.class); // Size of the square (5x5 in this case)
					double size_modifier = context.getArgument("modifier",Double.class);
					int off = floor((float) squareSize /2);
					ServerWorld world = context.getSource().getWorld();
					BlockPos pos = context.getSource().getPlayerOrThrow().getBlockPos();
					for (int z = 0; z < squareSize; z++) {
						for (int x = 0; x < squareSize; x++) {
							// Your code here
							// You can access the current block using (x, y) coordinates
							// For example: blockArray[x][y] or some other data structure
							double y2 = SimplexNoise.noise((x-off)/size_modifier,(z-off)/size_modifier);
							BlockPos offset = pos.add(x-off,y2-1,z-off);
							LOGGER.info(offset.toString());
							LOGGER.info(String.valueOf(y2));
							world.setBlockState(offset,Blocks.GOLD_BLOCK.getDefaultState());
						}
					}
					// For versions below 1.19, replace "Text.literal" with "new LiteralText".
					context.getSource().sendMessage(Text.literal("Called /foo with no arguments"));

					return 1;
				})));
		LOGGER.info("Hello Fabric world!");
	}
}
