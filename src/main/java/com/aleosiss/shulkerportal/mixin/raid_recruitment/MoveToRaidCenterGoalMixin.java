package com.aleosiss.shulkerportal.mixin.raid_recruitment;

import com.aleosiss.shulkerportal.carpet.ShulkerPortalCarpetSettings;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.raid.RaiderEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// credits to Fallen_Breath for this code
@Mixin(MoveToRaidCenterGoal.class)
public class MoveToRaidCenterGoalMixin<T extends RaiderEntity> {
    @Shadow @Final private T actor;

    @Redirect(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/goal/MoveToRaidCenterGoal;nextFreeRaiderCheckAge:I", ordinal = 0
            )
    )
    private int bringBackTo117(MoveToRaidCenterGoal<T> instance)
    {
        boolean conditionIn21w39a = this.actor.age % 20 == this.actor.getId() % 2;
        boolean conditionIn117 = this.actor.age % 20 == 0;
        if (ShulkerPortalCarpetSettings.enable117RaiderDiscoveryMechanic && conditionIn117)
        {
            // anyValue > MIN_VALUE == true
            return Integer.MIN_VALUE;
        }
        else
        {
            // anyValue > MAX_VALUE == false
            return Integer.MAX_VALUE;
        }
    }
}
