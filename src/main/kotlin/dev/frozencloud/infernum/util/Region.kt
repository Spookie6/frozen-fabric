package dev.frozencloud.infernum.util

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

data class Region(
    val min: Vec3,
    val max: Vec3
) {
    companion object {
        fun fromCorners(a: Vec3, b: Vec3): Region {
            return Region(
                Vec3(
                    minOf(a.x, b.x),
                    minOf(a.y, b.y),
                    minOf(a.z, b.z)
                ),
                Vec3(
                    maxOf(a.x, b.x),
                    maxOf(a.y, b.y),
                    maxOf(a.z, b.z)
                )
            )
        }
    }

    fun containsEntity(entity: Entity): Boolean {
        val bb = entity.boundingBox

        return bb.maxX >= this.min.x &&
                bb.minX <= this.max.x &&
                bb.maxZ >= this.min.z &&
                bb.minZ <= this.max.z
    }

    fun containsVec(vec: Vec3): Boolean {
        return vec.x in this.min.x..this.max.x && vec.z in this.min.z..this.max.z
    }
}