package me.dolphin2410.mcphysics

import kotlin.math.pow

class NewtonForce(private val v: PhysicsVector): MotionFactor {
    private var priorTime: Long = System.currentTimeMillis()
    private var isValid = true

    override fun deltaVelocity(mass: Double): PhysicsVector {
        if(!isValid) {
            return PhysicsVector.ZERO
        }
        val deltaTime = System.currentTimeMillis() - priorTime
        val deltaVel = v / mass * (deltaTime.toDouble() * 0.001)

        priorTime += deltaTime

        return deltaVel
    }

    override fun deltaPosition(mass: Double): PhysicsVector {
        if(!isValid) {
            return PhysicsVector.ZERO
        }
        val deltaTime = System.currentTimeMillis() - priorTime

        val deltaPos = v / mass * 0.5 * (2 * deltaTime * priorTime + deltaTime.toDouble().pow(2)) * (0.001 * 0.001)

        priorTime += deltaTime

        return deltaPos
    }

    override fun invalidate() {
        isValid = false
    }

    override fun revalidate() {
        priorTime = System.currentTimeMillis()
        isValid = true
    }
}