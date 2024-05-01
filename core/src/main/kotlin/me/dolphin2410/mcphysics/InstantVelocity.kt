package me.dolphin2410.mcphysics

class InstantVelocity (private val v: PhysicsVector): MotionFactor {
    private var priorTime: Long = System.currentTimeMillis()
    private var isValid = true

    override fun deltaVelocity(mass: Double): PhysicsVector {
        return PhysicsVector.ZERO
    }

    override fun deltaPosition(mass: Double): PhysicsVector {
        if(!isValid) {
            return PhysicsVector.ZERO
        }

        val deltaTime = System.currentTimeMillis() - priorTime

        val deltaPos = v * deltaTime.toDouble() * 0.001

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