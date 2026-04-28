package dev.frozencloud.infernum.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
    Stops pot effect icons in hud
 */
@Mixin(MobEffectInstance.class)
public class MixinMobEffectInstance {
    @Inject(method = "showIcon", at = @At("HEAD"), cancellable = true)
    public void frozen$onShowIcon(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
