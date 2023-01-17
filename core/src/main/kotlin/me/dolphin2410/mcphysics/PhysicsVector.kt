package me.dolphin2410.mcphysics

import kotlin.math.pow
import kotlin.math.sqrt

data class PhysicsVector(var x: Double, var y: Double, var z: Double) {
    companion object {
        @JvmStatic
        val ZERO = PhysicsVector(0, 0, 0)
    }

    constructor(x: Int, y: Int, z: Int): this(x.toDouble(), y.toDouble(), z.toDouble())

    operator fun div(n: Double) = PhysicsVector(x / n, y / n, z / n)
    operator fun plus(v: PhysicsVector) = PhysicsVector(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: PhysicsVector) = plus(v * -1.0)
    operator fun times(n: Double) = div(1 / n)

    operator fun plusAssign(v: PhysicsVector) {
        this.x += v.x
        this.y += v.y
        this.z += v.z
    }

    operator fun minusAssign(v: PhysicsVector) = plusAssign(v * -1.0)

    fun set(v: PhysicsVector) {
        this.x = v.x
        this.y = v.y
        this.z = v.z
    }

    fun clone() = PhysicsVector(x, y, z)

    val length: Double
        get() = sqrt(x.pow(2) + y.pow(2) + z.pow(2))

    infix fun dot(other: PhysicsVector): Double {
        return this.x * other.x + this.y * other.y + this.z + other.z
    }

    infix fun cross(other: PhysicsVector): PhysicsVector {
        return PhysicsVector(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x)
    }

    fun grow(newLength: Double) {
        val l = length

        this.x *= newLength / l
        this.y *= newLength / l
        this.z *= newLength / l
    }
}