package net.walksanator.world_split;

import at.petrak.hexcasting.common.lib.HexBlocks;
import dan200.computercraft.shared.Registry;
import li.cil.tis3d.common.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.floorDiv;
import static java.lang.Math.pow;
import static net.minecraft.util.math.MathHelper.clamp;

public class WorldSection {
    public static double dist_layers = 1000.0;
    public static List<int[]> offsets = SphereGenerator.generate(10.0);
    public enum Enabled {
        CC, Hex, TIS;

        public static BlockState[] blocksFromBitmap(int bitmap, BlockState def) {
            BlockState[] renders = new BlockState[Enabled.count];
            Arrays.fill(renders, def);
            for (Enabled e : Enabled.values()) {
                if ((e.bitmaskForEnabled() & bitmap) >0) {
                    renders[e.ordinal()] = e.blockStateForEnabled();
                }
            }
            return renders;
        }
        public static final int count = Enabled.values().length;
        public int bitmaskForEnabled() {
            return (int) pow(2,this.ordinal());
        }
        public BlockState blockStateForEnabled() {
            return switch (this) {
                case CC -> Registry.ModBlocks.COMPUTER_ADVANCED.getDefaultState();
                case Hex -> HexBlocks.AMETHYST_DUST_BLOCK.getDefaultState();
                case TIS -> Blocks.CASING.get().getDefaultState();
            };
        }
        public int calcRedstonePower(List<List<Integer>> bitmasks) {
            int bitmask = bitmaskForEnabled();
            int acumulator = 0;
            for (List<Integer> sublist : bitmasks) {
                for (int mask : sublist) {
                    if ((mask&bitmask)>0) {
                        acumulator+=1;
                    }
                }
            }
            return (int) ((acumulator/49.0)*15);
        }
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
        int[] newcords = xzToChunk(x,z);
        double height = SimplexNoise.noise( newcords[0],offset+r.nextDouble(),  newcords[1]);
        return height > 0;
    }

    public static boolean isEnabled(Enabled enable, int x, int z, long seed) {
        Random r = new Random();
        r.setSeed(seed);
        double offset = enable.ordinal()*dist_layers;
        int[] newchunk = xzToChunk(x,z);
        double height = SimplexNoise.noise( newchunk[0],offset+r.nextDouble(),  newchunk[1]);
        return height > 0;
    }

    public static int[] xzToChunk(int x, int z) {
        int newx = getSign(x);
        int newz = getSign(z);
        if (newx == -1) {
            newx = clamp(floorDiv(x,16),Integer.MIN_VALUE,-1);
        } else {
            newx = floorDiv(x,16);
        }
        if (newz == -1) {
            newz = clamp(floorDiv(z,16),Integer.MIN_VALUE,-1);
        } else {
            newz = floorDiv(z,16);
        }
        return new int[]{newx,newz};
    }

    public static int getSign(int number) {
        return Integer.compare(number, 0);
    }
}
