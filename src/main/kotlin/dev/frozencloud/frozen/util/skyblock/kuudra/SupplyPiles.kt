package dev.frozencloud.frozen.util.skyblock.kuudra

import net.minecraft.world.phys.Vec3

enum class SupplyPiles(val pos: Vec3, var placed: Boolean = false) {
    Shop(Vec3(-98.0, 79.0, -112.0)),
    Equals(Vec3(-98.0, 79.0, -99.0)),
    XCannon(Vec3(-110.0, 79.0, -106.0)),
    X(Vec3(-106.0, 79.0, -112.0)),
    Triangle(Vec3(-94.0, 79.0, -106.0)),
    Slash(Vec3(-106.0, 79.0, -99.0)),
    NONE(Vec3(0.0, 0.0, 0.0))
}