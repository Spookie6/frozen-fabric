package dev.frozencloud.infernum.mixin;

import dev.frozencloud.infernum.events.impl.GuiEvent;
import dev.frozencloud.infernum.events.impl.ScreenEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen {
    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void infernum$onDrawSlotPre(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        new GuiEvent.RenderSlotPre(guiGraphics, slot, (AbstractContainerScreen<?>) (Object) this).post();
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void infernum$onDrawSlotPost(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        new GuiEvent.RenderSlotPost(guiGraphics, slot, (AbstractContainerScreen<?>) (Object) this).post();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void infernum$onMouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (new ScreenEvent.MouseClicked(mouseButtonEvent.button(), (AbstractContainerScreen<?>) (Object) this).post()) cir.cancel();
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void infernum$onSlotClicked(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        if (new ScreenEvent.SlotClicked((AbstractContainerScreen<?>) (Object) this, i, j).post()) ci.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private  void infernum$onKeyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (new ScreenEvent.KeyTyped(keyEvent.key()).post()) cir.cancel();
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void infernum$onInit(CallbackInfo ci) {
        AbstractContainerScreen<?> a = (AbstractContainerScreen<?>) (Object) this;
        new ScreenEvent.Open(a).post();
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void infernum$onClose(CallbackInfo ci) {
        new ScreenEvent.Close().post();
    }
}
