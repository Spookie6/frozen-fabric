package dev.frozencloud.infernum.mixin;

import dev.frozencloud.infernum.util.skyblock.LocationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void frozen$onSetScreen(Screen screen, CallbackInfo ci) {
        if (LocationUtil.INSTANCE.getOnHypixel() && screen instanceof LevelLoadingScreen) {
            ci.cancel();
            setScreen(null);
        }
    }
}
