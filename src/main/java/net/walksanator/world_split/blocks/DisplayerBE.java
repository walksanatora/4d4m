package net.walksanator.world_split.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.*;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.walksanator.world_split.WorldSection;
import net.walksanator.world_split.WorldSplit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.math.IntMath.pow;
import static net.minecraft.util.math.MathHelper.clamp;

public class DisplayerBE extends BlockEntity {
    public List<List<Integer>> enable_map;
    public int visible_layer = -1;
    public DisplayerBE(BlockPos pos, BlockState state) {
        super(WorldSplit.DISPLAYER_ENTITY, pos, state);
        this.enable_map = new ArrayList<>(new ArrayList<>());
    }

    public void onClick() {
        visible_layer += 1;
        if (visible_layer > WorldSection.Enabled.count-1) {
            visible_layer = -1;
        }
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
        visible_layer = clamp(nbt.getInt("index"),-1, WorldSection.Enabled.count-1);
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
                        //WorldSplit.LOGGER.info("allowed mod at (%d,%d) %s %d".formatted(x-chop,z-chop,e,pow(2,e.ordinal())));
                        acu += pow(2,e.ordinal());
                    }
                }
                ia.add(acu);
            }
            map.add(new NbtIntArray(ia));
        }
        NbtList list = new NbtList();
        list.addAll(map);
        nbt.put("map",list);
        nbt.put("index", NbtInt.of(this.visible_layer));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

}
