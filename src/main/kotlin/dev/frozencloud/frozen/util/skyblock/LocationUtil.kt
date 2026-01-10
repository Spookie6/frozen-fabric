package dev.frozencloud.frozen.util.skyblock

import dev.frozencloud.frozen.Frozen.hma
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket

object LocationUtil {
    var currentIsland: Island? = Island.Unknown
    var onHypixel: Boolean = false

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

//    @EventHandler
//    fun onClientJoinServer(event: ConnectionEvent.ServerConnectEvent) {
//        val ip = event.handler.connection.address.toString()
//        onHypixel = ip.contains("hypixel.net")
//    }
}