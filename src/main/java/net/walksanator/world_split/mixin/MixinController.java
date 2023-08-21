package net.walksanator.world_split.mixin;

import li.cil.tis3d.common.block.entity.ControllerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.walksanator.world_split.WorldSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ControllerBlockEntity.class)
public abstract class MixinController {

    @Inject(at = @At("HEAD"), method = "serverTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lli/cil/tis3d/common/block/entity/ControllerBlockEntity;)V")
    private static void tick(World level, BlockPos pos, BlockState state, ControllerBlockEntity blockEntity, CallbackInfo ci) {
        if (!level.isClient) {
            boolean isEnabled = WorldSection.isEnabled(WorldSection.Enabled.TIS, pos,(ServerWorld) level);
            if (!isEnabled) {
                blockEntity.haltAndCatchFire();
            }
        }
    }
}
