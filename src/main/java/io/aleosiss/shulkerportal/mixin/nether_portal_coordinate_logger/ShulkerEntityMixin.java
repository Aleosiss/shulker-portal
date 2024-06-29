package io.aleosiss.shulkerportal.mixin.nether_portal_coordinate_logger;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerEntity.class)
public class ShulkerEntityMixin extends GolemEntity {

    public ShulkerEntityMixin(EntityType<? extends ShulkerEntity> entityType, World world) {
        super(entityType, world);
    }
}
