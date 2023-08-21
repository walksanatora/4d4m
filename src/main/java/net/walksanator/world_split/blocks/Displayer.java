package net.walksanator.world_split.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.walksanator.world_split.WorldSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Displayer extends BlockWithEntity {
    public Displayer(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayerBE(pos, state);
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    private static int countSetBits(int num) {
        int count = 0;
        while (num > 0) {
            num &= (num - 1);
            count++;
        }
        return count;
    }

    public int getComparatorOutput(BlockState state, @NotNull World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof DisplayerBE display) {
            if (display.visible_layer == -1) {
                return (int) ((countSetBits(display.enable_map.get(4).get(4))/(float)WorldSection.Enabled.count)*15.0);
            } else {
                return WorldSection.Enabled.values()[display.visible_layer].calcRedstonePower(display.enable_map);
            }
        }
        return 0;
    }


    @Override
    public ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient() & hand == Hand.MAIN_HAND) {
            world.playSound(pos.getX(),pos.getY(),pos.getZ(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS,1f,1f,true);
        }
        if (!world.isClient() & hand == Hand.MAIN_HAND) {
            BlockEntity maybeDisplay = world.getBlockEntity(pos);
            if (maybeDisplay instanceof DisplayerBE displayer) {
                displayer.onClick();
                world.updateListeners(pos,state,state, Block.NOTIFY_LISTENERS);
                ((ServerWorld)world).spawnParticles(ParticleTypes.SMOKE,pos.getX()+0.5,pos.getY()+1.2,pos.getZ()+0.5,32,0.1,0.05,0.1,0.1);
                return ActionResult.CONSUME;
            }
        }

        return ActionResult.PASS;
    }
}
