package dev.frozencloud.infernum.util

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

val Vec3.blockPos: BlockPos get() = BlockPos(x.toInt(), y.toInt(), z.toInt())
val Vec3.lenghSquared: Double get() = x * x + y * y + z * z

fun Vec3.div(s: Double): Vec3 = Vec3(this.x / s, this.y / s, this.z / s)
fun Vec3.mult(s: Double): Vec3 = Vec3(this.x * s, this.y * s, this.z * s)

