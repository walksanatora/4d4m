package net.walksanator.world_split;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static java.lang.Math.*;

public class WorldSection {
    public static double deadzone = 10.0;
    public static List<int[]> offsets = SphereGenerator.generate(10.0);
    public enum Enabled {
        CC, Hex, TIS
    }

    public static boolean isEnabled(Enabled enable, BlockPos pos, ServerWorld level) {
        return isEnabled(enable, pos.getX(), pos.getY(), pos.getZ(),level);
    }



    public static boolean isEnabled(Enabled enable, int x, int y, int z, ServerWorld level) {
        if (abs(x) == abs(z) ) {
            return true;
        }
        BlockPos pos =new BlockPos(x,y,z);
        for (int[] pos2 : offsets) {
            if (level.getBlockState(pos.add(pos2[0],pos2[1],pos2[2])).isOf(WorldSplit.NULLIFIER)) {
                return true;
            }
        }

        double distance = sqrt(pow(x,2) + pow(z,2));
        if (distance < deadzone){return true;}

        double angle = Math.toDegrees(Math.atan2(z, x));
        if (angle < 0) {
            angle += 360;
        }

        if (45 < angle && angle < 135) {
            return false; // North
        } else if (135 < angle && angle < 225) {
            return enable == Enabled.CC;
        } else if (225 < angle && angle < 315) {
            return enable == Enabled.Hex;
        } else {
            return enable == Enabled.TIS;
        }
    }

}
