package dev.frozencloud.infernum.util.skyblock.kuudra

import dev.frozencloud.infernum.Infernum.mc
import net.minecraft.world.phys.Vec3
import kotlin.math.pow

enum class PreSpot (val pos: Vec3, val crate: Crate, val second: Crate) {
    Triangle(Vec3(-69.0, 77.0, -122.0), Crate.Triangle, Crate.Shop),
    Equals(Vec3(-66.0, 76.0, -88.0), Crate.Equals, Crate.Square),
    Slash(Vec3(-113.0, 77.0, -70.0), Crate.Slash, Crate.Square),
    X(Vec3(-133.0, 77.0, -138.0), Crate.X, Crate.XCannon);

    val isClientNear: Boolean get() {
        val pp = mc.player?.position() ?: return false
        return ((pp.x - pos.x).pow(2) + (pp.z - pos.z).pow(2)) < 16
    }
}