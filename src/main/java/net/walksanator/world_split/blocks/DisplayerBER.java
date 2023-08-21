package net.walksanator.world_split.blocks;

import at.petrak.hexcasting.common.lib.HexBlocks;
import dan200.computercraft.shared.Registry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;


public class DisplayerBER implements BlockEntityRenderer<DisplayerBE> {
    public DisplayerBER() {
        super();
    }
    public DisplayerBER(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(DisplayerBE blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Random random = Random.create();
        BlockRenderManager blockRender = MinecraftClient.getInstance().getBlockRenderManager();

        matrices.push();
        blockRender.renderBlock(Blocks.CHISELED_STONE_BRICKS.getDefaultState(),blockEntity.getPos(),MinecraftClient.getInstance().world, matrices,vertexConsumers.getBuffer(RenderLayer.getSolid()),true, Random.create());
        matrices.pop();

        matrices.push();
        matrices.scale(1/10F,1/10F,1/10F);
        matrices.translate(1/20F,1/20F,1/20F);
        int diff = 7/2;
        int z = 0;
        for (List<Integer> row : blockEntity.enable_map) {
            int x = 0;
            for (int acu : row) {
                List<BlockState> renders = new ArrayList<>();
                matrices.push();
                matrices.translate((1.0/7.0)*10*x,((1/8.0)*3*10)+10,(1/7.0)*10*z);
                if (x==diff&z==diff) {//Center Indicator
                    blockRender.renderBlock(Blocks.REDSTONE_BLOCK.getDefaultState(),blockEntity.getPos(),MinecraftClient.getInstance().world, matrices,vertexConsumers.getBuffer(RenderLayer.getSolid()),false, random);
                }
                if (x==diff&z==diff-1) {
                    blockRender.renderBlock(Blocks.EMERALD_BLOCK.getDefaultState(), blockEntity.getPos(),MinecraftClient.getInstance().world, matrices,vertexConsumers.getBuffer(RenderLayer.getSolid()),false, random);
                }
                matrices.pop();
                if ((acu & 0b1) >= 1) {//TIS
                    renders.add(li.cil.tis3d.common.block.Blocks.CASING.get().getDefaultState());
                };
                if ((acu & 0b10) >= 1) {//HEX
                    renders.add(HexBlocks.AMETHYST_DUST_BLOCK.getDefaultState());
                };
                if ((acu & 0b100) >= 1) {//TIS
                    renders.add(Registry.ModBlocks.COMPUTER_ADVANCED.getDefaultState());
                };
                int yoff = 0;
                for (BlockState ren : renders) {
                    matrices.push();
                    matrices.translate((1.0/7.0)*10*x,((1/8.0)*yoff*10)+10,(1/7.0)*10*z);
                    blockRender.renderBlock(ren,blockEntity.getPos(),MinecraftClient.getInstance().world, matrices,vertexConsumers.getBuffer(RenderLayer.getSolid()),false, random);
                    matrices.pop();
                    yoff++;
                }
                x++;
            }
            z++;
        }
        matrices.pop();
    }

}
