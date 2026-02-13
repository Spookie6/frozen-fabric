package dev.frozencloud.frozen.util.skyblock.kuudra

import dev.frozencloud.frozen.util.Region
import net.minecraft.world.phys.Vec3

enum class Crate(
    val spawnRegion: Region,
    val pickupRegion: Region,
    val supplyPiles: SupplyPiles,
    val msg: String
) {
    Triangle(
        Region.fromCorners(
            Vec3(-70.0, 0.0, -133.0),
            Vec3(-47.0, 0.0, -113.0)
        ),
        Region.fromCorners(
            Vec3(-66.0, 0.0, -126.0),
            Vec3(-74.0, 0.0, -118.0)
        ),
        SupplyPiles.Triangle,
        "No Triangle!"
    ),

    Shop(
        Region.fromCorners(
            Vec3(-94.0, 0.0, -166.0),
            Vec3(-61.0, 0.0, -134.0)
        ),
        Region.fromCorners(
            Vec3(-67.0, 0.0, -140.0),
            Vec3(-94.0, 0.0, -127.0)
        ),
        SupplyPiles.Shop,
        "No Shop!"
    ),

    Equals(
        Region.fromCorners(
            Vec3(-71.0, 0.0, -96.0),
            Vec3(-41.0, 0.0, -74.0)
        ),
        Region.fromCorners(
            Vec3(-62.0, 0.0, -93.0),
            Vec3(-81.0, 0.0, -84.0)
        ),
        SupplyPiles.Slash,
        "No Equals!"
    ),

    Slash(
        Region.fromCorners(
            Vec3(-127.0, 0.0, -72.0),
            Vec3(-99.0, 0.0, -50.0)
        ),
        Region.fromCorners(
            Vec3(-105.0, 0.0, -74.0),
            Vec3(-120.0, 0.0, -66.0)
        ),
        SupplyPiles.Equals,
        "No Slash!"
    ),

    Square(
        Region.fromCorners(
            Vec3(-163.0, 0.0, -99.0),
            Vec3(-136.0, 0.0, -74.0)
        ),
        Region.fromCorners(
            Vec3(-132.0, 0.0, -79.0),
            Vec3(-148.0, 0.0, -96.0)
        ),
        SupplyPiles.NONE,
        "No Square!"
    ),

    XCannon(
        Region.fromCorners(
            Vec3(-156.0, 0.0, -136.0),
            Vec3(-126.0, 0.0, -110.0)
        ),
        Region.fromCorners(
            Vec3(-126.0, 0.0, -110.0),
            Vec3(-139.0, 0.0, -133.0)
        ),
        SupplyPiles.XCannon,
        "No X Cannon!"
    ),

    X(
        Region.fromCorners(
            Vec3(-150.0, 0.0, -156.0),
            Vec3(-123.0, 0.0, -137.0)
        ),
        Region.fromCorners(
            Vec3(-137.0, 0.0, -134.0),
            Vec3(-123.0, 0.0, -142.0)
        ),
        SupplyPiles.X,
        "No X!"
    );

    val regex by lazy {  Regex("Party > ((\\[(.)+])?) *(\\w{3,16}): $msg*") }
}