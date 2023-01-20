package me.dolphin2410.mcphysics

class Force(startTick: Int, val v: PhysicsVector) {
    private val children = ArrayList<Force>()

    var _startTick = 0
    var startTick: Int
        get() = _startTick
        set(value) {
            for (child in children) {
                child.startTick = value
            }

            _startTick = value
        }
    init {
        this.startTick = startTick
    }
    fun child(obj: PhysicsObject, v: PhysicsVector): Force {
        val force = Force(obj.flyTicks, v)
        children.add(force)
        return force
    }
}