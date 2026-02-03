package dev.frozencloud.frozen.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
    Held item translation, rotation, scaling
 */
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemRenderer {

    @Unique
    private boolean addedStack = false;

    @Inject(method = "shouldInstantlyReplaceVisibleItem", at = @At("HEAD"), cancellable = true)
    public void frozen$onShouldInstantlyReplaceVisibleItem(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "renderItem", at = @At(value = "HEAD"))
    public void frozen$onRenderItemHead(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, CallbackInfo ci) {
        if (!itemDisplayContext.firstPerson() || itemDisplayContext.leftHand()) return;

        float x = -0.05f;
        float y = 0.1f;
        float z = 0f;
        float scale = 0.5f;

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(scale, scale, scale);
        addedStack = true;
    }

    @Inject(method = "renderItem", at = @At(value = "TAIL"))
    public void frozen$onRenderItemTail(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, CallbackInfo ci) {
        if (addedStack) {
            poseStack.popPose();
            addedStack = false;
        }
    }
}
