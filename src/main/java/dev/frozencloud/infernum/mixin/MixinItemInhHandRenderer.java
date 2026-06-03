package dev.frozencloud.infernum.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.frozencloud.infernum.Infernum;
import dev.frozencloud.infernum.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
public abstract class MixinItemInhHandRenderer {

//    @Unique
//    private boolean addedStack = false;
//
//    @Inject(method = "shouldInstantlyReplaceVisibleItem", at = @At("HEAD"), cancellable = true)
//    public void frozen$onShouldInstantlyReplaceVisibleItem(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(true);
//    }
//
//    @Inject(method = "renderItem", at = @At(value = "HEAD"))
//    public void frozen$onRenderItemHead(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, CallbackInfo ci) {
//        if (!itemDisplayContext.firstPerson() || itemDisplayContext.leftHand()) return;
//
//        float x = -0.05f;
//        float y = 0.1f;
//        float z = 0f;
//        float scale = 0.5f;
//
//        poseStack.pushPose();
//        poseStack.translate(x, y, z);
//        poseStack.scale(scale, scale, scale);
//        addedStack = true;
//    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/ItemUseAnimation;")
    )
    private void applySwordBlockAnimation(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int j, CallbackInfo ci) {
        // Ensure we are working with the main hand and a sword
        if (interactionHand == InteractionHand.MAIN_HAND && itemStack.getItem() == Items.IRON_SWORD) {
            ChatUtil.INSTANCE.sendModInfo("Got here 2!", "", null);
            Minecraft mc = Infernum.getMc();

            // Check if the player is holding down right-click
            if (mc.options.keyUse.isDown()) {

                // 1. Revert any default swing/equip animations that might mess with the position
                // (Optional: You can use swingProgress here if you want 1.7-style "block hitting")

                // 2. Apply the old 1.8 Block transformation matrix
                poseStack.translate(-0.1F, 0.15F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(-52.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(-10.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-50.0F));
            }
        }
    }
}
