package dev.frozencloud.infernum.mixin;

import dev.frozencloud.infernum.features.impl.misc.NoScroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
    Prevent hotbar scrolling
 */
@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    public void frozen$onScroll(long l, double d, double e, CallbackInfo ci) {
        if (minecraft.screen == null && NoScroll.INSTANCE.getEnabled()) ci.cancel();
    }
}
