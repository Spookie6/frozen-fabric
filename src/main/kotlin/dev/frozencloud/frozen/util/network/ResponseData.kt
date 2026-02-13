package dev.frozencloud.frozen.util.network

import kotlinx.serialization.Serializable

@Serializable
data class MojangUUIDResponse(
    val name: String,
    val id: String
)