package dev.frozencloud.frozencloud.mixin;

import net.minecraft.client.option.Perspective;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Perspective.class)
public class PerspectiveMixin {
	@Inject(method = "next", at = @At("HEAD"), cancellable = true)
	private void frozen$onNext(CallbackInfoReturnable<Perspective> cir) {
        if (((Object) this) == Perspective.THIRD_PERSON_BACK) cir.setReturnValue(Perspective.FIRST_PERSON);
	}
}