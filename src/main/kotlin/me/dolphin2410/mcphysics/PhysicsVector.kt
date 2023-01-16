package me.dolphin2410.mcphysics

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
}