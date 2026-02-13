package dev.frozencloud.frozen.util.skyblock.kuudra

import net.minecraft.world.phys.Vec3

enum class PreSpot (val pos: Vec3, val crate: Crate, val second: Crate) {
    Triangle(Vec3(-69.0, 77.0, -122.0), Crate.Triangle, Crate.Shop),
    Equals(Vec3(-66.0, 76.0, -88.0), Crate.Equals, Crate.Square),
    Slash(Vec3(-113.0, 77.0, -70.0), Crate.Slash, Crate.Square),
    X(Vec3(-133.0, 77.0, -138.0), Crate.X, Crate.XCannon),
}