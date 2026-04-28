package dev.frozencloud.infernum.util

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.monster.Giant
import net.minecraft.world.phys.Vec3
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun calculateViewVector(pitch: Double, yaw: Double): Vec3 {
    val yawRadians = Math.toRadians(yaw)
    val pitchRadians = Math.toRadians(-pitch)

    val j = cos(pitchRadians)
    val k = sin(pitchRadians)
    val l = cos(yawRadians)
    val m = sin(yawRadians)
    return Vec3((k * l), (-m), (j * l))
}

fun Vec3.getLookYawTo(other: Vec3): Float {
    val dx = other.x - this.x
    val dz = other.z - this.z

    val yawRad = atan2(-dx, dz)
    val yawDeg = yawRad * (180.0 / PI)

    return yawDeg.toFloat()
}

fun Vec3.getPitch(): Double {
    val hori = sqrt(this.x * this.x + this.z * this.z)
    return Math.toDegrees(atan2(-this.y, hori))
}

fun Vec3.getYaw(): Double {
    val yawRad = atan2(-this.x, this.z)
    return yawRad * (180.0 / PI)
}

inline val Entity.yaw: Double get() = this.lookAngle.getYaw()
inline val Entity.pitch: Double get() = this.lookAngle.getPitch()

fun Number.toFixed(decimals: Int): String {
    return "%.${decimals}f".format(this)
}

inline val Giant.cratePos: Vec3 get() {
    val x = this.position().x + 0.5 + (4.2 * cos(Math.toRadians((yaw + 130))));
    val z = this.position().z + 0.5 + (4.2 * sin(Math.toRadians((yaw + 130))));
    return Vec3(x, 75.0, z);
}

operator fun Vec3.unaryMinus(): Vec3 = Vec3(-x, -y, -z)