package dev.frozencloud.infernum.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.frozencloud.infernum.features.impl.general.AutoSprint;
import dev.frozencloud.infernum.util.skyblock.LocationUtil;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/*
    Auto sprint
 */
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    @Shadow
    public ClientInput input;

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;sprint()Z"))
    public boolean frozen$onTickMovement(boolean original) {
        if (AutoSprint.INSTANCE.getEnabled()) {
            if (AutoSprint.INSTANCE.getSkyblockOnly()) return LocationUtil.INSTANCE.getOnSkyblock();
            else return true;
        }
        return original;
    }
}