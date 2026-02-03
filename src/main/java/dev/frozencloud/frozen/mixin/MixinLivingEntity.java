package dev.frozencloud.frozen.mixin;

import dev.frozencloud.frozen.Frozen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
    Swing animation speed
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    public void frozen$onGetCurrentSwingDuration(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this != Frozen.getMc().player) return;
        cir.setReturnValue(10);
    }
}