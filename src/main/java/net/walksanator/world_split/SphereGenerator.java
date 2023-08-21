package net.walksanator.world_split;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.List;
import java.util.ArrayList;

public class SphereGenerator {

    public static List<int[]> generate(double sphereRadius) {
        int edge_size = (int) Math.ceil(sphereRadius);
        List<int[]> output = new ArrayList<>();
        for (BlockPos point : BlockPos.iterateOutwards(new BlockPos(0,0,0),edge_size,edge_size,edge_size)) {
            if (point.isWithinDistance(Vec3i.ZERO,sphereRadius)) {
                output.add(new int[]{point.getX(), point.getY(), point.getZ()});
            }
        }
        return output;
    }

    // You can choose to implement the remove() method if required
}
