package me.dolphin2410.mcphysics

import kotlin.math.pow

abstract class PhysicsObject(private val runtime: McPhysics) {
    var isOnGround: Boolean
        get() = flyTicks == 0
        set(value) {
            if (value) {
                flyTicks = 0
            }
        }

    abstract var position: PhysicsVector

    private var flyTicks = 0
    private val force = PhysicsVector(0, 0, 0)

    // Set by computation
    private val relCenter = PhysicsVector(0, 0, 0)
    private val relPos = PhysicsVector(0, 0, 0)

    // Set by applying force
    private val velZero = PhysicsVector(0, 0, 0)

    abstract val mass: Double

    abstract fun getFloorPos(): PhysicsVector

    fun update() {
        if (getFloorPos().y + 1 == position.y && isOnGround && velZero == PhysicsVector.ZERO) return // is standing

        flyTicks++
        val flySeconds = flyTicks.toDouble() / runtime.ticksPerSecond

        val acceleration = force / mass
        acceleration -= PhysicsVector(0.0, 9.8, 0.0)   // Gravity

        // s = v0 * t + 1/2 a * t^2 where v0 = 0
        relPos.set(velZero * flySeconds + acceleration * flySeconds.pow(2) / 2.0)

        val pre = relCenter + relPos
        if (pre.y < getFloorPos().y + 1) {
            pre.y = getFloorPos().y + 1
            velZero.set(PhysicsVector.ZERO)
            flyTicks = 0
            relCenter.set(position.clone())
            isOnGround = true
        }

        position = pre
    }

    fun project(velocity: PhysicsVector) {
        relCenter.set(position.clone())
        velZero.set(velocity)
    }
}