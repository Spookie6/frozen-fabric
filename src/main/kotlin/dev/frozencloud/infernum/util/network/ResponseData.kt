package dev.frozencloud.infernum.util.network

import kotlinx.serialization.Serializable

@Serializable
data class MojangUUIDResponse(
    val name: String,
    val id: String
)