package net.walksanator.world_split.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.walksanator.world_split.SimplexNoise;
import net.walksanator.world_split.WorldSection;
import net.walksanator.world_split.WorldSplit;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.lang.Math.floor;
import static net.walksanator.world_split.WorldSection.dist_layers;

public class DisplayerBE extends BlockEntity {
    public List<List<Integer>> enable_map;
    public DisplayerBE(BlockPos pos, BlockState state) {
        super(WorldSplit.DISPLAYER_ENTITY, pos, state);
        this.enable_map = new ArrayList<>(new ArrayList<>());
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtList lines = nbt.getList("map",NbtList.INT_ARRAY_TYPE);
        enable_map.clear();
        for (NbtElement ia: lines) {
            NbtIntArray remap = (NbtIntArray) ia;
            List<Integer> object_line = new ArrayList<>();
            for (int i: remap.getIntArray()) {
                object_line.add(i);
            }
            enable_map.add(object_line);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        BlockPos pos = getPos();
        int mode = 7;
        int chop = mode/2;
        long seed = ((ServerWorld) Objects.requireNonNull(getWorld())).getSeed();
        List<NbtIntArray> map = new ArrayList<>();
        for (int z = 0; z < mode; z++) {
            List<Integer> ia = new ArrayList<>();
            for (int x = 0; x < mode; x++) {
                int acu = 0;
                for (WorldSection.Enabled e : WorldSection.Enabled.values()) {
                    if (WorldSection.isEnabled(e,pos.add((x-chop)*16,0,(z-chop)*16),seed)) {
                        WorldSplit.LOGGER.info("nbt for %s, %s is enabled, (%d,%d)".formatted(this.getPos(),e,x-chop,z-chop));
                        acu += 2 ^ (e.ordinal() + 1);
                    }
                }
                ia.add(acu);
            }
            map.add(new NbtIntArray(ia));
        }
        NbtList list = new NbtList();
        list.addAll(map);
        nbt.put("map",list);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

}
