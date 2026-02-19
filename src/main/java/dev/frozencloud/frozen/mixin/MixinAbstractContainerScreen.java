package dev.frozencloud.frozen.mixin;

import dev.frozencloud.frozen.events.impl.GuiSlotRenderEvent;
import dev.frozencloud.frozen.events.impl.ScreenEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen {
    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void frozen$onDrawSlotPre(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        new GuiSlotRenderEvent.Pre(guiGraphics, slot).post();
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void frozen$onDrawSlotPost(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        new GuiSlotRenderEvent.Post(guiGraphics, slot).post();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void frozen$onMouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (new ScreenEvent.MouseClicked(mouseButtonEvent.button()).post()) cir.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private  void frozen$onKeyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (new ScreenEvent.KeyTyped(keyEvent.key()).post()) cir.cancel();
    }
}
