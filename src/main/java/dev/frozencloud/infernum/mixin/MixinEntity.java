package dev.frozencloud.infernum.mixin;

import dev.frozencloud.infernum.util.render.EntityOutlineRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public abstract boolean isCurrentlyGlowing();

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void frozen$onIsCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(EntityOutlineRenderer.INSTANCE.shouldGlow((Entity) (Object) this));
    }

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void frozen$onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        if (this.isCurrentlyGlowing()) {
            cir.setReturnValue(EntityOutlineRenderer.INSTANCE.getGlowColor((Entity) (Object) this));
            cir.cancel();
        }
    }
}
