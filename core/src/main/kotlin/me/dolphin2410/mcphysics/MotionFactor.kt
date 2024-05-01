package me.dolphin2410.mcphysics

interface MotionFactor {
    fun deltaVelocity(mass: Double): PhysicsVector
    fun deltaPosition(mass: Double): PhysicsVector
    fun invalidate()
    fun revalidate()
}