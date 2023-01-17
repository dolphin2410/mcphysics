package me.dolphin2410.mcphysics

import kotlin.math.*

abstract class PhysicsObject(private val runtime: PhysicsRuntime) {
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

    // 속도 설정
    private val velocity = PhysicsVector(0, 0, 0)

    abstract val mass: Double

    private val updates = ArrayList<() -> Unit>()

    abstract fun getFloorPos(): PhysicsVector

    fun update() {
        if (getFloorPos().y + 1 == position.y && isOnGround && velocity == PhysicsVector.ZERO) return // is standing

        flyTicks++
        for (update in updates) {
            update()
        }

        val flySeconds = flyTicks.toDouble() / runtime.ticksPerSecond

        val acceleration = force / mass
        acceleration -= PhysicsVector(0.0, 9.8, 0.0)   // Gravity

        // s = v0 * t + 1/2 a * t^2 where v0 = 0
        relPos += velocity * flySeconds + acceleration * flySeconds.pow(2) / 2.0

        val pre = relCenter + relPos

        println(pre)

        if (pre.y < getFloorPos().y + 1) {
            pre.y = getFloorPos().y + 1
            velocity.set(PhysicsVector.ZERO)
            flyTicks = 0
            relCenter.set(position.clone())
            isOnGround = true
        }

        position = pre

        relPos.set(PhysicsVector.ZERO)
    }

    // 등속원운동
    fun circle(center: PhysicsVector, radius: Double) {
        val mg = PhysicsVector(0.0, 9.8, 0.0) * mass
        force.set(mg)

        val distance = center - position
        val theta = asin(radius / distance.length)
        val r = distance.length * sin(theta)
        val w = sqrt(9.8 / (distance.length * cos(theta)))

        relCenter.set(center - PhysicsVector(0.0, distance.length * cos(theta), 0.0))

        updates.add {
            val flySeconds = flyTicks.toDouble() / runtime.ticksPerSecond

            relPos.set(PhysicsVector(r * cos(w * flySeconds), 0.0, r * sin(w * flySeconds)))
        }
    }

    fun project(velocity: PhysicsVector) {
        if (relCenter == PhysicsVector.ZERO) {
            relCenter.set(position.clone())
        }

        this.velocity.set(velocity)
    }

    fun addForce(force: PhysicsVector) {
        this.force += force
    }
}