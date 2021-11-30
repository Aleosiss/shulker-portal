package com.aleosiss.shulkerportal.mixin.nether_portal_coordinate_logger;

import com.aleosiss.shulkerportal.ShulkerPortal;
import com.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.AreaHelper;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {
	Logger logger = LoggerFactory.getLogger(EntityMixin.class);

	@Shadow public World world;
	@Shadow protected BlockPos lastNetherPortalPosition;
	@Shadow private int id;
	@Shadow private BlockPos blockPos;
	@Shadow private Vec3d pos;

	@Shadow public final double getX() { return 0;};
	@Shadow public final double getY() { return 0;};
	@Shadow public final double getZ() { return 0;};
	@Shadow protected Optional<BlockLocating.Rectangle> getPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder) { return null; };
	@Shadow protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) { return null; };
	@Shadow public EntityDimensions getDimensions(EntityPose pose) { return null; };
	@Shadow public EntityPose getPose() { return null; };
	@Shadow public Vec3d getVelocity() { return null; };
	@Shadow public float getYaw() {return 0;};
	@Shadow public float getPitch() {return 0;};

	@Inject(method = "tickNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getServer()Lnet/minecraft/server/MinecraftServer;"))
	private void netherPortalTickMixin(CallbackInfo ci) {
		if(!ShulkerPortalCarpetSettings.enableShulkerPortalPositioningFix) {
			return;
		}

		boolean isShulker = ShulkerEntity.class.equals(this.getClass());
		if(isShulker) {
			this.blockPos = this.lastNetherPortalPosition;
			this.pos = Vec3d.ofBottomCenter(blockPos);
		}
	}

	@Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
	private void teleportMixin(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
		if(!ShulkerPortalCarpetSettings.enableShulkerPortalDebugging) {
			return;
		}

		List<String> errors = new ArrayList<>();
		boolean isShulker = ShulkerEntity.class.equals(this.getClass());
		if(!isShulker) {
			return;
		}

		boolean goingToOverworldFromEnd = this.world.getRegistryKey() == World.END && destination.getRegistryKey() == World.OVERWORLD;
		boolean goingToEnd = destination.getRegistryKey() == World.END;

		if (!goingToOverworldFromEnd && !goingToEnd) {
			boolean goingToNether = destination.getRegistryKey() == World.NETHER;
			if (this.world.getRegistryKey() != World.NETHER && !goingToNether) {
				cir.setReturnValue(null);
			} else {
				WorldBorder worldBorder = destination.getWorldBorder();
				double d = DimensionType.getCoordinateScaleFactor(this.world.getDimension(), destination.getDimension());// 2548
				BlockPos otherSideBlockPos = worldBorder.clamp(this.getX() * d, this.getY(), this.getZ() * d);
				boolean isInNetherPortal = Blocks.NETHER_PORTAL.equals(this.world.getBlockState(blockPos).getBlock());
				if(!isInNetherPortal && !goingToNether && isShulker) {
					String message = String.format("Shulker[%s] is not teleporting from within a nether portal...? Logging teleport from [%s]!", id, pos);
					errors.add(message);
					logger.error(message);
				}

				TeleportTarget target = this.getPortalRect(destination, otherSideBlockPos, goingToNether, worldBorder).map((rect) -> {
					BlockState blockState = this.world.getBlockState(this.lastNetherPortalPosition);
					Direction.Axis axis;
					Vec3d vec3d;
					if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
						axis = blockState.get(Properties.HORIZONTAL_AXIS);
						BlockLocating.Rectangle rectangle = BlockLocating.getLargestRectangle(this.lastNetherPortalPosition, axis, 21, Direction.Axis.Y, 21,
								(blockPos3) -> this.world.getBlockState(blockPos3) == blockState);
						vec3d = this.positionInPortal(axis, rectangle);
					} else {
						axis = Direction.Axis.X;
						vec3d = new Vec3d(0.5D, 0.0D, 0.0D);
					}

					TeleportTarget teleportTarget = AreaHelper.getNetherTeleportTarget(destination, rect, axis, vec3d, this.getDimensions(this.getPose()), this.getVelocity(), this.getYaw(), this.getPitch());
					if(!goingToNether && isShulker) {
						ShulkerPortal.processShulkerTeleport(this.id, pos, teleportTarget.position, errors);
					}

					return teleportTarget;
				}).orElse(null);
				cir.setReturnValue(target);
			}
		} else {
			BlockPos blockPos;
			if (goingToEnd) {
				blockPos = ServerWorld.END_SPAWN_POS;
			} else {
				blockPos = destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());
			}

			TeleportTarget target = new TeleportTarget(new Vec3d((double)blockPos.getX() + 0.5D, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5D), this.getVelocity(), this.getYaw(), this.getPitch());
			cir.setReturnValue(target);
		}
	}

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void tickMixin(CallbackInfo ci) {
		ShulkerPortal.mixin();
	}
}
