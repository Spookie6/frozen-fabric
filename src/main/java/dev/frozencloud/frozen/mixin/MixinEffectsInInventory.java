package dev.frozencloud.frozen.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
    Stops rendering of icons in gui
 */
@Mixin(EffectsInInventory.class)
public class MixinEffectsInInventory {
    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    public void frozen$onRenderEffects(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        ci.cancel();
    }
}
