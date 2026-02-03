package dev.frozencloud.frozen.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class MixinClientLevel {
    @Inject(method = "addBreakingBlockEffect", at = @At("HEAD"), cancellable = true)
    public void frozen$onAddBlockBreakParticle(BlockPos blockPos, Direction direction, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "doAddParticle", at = @At("HEAD"), cancellable = true)
    public void frozen$onAddParticle(ParticleOptions particleOptions, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i, CallbackInfo ci) {
        if (particleOptions.getType() == ParticleTypes.POOF || particleOptions.getType() == ParticleTypes.EXPLOSION_EMITTER || particleOptions.getType() == ParticleTypes.HEART || particleOptions.getType() == ParticleTypes.EXPLOSION || particleOptions.getType() == ParticleTypes.EFFECT) ci.cancel();
    }
}
