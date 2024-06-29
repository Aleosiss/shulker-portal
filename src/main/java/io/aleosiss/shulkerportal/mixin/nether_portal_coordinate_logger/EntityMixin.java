package io.aleosiss.shulkerportal.mixin.nether_portal_coordinate_logger;

import io.aleosiss.shulkerportal.ShulkerPortal;
import io.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.PortalManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow private BlockPos blockPos;
	@Shadow private Vec3d pos;
	@Shadow public PortalManager portalManager;


	@SuppressWarnings({"ConstantConditions", "EqualsBetweenInconvertibleTypes"})
	@Inject(method = "tickPortalTeleportation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;resetPortalCooldown()V"))
	private void netherPortalTickMixin(CallbackInfo ci) {
		if(!ShulkerPortalCarpetSettings.enableShulkerPortalPositioningFix) {
			return;
		}

		boolean isShulker = ShulkerEntity.class.equals(this.getClass());
		if(isShulker) {
			// block pos is sometimes where the shulker was picked up from. Make sure it's from inside the portal
			this.blockPos = portalManager.getPortalPos();
			this.pos = Vec3d.ofBottomCenter(blockPos);
		}
	}

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void tickMixin(CallbackInfo ci) {
		ShulkerPortal.mixin();
	}
}
