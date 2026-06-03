package dev.frozencloud.infernum.mixin.accessors;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AccessorAbstractContainerScreen {
    @Accessor("hoveredSlot")
    Slot infernum_getHoveredSlot();

    @Accessor("topPos")
    int infernum_getTopPos();

    @Accessor("leftPos")
    int infernum_getLeftPos();
}
