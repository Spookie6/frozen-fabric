package dev.frozencloud.frozen.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen {

    @Shadow
    private TextFieldHelper signField;

    @Unique
    private Minecraft mc = Minecraft.getInstance();

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void frozen$onKeyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (keyEvent.key() == GLFW.GLFW_KEY_ENTER) {
            ((AbstractSignEditScreen) (Object) this).onClose();
            cir.cancel();
        }

        if (keyEvent.key() == GLFW.GLFW_KEY_C && keyEvent.hasControlDown()) {
            signField.insertText(mc.keyboardHandler.getClipboard());
            cir.cancel();
        }
    }
}
