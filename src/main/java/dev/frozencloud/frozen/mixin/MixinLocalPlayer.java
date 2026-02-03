package dev.frozencloud.frozen.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

/*
    Auto sprint
 */
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;sprint()Z"))
    public boolean frozen$onTickMovement(boolean original) {
        return true;
    }
}