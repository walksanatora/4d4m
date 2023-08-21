package net.walksanator.world_split;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.walksanator.world_split.blocks.Displayer;
import net.walksanator.world_split.blocks.DisplayerBE;
import net.walksanator.world_split.blocks.DisplayerBER;
import net.walksanator.world_split.blocks.Nullifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.math.MathHelper.floor;


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

		BlockEntityRendererRegistry.register(DISPLAYER_ENTITY, DisplayerBER::new);
		/*new BlockEntityRenderer<DisplayerBE>() {
			@Override
			public void render(DisplayerBE blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
				Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();

				buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
				buffer.vertex(positionMatrix, 0, 1, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
				buffer.vertex(positionMatrix, 0, 0, 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
				buffer.vertex(positionMatrix, 1, 0, 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
				buffer.vertex(positionMatrix, 1, 1, 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();
				// Your block rendering logic here
			}
		};*/


		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("simplex")
				.then(CommandManager.argument("modifier",DoubleArgumentType.doubleArg(0.1,1000.0))
						.then(CommandManager.argument("range", IntegerArgumentType.integer(0))
								.executes(context -> {
									double size_modifier = context.getArgument("modifier",Double.class);
									int squareSize = context.getArgument("range",Integer.class);
									int off = floor((float) squareSize /2);
									ServerWorld world = context.getSource().getWorld();
									BlockPos pos = context.getSource().getPlayerOrThrow().getBlockPos();
									for (int z = 0; z < squareSize; z++) {
										for (int x = 0; x < squareSize; x++) {
											// Your code here
											// You can access the current block using (x, y) coordinates
											// For example: blockArray[x][y] or some other data structure
											double y2 = SimplexNoise.noise((x-off)/size_modifier,(z-off)/size_modifier)+1;
											BlockPos offset = pos.add(x-off,(y2*size_modifier),z-off);
											LOGGER.info(offset.toString());
											LOGGER.info(String.valueOf(y2));
											world.setBlockState(offset,Blocks.GOLD_BLOCK.getDefaultState());
										}
									}
									context.getSource().sendMessage(Text.literal("Called /foo with no arguments"));
									return 1;
								}
								)
						)
				)
		));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("enabled")
						.executes(context -> {
							try {
								int mode = 7;
								ServerPlayerEntity pl = context.getSource().getPlayerOrThrow();
								BlockPos bp = pl.getBlockPos();
								int cx = floor(bp.getX() / 16.0);
								int cz = floor(bp.getZ() / 16.0);
								long seed = pl.getWorld().getSeed();
								int chop = mode / 2;
								List<Text> map = new ArrayList<>();
								for (int z = 0; z < mode; z++) {
									MutableText line = Text.empty();
									for (int x = 0; x < mode; x++) {
										int acu = 0;
										for (WorldSection.Enabled e : WorldSection.Enabled.values()) {
											Random r = new Random();
											r.setSeed(seed);
											double height = SimplexNoise.noise((x + cx - chop) + r.nextDouble(), e.ordinal() * 1000.0 + r.nextDouble(), (z + cz - chop) + r.nextDouble());
											if (height > 0) {
												LOGGER.info("%s is enabled at (%d,%d)".formatted(e, (x - chop), (z - chop)));
												acu += 2 ^ (e.ordinal()+1);
											}
										}
										LOGGER.info(Integer.toBinaryString(acu));
										Style style = Style.EMPTY;

										if ((acu & 0b1) >= 1) {//CC
											style.withColor(Formatting.YELLOW);
										};
										if ((acu & 0b10) >= 1) {//HEX
											style.withUnderline(true);
										};
										if ((acu & 0b100) >= 1) {//TIS
											style.withBold(true);
										};

										//int g = 255 * (acu & 0b10);
										//int b = 255 * (acu & 0b100);
										String display = "O";
										if ((z == chop) & (x == chop)) {
											display = "X";
										}
										//Style.EMPTY.withColor((r << 16) | (g << 8) | b)
										Text cha = Text.literal(display).setStyle(style);
										line.append(cha);
									}
									map.add(line);
								}
								for (Text line : map) {
									pl.sendMessage(line);
								}
								return 1;
							} catch (Exception e) {
								e.printStackTrace();
								return 0;
							}
						})
		));
		LOGGER.info("Hello Fabric world!");
	}
}
