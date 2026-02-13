package dev.frozencloud.frozen.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import dev.frozencloud.frozen.events.impl.InputEvent;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public class MixinKeyMapping {
    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void frozen$onClick(InputConstants.Key key, CallbackInfo ci) {
        if (new InputEvent(key).post()) ci.cancel();
    }
}
