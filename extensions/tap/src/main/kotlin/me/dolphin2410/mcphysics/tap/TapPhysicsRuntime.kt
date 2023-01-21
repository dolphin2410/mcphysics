package me.dolphin2410.mcphysics.tap

import io.github.monun.tap.fake.FakeEntity
import me.dolphin2410.mcphysics.PhysicsObject
import me.dolphin2410.mcphysics.PhysicsRuntime
import me.dolphin2410.mcphysics.tap.ConversionUtil.toPhysicsObject
import org.bukkit.entity.Entity

class TapPhysicsRuntime: PhysicsRuntime(20) {
    override val objects = ArrayList<TapPhysicsObject>()

    fun addObject(entity: FakeEntity<out Entity>): TapPhysicsObject {
        val obj = entity.toPhysicsObject(this)
        objects.add(obj)
        return obj
    }

    fun removeObject(entity: FakeEntity<out Entity>) {
        objects.removeIf { it.entity == entity }
    }
}