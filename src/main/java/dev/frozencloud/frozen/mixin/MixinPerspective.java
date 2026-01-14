package dev.frozencloud.frozen.mixin;

import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CameraType.class)
public class MixinPerspective {
	@Inject(method = "cycle", at = @At("HEAD"), cancellable = true)
	private void frozen$onNext(CallbackInfoReturnable<CameraType> cir) {
        if (((Object) this) == CameraType.THIRD_PERSON_BACK) cir.setReturnValue(CameraType.FIRST_PERSON);
	}
}