package dev.frozencloud.infernum.util.skyblock.kuudra

import net.minecraft.world.phys.Vec3

enum class SupplyPiles(val pos: Vec3, var collected: Boolean = false) {
    Shop(Vec3(-98.0, 79.0, -112.9375)),
    Triangle(Vec3(-94.0, 79.0, -106.0)),
    Slash(Vec3(-98.03125, 79.0, -99.09375)),
    Equals(Vec3(-106.03125, 79.0, -99.09375)),
    XCannon(Vec3(-110.0, 79.0, -106.03125)),
    X(Vec3(-106.0, 79.0, -112.9375));
}