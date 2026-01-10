package dev.frozencloud.frozen.mixin;

import net.minecraft.entity.boss.BossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BossBar.class)
public class BossBarMixin {
    @Inject(method = "shouldThickenFog", at = @At("HEAD"), cancellable = true)
    public void frozen$onShouldThickenFog(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}