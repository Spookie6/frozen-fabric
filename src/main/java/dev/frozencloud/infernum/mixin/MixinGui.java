package dev.frozencloud.infernum.mixin;

import dev.frozencloud.infernum.features.impl.rendering.VanillaHud;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui {
    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void infernum$onRenderHearts(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl, CallbackInfo ci) {
        if (VanillaHud.INSTANCE.getEnabled() && VanillaHud.INSTANCE.getNoHearts()) ci.cancel();
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void infernum$onRenderFood(GuiGraphics guiGraphics, Player player, int i, int j, CallbackInfo ci) {
        if (VanillaHud.INSTANCE.getEnabled() && VanillaHud.INSTANCE.getNoFood()) ci.cancel();
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void infernum$onRenderArmor(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, CallbackInfo ci) {
        if (VanillaHud.INSTANCE.getEnabled() && VanillaHud.INSTANCE.getNoArmor()) ci.cancel();
    }
}
