package dev.frozencloud.infernum.util.network

import dev.frozencloud.infernum.Infernum.mc
import java.util.UUID

object AccountInfo {
    var username: String
        private set

    var uuid: UUID
        private set

    init {
        username = mc.player?.name.toString()
        uuid = mc.player?.uuid ?: UUID.fromString("00000000-0000-0000-0000-000000000000")
    }
}