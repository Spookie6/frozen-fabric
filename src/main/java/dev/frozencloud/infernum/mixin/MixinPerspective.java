package dev.frozencloud.infernum.mixin;

import dev.frozencloud.infernum.features.impl.misc.NoSelfie;
import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
    No selfie cam
 */
@Mixin(CameraType.class)
public class MixinPerspective {
	@Inject(method = "cycle", at = @At("HEAD"), cancellable = true)
	private void frozen$onNext(CallbackInfoReturnable<CameraType> cir) {
        if (!NoSelfie.INSTANCE.getEnabled()) return;
        if (((Object) this) == CameraType.THIRD_PERSON_BACK) cir.setReturnValue(CameraType.FIRST_PERSON);
	}
}