package dev.frozencloud.frozen.util.skyblock

import dev.frozencloud.frozen.Frozen.hma
import dev.frozencloud.frozen.Frozen.mc
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket

object LocationUtil {
    var currentIsland: Island? = Island.Unknown
    val onHypixel: Boolean get() = mc.currentServer?.ip?.contains("hypixel.net") == true

    init {
        hma.subscribeToEventPacket(ClientboundLocationPacket::class.java)

        hma.createHandler(ClientboundLocationPacket::class.java, this::handleLocationPacket)
        hma.createHandler(ClientboundHelloPacket::class.java, this::handleHelloPacket)
        hma.createHandler(ClientboundPartyInfoPacket::class.java, this::handlePartyInfoPacket)
    }

    fun handleLocationPacket(packet: ClientboundLocationPacket) {
        println(packet.toString())
    }

    fun handleHelloPacket(packet: ClientboundHelloPacket) {
        println(packet.toString())
    }

    fun handlePartyInfoPacket(packet: ClientboundPartyInfoPacket) {
        println(packet.toString())
    }
}