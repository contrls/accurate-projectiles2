package com.accurateprojectiles;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point. The real work is done by the mixins; this class only loads the config file.
 */
public final class AccurateProjectilesMod implements ModInitializer {
    public static final String MOD_ID = "accurate_projectiles";
    public static final Logger LOGGER = LoggerFactory.getLogger("Accurate Projectiles");

    @Override
    public void onInitialize() {
        AccurateProjectilesConfig.load();
        AccurateProjectilesConfig config = AccurateProjectilesConfig.get();
        LOGGER.info(
            "Accurate Projectiles loaded (pearls: {}, wind charges: {}, dispensers: {}, breeze charges: {})",
            config.removePearlSpread,
            config.removeWindChargeSpread,
            config.affectDispensers,
            config.affectBreezeWindCharges
        );
    }
}
