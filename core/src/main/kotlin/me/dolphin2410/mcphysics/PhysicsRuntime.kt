package me.dolphin2410.mcphysics

/**
 * McPhysics 시공간 단위
 */
abstract class PhysicsRuntime(val ticksPerSecond: Int) {
    abstract val objects: ArrayList<out PhysicsObject>

    // Runs every tick
    fun update() {
        val iter = objects.iterator()
        while (iter.hasNext()) {
            val obj = iter.next()
            obj.update()

            if (!obj.valid) {
                iter.remove()
            }
        }
    }
}