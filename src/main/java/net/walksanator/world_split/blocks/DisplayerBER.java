package net.walksanator.world_split.blocks;

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
import net.walksanator.world_split.WorldSection;

import java.util.List;


public class DisplayerBER implements BlockEntityRenderer<DisplayerBE> {
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
        int diff = 7/2;
        int z = 0;
        for (List<Integer> row : blockEntity.enable_map) {
            int x = 0;
            for (int acu : row) {
                BlockState glass = Blocks.GLASS.getDefaultState();
                BlockState[] renders = new BlockState[]{glass};
                matrices.push();
                matrices.translate((1.0/7.0)*10*x+(1/20F),0.0,(1/7.0)*10*z+(1/20F));
                if (x==diff&z==diff) {//Center Indicator
                    if (blockEntity.visible_layer == -1) {
                        matrices.translate(0.0,((1/8.0)* WorldSection.Enabled.count*10)+10,0.0);
                    } else {
                        matrices.translate(0.0,((1/8.0)*1*10)+10,0.0);
                    }
                    blockRender.renderBlock(Blocks.REDSTONE_BLOCK.getDefaultState(),blockEntity.getPos(),MinecraftClient.getInstance().world, matrices,vertexConsumers.getBuffer(RenderLayer.getSolid()),false, random);
                }
                matrices.pop();
                if (blockEntity.visible_layer == -1) {
                    renders = WorldSection.Enabled.blocksFromBitmap(acu,glass);
                } else {
                    WorldSection.Enabled enabled = WorldSection.Enabled.values()[blockEntity.visible_layer];
                    int bitmask = enabled.bitmaskForEnabled();
                    if ((acu&bitmask)>=1) {
                        renders[0] = enabled.blockStateForEnabled();
                    }
                }
                int yOffset = 0;
                for (BlockState ren : renders) {
                    if (ren == glass) {
                        continue;
                    }
                    matrices.push();
                    matrices.translate((1.0/7.0)*10*x+(1/20.0),((1/8.0)*yOffset*10)+10,(1/7.0)*10*z+(1/20.0));
                    blockRender.renderBlock(ren,blockEntity.getPos(),MinecraftClient.getInstance().world, matrices,vertexConsumers.getBuffer(RenderLayer.getSolid()),false, random);
                    matrices.pop();
                    yOffset++;
                }
                x++;
            }
            z++;
        }
        matrices.pop();
    }

}
