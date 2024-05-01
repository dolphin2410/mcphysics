package me.dolphin2410.mcphysics

class ProtectedPhysicsVector(private val value: PhysicsVector) {
    fun getValue(): PhysicsVector {
        return PhysicsVector.clone(value)
    }

    internal fun setValue(newVector: PhysicsVector) {
        value.set(newVector)
    }
}