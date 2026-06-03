package dev.frozencloud.infernum.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.frozencloud.infernum.features.impl.rendering.PlayerScale;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class MixinAvatarRenderer {
    @Inject(method = "scale(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("TAIL"))
    private void onScale(AvatarRenderState state, PoseStack poseStack, CallbackInfo ci) {
        if (!PlayerScale.INSTANCE.getEnabled()) return;
        float xMult = PlayerScale.INSTANCE.getPlayerXMult();
        float yMult = PlayerScale.INSTANCE.getPlayerYMult();
        float zMult = PlayerScale.INSTANCE.getPlayerZMult();

        poseStack.scale(xMult, yMult, zMult);
    }
}
