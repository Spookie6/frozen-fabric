package dev.frozencloud.infernum.mixin.accessors;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientLevel.class)
public interface AccessorClientLevel {
    @Accessor("entityStorage")
    public TransientEntitySectionManager<Entity> entityStorage();
}
