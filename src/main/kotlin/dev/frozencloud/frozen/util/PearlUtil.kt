package dev.frozencloud.frozen.util

import net.minecraft.world.phys.Vec3

object PearlUtil {
    const val GRAVITY = 0.03
    const val DRAG = 0.99
    const val TERMINAL_VELOCITY = 2.97
    const val BASE_VELOCITY = 1.5

    const val MAX_TICKS = 100

    val MIN_SKY_THETA = Math.toRadians(34.0)
    val MAX_FLAT_THETA = Math.toRadians(32.0)

    fun getPearlThrow(pos: Vec3, lookingVec: Vec3): PearlThrow {
        val velocityVec = lookingVec.scale(BASE_VELOCITY)
        return PearlThrow(pos, velocityVec)
    }

    fun PearlThrow.tick() {
        pos = pos.add(velocity)
        val newY = velocity.y - GRAVITY
        velocity = velocity.scale(DRAG)
        pos = Vec3(pos.x, newY, pos.z)
        ticks++
    }

    data class PearlThrow(
        var pos: Vec3,
        var velocity: Vec3,
        var collisionPos: Vec3? = null,
        var ticks: Int = 0
    )

    data class SimulationContext(
        val isSkyPearl: Boolean = false
    )
}