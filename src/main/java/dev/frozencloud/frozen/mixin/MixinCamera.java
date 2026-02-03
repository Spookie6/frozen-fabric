package dev.frozencloud.frozen.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class MixinCamera {

    @Unique
    private boolean instantSneak$wasSneaking = false;

    @Shadow
    private Entity entity;

    @Shadow
    private float eyeHeightOld;

    @Shadow
    private float eyeHeight;

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Camera;eyeHeight:F", opcode = Opcodes.PUTFIELD))
    public void frozen$onTick(CallbackInfo ci) {
        if (entity instanceof Player) {
            if (entity.getPose() == Pose.CROUCHING) {
                instantSneak$wasSneaking = true;
                eyeHeightOld = eyeHeight = entity.getEyeHeight();
            } else if (instantSneak$wasSneaking) {
                instantSneak$wasSneaking = false;
                eyeHeightOld = eyeHeight = entity.getEyeHeight();
            }
        }
    }
}
