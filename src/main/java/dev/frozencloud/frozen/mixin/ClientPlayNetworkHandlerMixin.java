package dev.frozencloud.frozen.mixin;

import dev.frozencloud.frozen.events.impl.PacketEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onScoreboardScoreUpdate", at = @At("HEAD"), cancellable = true)
    public void frozen$onScoreboardUpdate(ScoreboardScoreUpdateS2CPacket packet, CallbackInfo ci) {
        if (new PacketEvent.Received(packet).post()) ci.cancel();
    }

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void frozen$onChatMessage (ChatMessageS2CPacket packet, CallbackInfo ci) {
        if (new PacketEvent.ChatPacketReceived(packet).post()) ci.cancel();
    }

//
//    @Inject(method = "", at = @At("HEAD"), cancellable = true)
//    public void frozen$ () {
//
//    }
//
//    @Inject(method = "", at = @At("HEAD"), cancellable = true)
//    public void frozen$ () {
//
//    }
//
//    @Inject(method = "", at = @At("HEAD"), cancellable = true)
//    public void frozen$ () {
//
//    }

}
