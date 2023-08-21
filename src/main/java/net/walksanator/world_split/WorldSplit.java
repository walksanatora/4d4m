package net.walksanator.world_split;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.walksanator.world_split.blocks.Displayer;
import net.walksanator.world_split.blocks.DisplayerBE;
import net.walksanator.world_split.blocks.DisplayerBER;
import net.walksanator.world_split.blocks.Nullifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static java.lang.Math.pow;
import static net.minecraft.server.command.CommandManager.literal;
import static net.walksanator.world_split.WorldSection.xzToChunk;


public class WorldSplit implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("worldsplit");
	public static Nullifier NULLIFIER = Registry.register(Registry.BLOCK,new Identifier("worldsplit","nullifier"),new Nullifier(FabricBlockSettings.of(Material.AMETHYST)));
	public static Item NULLIFIER_ITEM =  Registry.register(Registry.ITEM, new Identifier("worldsplit", "nullifier"), new BlockItem(NULLIFIER, new FabricItemSettings()));

	public static Displayer DISPLAYER = Registry.register(Registry.BLOCK,new Identifier("worldsplit","displayer"),new Displayer(FabricBlockSettings.of(Material.AMETHYST)));
	public static Item DISPLAYER_ITEM =  Registry.register(Registry.ITEM, new Identifier("worldsplit", "displayer"), new BlockItem(DISPLAYER, new FabricItemSettings()));

	public static BlockEntityType<DisplayerBE> DISPLAYER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("worldsplit","displayer"), FabricBlockEntityTypeBuilder.create(DisplayerBE::new, DISPLAYER).build());

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		BlockEntityRendererFactories.register(DISPLAYER_ENTITY, DisplayerBER::new);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("enabled")
				.executes(context -> {
					ServerCommandSource src = context.getSource();
					BlockPos pos = Objects.requireNonNull(src.getEntity()).getBlockPos();
					ServerWorld world = src.getWorld();
					int[] xz = xzToChunk(pos.getX(),pos.getZ());
					Objects.requireNonNull(src.getPlayer()).sendMessage(
							Text.literal("enabled for (%d,%d):".formatted(
									xz[0],xz[1]
							))
					);
					for (WorldSection.Enabled e : WorldSection.Enabled.values()) {
						if (WorldSection.isEnabled(e,pos,world)) {
							Objects.requireNonNull(src.getPlayer()).sendMessage(
									Text.literal("%s (%d, %s) is enabled for chunk".formatted(
											e,
											e.ordinal(),
											pow(2,e.ordinal())
									))
							);
						}
					}
					return 1;
				})
		));
		LOGGER.info("Hello Fabric world!");
	}
}
