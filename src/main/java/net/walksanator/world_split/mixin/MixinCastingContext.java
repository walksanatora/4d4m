package net.walksanator.world_split.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.walksanator.world_split.WorldSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CastingContext.class)
public abstract class MixinCastingContext {

	@Shadow
	public abstract ServerWorld getWorld();

	@Inject(at = @At("RETURN"), method = "isVecInWorld(Lnet/minecraft/util/math/Vec3d;)Z", cancellable = true)
	private void worldsplit$isEntityInWorld(Vec3d vec, CallbackInfoReturnable<Boolean> cir) {
		boolean enabled = WorldSection.isEnabled(WorldSection.Enabled.Hex,(int)vec.x,(int)vec.y,(int)vec.z,this.getWorld());
		cir.setReturnValue(cir.getReturnValue() & enabled);
	}
	@Inject(at=@At("RETURN"), method = "isEntityInRange(Lnet/minecraft/entity/Entity;)Z",cancellable = true)
	private void worldsplit$isEntityInRange(Entity entity, CallbackInfoReturnable<Boolean> cir) { //have to mixin seperately because *technically* circles allow in range but not in world
		cir.setReturnValue(cir.getReturnValue() & WorldSection.isEnabled(WorldSection.Enabled.Hex,entity.getBlockPos(),this.getWorld()));
	}
	@Inject(at=@At("RETURN"), method = "isVecInRange(Lnet/minecraft/util/math/Vec3d;)Z", cancellable = true)
	private void worldsplit$isVecInRange(Vec3d vec, CallbackInfoReturnable<Boolean> cir) {
		boolean enabled = WorldSection.isEnabled(WorldSection.Enabled.Hex,(int)vec.x,(int)vec.y,(int)vec.z,this.getWorld());
		cir.setReturnValue(cir.getReturnValue() & enabled);
	}
}
