package net.walksanator.world_split;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static java.lang.Math.*;

public class WorldSection {
    public static double dist_layers = 1000.0;
    public static List<int[]> offsets = SphereGenerator.generate(10.0);
    public enum Enabled {
        CC, Hex, TIS
    }

    public static boolean isEnabled(Enabled enable, BlockPos pos, ServerWorld level) {
        return isEnabled(enable, pos.getX(), pos.getY(), pos.getZ(),level,level.getSeed());
    }
    public static boolean isEnabled(Enabled enable, int x, int y, int z, ServerWorld level) {
        return isEnabled(enable, x,y,z, level, level.getSeed());
    }
    public static boolean isEnabled(Enabled enable, BlockPos pos, long seed) {
        return isEnabled(enable, pos.getX(),pos.getZ(), seed);
    }



    public static boolean isEnabled(Enabled enable, int x, int y, int z, World level, long seed) {
        Random r = new Random();
        r.setSeed(seed);
        double offset = enable.ordinal()*dist_layers;

        BlockPos pos = new BlockPos(x,y,z);
        for (int[] pos2 : offsets) {
            if (level.getBlockState(pos.add(pos2[0],pos2[1],pos2[2])).isOf(WorldSplit.NULLIFIER)) {
                return true;
            }
        }
        int sig_x = getSign(x);
        int sig_z = getSign(z);
        double height = SimplexNoise.noise( ceil(abs(x) / 16.0)*sig_x,offset+r.nextDouble(),  ceil(abs(z) / 16.0)*sig_z);
        return height > 0;
    }

    public static boolean isEnabled(Enabled enable, int x, int z, long seed) {
        Random r = new Random();
        r.setSeed(seed);
        double offset = enable.ordinal()*dist_layers;
        int sig_x = getSign(x);
        int sig_z = getSign(z);
        double height = SimplexNoise.noise( ceil(abs(x) / 16.0)*sig_x,offset+r.nextDouble(),  ceil(abs(z) / 16.0)*sig_z);
        return height > 0;
    }

    private static int getSign(int number) {
        if (number >= 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
