package dev.frozencloud.frozen.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
    Prevents trying to make path blocks
 */
@Mixin(ShovelItem.class)
public class MixinShovelItem {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void frozen$onUseOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        BlockState blockState = useOnContext.getLevel().getBlockState(useOnContext.getClickedPos());

        if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.DIRT) || blockState.is(Blocks.DIRT_PATH)) cir.setReturnValue(InteractionResult.FAIL);
    }
}

