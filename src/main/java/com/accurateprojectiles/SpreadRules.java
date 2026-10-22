package com.accurateprojectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;

/**
 * Decides whether a projectile that is being launched should lose its random spread.
 * It lives outside the mixin classes so the mixins stay tiny and this logic is easy to read.
 *
 * The projectile is recognised by its entity type (not by instanceof), so the mod does not
 * depend on where the wind charge classes live. Every other projectile type (arrows,
 * snowballs, tridents, potions, ...) falls through to "false" and stays completely vanilla.
 */
public final class SpreadRules {
    private SpreadRules() {
    }

    /**
     * @param projectile the projectile whose launch velocity is being set
     * @return true if its random spread (inaccuracy) must be forced to zero
     */
    public static boolean shouldRemoveSpread(ProjectileEntity projectile) {
        AccurateProjectilesConfig config = AccurateProjectilesConfig.get();
        EntityType<?> type = projectile.getType();

        if (type == EntityType.ENDER_PEARL) {
            return config.removePearlSpread && (config.affectDispensers || !isFiredByDispenser(projectile));
        }
        if (type == EntityType.WIND_CHARGE) {
            return config.removeWindChargeSpread && (config.affectDispensers || !isFiredByDispenser(projectile));
        }
        if (type == EntityType.BREEZE_WIND_CHARGE) {
            return config.removeWindChargeSpread && config.affectBreezeWindCharges;
        }
        return false;
    }

    /**
     * A projectile launched by a player (or any other entity) already has its owner set when its
     * velocity is applied. One spawned by a dispenser has no owner.
     */
    private static boolean isFiredByDispenser(ProjectileEntity projectile) {
        return projectile.getOwner() == null;
    }
}
