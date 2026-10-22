package com.accurateprojectiles.mixin;

import com.accurateprojectiles.SpreadRules;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Removes the random spread of ender pearls and wind charges.
 *
 * Every launch ends up in ProjectileEntity#setVelocity(double, double, double, float, float)
 * (Mojang name: Projectile#shoot). The player throw path, setVelocity(Entity, pitch, yaw, roll, speed,
 * divergence) (Mojang: shootFromRotation), calls it, and other code such as the breeze calls it directly,
 * so hooking this one method covers both. The random offset is scaled by the last argument (the
 * inaccuracy), so replacing that argument with 0 removes the randomness and nothing else.
 */
@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    /**
     * Runs at the very start of setVelocity(double, double, double, float, float) and replaces the
     * inaccuracy argument (the second float; the first float is the speed) with 0 when the projectile
     * is one we should make exact. Speed, gravity, drag and the shooter's own movement are untouched.
     */
    @ModifyVariable(
        method = "setVelocity(DDDFF)V",
        at = @At("HEAD"),
        ordinal = 1,
        argsOnly = true
    )
    private float accurateprojectiles$removeSpread(float inaccuracy) {
        ProjectileEntity self = (ProjectileEntity) (Object) this;
        return SpreadRules.shouldRemoveSpread(self) ? 0.0F : inaccuracy;
    }
}
