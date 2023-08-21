package net.walksanator.world_split.mixin;

import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.walksanator.world_split.WorldSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerComputer.class)
public abstract class MixinServerComputer {

    @Shadow private BlockPos position;

    @Shadow private World level;

    @Shadow(remap = false) public abstract void shutdown();

    @Inject(at = @At("HEAD"), method = "update()V", remap = false)
    public void tick(CallbackInfo ci) {
        if (!this.level.isClient) {
            boolean isEnabled = WorldSection.isEnabled(WorldSection.Enabled.CC, this.position,(ServerWorld) this.level);
            if (!isEnabled) {
                this.shutdown();
            }
        }
    }
}
