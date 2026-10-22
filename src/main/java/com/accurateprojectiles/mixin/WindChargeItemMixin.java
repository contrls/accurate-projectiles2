package com.accurateprojectiles.mixin;

import com.accurateprojectiles.AccurateProjectilesConfig;
import net.minecraft.item.WindChargeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Dispenser-fired wind charges. A dispenser does not go through the player throw path: the wind charge item
 * builds the charge itself in createEntity and randomises its direction with three
 * Random#nextTriangular(centre, deviation) calls. This sets the deviation of each call to 0 when
 * removeWindChargeSpread and affectDispensers are both on. createEntity is only used by dispensers.
 *
 * This mixin lives in its own optional mixin config (required: false), so if it ever fails to apply the
 * game still starts and only dispenser wind charges keep their vanilla spread.
 */
@Mixin(WindChargeItem.class)
public abstract class WindChargeItemMixin {
    @ModifyArg(
        method = "createEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/random/Random;nextTriangular(DD)D"
        ),
        index = 1
    )
    private double accurateprojectiles$exactDispenserDirection(double deviation) {
        AccurateProjectilesConfig config = AccurateProjectilesConfig.get();
        return config.removeWindChargeSpread && config.affectDispensers ? 0.0D : deviation;
    }
}
