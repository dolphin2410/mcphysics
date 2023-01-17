package me.dolphin2410.mcphysics.paper

import io.github.monun.tap.fake.FakeEntity
import me.dolphin2410.mcphysics.Maths.toBukkit
import me.dolphin2410.mcphysics.Maths.toPhysics
import me.dolphin2410.mcphysics.PhysicsObject
import me.dolphin2410.mcphysics.PhysicsVector
import org.bukkit.FluidCollisionMode
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

fun FakeEntity<out Entity>.toPhysicsObject(runtime: PaperPhysics) = PaperPhysicsObject(runtime, this)
class PaperPhysicsObject(
    runtime: PaperPhysics,
    val entity: FakeEntity<out Entity>
) : PhysicsObject(runtime) {
    override val mass: Double = 1.0

    override fun getFloorPos(): PhysicsVector {
        val res = entity.bukkitEntity.world.rayTraceBlocks(entity.bukkitEntity.location.clone(), Vector(0, -1, 0), 10.0, FluidCollisionMode.NEVER, true)
        return res?.hitBlock?.location?.toPhysics() ?: (entity.bukkitEntity.location.toPhysics().apply { y = -64.0 })
    }

    override var position: PhysicsVector
        get() = entity.location.toPhysics()
        set(value) {
            entity.moveTo(value.toBukkit(entity.bukkitEntity.world))
        }
}