package io.aleosiss.shulkerportal.mixin.nether_portal_coordinate_logger;

import com.llamalad7.mixinextras.sugar.Local;
import io.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings;
import io.aleosiss.shulkerportal.service.ShulkerPortalLoggingService;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    @Unique
    ShulkerPortalLoggingService shulkerPortal = ShulkerPortalLoggingService.INSTANCE;

    @Inject(method = "createTeleportTarget", at = @At("RETURN"))
    private void teleportMixin(ServerWorld origin, Entity entity, BlockPos blockPos, CallbackInfoReturnable<TeleportTarget> cir, @Local(name = "serverWorld") ServerWorld destination) {
        if(!ShulkerPortalCarpetSettings.enableShulkerPortalDebugging) {
            return;
        }

        List<String> errors = new ArrayList<>();
        boolean isShulker = ShulkerEntity.class.equals(entity.getClass());
        if(!isShulker) {
            return;
        }

        boolean goingToOverworldFromEnd = entity.getWorld().getRegistryKey() == World.END && destination.getRegistryKey() == World.OVERWORLD;
        boolean goingToEnd = destination.getRegistryKey() == World.END;

        if (!goingToOverworldFromEnd && !goingToEnd) {
            boolean goingToNether = destination.getRegistryKey() == World.NETHER;
             if(!goingToNether) {
                 shulkerPortal.processShulkerTeleport(entity.getId(), entity.getPos(), cir.getReturnValue().pos(), errors);
             }
        }
    }
}
