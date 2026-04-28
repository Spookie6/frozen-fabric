package dev.frozencloud.infernum.mixin;

import dev.frozencloud.infernum.events.impl.ScreenEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {
    @Inject(method = "render", at = @At("HEAD"))
    private void frozen$onRenderPre(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        new ScreenEvent.ScreenRenderEventPre(guiGraphics, i, j, f).post();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void frozen$onRenderPost(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        new ScreenEvent.ScreenRenderEventPost(guiGraphics, i, j, f).post();
    }
}
