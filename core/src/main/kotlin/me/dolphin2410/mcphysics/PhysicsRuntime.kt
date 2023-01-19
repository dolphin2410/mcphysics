package me.dolphin2410.mcphysics

/**
 * McPhysics 시공간 단위
 */
abstract class PhysicsRuntime(val ticksPerSecond: Int) {
    private val objects = ArrayList<PhysicsObject>()

    fun addObject(obj: PhysicsObject) {
        objects.add(obj)
    }

    fun removeObject(obj: PhysicsObject) {
        objects.remove(obj)
    }

    // Runs every tick
    fun update() {
        for (obj in objects) {
            obj.update()
        }
    }
}