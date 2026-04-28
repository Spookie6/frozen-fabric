package dev.frozencloud.infernum.util

import net.minecraft.world.phys.Vec3
import kotlin.math.*

object PearlUtil {
    val acceleration = Vec3(0.0, -0.03, 0.0)
    val drag = 0.01
    val speed = 1.5
    val squaredSpeed = speed * speed

    val PHI = (1 + sqrt(5.0)) / 2
    val INV_PHI = 1.0 / PHI

    /**
     * Computes velocity after time t that reaches the given displacement under constant acceleration + linear drag.
     */
    private fun velocityGivenTime(t: Double, displacement: Vec3): Vec3 {
        if (t < 1e-9) return Vec3(0.0, 0.0, 0.0)

        val expTerm = -expm1(-drag * t)  // 1 - e^(-drag*t)
        if (expTerm < 1e-12) return Vec3(0.0, 0.0, 0.0)

        return displacement.mult(drag)
            .subtract(acceleration.mult(t))
            .div(expTerm)
            .add(acceleration.div(drag))
    }

    /**
     * Golden-section search for minimizing a scalar function on [a, b]
     */
    private fun minimizeScalarBounded(f: (Double) -> Double, a: Double, b: Double, tol: Double = 1e-6, maxIter: Int = 50): Double {
        var left = a
        var right = b
        var c = right - (right - left) * INV_PHI
        var d = left + (right - left) * INV_PHI

        var fc = f(c)
        var fd = f(d)

        repeat(maxIter) {
            if (right - left < tol) return 0.5 * (left + right)

            if (fc < fd) {
                right = d
                d = c
                fd = fc
                c = right - (right - left) * INV_PHI
                fc = f(c)
            } else {
                left = c
                c = d
                fc = fd
                d = left + (right - left) * INV_PHI
                fd = f(d)
            }
        }
        return 0.5 * (left + right)
    }

    /**
     * Finds the best initial direction and flight time to hit the target with the given speed.
     * @param pos Current position
     * @param target Target position
     * @param flatPearl If true, searches in short time range (normal pearl), else long range (high pearl)
     * @return Pair of (normalized direction, flight time)
     */
    fun findLookDirAndTime(pos: Vec3, target: Vec3, flatPearl: Boolean = true): Pair<Vec3, Double> {
        val dpos = target.subtract(pos)

        val objective: (Double) -> Double = { time ->
            val vel = velocityGivenTime(time, dpos)
            abs(vel.lenghSquared - squaredSpeed)
        }

        val minTime = 1e-6
        val maxTime = if (flatPearl) 60.0 else 120.0

        val time = minimizeScalarBounded(objective, minTime, maxTime)

        val desiredVelocity = velocityGivenTime(time, dpos)
        val dir = desiredVelocity.normalize()

        return dir to time
    }
}